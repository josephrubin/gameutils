package box.shoe.gameutils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import box.gift.gameutils.R;
import box.shoe.gameutils.engine.Engine;
import box.shoe.gameutils.engine.Game;
import box.shoe.gameutils.screen.Screen;
import box.shoe.gameutils.screen.SurfaceViewScreen;

/**
 * Created by Joseph on 3/7/2018.
 */

public abstract class GameActivity extends Activity
{
    private Engine currentEngine;
    private Game currentGame;
    private Screen currentScreen;

    // References to Views that we will need in order to switch what we display to the user.
    private ViewGroup gameSection;
    private ViewGroup menuSection;
    private ViewGroup gameScreenView;
    private View      pauseLayoutView;

    // ___________________
    // ACTIVITY LIFECYCLE.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Our master layout contains all the sections that we want to display, without switching contexts later on.
        setContentView(R.layout.master_layout);

        // Programmatically get the Menu section layout and inflate the stub.
        inflateMenu();
    }

    // Whenever we launch, grab the references dependant on the View hierarchy.
    // By doing so, we do not assume that UI components must be the same objects between Activity showing/hiding.
    @Override
    protected void onStart()
    {
        super.onStart();

        // Save references to various Views we will need.
        gameSection = findViewById(R.id.gameSection);
        menuSection = findViewById(R.id.menuSection);
        gameScreenView = findViewById(R.id.gameScreenView);
        pauseLayoutView = findViewById(R.id.pauseLayout);
    }

    // When we are visually obstructed, simply pause if the game is playing.
    @Override
    protected void onPause()
    {
        super.onPause();

        if (gameIsActiveAndNotPaused())
        {
            currentEngine.pauseGame();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (gameIsActiveAndPaused())
        {
            currentEngine.resumeGame();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (gameIsActive())
        {
            currentEngine.stopGame();//todo: should we expect this to cleanup? should engines be reusable? make it clear that they aren't.
        }

        currentEngine = null;
        currentGame = null;
        currentScreen = null;

        gameSection = null;
        menuSection = null;
        gameScreenView = null;
        pauseLayoutView = null;
    }

    // _______________
    // GAME LIFECYCLE.

    private void launchGame()
    {
        // Make a new engine...
        currentGame   = provideNewGame();
        currentScreen = provideNewScreen(getApplicationContext());
        currentEngine = new Engine(currentScreen, currentGame);

        // ...show the Screen to the user...
        toGameSection();
        gameScreenView.addView(currentScreen.asView());

        // ...and start the Game.
        currentEngine.startGame();
    }

    private void killGame()
    {
        gameScreenView.removeAllViews();

        currentEngine.stopGame();
        currentEngine = null;
        currentGame = null;
        currentScreen = null;

        toMenuSection();
    }

    // __________________
    // GAME-STATE CHECKS.

    private boolean gameIsActive()
    {
        return currentEngine != null && currentEngine.isActive();
    }

    private boolean gameIsActiveAndPaused()
    {
        return currentEngine != null && currentEngine.isActive() && !currentEngine.isPlaying();
    }

    private boolean gameIsActiveAndNotPaused()
    {
        return currentEngine != null && currentEngine.isPlaying();
    }

    // _____________________________________
    // VIEW INFLATION AND SECTION SWITCHING.

    private void inflateMenu()
    {
        ViewStub stub = findViewById(R.id.menuLayoutStub);
        stub.setLayoutResource(provideMainMenuLayoutResource());
        try
        {
            stub.inflate();
        }
        catch (IllegalArgumentException e)
        {
            Log.w(getString(R.string.library_name), "Main menu could not be loaded. Did you return a valid layout resource id from provideMainMenuLayoutResId()? Using default.");
            stub.setLayoutResource(R.layout.default_main_menu_layout);
            stub.inflate();
        }
    }

    private void toMenuSection()
    {
        gameSection.setVisibility(View.GONE);
        menuSection.setVisibility(View.VISIBLE);
    }

    private void toGameSection()
    {
        menuSection.setVisibility(View.GONE);
        gameSection.setVisibility(View.VISIBLE);
    }

    // ___________________________________
    // PROVIDED INFORMATION FROM SUBCLASS.

    @LayoutRes
    protected int provideMainMenuLayoutResource()
    {
        return R.layout.default_main_menu_layout; //todo: the default layout should recommend overriding this method.
    }

    // By default, we provide a SurfaceViewScreen (recommended).
    // The subclass activity is free to supply a different type by overriding.
    @NonNull
    protected Screen provideNewScreen(Context context)
    {
        return new SurfaceViewScreen(context);
    }

    // Abstract, because there is no default Game.
    @NonNull
    protected abstract Game provideNewGame();

    // _____________
    // CLICK EVENTS.

    public void startGame(View view)
    {
        launchGame();
    }

    public void stopGame(View view)
    {
        killGame();
    }

    // _____________
    // OTHER EVENTS.

    // If we are in game, back switches between paused and resumed.
    // If we are not in game, then let the OS do whatever it wants (likely to finish this activity).
    @Override
    public void onBackPressed()
    {
        if (gameIsActiveAndPaused())
        {
            currentEngine.resumeGame();
        }
        else if (gameIsActiveAndNotPaused())
        {
            currentEngine.pauseGame();
        }
        else
        {
            super.onBackPressed();
        }
    }

    // When we lose focus, pause the game.
    // When we gain focus, resume the game.
    // If we are not in game, do nothing.
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (gameIsActiveAndPaused() && hasFocus)
        {
            currentEngine.resumeGame();
        }
        else if (gameIsActiveAndNotPaused() && !hasFocus)
        {
            currentEngine.pauseGame();
        }
        super.onWindowFocusChanged(hasFocus);
    }
}
