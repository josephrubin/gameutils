package box.shoe.gameutils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import box.gift.gameutils.R;
import box.shoe.gameutils.engine.Engine;
import box.shoe.gameutils.engine.Game;
import box.shoe.gameutils.screen.Screen;
import box.shoe.gameutils.screen.SurfaceViewScreen;

/**
 * @deprecated
 */

public abstract class GameActivityOld extends Activity
{
//     private SharedPreferences sharedPreferences;
//
//     private Engine engine;
//     private Screen screen;
//
//     private Runnable readyForPaintingListener;
//
//     private ViewGroup gameView;
//     private ViewGroup mainMenuView;
//     private ViewGroup gameContainer;
//     private View pauseMenu;
//
//     // Settings. //TODO: organize these better as to how exactly we get the config
//     private boolean pauseMenuEnabled;
//
//     @Override
//     protected void onCreate(Bundle savedInstanceState)
//     {
//         super.onCreate(savedInstanceState);
//
//         // Settings.
//         pauseMenuEnabled = pauseMenuEnabled();
//
//         // For high scores.
//         sharedPreferences = getPreferences(Context.MODE_PRIVATE);
//
//         readyForPaintingListener = new Runnable()
//         {
//             @Override
//             public void run()
//             {
//                 runOnUiThread(new Runnable()
//                 {
//                     @Override
//                     public void run()
//                     {
//                         if (screen != null && engine != null)
//                         {
//                             if (!engine.isActive()) //TODO: only start if hasn't been started (i.e. do not start if the same engine has simply been stopped).
//                             {
//                                 //engine.startGame();
//                             }
//                             else if (!engine.isPlaying())
//                             {
//                                 //TODO: show the last frame.
//                                 /*if (screenshot != null && !screenshot.isRecycled())
//                                 {
//                                     screen.preparePaint();
//                                     screen.paintStatic(screenshot);
//                                 }*/
//                             }
//                         }
//                     }
//                 });
//             }
//         };
//
//         setContentView(R.layout.master_layout);
//
//         // Programmatically get the Main Menu layout and inflate the stub.
//         ViewStub stub = findViewById(R.id.mainMenuStub);
//         stub.setLayoutResource(provideMainMenuLayoutResId());
//         try
//         {
//             stub.inflate();
//         }
//         catch (IllegalArgumentException e)
//         {
//             Log.w(getString(R.string.library_name), "Main menu could not be loaded. Did you return a valid layout resource id from provideMainMenuLayoutResId()? Using default.");
//             stub.setLayoutResource(R.layout.default_main_menu_layout);
//             stub.inflate();
//         }
//
//         // Programmatically get the Pause Menu layout and inflate the stub.
//         if (pauseMenuEnabled)
//         {
//             stub = findViewById(R.id.pauseMenuStub);
//             stub.setLayoutResource(providePauseMenuLayoutResId());
//             try
//             {
//                 stub.inflate();
//             }
//             catch (IllegalArgumentException e)
//             {
//                 Log.w(getString(R.string.library_name), "Pause menu could not be loaded. Did you return a valid layout resource id from providePauseMenuLayoutResId()? Using default.");
//                 stub.setLayoutResource(R.layout.default_pause_menu_layout);
//                 stub.inflate();
//             }
//         }
//
//         // Save references to various Views we will need.
//         gameView = findViewById(R.id.gameScreen);
//         mainMenuView = findViewById(R.id.mainScreen);
//         gameContainer = findViewById(R.id.gameContainer);
//         pauseMenu = findViewById(R.id.pauseMenu);
//
//         if (pauseMenuEnabled)
//         {
//             hidePauseMenu();
//         }
//
//         showMainMenuLayout(0, sharedPreferences.getInt(getString(R.string.pref_best), 0));
//     }
//
//     private void showMainMenuLayout(int score, int best) //TODO: score and best are a certain type of game, this should be an override in app mod, or even better, provided as a type of activity option
//     {
//         gameView.setVisibility(View.GONE);
//         mainMenuView.setVisibility(View.VISIBLE);
//
//         TextView scoreView = findViewById(provideScoreTextViewIdResId());
//         TextView bestView = findViewById(provideBestTextViewIdResId());
//
//         if (scoreView != null)
//         {
//             scoreView.setText(String.valueOf(score));
//         }
//         if (bestView != null)
//         {
//             bestView.setText(String.valueOf(best));
//         }
//     }
//
//     private void showGameLayout()
//     {
//         mainMenuView.setVisibility(View.GONE);
//         gameView.setVisibility(View.VISIBLE);
//     }
//
//     private void createGame()
//     {
//         screen = provideNewScreen(getApplicationContext(), readyForPaintingListener);
//         engine = new Engine(screen, provideNewGame());
//     }
//
//     private void showGame()
//     {
//         gameContainer.removeAllViews();
//         gameContainer.addView(screen.asView());
//     }
//
//     @Override
//     protected void onStart()
//     {
//         super.onStart();
//
//         // Determine if the game is stopped or just paused.
//         if (engine != null && engine.isActive())
//         {
//             if (pauseMenuEnabled)
//             {
//                 showPauseMenu();
//             }
//             else
//             {
//                 screen.setReadyListener(readyForPaintingListener);
//             }
//         }
//     }
//
//     @Override
//     protected void onPause()
//     {
//         pauseGameIfPlaying();
//         super.onPause();
//     }
//
//     @Override
//     public void onWindowFocusChanged(boolean hasFocus)
//     {
//         super.onWindowFocusChanged(hasFocus);
//         if (engine != null && engine.isActive())
//         {
//             if (hasFocus && !engine.isPlaying())
//             {
//                 if (pauseMenuEnabled)
//                 {
//                     showPauseMenu();
//                 }
//                 else
//                 {
//                     resumeGame();
//                 }
//             }
//             else if (!hasFocus)
//             {
//                 pauseGameIfPlaying();
//             }
//         }
//     }
//
//     /**
//      * If playing the game, pause it.
//      * If game is paused, resume it.
//      * Otherwise, default back button action.
//      */
//     @Override
//     public void onBackPressed()
//     {
//         if (pauseGameIfPlaying())
//         {
//             // On a game pause, since we have not left the activity, show the pause menu.
//             if (pauseMenuEnabled)
//             {
//                 showPauseMenu();
//             }
//             else
//             {
//                 stopGame();
//             }
//         }
//         else if (engine != null && engine.isActive() && !engine.isPlaying())
//         {
//             // If the game is paused, return to the game, as if the resume game button was pressed.
//             resumeGame();
//         }
//         else
//         {
//             // If the game was not active, simply let the OS do whatever it wants.
//             super.onBackPressed();
//         }
//     }
//
//     // Returns whether or not the game was running (and thus was paused)
//     private boolean pauseGameIfPlaying()
//     {
//         if (screen != null)
//         {
//             if (engine.isPlaying())
//             {
//                 engine.pauseGame();
//                 return true;
//             }
//         }
//         return false;
//     }
//
//     @Override
//     protected void onStop()
//     {/*
//         // save screenshot of the screen to paint when we resume.
//         if (screen != null && engine != null && engine.isActive())
//         {
//             if (screenshot != null && !screenshot.isRecycled())
//             {
//                 screenshot.recycle();
//             }
//             screenshot = screen.getScreenshot();
//         }*/
//         super.onStop();
//     }
//
//     /**
//      * Be a good citizen and stop the game for good on destroy.
//      * Free memory, kill threads. This is not necessary, because the system will do any reclaim
//      * necessary. Even so, we can dismantle our threads properly without much effort.
//      * But if onDestroy is never called (as is possible), there will not be any problems.
//      */
//     @Override
//     protected void onDestroy()
//     {/*
//         if (screenshot != null)
//         {
//             screenshot.recycle();
//             screenshot = null;
//         }*/
//         if (engine != null)
//         {
//             screen.removeReadyListener();
//             if (engine.isActive())
//             {
//                 engine.stopGame();
//             }
//         }
//         engine = null;
//         readyForPaintingListener = null;
//         gameContainer = null;
//         pauseMenu = null;
//         screen = null;
//
//         super.onDestroy();
//     }
//
//     private void startGame()
//     {
//         showGameLayout();
//         createGame();
//         showGame();
//     }
//
//     private void resumeGame()
//     {
//         if (pauseMenuEnabled)
//         {
//             hidePauseMenu();
//         }
//         engine.resumeGame();
//     }
//
//     private void stopGame()
//     {
//         screen = null;
//
//         // If we came from the pause menu, hide it.
//         if (pauseMenuEnabled)
//         {
//             hidePauseMenu();
//         }
//
//         if (engine != null)
//         {
//             if (engine.isActive())
//             {
//                 engine.stopGame();
//             }
//             else
//             {
//                 throw new IllegalStateException("Cannot stop an inactive game!");
//             }
//
//             int score = 0;//todo:engine.getResult();
//             int best = sharedPreferences.getInt(getString(R.string.pref_best), 0);
//
//             SharedPreferences.Editor editor = sharedPreferences.edit();
//             // Save last score.
//             editor.putInt(getString(R.string.pref_last_score), score);
//             // If new high score, update best.
//             if (score > best)
//             {
//                 editor.putInt(getString(R.string.pref_best), score);
//             }
//             editor.apply();
//
//             engine = null;
//
//             showMainMenuLayout(score, sharedPreferences.getInt(getString(R.string.pref_best), 0));
//         }
//     }
//
//     // Should work even if pause menu is already showing.
//     private void showPauseMenu()
//     {
//         pauseMenu.setVisibility(View.VISIBLE);
//     }
//
//     // Should work even if pause menu is already hidden.
//     private void hidePauseMenu()
//     {
//         pauseMenu.setVisibility(View.GONE);
//     }
// /*
//     @Override
//     public Object onRetainNonConfigurationInstance()
//     {
//         return engine;
//     }
// */
//
//     @NonNull
//     protected abstract Game provideNewGame();
//
//     // By default, we provide a SurfaceViewScreen (recommended). The subclass activity is free to supply a different type by overriding.
//     @NonNull
//     protected Screen provideNewScreen(Context context, Runnable readyForPaintingListener)
//     {
//         return new SurfaceViewScreen(context, readyForPaintingListener);
//     }
//
//     @LayoutRes
//     protected abstract int provideMainMenuLayoutResId();
//     @LayoutRes
//     protected abstract int providePauseMenuLayoutResId();
//
//     @IdRes
//     protected abstract int provideScoreTextViewIdResId();
//     @IdRes
//     protected abstract int provideBestTextViewIdResId();
//
//     //TODO: make a better way of setting preferences/settings.
//     protected boolean pauseMenuEnabled()
//     {
//         return false;
//     }
//
//     //TODO: remove the tugs, these should be used and renamed to be full words.
//
//     public void startGame(View view)
//     {
//         startGame();
//     }
//
//     public void q(View view)
//     {
//         runOnUiThread(new Runnable()
//         {
//             @Override
//             public void run()
//             {
//                 stopGame();
//             }
//         });
//     }
//
//     public void resumeGame(View view)
//     {
//         runOnUiThread(new Runnable()
//         {
//             @Override
//             public void run()
//             {
//                 resumeGame();
//             }
//         });
//     }
}
