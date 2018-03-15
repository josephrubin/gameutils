package box.shoe.gameutils.screen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Joseph on 10/23/2017.
 */
//TODO: when game resumes, should not jump so much from previous frame! (engine problem).
public class SurfaceViewScreen extends SurfaceView implements SurfaceHolder.Callback, Screen
{
    private SurfaceHolder surfaceHolder;
    private volatile boolean surfaceReady = false;
    private boolean preparedToPaint = false;

    // Canvas returned from lockCanvas. Must be passed back to unlockCanvasAndPost.
    private Canvas surfaceCanvas;

    private boolean hasDimensions = false;

    private ReadyListener readyListener;
    private SizeChangedListener sizeChangedListener;

    private int oldWidth;
    private int oldHeight;

    public SurfaceViewScreen(Context context)
    {
        super(context);

        surfaceHolder = getHolder();
        this.readyListener = null;

        // For a custom surface view, the onDraw method is not necessary for anything.
        // Also, our screen is drawn on the locked canvas through the surface API.
        setWillNotDraw(true);

        // This call is critical to placing the Surface above the window,
        // so the background can be seen behind it (see next comment).
        setZOrderOnTop(true);

        // Drawing can be slow. So if we want to be able to draw a static background behind this Screen so we don't have
        // to render it every frame, make sure that any transparent pixels drawn to the Surface do not block behind it.
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        setClickable(true);

        // We must register a callback to get surface lifecycle events.
        // We simply implement SurfaceHolder.Callback ourselves so we can pass in 'this'.
        surfaceHolder.addCallback(this);
    }

    @Override
    public Canvas startRender()
    {
        if (isRendering())
        {
            throw new IllegalStateException("Already called startRender()! You must endRender() before starting a new render." +
                    "Call isRendering() to check.");
        }
        if (!isActive())
        {
            throw new IllegalStateException("Surface is not ready to render. Please call isActive() to check.");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            surfaceCanvas = surfaceHolder.lockHardwareCanvas();
        }
        else
        {
            surfaceCanvas = surfaceHolder.lockCanvas();
        }

        preparedToPaint = true;
        return surfaceCanvas;
    }

    @Override
    public void endRender()
    {
        if (!isRendering())
        {
            throw new IllegalStateException("Not prepared to render. Please call startRender() before calling endRender() each time." +
                    "Call isRendering() to check.");
        }
        if (!isActive())
        {
            throw new IllegalStateException("Surface is not ready to render. Please call isActive() to check.");
        }

        preparedToPaint = false;
        surfaceHolder.unlockCanvasAndPost(surfaceCanvas);
    }

    @Override
    public boolean isActive()
    {
        return surfaceReady && hasDimensions;
    }

    @Override
    public boolean isRendering()
    {
        return preparedToPaint;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        this.surfaceHolder = surfaceHolder;
        surfaceReady = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
    {
        if (width > 0 && height > 0)
        {
            hasDimensions = true;
            synchronized (this)
            {
                if (readyListener != null)
                {
                    this.readyListener.onReady(width, height);
                    removeReadyListener();
                }
                else if (sizeChangedListener != null)
                {
                    sizeChangedListener.onSizeChanged(width, height, oldWidth, oldHeight);
                }
            }
        }
        oldWidth = width;
        oldHeight = height;
    }

    public void setReadyListener(ReadyListener readyListener)
    {
        this.readyListener = readyListener;
        synchronized (this)
        {
            if (hasDimensions && surfaceReady && readyListener != null)
            {
                this.readyListener.onReady(oldWidth, oldHeight);
                removeReadyListener();
            }
        }
    }

    public void removeReadyListener()
    {
        readyListener = null;
    }

    public void setSizeChangedListener(SizeChangedListener sizeChangedListener)
    {
        this.sizeChangedListener = sizeChangedListener;
    }

    public void removeSizeChangedListener()
    {
        sizeChangedListener = null;
    }

    @Override
    public View asView()
    {
        return this;
    }

    public void cleanup()
    {
        if (isRendering())
        {
            throw new IllegalStateException("Surface is being cleaned up but we have not yet released the canvas lock!" +
                    "endRender() should have been called!");
        }
        surfaceReady = false;
        this.surfaceHolder = null;
        removeReadyListener();
        surfaceCanvas = null;
        oldWidth = 0;
        oldHeight = 0;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) //fixme: make the cleanup run even if this callback is not
    {
        cleanup();
    }
}