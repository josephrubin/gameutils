package box.shoe.gameutils.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.RestrictTo;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import box.gift.gameutils.BuildConfig;
import box.gift.gameutils.R;
import box.shoe.gameutils.Interpolatable;
import box.shoe.gameutils.Interpolation;
import box.shoe.gameutils.screen.Screen;

/**
 * Created by Joseph on 10/23/2017.
 *
 * The Game Engine uses two threads. One for game updates, and one to paint frames.
 * Game updates are run at a frequency determined by the supplied UPS given in the constructor.
 * Frames are painted at each new VSYNC (Screen refresh), supplied by internal Choreographer.
 * Because these are not aligned, the engine interpolates between two game states for frame rendering based on time,
 * and gives the interpolated game state to the Screen supplied to the constructor.
 */
//TODO: synchronization tends to be overkill in this class. clean it up.
//TODO: perhaps move the threads to different files because this is getting large (many responsibilities).
public class Engine //TODO: redo input system. make it easy, usable.
{ //TODO: remove isActive()/isPlaying() and replace with a single state variable.
    //TODO: need an easier way to load scaled bitmaps. use preload scaling if possible (pow of 2), and after loading scaling otherwise. Have pixel art mode to disable filtering/anti-aliasing.

    // Define the possible UPS options, which are factors of 1000 (so we get an even number of MS per update).
    // This is not a hard requirement, and the annotation may be suppressed,
    // at the risk of possible jittery frame display. //TODO: is this actually a real concern?
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({1, 2, 4, 5, 8, 10, 20, 25, 40, 50, 100, 125, 200, 250, 500, 1000})
    public @interface UPS_Options {}

    // Number of Updates Per Second that we would like to receive.
    // There are timing accuracy limitations and it is possible for the updates to take too long
    // for this to be possible (lag), so we call it 'TARGET.'
    private final int TARGET_UPS;

    // Based on the TARGET_UPS we can define how long we expect each update to take, in milliseconds and nanoseconds.
    private final long expectedUpdateTimeMS;
    private final long expectedUpdateTimeNS;

    // The game itself, which the engine handles by calling its methods and telling it when to update/render.
    private Game game;

    // The screen which will display a representation of the state of the game.
    private Screen screen;

    // Control - try to mitigate use of volatile variables when possible.
    // (issue is not the number of volatile variables, rather how often they
    // are read causing memory to be flushed by the thread that set it).
    // Be careful with ints, because even incrementation is three operations (not wholly atomic).
    private volatile boolean started = false;
    private volatile boolean stopped = false;
    private volatile boolean stopThreads = false;
    private volatile boolean paused = false;
    private volatile boolean pauseThreads = false;

    // Threads.
    // Runs game updates.
    private Thread updateThread;
    private Looper updateThreadLooper;
    // Runs frame rendering.
    private Thread frameThread;
    private Looper frameThreadLooper;
    // For instantiating CountDownLatches.
    private final int NUMBER_OF_THREADS = 2;

    // Concurrent - for simplicity, define as few as possible.
    // Always used in no more than one un-synchronized blocks, so no need to be volatile?
    private final Object monitorUpdateFrame = new Object();
    private final Object monitorControl = new Object();
    private CountDownLatch pauseLatch; // Makes sure all necessary threads pause before returning pauseGame.
    private CountDownLatch stopLatch; // Makes sure all necessary threads stop before returning stopGame.

    // Objs - remember to cleanup those that can be!
    private Choreographer vsync;
    private List<GameState> gameStates;

    //TODO: sort
    private double displayRefreshRate;

    // We keep track of the number of milliseconds that can be taken to render a frame (rounded up) based on this device's
    // display's refresh rate (how often a VSYNC occurs). We use this to see if our frame rendering is taking too long.
    // If it is, then we may be in a 'spiral of death,' where each new frame is taking longer due to being more and more
    // behind. To attempt to break free, we will skip a frame when [time it took to render] > ALLOTTED_TIME_PER_FRAME_MS.
    private final int ALLOTTED_TIME_PER_FRAME_MS;

    // Fixed display mode - display will attempt to paint
    // pairs of updates for a fixed amount of time (expectedUpdateDelayNS)
    // regardless of the amount of time that passed between
    // the generation of the two updates. When an update
    // happens too quickly or slowly, this will cut short
    // or artificially lengthen the painting of a pair of updates
    // because the next update comes too early or late.
    // Pro: looks better when an occasional update comes too early or too late.
    private static final int DIS_MODE_FIX_UPDATE_DISPLAY_DURATION = 0;

    // Varied display mode - display will lengthen or shorten
    // the amount of time it takes to display a pair of updates.
    // When an update happens too quickly or slowly, this will
    // cause quite a jitter.
    // Pro: When many updates in a row come to early or late, instead
    // of jittering the display will simply speed up or slow down
    // to keep pace with the updates, which looks very nice.
    //fixme:BUG- game is slow for a whole update after coming back from a pause, since that pause time is counted.
    private static final int DIS_MODE_VAR_UPDATE_DISPLAY_DURATION = 1;

    // Which display mode the engine is currently using.
    private int displayMode = DIS_MODE_FIX_UPDATE_DISPLAY_DURATION;

    // Choreographer tells you a fake time stamp for beginning of VSYNC.
    // It really occurs at (frameTimeNanos - vsyncOffsetNanos).
    // This is not a huge deal, but if we can correct for it, why not?
    private long vsyncOffsetNanos;

    public Engine(Screen screen, final Game game) //target ups should divide evenly into 1000000000, updates are accurately called to within about 10ms
    {
        this.TARGET_UPS = game.getTargetUpdatesPerSecond();
        this.expectedUpdateTimeMS = 1000 / this.TARGET_UPS;
        this.expectedUpdateTimeNS = this.expectedUpdateTimeMS * 1000000;

        this.game = game;
        this.screen = screen;

        gameStates = new LinkedList<>();

        // Setup the 'Updates' thread.
        updateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
                Looper.prepare();
                updateThreadLooper = Looper.myLooper();
                runUpdates();

            }
        }, this.screen.asView().getContext().getString(R.string.update_thread_name));

        // Setup the 'Frames' thread.
        frameThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
                Looper.prepare();
                frameThreadLooper = Looper.myLooper();
                runFrames();
            }
        }, this.screen.asView().getContext().getString(R.string.frame_thread_name));

        this.screen.asView().setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                // The game takes all input, and no extra effects should interfere,
                // so no need to call view.performClick();

                // Only use touch event if not paused.
                if (isPlaying() && motionEvent != null)
                {
                    synchronized (monitorUpdateFrame)
                    {
                        game.onTouch(motionEvent);
                    }
                    return true;
                }

                return false;
            }
        });

        //TODO: a different way to get it?
        /*

        DisplayInfo di = DisplayManagerGlobal.getInstance().getDisplayInfo(
                Display.DEFAULT_DISPLAY);
        return di.getMode().getRefreshRate();

         */
        //TODO: fallback if this cannot be done? (when the display returns null).
        Display display = ((WindowManager) this.screen.asView().getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        displayRefreshRate = display.getRefreshRate();
        ALLOTTED_TIME_PER_FRAME_MS = (int) Math.ceil(1000 / displayRefreshRate);
        //L.d("timer per, expected 17 on this device: " + ALLOTTED_TIME_PER_FRAME_MS, "optimization");
        //L.d("Display Refresh Rate: " + displayRefreshRate, "optimization");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            vsyncOffsetNanos = display.getAppVsyncOffsetNanos();
        }
        else
        {
            vsyncOffsetNanos = 0;
        }
    }

    private void runUpdates()
    {
        // Make sure that we are on updateThread.
        if (!Thread.currentThread().getName().equals(screen.asView().getContext().getString(R.string.update_thread_name)))
        {
            throw new IllegalThreadStateException("Can only be called from updateThread!");
        }

        final Handler updateHandler = new Handler();

        Runnable updateCallback = new Runnable()
        {
            private boolean updateThreadPaused;
            private long startUpdateTimeNS = 0;

            @Override
            public void run()
            {
                // Keep track of what time it is now. Goes first to get most accurate timing.
                startUpdateTimeNS = System.nanoTime();

                // Schedule next update. Goes second to get as accurate as possible updates.
                // We do it at the start to make sure we are waiting a precise amount of time
                // (as precise as we can get with postDelayed). This means we manually remove
                // the callback if the game stops.
                //updateHandler.removeCallbacksAndMessages(null);
                updateHandler.postDelayed(this, expectedUpdateTimeMS);

                // Acquire the monitor lock, because we cannot update the game at the same time we are trying to draw it.
                synchronized (monitorUpdateFrame)
                {
                    game.update();

                    // Make new GameState with time stamp of the start of this update round.
                    GameState gameState = new GameState();
                    gameState.setTimeStamp(startUpdateTimeNS);

                    // Allow all Interpolatables to save their values.
                    doSaveInterpValues(gameState);

                    gameStates.add(gameState);

                    // Pause game (postDelayed runnable should not run while this thread is waiting, so no issues there)
                    while (pauseThreads)
                    {
                        try
                        {
                            if (!updateThreadPaused)
                            {
                                pauseLatch.countDown();
                            }
                            updateThreadPaused = true;
                            monitorUpdateFrame.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    updateThreadPaused = false;

                    // Stop thread.
                    if (stopThreads)
                    {
                        updateHandler.removeCallbacksAndMessages(null);
                        stopLatch.countDown();
                        return;
                    }
                    //TODO: if updates are consistently taking too long (or even too short! [impossible?]) we can switch visualization modes.
                }
            }
        };

        updateHandler.post(updateCallback);
        Looper.loop();
    }

    private void doSaveInterpValues(GameState gameState)
    {
        Map<Interpolatable, float[]> savedInterpValues = gameState.getSavedInterpValues();
        for (Interpolatable interpolatable : Interpolatable.INTERPOLATABLE_SERVICE.getMembers())
        {
            int interpValuesArrayMaxIndex = interpolatable.getInterpValuesArrayMaxIndex();
            float[] out = new float[interpValuesArrayMaxIndex + 1];

            interpolatable.saveInterpValues(out);

            savedInterpValues.put(interpolatable, out);
        }
    }

    private void doLoadInterpValues(GameState gameStatePast, GameState gameStateCurrent, double interpolationRatio)
    {
        Map<Interpolatable, float[]> savedInterpValuesPast = gameStatePast.getSavedInterpValues();
        Map<Interpolatable, float[]> savedInterpValuesCurrent = gameStateCurrent.getSavedInterpValues();
        for (Interpolatable interpolatable : savedInterpValuesCurrent.keySet())
        {
            if (savedInterpValuesPast.containsKey(interpolatable))
            {
                float[] pastOut = savedInterpValuesPast.get(interpolatable);
                float[] currentOut = savedInterpValuesCurrent.get(interpolatable);

                if (pastOut.length != currentOut.length)
                {
                    throw new IllegalStateException("Interpolatable " + interpolatable + " does not consistantly " +
                            "save the same amount of interp values!");
                }

                int outLength = currentOut.length;
                float[] in = new float[outLength];
                for (int i = 0; i < outLength; i++)
                {
                    float pastValue = pastOut[i];
                    float currentValue = currentOut[i];

                    float interpolatedValue = Interpolation.interpolateFloat(pastValue, currentValue, interpolationRatio);
                    in[i] = interpolatedValue;
                }

                interpolatable.loadInterpValues(in);
            }
        }
    }

    private void runFrames()
    {
        // Make sure that we are on frameThread.
        if (!Thread.currentThread().getName().equals(screen.asView().getContext().getString(R.string.frame_thread_name)))
        {
            throw new IllegalThreadStateException("Can only be called from frameThread!");
        }

        vsync = Choreographer.getInstance();

        Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback()
        {
            private long beginDoFrameThreadTimeMS;
            private boolean skipNextFrame = false;

            private boolean frameThreadPaused;

            private Canvas renderCanvas = null;

            @Override
            public void doFrame(long frameTimeNanos)
            {
                beginDoFrameThreadTimeMS = SystemClock.currentThreadTimeMillis();

                // Correct for minor difference in VSYNC time.
                // This is probably totally unnecessary, but we might as well be as accurate as possible.
                // (And will only change frameTimeNanos in a sufficiently high API anyway).
                // See notes above for field vsyncOffsetNanos.
                frameTimeNanos -= vsyncOffsetNanos;

                // Skip a frame if last frame indicated that it took too long.
                if (skipNextFrame)
                {
                    skipNextFrame = false;

                    // If we plan on stopping or pausing during this frame, then
                    // we will not skip it, and instead continue as normal.
                    if (!stopThreads && !pauseThreads)
                    {
                        // Put ourselves up for the next frame...
                        vsync.postFrameCallback(this);
                        // ...and don't do any further work this frame.
                        return;
                    }
                } //TODO: we may need to not skip if we just came out of a pause because the engine might misidentify that as a frame that took too long.

                boolean paintedFrame = false;
                synchronized (monitorUpdateFrame)
                {
                    // Pause this thread if prompted.
                    // Spin lock when we want to pause.
                    while (pauseThreads)
                    {
                        try
                        {
                            if (!frameThreadPaused)
                            {
                                if (screen.isRendering())
                                {
                                    // Unlock the canvas without painting anything new.
                                    screen.endRender(); //TODO: we should really be posting the most recent state, we don't want to end up rendering the last frame (due to buffer swapping).
                                }

                                // Let the pauser know that we have reached the pause routine.
                                pauseLatch.countDown();
                            }

                            // We are now paused, so make sure that we know in event of a spurious wakeup
                            // that we were paused already (and do not count down latch/endRender again).
                            frameThreadPaused = true;

                            // Now we wait on the monitor. It is true that we already counted down the pause latch,
                            // but the pauser will not truly continue until we have called .wait() (and released the lock).
                            monitorUpdateFrame.wait();
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    frameThreadPaused = false;

                    // Stop this thread if prompted.
                    if (stopThreads)
                    {
                        stopLatch.countDown();

                        // It is true that we already counted down the stop latch, but the stopper will not truly
                        // continue until we have returned (and released the lock). This is to be sure that our
                        // Looper has processed that this Choreographer callback is over before our Looper is stopped.
                        return;
                    }
                }

                if (!screen.isRendering())
                {
                    renderCanvas = screen.startRender();
                }

                // Must ask for new callback each frame!
                vsync.postFrameCallback(this);

                synchronized (monitorUpdateFrame)
                {
                    // Paint frame.
                    while (gameStates.size() >= 2 && !paintedFrame)
                    {
                        GameState gameStatePast = gameStates.get(0);
                        GameState gameStateCurrent = gameStates.get(1);

                        double interpolationRatio;
                        // TODO: auto switch paint modes in response to update lag. priority=low
                        if (displayMode == DIS_MODE_FIX_UPDATE_DISPLAY_DURATION)
                        {
                            interpolationRatio = (frameTimeNanos - gameStateCurrent.getTimeStamp()) / ((double) expectedUpdateTimeNS);
                        }
                        else if (displayMode == DIS_MODE_VAR_UPDATE_DISPLAY_DURATION)
                        {
                            // Time that passed between the game states in question.
                            long timeBetween = gameStateCurrent.getTimeStamp() - gameStatePast.getTimeStamp();
                            interpolationRatio = (frameTimeNanos - gameStateCurrent.getTimeStamp()) / ((double) timeBetween);
                        }
                        else
                        {
                            throw new IllegalStateException("Engine is in an invalid displayMode.");
                        }

                        if (interpolationRatio >= 1)
                        {
                            // Remove the old GameState.
                            if (gameStates.size() >= 1)
                            {
                                gameStates.remove(0);
                            }
                        }
                        else
                        {
                            doLoadInterpValues(gameStatePast, gameStateCurrent, interpolationRatio);

                            game.render(screen.asView().getResources(), renderCanvas); //TODO: i don't like getting context from the Screen view....
                            paintedFrame = true;
                        }
                    }
                }
                if (paintedFrame)
                {
                    screen.endRender();
                }

                if (!paintedFrame && BuildConfig.DEBUG)
                {
                    Log.i("Engine", "No frame was painted because there were not enough new GameStates!" +
                            " Perhaps your update code is taking too long.");
                }

                //TODO: we should only skip frames if it takes too long to draw, not if the updates are taking too long. so this needs to make sure to only count the draw frame time, which is not working.
                // If we took more time than we are allowed, it may be that we are stuck
                // in a "death spiral", where we are constantly unable to catch up due to
                // the constant demand to generate the next frame. So we ease up a little bit
                // by skipping the next frame, so that we do not get screen jank.
                if (SystemClock.currentThreadTimeMillis() - beginDoFrameThreadTimeMS > ALLOTTED_TIME_PER_FRAME_MS)
                {
                    skipNextFrame = true;
                    if (BuildConfig.DEBUG)
                    {
                        Log.i(Engine.this.getClass().getSimpleName(),
                                "We took too long to generate this past frame, so we will skip the next one to" +
                                        "ease up on the load and avoid jank. If this is happening a lot, your drawing" +
                                        "routine may be doing too much work!");
                    }
                }
            }
        };
        //Looper.myLooper().setMessageLogging(new LogPrinter(Log.DEBUG, "Looper"));
        vsync.postFrameCallback(frameCallback);
        Looper.loop();
    }

    public void startGame()
    {
        if (started)
        {
            // May not start a game that is already started.
            throw new IllegalStateException("Game already started!");
        }
        else
        {
            started = true;
            screen.setReadyListener(new Screen.ReadyListener()
            {
                @Override
                public void onReady(int screenWidth, int screenHeight)
                {
                    screen.removeReadyListener();
                    screen.setSizeChangedListener(new Screen.SizeChangedListener()
                    {
                        @Override
                        public void onSizeChanged(int newScreenWidth, int newScreenHeight, int oldScreenWidth, int oldScreenHeight)
                        {
                            game.onScreenSizeChanged(newScreenWidth, newScreenHeight, oldScreenWidth, oldScreenHeight);
                        }
                    });
                    launch();
                }
            });
        }
    }

    private void launch()
    {
        // At this point, the Screen has dimensions, so we can do initialization based on them.
        game.onStart(screen.getWidth(), screen.getHeight());

        // We will launch two threads.
        // 1) Do game logic (game updates).
        // 2) Alert surface view (render frames).
        updateThread.start();
        frameThread.start();
    }

    /**
     * Stop the game.
     * Stops the threads responsible for updating and rendering the game.
     * After calling this, the Engine is no longer usable.
     * Cleanup happens in this method.
     */
    public void stopGame()
    {
        synchronized (monitorControl)
        { //TODO: any reason not to move these checks outside the sync block?
            if (Thread.currentThread().getName().equals(screen.asView().getContext().getString(R.string.update_thread_name)))
            {
                throw new IllegalThreadStateException("Cannot be called from "
                        + screen.asView().getContext().getString(R.string.update_thread_name)
                        + " thread, because it cannot stop itself!");
            }
            else if (Thread.currentThread().getName().equals(screen.asView().getContext().getString(R.string.frame_thread_name)))
            {
                throw new IllegalThreadStateException("Cannot be called from "
                        + screen.asView().getContext().getString(R.string.frame_thread_name)
                        + " thread, because it cannot stop itself!");
            }

            if (!isActive())
            {
                throw new IllegalStateException("Game was never started!");
            }

            if (stopThreads)
            {
                throw new IllegalStateException("Engine already in process of stopping!");
            }

            if (!isActive())
            {
                throw new IllegalStateException("Cannot stop if game is not active!");
            }

            // Check if we are paused.
            // If so, we must first pause the game before we can attempt to stop it.
            if (isPlaying())
            {
                // Will not return until the game is paused.
                pauseGame();
            }

            stopLatch = new CountDownLatch(NUMBER_OF_THREADS);
            stopThreads = true;

            // Now we know the threads are paused, and we have set them up to stop when they can.
            // We can finally resume the game so they can stop.
            resumeGame();

            try
            {
                stopLatch.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // Make sure the threads have actually returned from their callbacks
            // before stopping the Loopers. This ensures that the Handlers can gracefully
            // finish processing their messages before we wrench the Loopers from their cold,
            // dead hands. Therefore, wait until we can grab the lock.
            synchronized (monitorUpdateFrame)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                {
                    updateThreadLooper.quitSafely();
                    frameThreadLooper.quitSafely();
                }
                else
                {
                    updateThreadLooper.quit();
                    frameThreadLooper.quit();
                }
            }

            stopped = true;
            stopThreads = false;
        }

        // Now cleanup all references.
        // After calling stopGame, this engine is no longer usable.
        game.onStop();
        game = null;
        screen.cleanup();
        screen = null;
        updateThread = null;
        updateThreadLooper = null;
        frameThread = null;
        frameThreadLooper = null;
        gameStates.clear(); //TODO: throw interps to the pool?
    }

    /**
     * Pause the game.
     * Pauses the threads responsible for updating and rendering the game.
     */
    public void pauseGame()
    {
        synchronized (monitorControl)
        { //TODO: any reason not to move these checks outside the sync block?
            if (Thread.currentThread().getName().equals(screen.asView().getContext().getString(R.string.update_thread_name)))
            {
                throw new IllegalThreadStateException("Cannot be called from "
                        + screen.asView().getContext().getString(R.string.update_thread_name)
                        + " thread, because it cannot stop itself!");
            }
            else if (Thread.currentThread().getName().equals(screen.asView().getContext().getString(R.string.frame_thread_name)))
            {
                throw new IllegalThreadStateException("Cannot be called from "
                        + screen.asView().getContext().getString(R.string.frame_thread_name)
                        + " thread, because it cannot stop itself!");
            }

            if (pauseThreads)
            {
                throw new IllegalStateException("Engine already in process of pausing!");
            }

            if (!isActive())
            {
                throw new IllegalStateException("Cannot pause game that isn't running!");
            }

            pauseLatch = new CountDownLatch(NUMBER_OF_THREADS);

            // Tell threads to pause.
            pauseThreads = true;

            // Wait for threads to pause.
            // We do not need to worry that the threads have counted down the latch to 0 before
            // we actually call await on it, because await specifies that in such a case, it will
            // simply return immediately.
            try
            {
                pauseLatch.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            paused = true;
        }
    }

    /**
     * Resume the game.
     * Unpauses the threads responsible for updating and rendering the game.
     */
    public void resumeGame()
    {
        if (Thread.currentThread().getName().equals(screen.asView().getContext().getString(R.string.update_thread_name)))
        {
            throw new IllegalThreadStateException("Cannot be called from "
                    + screen.asView().getContext().getString(R.string.update_thread_name)
                    + " thread, because it cannot resume itself!");
        }
        else if (Thread.currentThread().getName().equals(screen.asView().getContext().getString(R.string.frame_thread_name)))
        {
            throw new IllegalThreadStateException("Cannot be called from "
                    + screen.asView().getContext().getString(R.string.frame_thread_name)
                    + " thread, because it cannot resume itself!");
        }
        if (!isActive())
        {
            throw new IllegalStateException("Cannot resume game that isn't active.");
        }
        if (isPlaying())
        {
            throw new IllegalStateException("Cannot resume game that isn't paused.");
        }

        //TODO: When we resume, time has passed, so push game states ahead because they are not invalid yet. (Visual fix).

        pauseThreads = false;
        paused = false;

        // Release the threads once we are sure the Screen is ready.
        // If it is already ready, this will run right away.
        screen.setReadyListener(new Screen.ReadyListener()
        {
            @Override
            public void onReady(int screenWidth, int screenHeight)
            {
                screen.removeReadyListener();
                screen.setSizeChangedListener(new Screen.SizeChangedListener()
                {
                    @Override
                    public void onSizeChanged(int newScreenWidth, int newScreenHeight, int oldScreenWidth, int oldScreenHeight)
                    {
                        game.onScreenSizeChanged(newScreenWidth, newScreenHeight, oldScreenWidth, oldScreenHeight);
                    }
                });
                synchronized (monitorUpdateFrame)
                {
                    monitorUpdateFrame.notifyAll();
                }
            }
        });
    }

    public boolean isActive()
    {
        return started && !stopped;
    }

    public boolean isPlaying()
    {
        return isActive() && !paused;
    }

    /*public int getGameWidth()
    {
        return screen.getWidth();
    }

    public int getGameHeight()
    {
        return screen.getHeight();
    }*/

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected Object getUpdateMonitor() //TODO: remove? standardize the synchronization first, and how input is handled, then make a decision.
    {
        return monitorUpdateFrame;
    }
}