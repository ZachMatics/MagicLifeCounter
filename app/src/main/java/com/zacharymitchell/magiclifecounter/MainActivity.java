package com.zacharymitchell.magiclifecounter;

import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements TrashDialog.Commincator {

    InterstitialAd interstitialAd;

    private ViewGroup display_top, display_bottom, RelativeLayout_theme_menu_top,
            RelativeLayout_theme_menu_bottom, RelativeLayout_menu_center, menu_numbers_center,
            RelativeLayout_menu_history, RelativeLayout_menu_settings, LinearLayout_poison_bottom,
            LinearLayout_poison_top, menu_names, black_screen;

    private ListView list_view_history;

    private ImageButton image_button_center, image_button_theme_top, image_button_theme_bottom,
            button_clear_top, button_clear_bottom, button_clear_center,
            image_button_red_top, image_button_green_top, image_button_white_top,
            image_button_blue_top, image_button_black_top, image_button_poison_top,
            icon_dice_dot_top, image_button_red_bottom, image_button_green_bottom,
            image_button_white_bottom, image_button_blue_bottom, image_button_black_bottom,
            image_button_poison_bottom, icon_dice_dot_bottom, image_button_numbers_back,
            image_button_10, image_button_20, image_button_30, image_button_40, image_button_50,
            image_button_reset, image_button_numbers, image_button_history, image_button_name,
            image_button_name_menu_back, image_button_name_menu_reset, image_button_name_menu_confirm,
            image_button_dice, image_button_settings, button_resume_dice, image_button_settings_back,
            image_button_settings_restore;

    private Button button_minus_top, button_plus_top, button_minus_bottom, button_plus_bottom,
            button_reset, button_dice, button_red_top, button_green_top, button_white_top,
            button_blue_top, button_black_top, button_poison_top, button_red_bottom,
            button_green_bottom, button_white_bottom, button_blue_bottom, button_black_bottom,
            button_poison_bottom, button_numbers, button_numbers_back, button_10, button_20,
            button_30, button_40, button_50, button_history, button_history_back, button_bottom_player_turn,
            button_top_player_turn, button_name, button_name_menu_back, button_name_menu_reset,
            button_name_menu_confirm, button_settings, button_settings_back, button_settings_restore,
            button_rate_app;

    private ToggleButton toggle_button_turn_timer, toggle_button_names, toggle_button_toast,
            toggle_button_vibration;

    private TextView text_number_top, text_number_bottom, poisonTopText, poisonBottomText, testbottomCount,
            text_view_top_player_name, text_view_bottom_player_name, text_view_top_player_time,
            text_view_bottom_player_time, history_first_name_heading, history_second_name_heading,
            history_turn_heading, text_toggle_vibration, text_toggle_toast, text_toggle_timer,
            text_toggle_names, email_note;

    boolean menuTopOpen, menuBottomOpen, menuCenterOpen, poisonFClicked, poisonClicked,
            menuHistoryOpen, lifeCountMenuOpen, menuSettingsOpen, backPressEnabled, nameMenuOpen,
            topTurnActive, bottomTurnActive, allowToast, allowVibration, showPlayerNames, showTurnTimer,
            adShow;

    private SeekBar poisonBarTop, poisonBarBottom;

    private Vibrator myVib;

    private EditText edit_text_top_player_name, edit_text_bottom_player_name;

    private Handler topHandler, bottomHandler;

    int poisonTopUpdate;
    int poisonBottomUpdate;

    DatabaseHelper myDb;
    SimpleCursorAdapter simpleCursorAdapter;

    int i, j, previousTopi, previousBottomi;

    String currentTextNumber, currentTurnName, lastBottomTurnTime, lastTopTurnTime, currentTurnTimeInterval,
            topTheme, bottomTheme;

    int topPlayerSeconds, bottomPlayerSeconds, turnNumber;
    long globalSeconds, previousGlobalSeconds;

    float yMove, yMoveName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        interstitialAd = new InterstitialAd(this);
        //test
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //real
        //interstitialAd.setAdUnitId("ca-app-pub-1604920415696861/2805434436");
        requestNewInterstitial();


        black_screen = (ViewGroup) findViewById(R.id.black_screen);

        display_top = (ViewGroup) findViewById(R.id.display_top);
        display_bottom = (ViewGroup) findViewById(R.id.display_bottom);
        text_view_top_player_name = (TextView) findViewById(R.id.text_view_top_player_name);
        text_view_bottom_player_name = (TextView) findViewById(R.id.text_view_bottom_player_name);
        text_view_top_player_time = (TextView) findViewById(R.id.text_view_top_player_time);
        text_view_bottom_player_time = (TextView) findViewById(R.id.text_view_bottom_player_time);

        button_bottom_player_turn = (Button) findViewById(R.id.button_bottom_player_turn);
        button_top_player_turn = (Button) findViewById(R.id.button_top_player_turn);

        LinearLayout_poison_bottom = (ViewGroup) findViewById(R.id.LinearLayout_poison_bottom);
        LinearLayout_poison_top = (ViewGroup) findViewById(R.id.LinearLayout_poison_top);


        text_number_top = (TextView) findViewById(R.id.text_number_top);
        text_number_bottom = (TextView) findViewById(R.id.text_number_bottom);
        button_plus_top = (Button) findViewById(R.id.button_plus_top);
        button_minus_top = (Button) findViewById(R.id.button_minus_top);
        button_plus_bottom = (Button) findViewById(R.id.button_plus_bottom);
        button_minus_bottom = (Button) findViewById(R.id.button_minus_bottom);

        //image_button_theme_top = (ImageButton) findViewById(R.id.image_button_theme_top);
        //image_button_theme_bottom = (ImageButton) findViewById(R.id.image_button_theme_bottom);

        button_clear_top = (ImageButton) findViewById(R.id.button_clear_top);
        button_clear_bottom = (ImageButton) findViewById(R.id.button_clear_bottom);
        button_clear_center = (ImageButton) findViewById(R.id.button_clear_center);

        RelativeLayout_menu_center = (ViewGroup) findViewById(R.id.RelativeLayout_menu_center);
        RelativeLayout_theme_menu_top = (ViewGroup) findViewById(R.id.RelativeLayout_theme_menu_top);
        RelativeLayout_theme_menu_bottom = (ViewGroup) findViewById(R.id.RelativeLayout_theme_menu_bottom);

        image_button_center = (ImageButton) findViewById(R.id.image_button_center);
        //image_button_theme_top = (ImageButton) findViewById(R.id.image_button_theme_top);
        //image_button_theme_bottom = (ImageButton) findViewById(R.id.image_button_theme_bottom);

        image_button_red_top = (ImageButton) findViewById(R.id.image_button_red_top);
        image_button_green_top = (ImageButton) findViewById(R.id.image_button_green_top);
        image_button_white_top = (ImageButton) findViewById(R.id.image_button_white_top);
        image_button_blue_top = (ImageButton) findViewById(R.id.image_button_blue_top);
        image_button_black_top = (ImageButton) findViewById(R.id.image_button_black_top);
        image_button_poison_top = (ImageButton) findViewById(R.id.image_button_poison_top);
        icon_dice_dot_top = (ImageButton) findViewById(R.id.icon_dice_dot_top);
        image_button_red_bottom = (ImageButton) findViewById(R.id.image_button_red_bottom);
        image_button_green_bottom = (ImageButton) findViewById(R.id.image_button_green_bottom);
        image_button_white_bottom = (ImageButton) findViewById(R.id.image_button_white_bottom);
        image_button_blue_bottom = (ImageButton) findViewById(R.id.image_button_blue_bottom);
        image_button_black_bottom = (ImageButton) findViewById(R.id.image_button_black_bottom);
        image_button_poison_bottom = (ImageButton) findViewById(R.id.image_button_poison_bottom);
        icon_dice_dot_bottom = (ImageButton) findViewById(R.id.icon_dice_dot_bottom);


        button_red_top = (Button) findViewById(R.id.button_red_top);
        button_green_top = (Button) findViewById(R.id.button_green_top);
        button_white_top = (Button) findViewById(R.id.button_white_top);
        button_blue_top = (Button) findViewById(R.id.button_blue_top);
        button_black_top = (Button) findViewById(R.id.button_black_top);
        button_poison_top = (Button) findViewById(R.id.button_poison_top);
        button_red_bottom = (Button) findViewById(R.id.button_red_bottom);
        button_green_bottom = (Button) findViewById(R.id.button_green_bottom);
        button_white_bottom = (Button) findViewById(R.id.button_white_bottom);
        button_blue_bottom = (Button) findViewById(R.id.button_blue_bottom);
        button_black_bottom = (Button) findViewById(R.id.button_black_bottom);
        button_poison_bottom = (Button) findViewById(R.id.button_poison_bottom);

        poisonBarTop = (SeekBar) findViewById(R.id.seekbar_poison_top);
        poisonBarBottom = (SeekBar) findViewById(R.id.seekbar_poison_bottom);
        poisonTopText = (TextView) findViewById(R.id.text_poison_top);
        poisonBottomText = (TextView) findViewById(R.id.text_poison_bottom);

        image_button_reset = (ImageButton) findViewById(R.id.image_button_reset);
        image_button_numbers = (ImageButton) findViewById(R.id.image_button_numbers);
        image_button_history = (ImageButton) findViewById(R.id.image_button_history);
        image_button_name = (ImageButton) findViewById(R.id.image_button_name);
        image_button_dice = (ImageButton) findViewById(R.id.image_button_dice);
        image_button_settings = (ImageButton) findViewById(R.id.image_button_settings);

        button_reset = (Button) findViewById(R.id.button_reset);
        button_numbers = (Button) findViewById(R.id.button_numbers);
        button_dice = (Button) findViewById(R.id.button_dice);
        button_resume_dice = (ImageButton) findViewById(R.id.button_resume_dice);
        button_history = (Button) findViewById(R.id.button_history);
        button_history_back = (Button) findViewById(R.id.button_history_back);
        button_settings = (Button) findViewById(R.id.button_settings);

        menu_numbers_center = (ViewGroup) findViewById(R.id.menu_numbers_center);
        image_button_numbers_back = (ImageButton) findViewById(R.id.image_button_numbers_back);
        image_button_10 = (ImageButton) findViewById(R.id.image_button_10);
        image_button_20 = (ImageButton) findViewById(R.id.image_button_20);
        image_button_30 = (ImageButton) findViewById(R.id.image_button_30);
        image_button_40 = (ImageButton) findViewById(R.id.image_button_40);
        image_button_50 = (ImageButton) findViewById(R.id.image_button_50);

        button_10 = (Button) findViewById(R.id.button_10);
        button_20 = (Button) findViewById(R.id.button_20);
        button_30 = (Button) findViewById(R.id.button_30);
        button_40 = (Button) findViewById(R.id.button_40);
        button_50 = (Button) findViewById(R.id.button_50);

        button_numbers_back = (Button) findViewById(R.id.button_numbers_back);

        RelativeLayout_menu_history = (ViewGroup) findViewById(R.id.RelativeLayout_menu_history);
        list_view_history = (ListView) findViewById(R.id.list_view_history);
        history_first_name_heading = (TextView) findViewById(R.id.history_first_name_heading);
        history_second_name_heading = (TextView) findViewById(R.id.history_second_name_heading);
        history_turn_heading = (TextView) findViewById(R.id.history_turn_heading);

        menu_names = (ViewGroup) findViewById(R.id.menu_names);
        button_name = (Button) findViewById(R.id.button_name);

        edit_text_top_player_name = (EditText) findViewById(R.id.edit_text_top_player_name);
        edit_text_bottom_player_name = (EditText) findViewById(R.id.edit_text_bottom_player_name);
        image_button_name_menu_back = (ImageButton) findViewById(R.id.image_button_name_menu_back);
        image_button_name_menu_reset = (ImageButton) findViewById(R.id.image_button_name_menu_reset);
        image_button_name_menu_confirm = (ImageButton) findViewById(R.id.image_button_name_menu_confirm);
        button_name_menu_back = (Button) findViewById(R.id.button_name_menu_back);
        button_name_menu_reset = (Button) findViewById(R.id.button_name_menu_reset);
        button_name_menu_confirm = (Button) findViewById(R.id.button_name_menu_confirm);

        RelativeLayout_menu_settings = (ViewGroup) findViewById(R.id.RelativeLayout_menu_settings);

        toggle_button_turn_timer = (ToggleButton) findViewById(R.id.toggle_button_turn_timer);
        toggle_button_names = (ToggleButton) findViewById(R.id.toggle_button_names);
        toggle_button_toast = (ToggleButton) findViewById(R.id.toggle_button_toast);
        toggle_button_vibration = (ToggleButton) findViewById(R.id.toggle_button_vibration);
        text_toggle_vibration = (TextView) findViewById(R.id.text_toggle_vibration);
        text_toggle_toast = (TextView) findViewById(R.id.text_toggle_toast);
        text_toggle_timer = (TextView) findViewById(R.id.text_toggle_timer);
        text_toggle_names = (TextView) findViewById(R.id.text_toggle_names);
        button_rate_app = (Button) findViewById(R.id.button_rate_app);
        image_button_settings_back = (ImageButton) findViewById(R.id.image_button_settings_back);
        image_button_settings_restore = (ImageButton) findViewById(R.id.image_button_settings_restore);
        button_settings_back = (Button) findViewById(R.id.button_settings_back);
        button_settings_restore = (Button) findViewById(R.id.button_settings_restore);
        email_note = (TextView) findViewById(R.id.email_note);

        //Creates new Vibrator that will be used on all buttons for haptic feedback.
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        //Used in the onBackPressed() method in order to determine which menu to return to.
        menuTopOpen = false;
        menuBottomOpen = false;
        menuCenterOpen = false;
        menuHistoryOpen = false;
        nameMenuOpen = false;
        lifeCountMenuOpen = false;
        menuSettingsOpen = false;
        allowVibration = true;

        /* Establishes click state for top/bottom poison buttons. Used to determine seek-bar/button
          properties in poisonButtonClicked() method. */

        backPressEnabled = true;
        topTurnActive = false;
        bottomTurnActive = false;

        currentTurnName = text_view_bottom_player_name.getText().toString();

        edit_text_top_player_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        edit_text_bottom_player_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});


        /*
        Retrieves device's respective display parameters for use in menu animations. This ensures
        that menu animations are identical on all devices.
         */
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Point size = new Point();
        display.getSize(size);
        yMove = size.y / 4f; // Used for standard top/bottom menu animations.
        yMoveName = 40f * displayMetrics.density; //Used when Name Menu is opened.

        //Creates new SQLLite database for score data. This is used to populate the history menu.
        myDb = new DatabaseHelper(this);

        //Sets stylistic font on text fields.
        Typeface numberFont = Typeface.createFromAsset(getAssets(), "Fonts/custom.TTF");
        text_number_top.setTypeface(numberFont);
        text_number_bottom.setTypeface(numberFont);
        button_top_player_turn.setTypeface(numberFont);
        button_bottom_player_turn.setTypeface(numberFont);
        button_rate_app.setTypeface(numberFont);
        edit_text_top_player_name.setTypeface(numberFont);
        edit_text_bottom_player_name.setTypeface(numberFont);
        text_view_top_player_name.setTypeface(numberFont);
        text_view_bottom_player_name.setTypeface(numberFont);
        text_view_top_player_time.setTypeface(numberFont);
        text_view_bottom_player_time.setTypeface(numberFont);
        poisonTopText.setTypeface(numberFont);
        poisonBottomText.setTypeface(numberFont);
        history_first_name_heading.setTypeface(numberFont);
        history_second_name_heading.setTypeface(numberFont);
        history_turn_heading.setTypeface(numberFont);
        text_toggle_vibration.setTypeface(numberFont);
        text_toggle_toast.setTypeface(numberFont);
        text_toggle_timer.setTypeface(numberFont);
        text_toggle_names.setTypeface(numberFont);
        email_note.setTypeface(numberFont);

        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                MODE_PRIVATE);
        toggle_button_toast.setChecked(sharedPreferences.getBoolean(getString(R.string.TOAST_TOGGLE), true));
        allowToast = sharedPreferences.getBoolean(getString(R.string.TOAST_TOGGLE), true);
        toggle_button_turn_timer.setChecked(sharedPreferences.getBoolean(getString(R.string.TURN_TIMER_TOGGLE), true));
        showTurnTimer = sharedPreferences.getBoolean(getString(R.string.TURN_TIMER_TOGGLE), true);
        toggle_button_names.setChecked(sharedPreferences.getBoolean(getString(R.string.PLAYER_NAMES_TOGGLE), true));
        showPlayerNames = sharedPreferences.getBoolean(getString(R.string.PLAYER_NAMES_TOGGLE), true);
        toggle_button_vibration.setChecked(sharedPreferences.getBoolean(getString(R.string.VIBRATION_TOGGLE), true));
        allowVibration = sharedPreferences.getBoolean(getString(R.string.VIBRATION_TOGGLE), true);
        topTheme = sharedPreferences.getString(getString(R.string.TOP_THEME), "DEFAULT");
        bottomTheme = sharedPreferences.getString(getString(R.string.BOTTOM_THEME), "DEFAULT");
        poisonFClicked = sharedPreferences.getBoolean(getString(R.string.POISON_TOP_OPEN), false);
        poisonClicked = sharedPreferences.getBoolean(getString(R.string.POISON_BOTTOM_OPEN), false);
        currentTextNumber = sharedPreferences.getString(getString(R.string.STARTING_LIFE_COUNT), "20");
        text_number_top.setText(sharedPreferences.getString(getString(R.string.TOP_SCORE), currentTextNumber));
        text_number_bottom.setText(sharedPreferences.getString(getString(R.string.BOTTOM_SCORE), currentTextNumber));
        lastTopTurnTime = sharedPreferences.getString(getString(R.string.LAST_TOP_TURN_TIME), "0:00");
        lastBottomTurnTime = sharedPreferences.getString(getString(R.string.LAST_BOTTOM_TURN_TIME), "0:00");
        text_view_top_player_time.setText(sharedPreferences.getString(getString(R.string.TOP_PLAYER_TIME), "0:00"));
        text_view_bottom_player_time.setText(sharedPreferences.getString(getString(R.string.BOTTOM_PLAYER_TIME), "0:00"));
        topPlayerSeconds = sharedPreferences.getInt(getString(R.string.TOP_PLAYER_SECONDS), 0);
        bottomPlayerSeconds = sharedPreferences.getInt(getString(R.string.BOTTOM_PLAYER_SECONDS), 0);
        text_view_top_player_name.setText(sharedPreferences.getString(getString(R.string.TOP_NAME), "Opponent"));
        text_view_bottom_player_name.setText(sharedPreferences.getString(getString(R.string.BOTTOM_NAME), "User"));
        //currentTurnTimeInterval = sharedPreferences.getString(getString(R.string.CURRENT_TIME_INTERVAL), "0:00");
        currentTurnTimeInterval = "0:00";
        turnNumber = sharedPreferences.getInt(getString(R.string.TURN_NUMBER), 0);
        adShow = sharedPreferences.getBoolean(getString(R.string.AD_SHOW), false);


        poisonTopUpdate = sharedPreferences.getInt(getString(R.string.TOP_POISON_COUNT), 0);
        poisonBottomUpdate = sharedPreferences.getInt(getString(R.string.BOTTOM_POISON_COUNT), 0);
        System.out.println(poisonTopUpdate + " " + poisonBottomUpdate);


        poisonTopText.setText("Poison: " + poisonTopUpdate);
        poisonBottomText.setText("Poison: " + poisonBottomUpdate);
        previousTopi = poisonTopUpdate;
        previousBottomi = poisonBottomUpdate;
        poisonBarTop.setProgress(poisonTopUpdate * 10);
        poisonBarBottom.setProgress(poisonBottomUpdate * 10);


        if (topPlayerSeconds == 0 && bottomPlayerSeconds == 0) {
            AddData();
        }
        previousGlobalSeconds = 0;


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation animation = new AlphaAnimation(1f, 0f);
                animation.setDuration(5000);
                black_screen.startAnimation(animation);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        black_screen.setAlpha(0f);
                    }
                }, 5000);


                globalSeconds = System.currentTimeMillis();

                if (interstitialAd.isLoaded() && (globalSeconds - previousGlobalSeconds >= 60000) && adShow) {
                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            interstitialAd.show();
                            previousGlobalSeconds = globalSeconds;

                        }
                    }, 3000);

                    adShow = false;
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                            MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.AD_SHOW), adShow);
                    editor.apply();
                } else {
                    adShow = true;
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                            MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.AD_SHOW), adShow);
                    editor.apply();
                }


            }
        }, 2000);


        initialMenuConfigurations();
        centerMenuOpen();
        centerMenuClose();
        themeUpdate();
        poisonButtonClicked();
        diceSpinAnimation();
        scoreReset();
        startingLifeCountUpdate();
        lifeCountMenuOpen();
        menuCenterReturn();
        historyMenuOpen();
        settingMenuOpen();
        seekbarUpdate();
        historyButtonBack();
        nameMenuOpen();
        startPlayerTurn();
        settings();
        adListener();
        rateApp();

        testbottomCount = (TextView) findViewById(R.id.bottomCount);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    public void initialMenuConfigurations() {
        RelativeLayout_menu_center.setVisibility(View.INVISIBLE);
        RelativeLayout_theme_menu_top.setVisibility(View.INVISIBLE);
        RelativeLayout_theme_menu_bottom.setVisibility(View.INVISIBLE);

        //While invisible all menus collapse to their starting positions.
        menuAnimation(RelativeLayout_menu_center, 0f, 0f, 0.0005f, 0);
        menuAnimation(RelativeLayout_theme_menu_top, 0f, yMove, 0.0005f, 0);
        menuAnimation(RelativeLayout_theme_menu_bottom, 0f, -yMove, 0.0005f, 0);
        menuAnimation(menu_names, 0f, 0f, 0.0005f, 0);
        menuAnimation(menu_numbers_center, 0f, 0f, 0.0005f, 0);
        menuAnimation(RelativeLayout_menu_history, 0f, 0f, 0.0005f, 0);
        menuAnimation(RelativeLayout_menu_settings, 0f, 0f, 0.0005f, 0);

        //Sets visibility of display items according to Shared Preferences.
        if (showTurnTimer) {
            button_top_player_turn.setVisibility(View.VISIBLE);
            button_bottom_player_turn.setVisibility(View.VISIBLE);
            text_view_top_player_time.setVisibility(View.VISIBLE);
            text_view_bottom_player_time.setVisibility(View.VISIBLE);

        } else {
            button_top_player_turn.setVisibility(View.GONE);
            button_bottom_player_turn.setVisibility(View.GONE);
            text_view_top_player_time.setVisibility(View.GONE);
            text_view_bottom_player_time.setVisibility(View.GONE);
        }

        if (showPlayerNames) {
            text_view_top_player_name.setVisibility(View.VISIBLE);
            text_view_bottom_player_name.setVisibility(View.VISIBLE);
        } else {
            text_view_top_player_name.setVisibility(View.INVISIBLE);
            text_view_bottom_player_name.setVisibility(View.INVISIBLE);
        }

        image_button_red_top.setAlpha(1f);
        image_button_green_top.setAlpha(1f);
        image_button_white_top.setAlpha(1f);
        image_button_blue_top.setAlpha(1f);
        image_button_black_top.setAlpha(1f);

        if (topTheme.equals("RED")) {
            display_top.setBackgroundResource(R.drawable.gradient_red_bottom);
            image_button_red_top.setAlpha(0.5f);
            image_button_reset.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
            image_button_numbers.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
            image_button_name.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
            image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
            image_button_10.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
            image_button_20.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
        } else if (topTheme.equals("GREEN")) {
            display_top.setBackgroundResource(R.drawable.gradient_green_bottom);
            image_button_green_top.setAlpha(0.5f);
            image_button_reset.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
            image_button_numbers.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
            image_button_name.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
            image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
            image_button_10.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
            image_button_20.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
        } else if (topTheme.equals("WHITE")) {
            display_top.setBackgroundResource(R.drawable.gradient_white_bottom);
            image_button_white_top.setAlpha(0.5f);
            image_button_reset.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
            image_button_numbers.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
            image_button_name.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
            image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
            image_button_10.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
            image_button_20.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
        } else if (topTheme.equals("BLUE")) {
            display_top.setBackgroundResource(R.drawable.gradient_blue_bottom);
            image_button_blue_top.setAlpha(0.5f);
            image_button_reset.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
            image_button_numbers.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
            image_button_name.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
            image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
            image_button_10.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
            image_button_20.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
        } else if (topTheme.equals("BLACK")) {
            display_top.setBackgroundResource(R.drawable.gradient_black_bottom);
            image_button_black_top.setAlpha(0.5f);
            image_button_reset.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
            image_button_numbers.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
            image_button_name.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
            image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
            image_button_10.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
            image_button_20.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
        } else {
            display_top.setBackgroundResource(R.drawable.gradient_default_bottom);
            image_button_reset.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
            image_button_numbers.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
            image_button_name.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
            image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
            image_button_10.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
            image_button_20.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
        }

        image_button_red_bottom.setAlpha(1f);
        image_button_green_bottom.setAlpha(1f);
        image_button_white_bottom.setAlpha(1f);
        image_button_blue_bottom.setAlpha(1f);
        image_button_black_bottom.setAlpha(1f);

        if (bottomTheme.equals("RED")) {
            display_bottom.setBackgroundResource(R.drawable.gradient_red_bottom);
            image_button_red_bottom.setAlpha(0.5f);
            image_button_history.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
            image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
            image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
            image_button_30.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
            image_button_40.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
            image_button_50.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
            image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
            image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
            image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
        } else if (bottomTheme.equals("GREEN")) {
            display_bottom.setBackgroundResource(R.drawable.gradient_green_bottom);
            image_button_green_bottom.setAlpha(0.5f);
            image_button_history.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
            image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
            image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
            image_button_30.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
            image_button_40.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
            image_button_50.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
            image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
            image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
            image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
        } else if (bottomTheme.equals("WHITE")) {
            display_bottom.setBackgroundResource(R.drawable.gradient_white_bottom);
            image_button_white_bottom.setAlpha(0.5f);
            image_button_history.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
            image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
            image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
            image_button_30.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
            image_button_40.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
            image_button_50.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
            image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
            image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
            image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
        } else if (bottomTheme.equals("BLUE")) {
            display_bottom.setBackgroundResource(R.drawable.gradient_blue_bottom);
            image_button_blue_bottom.setAlpha(0.5f);
            image_button_history.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
            image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
            image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
            image_button_30.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
            image_button_40.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
            image_button_50.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
            image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
            image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
            image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
        } else if (bottomTheme.equals("BLACK")) {
            display_bottom.setBackgroundResource(R.drawable.gradient_black_bottom);
            image_button_black_bottom.setAlpha(0.5f);
            image_button_history.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
            image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
            image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
            image_button_30.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
            image_button_40.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
            image_button_50.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
            image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
            image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
            image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
        } else {
            display_bottom.setBackgroundResource(R.drawable.gradient_default_bottom);
            image_button_history.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
            image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
            image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
            image_button_30.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
            image_button_40.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
            image_button_50.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
            image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
            image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
            image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
        }

        if (poisonFClicked) {
            image_button_poison_top.setAlpha(0.5f);
            LinearLayout_poison_top.setVisibility(View.VISIBLE);
            poisonFClicked = true;
        } else {
            image_button_poison_top.setAlpha(1f);
            LinearLayout_poison_top.setVisibility(View.GONE);
            poisonFClicked = false;
        }

        if (poisonClicked) {
            image_button_poison_bottom.setAlpha(0.5f);
            LinearLayout_poison_bottom.setVisibility(View.VISIBLE);
            poisonClicked = true;
        } else {
            image_button_poison_bottom.setAlpha(1f);
            LinearLayout_poison_bottom.setVisibility(View.GONE);
            poisonClicked = false;
        }

        switch (currentTextNumber) {
            case "10":
                image_button_10.setAlpha(0.5f);
                break;
            case "20":
                image_button_20.setAlpha(0.5f);
                break;
            case "30":
                image_button_30.setAlpha(0.5f);
                break;
            case "40":
                image_button_40.setAlpha(0.5f);
                break;
            case "50":
                image_button_50.setAlpha(0.5f);
                break;
            default:
                break;
        }

        history_first_name_heading.setText(text_view_top_player_name.getText());
        history_second_name_heading.setText(text_view_bottom_player_name.getText());

    }

    public void menuAnimation(ViewGroup menu, float i, float j, float k, int duration) {
        menu.animate().translationXBy(i).setDuration(duration);
        menu.animate().translationYBy(j).setDuration(duration);
        menu.animate().scaleX(k).setDuration(duration);
        menu.animate().scaleY(k).setDuration(duration);
    }

    public void scoreUpdate(View view) {
        //Haptic Feedback
        if (allowVibration) {
            myVib.vibrate(5);
        }

        /* Sets OnClickListeners for top/bottoms players' respective plus/minus buttons. Scores
         are then either incremented or decremented if score is between 0 and +100. */

        button_minus_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                int Num = Integer.parseInt(text_number_bottom.getText().toString());

                if (0 < Num && Num <= 99) {
                    text_number_bottom.setText(Integer.toString(--Num));
                } else if (Num == 0) {
                    text_number_bottom.setText(Integer.toString(Num));

                    //Shakes score to indicate max/min limit has been reached.
                    Animation vibrate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_vibrate);
                    text_number_bottom.startAnimation(vibrate);
                }

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.BOTTOM_SCORE), text_number_bottom.getText().toString());
                editor.apply();

            }
        });

        button_plus_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                int Num = Integer.parseInt(text_number_bottom.getText().toString());

                if (0 <= Num && Num < 99) {
                    text_number_bottom.setText(Integer.toString(++Num));
                } else if (Num == 99) {
                    text_number_bottom.setText(Integer.toString(Num));

                    //Shakes score to indicate max/min limit has been reached.
                    Animation vibrate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_vibrate);
                    text_number_bottom.startAnimation(vibrate);
                }

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.BOTTOM_SCORE), text_number_bottom.getText().toString());
                editor.apply();
            }
        });

        button_minus_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                int Num = Integer.parseInt(text_number_top.getText().toString());

                if (0 < Num && Num <= 99) {
                    text_number_top.setText(Integer.toString(--Num));
                } else if (Num == 0) {
                    text_number_top.setText(Integer.toString(Num));

                    //Shakes score to indicate max/min limit has been reached.
                    Animation vibrate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_vibrate);
                    text_number_top.startAnimation(vibrate);
                }

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TOP_SCORE), text_number_top.getText().toString());
                editor.apply();

            }
        });

        button_plus_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                int Num = Integer.parseInt(text_number_top.getText().toString());

                if (0 <= Num && Num < 99) {
                    text_number_top.setText(Integer.toString(++Num));
                } else if (Num == 99) {
                    text_number_top.setText(Integer.toString(Num));

                    //Shakes score to indicate max/min limit has been reached.
                    Animation vibrate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_vibrate);
                    text_number_top.startAnimation(vibrate);
                }

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TOP_SCORE), text_number_top.getText().toString());
                editor.apply();
            }
        });
    }

    public void startPlayerTurn() {
        final Animation vibrate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_vibrate);

        //Sets OnClickListeners for top/bottom turn buttons.
        button_top_player_turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                //Once a turn is initiated, both the top and bottom turn-button texts disappear.
                button_top_player_turn.setText("");
                button_bottom_player_turn.setText("");

                //Timer Begins.
                final Runnable topRunnable = new Runnable() {
                    @Override
                    public void run() {
                        topHandler.postDelayed(this, 1000);

                        topPlayerSeconds += 1;

                        int mins = topPlayerSeconds / 60;
                        int secs = topPlayerSeconds - mins * 60;

                        if (0 <= secs && secs <= 9) {
                            String secsString = "0" + Integer.toString(secs);

                            text_view_top_player_time.setText((Integer.toString(mins) + ":" + secsString));
                        } else {
                            text_view_top_player_time.setText(Integer.toString(mins) + ":" + Integer.toString(secs));
                        }

                        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.TOP_PLAYER_TIME), text_view_top_player_time.getText().toString());
                        editor.putInt(getString(R.string.TOP_PLAYER_SECONDS), topPlayerSeconds);
                        editor.apply();
                    }
                };

                //Checks to see which turn configuration is present and updates history accordingly.
                if (!topTurnActive && bottomTurnActive) {
                    bottomTurnActive = false;
                    topTurnActive = true;

                    //Begins top timer and clears bottom timer.
                    topHandler = new Handler();
                    topHandler.postDelayed(topRunnable, 1000);
                    bottomHandler.removeCallbacksAndMessages(null);

                    //Updates turn number and current time interval and then adds data to database.
                    turnNumber += 1;
                    currentTurnTimeInterval = lastBottomTurnTime + " - " + text_view_bottom_player_time.getText().toString();
                    AddData();

                    if (allowToast) {
                        Toast.makeText(MainActivity.this, "History Updated", Toast.LENGTH_SHORT).show();
                    }

                    //Updates previous times and current turn name.
                    lastTopTurnTime = text_view_top_player_time.getText().toString();
                    lastBottomTurnTime = text_view_bottom_player_time.getText().toString();

                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                            MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.LAST_TOP_TURN_TIME), lastTopTurnTime);
                    editor.putString(getString(R.string.LAST_BOTTOM_TURN_TIME), lastBottomTurnTime);
                    editor.putString(getString(R.string.CURRENT_TIME_INTERVAL), currentTurnTimeInterval);
                    editor.putInt(getString(R.string.TURN_NUMBER), turnNumber);
                    editor.apply();
                    currentTurnName = text_view_top_player_name.getText().toString();

                    //Changes color of turn buttons to indicate which turn is currently active.
                    button_top_player_turn.setBackgroundResource(R.drawable.turn_button_on);
                    button_bottom_player_turn.setBackgroundResource(R.drawable.layout_menu_center);

                    //Shakes player's display items to indicate turn has begun.
                    text_view_top_player_time.startAnimation(vibrate);
                    text_number_top.startAnimation(vibrate);
                    button_plus_top.startAnimation(vibrate);
                    button_minus_top.startAnimation(vibrate);
                    text_view_top_player_name.startAnimation(vibrate);
                    poisonBarTop.startAnimation(vibrate);
                    poisonTopText.startAnimation(vibrate);


                } else if (!topTurnActive) {
                    bottomTurnActive = false;
                    topTurnActive = true;

                    //Begins top timer.
                    topHandler = new Handler();
                    topHandler.postDelayed(topRunnable, 1000);

                    //Changes color of turn buttons to indicate which turn is currently active.
                    button_top_player_turn.setBackgroundResource(R.drawable.turn_button_on);

                    currentTurnName = text_view_top_player_name.getText().toString();

                    //Shakes player's display items to indicate turn has begun.
                    text_view_top_player_time.startAnimation(vibrate);
                    text_number_top.startAnimation(vibrate);
                    button_plus_top.startAnimation(vibrate);
                    button_minus_top.startAnimation(vibrate);
                    text_view_top_player_name.startAnimation(vibrate);
                    poisonBarTop.startAnimation(vibrate);
                    poisonTopText.startAnimation(vibrate);

                } else {
                    bottomTurnActive = false;
                    topTurnActive = false;

                    //Updates previous times and current turn name.
                    currentTurnTimeInterval = lastTopTurnTime + " - " + text_view_top_player_time.getText().toString();
                    lastTopTurnTime = text_view_top_player_time.getText().toString();

                    //Updates turn number and current time interval and then adds data to database.
                    turnNumber += 1;
                    currentTurnName = text_view_top_player_name.getText().toString();
                    AddData();
                    if (allowToast) {
                        Toast.makeText(MainActivity.this, "History Updated", Toast.LENGTH_SHORT).show();
                    }

                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                            MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.LAST_TOP_TURN_TIME), lastTopTurnTime);
                    editor.putString(getString(R.string.LAST_BOTTOM_TURN_TIME), lastBottomTurnTime);
                    editor.putString(getString(R.string.CURRENT_TIME_INTERVAL), currentTurnTimeInterval);
                    editor.putInt(getString(R.string.TURN_NUMBER), turnNumber);
                    editor.apply();

                    //Clear top timer.
                    topHandler.removeCallbacksAndMessages(null);

                    //Sets top turn button to un-clicked state.
                    button_top_player_turn.setBackgroundResource(R.drawable.layout_menu_center);
                }
            }
        });

        button_bottom_player_turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                //Once a turn is initiated, both the top and bottom turn-button texts disappear.
                button_top_player_turn.setText("");
                button_bottom_player_turn.setText("");

                //Timer Begins.
                final Runnable topRunnable = new Runnable() {
                    @Override
                    public void run() {
                        bottomHandler.postDelayed(this, 1000);

                        bottomPlayerSeconds += 1;

                        int mins = bottomPlayerSeconds / 60;
                        int secs = bottomPlayerSeconds - mins * 60;

                        if (0 <= secs && secs <= 9) {
                            String secsString = "0" + Integer.toString(secs);

                            text_view_bottom_player_time.setText((Integer.toString(mins) + ":" + secsString));
                        } else {
                            text_view_bottom_player_time.setText(Integer.toString(mins) + ":" + Integer.toString(secs));
                        }

                        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.BOTTOM_PLAYER_TIME), text_view_bottom_player_time.getText().toString());
                        editor.putInt(getString(R.string.BOTTOM_PLAYER_SECONDS), bottomPlayerSeconds);
                        editor.apply();
                    }
                };

                //Checks to see which turn configuration is present and updates history accordingly.
                if (!bottomTurnActive && topTurnActive) {
                    topTurnActive = false;
                    bottomTurnActive = true;

                    //Begins bottom timer and clears top timer.
                    bottomHandler = new Handler();
                    bottomHandler.postDelayed(topRunnable, 1000);
                    topHandler.removeCallbacksAndMessages(null);

                    //Updates turn number and current time interval and then adds data to database.
                    turnNumber += 1;
                    currentTurnTimeInterval = lastTopTurnTime + " - " + text_view_top_player_time.getText().toString();
                    AddData();

                    if (allowToast) {
                        Toast.makeText(MainActivity.this, "History Updated", Toast.LENGTH_SHORT).show();
                    }

                    //Updates previous times and current turn name.
                    lastBottomTurnTime = text_view_bottom_player_time.getText().toString();
                    lastTopTurnTime = text_view_top_player_time.getText().toString();
                    currentTurnName = text_view_bottom_player_name.getText().toString();

                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                            MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.LAST_TOP_TURN_TIME), lastTopTurnTime);
                    editor.putString(getString(R.string.LAST_BOTTOM_TURN_TIME), lastBottomTurnTime);
                    editor.putString(getString(R.string.CURRENT_TIME_INTERVAL), currentTurnTimeInterval);
                    editor.putInt(getString(R.string.TURN_NUMBER), turnNumber);
                    editor.apply();

                    //Changes color of turn buttons to indicate which turn is currently active.
                    button_bottom_player_turn.setBackgroundResource(R.drawable.turn_button_on);
                    button_top_player_turn.setBackgroundResource(R.drawable.layout_menu_center);

                    //Shakes player's display items to indicate turn has begun.
                    text_view_bottom_player_time.startAnimation(vibrate);
                    text_number_bottom.startAnimation(vibrate);
                    button_plus_bottom.startAnimation(vibrate);
                    button_minus_bottom.startAnimation(vibrate);
                    text_view_bottom_player_name.startAnimation(vibrate);
                    poisonBarBottom.startAnimation(vibrate);
                    poisonBottomText.startAnimation(vibrate);


                } else if (!bottomTurnActive) {
                    topTurnActive = false;
                    bottomTurnActive = true;

                    //Begins bottom timer.
                    bottomHandler = new Handler();
                    bottomHandler.postDelayed(topRunnable, 1000);

                    //Changes color of turn buttons to indicate which turn is currently active.
                    button_bottom_player_turn.setBackgroundResource(R.drawable.turn_button_on);

                    currentTurnName = text_view_bottom_player_name.getText().toString();

                    //Shakes player's display items to indicate turn has begun.
                    text_view_bottom_player_time.startAnimation(vibrate);
                    text_number_bottom.startAnimation(vibrate);
                    button_plus_bottom.startAnimation(vibrate);
                    button_minus_bottom.startAnimation(vibrate);
                    text_view_bottom_player_name.startAnimation(vibrate);
                    poisonBarBottom.startAnimation(vibrate);
                    poisonBottomText.startAnimation(vibrate);

                } else {
                    topTurnActive = false;
                    bottomTurnActive = false;

                    //Updates previous times and current turn name.
                    currentTurnTimeInterval = lastBottomTurnTime + " - " + text_view_bottom_player_time.getText().toString();
                    lastBottomTurnTime = text_view_bottom_player_time.getText().toString();

                    //Updates turn number and current time interval and then adds data to database.
                    turnNumber += 1;
                    currentTurnName = text_view_bottom_player_name.getText().toString();

                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                            MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.LAST_TOP_TURN_TIME), lastTopTurnTime);
                    editor.putString(getString(R.string.LAST_BOTTOM_TURN_TIME), lastBottomTurnTime);
                    editor.putString(getString(R.string.CURRENT_TIME_INTERVAL), currentTurnTimeInterval);
                    editor.putInt(getString(R.string.TURN_NUMBER), turnNumber);
                    editor.apply();

                    AddData();

                    if (allowToast) {
                        Toast.makeText(MainActivity.this, "History Updated", Toast.LENGTH_SHORT).show();
                    }

                    //Clear bottom timer.
                    bottomHandler.removeCallbacksAndMessages(null);

                    //Sets bottom turn button to un-clicked state.
                    button_bottom_player_turn.setBackgroundResource(R.drawable.layout_menu_center);
                }
            }
        });
    }

    public void centerMenuOpen() {
        image_button_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                //Disables buttons that would interrupt the menu animation.
                button_numbers_back.setEnabled(false);
                button_clear_center.setEnabled(false);

                //Used in onBackPressed(). Locks device's back button during animation.
                menuCenterOpen = true;
                backPressEnabled = false;

                //Makes main, top, and bottom menus visible.
                RelativeLayout_menu_center.setVisibility(View.VISIBLE);
                RelativeLayout_theme_menu_top.setVisibility(View.VISIBLE);
                RelativeLayout_theme_menu_bottom.setVisibility(View.VISIBLE);

                //Begins main, top, and bottom menu animations.
                menuAnimation(RelativeLayout_menu_center, 0f, 0f, 1f, 150);
                menuAnimation(RelativeLayout_theme_menu_top, 0f, -yMove, 1f, 150);
                menuAnimation(RelativeLayout_theme_menu_bottom, 0f, yMove, 1f, 150);

                //Wait for animation to complete.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Restores back button and clear button functionality.
                        button_clear_center.setVisibility(View.VISIBLE); //Closes menus when clicked.
                        button_numbers_back.setEnabled(true);
                        button_clear_center.setEnabled(true);
                        backPressEnabled = true;
                    }
                }, 150);

                //Center icon disappears, it's alpha is set to 0, and it is disabled. The icon's
                //alpha will fade back to 1 when the clear button is clicked.
                image_button_center.setVisibility(View.GONE);
                image_button_center.animate().alpha(0f);
                image_button_center.setEnabled(false);
            }
        });
    }

    public void centerMenuClose() {

        button_clear_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //Used in onBackPressed(). Locks device's back button during animation.
                backPressEnabled = false;
                menuCenterOpen = false;

                image_button_red_top.setEnabled(true);
                image_button_green_top.setEnabled(true);
                image_button_white_top.setEnabled(true);
                image_button_blue_top.setEnabled(true);
                image_button_black_top.setEnabled(true);
                image_button_poison_top.setEnabled(true);
                image_button_red_bottom.setEnabled(true);
                image_button_green_bottom.setEnabled(true);
                image_button_white_bottom.setEnabled(true);
                image_button_blue_bottom.setEnabled(true);
                image_button_black_bottom.setEnabled(true);
                image_button_poison_bottom.setEnabled(true);

                button_red_top.setEnabled(true);
                button_green_top.setEnabled(true);
                button_white_top.setEnabled(true);
                button_blue_top.setEnabled(true);
                button_black_top.setEnabled(true);
                button_poison_top.setEnabled(true);
                button_red_bottom.setEnabled(true);
                button_green_bottom.setEnabled(true);
                button_white_bottom.setEnabled(true);
                button_blue_bottom.setEnabled(true);
                button_black_bottom.setEnabled(true);
                button_poison_bottom.setEnabled(true);

                button_reset.setEnabled(true);
                button_numbers.setEnabled(true);
                button_name.setEnabled(true);
                button_history.setEnabled(true);
                button_dice.setEnabled(true);
                button_settings.setEnabled(true);

                button_rate_app.setVisibility(View.GONE);

                //Wait for animation to complete.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Restores back button functionality.
                        backPressEnabled = true;
                    }
                }, 150);

                //Checks to see if the Name Menu is open. If so, the top/bottom theme menus must
                //execute two consecutive animations in order to return to the starting position.
                if (nameMenuOpen) {
                    menuAnimation(RelativeLayout_theme_menu_top, 0f, yMoveName, 1f, 0);
                    menuAnimation(RelativeLayout_theme_menu_bottom, 0f, -yMoveName, 1f, 0);
                    nameMenuOpen = false;
                    final Handler handler2 = new Handler(); //Wait for Animation to Complete
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            menuAnimation(RelativeLayout_theme_menu_top, 0f, yMove, 0.0005f, 150);
                            menuAnimation(RelativeLayout_theme_menu_bottom, 0f, -yMove, 0.0005f, 150);
                        }
                    }, 5);
                } else {
                    menuAnimation(RelativeLayout_theme_menu_top, 0f, yMove, 0.0005f, 150);
                    menuAnimation(RelativeLayout_theme_menu_bottom, 0f, -yMove, 0.0005f, 150);
                }

                //All menus collapse to their starting position.
                menuAnimation(RelativeLayout_menu_center, 0f, 0f, 0.0005f, 150);
                menuAnimation(menu_numbers_center, 0f, 0f, 0.0005f, 150);
                menuAnimation(menu_names, 0f, 0f, 0.0005f, 150);
                menuAnimation(RelativeLayout_menu_history, 0f, 0f, 0.0005f, 150);
                menuAnimation(RelativeLayout_menu_settings, 0f, 0f, 0.0005f, 150);


                //Clear button is no longer present.
                button_clear_center.setVisibility(View.GONE);

                //Wait for animation to complete to re-enable center button.
                final Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        image_button_center.setEnabled(true);
                        image_button_center.setVisibility(View.VISIBLE);
                        image_button_center.animate().alpha(1f);
                    }
                }, 150);
            }
        });
    }

    public void menuCenterReturn() {
        button_numbers_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                menu_numbers_center.setVisibility(View.GONE);
                menuAnimation(RelativeLayout_menu_center, 0f, 0f, 1f, 150);
                menuAnimation(menu_numbers_center, 0f, 0f, 0.0005f, 150);
                menuAnimation(RelativeLayout_menu_history, 0f, 0f, 0.0005f, 150);
                //menuAnimation(menuName, 0f, 0f, 0.0005f, 200);
                menuHistoryOpen = false;
//                menuNameOpen=false;
                lifeCountMenuOpen = false;


            }
        });
    }

    public void themeUpdate() {

        button_red_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_top.setAlpha(1f);
                image_button_green_top.setAlpha(1f);
                image_button_white_top.setAlpha(1f);
                image_button_blue_top.setAlpha(1f);
                image_button_black_top.setAlpha(1f);

                display_top.setBackgroundResource(R.drawable.gradient_red_bottom);
                image_button_red_top.setAlpha(0.5f);
                image_button_reset.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
                image_button_numbers.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
                image_button_name.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
                image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
                image_button_10.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);
                image_button_20.setBackgroundResource(R.drawable.gradient_top_red_menu_button_theme);

                topTheme = "RED";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TOP_THEME), topTheme);
                editor.apply();
            }
        });

        button_green_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_top.setAlpha(1f);
                image_button_green_top.setAlpha(1f);
                image_button_white_top.setAlpha(1f);
                image_button_blue_top.setAlpha(1f);
                image_button_black_top.setAlpha(1f);

                display_top.setBackgroundResource(R.drawable.gradient_green_bottom);
                image_button_green_top.setAlpha(0.5f);
                image_button_reset.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
                image_button_numbers.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
                image_button_name.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
                image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
                image_button_10.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);
                image_button_20.setBackgroundResource(R.drawable.gradient_top_green_menu_button_theme);

                topTheme = "GREEN";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TOP_THEME), topTheme);
                editor.apply();
            }
        });

        button_white_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_top.setAlpha(1f);
                image_button_green_top.setAlpha(1f);
                image_button_white_top.setAlpha(1f);
                image_button_blue_top.setAlpha(1f);
                image_button_black_top.setAlpha(1f);

                display_top.setBackgroundResource(R.drawable.gradient_white_bottom);
                image_button_white_top.setAlpha(0.5f);
                image_button_reset.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
                image_button_numbers.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
                image_button_name.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
                image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
                image_button_10.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);
                image_button_20.setBackgroundResource(R.drawable.gradient_top_white_menu_button_theme);

                topTheme = "WHITE";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TOP_THEME), topTheme);
                editor.apply();
            }
        });

        button_blue_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_top.setAlpha(1f);
                image_button_green_top.setAlpha(1f);
                image_button_white_top.setAlpha(1f);
                image_button_blue_top.setAlpha(1f);
                image_button_black_top.setAlpha(1f);

                display_top.setBackgroundResource(R.drawable.gradient_blue_bottom);
                image_button_blue_top.setAlpha(0.5f);
                image_button_reset.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
                image_button_numbers.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
                image_button_name.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
                image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
                image_button_10.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);
                image_button_20.setBackgroundResource(R.drawable.gradient_top_blue_menu_button_theme);

                topTheme = "BLUE";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TOP_THEME), topTheme);
                editor.apply();
            }
        });

        button_black_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_top.setAlpha(1f);
                image_button_green_top.setAlpha(1f);
                image_button_white_top.setAlpha(1f);
                image_button_blue_top.setAlpha(1f);
                image_button_black_top.setAlpha(1f);

                display_top.setBackgroundResource(R.drawable.gradient_black_bottom);
                image_button_black_top.setAlpha(0.5f);
                image_button_reset.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
                image_button_numbers.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
                image_button_name.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
                image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
                image_button_10.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);
                image_button_20.setBackgroundResource(R.drawable.gradient_top_black_menu_button_theme);

                topTheme = "BLACK";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TOP_THEME), topTheme);
                editor.apply();
            }
        });

        button_red_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_bottom.setAlpha(1f);
                image_button_green_bottom.setAlpha(1f);
                image_button_white_bottom.setAlpha(1f);
                image_button_blue_bottom.setAlpha(1f);
                image_button_black_bottom.setAlpha(1f);

                display_bottom.setBackgroundResource(R.drawable.gradient_red_bottom);
                image_button_red_bottom.setAlpha(0.5f);
                image_button_history.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
                image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
                image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
                image_button_30.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
                image_button_40.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
                image_button_50.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
                image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
                image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);
                image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_red_menu_button_theme);

                bottomTheme = "RED";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.BOTTOM_THEME), bottomTheme);
                editor.apply();
            }
        });

        button_green_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_bottom.setAlpha(1f);
                image_button_green_bottom.setAlpha(1f);
                image_button_white_bottom.setAlpha(1f);
                image_button_blue_bottom.setAlpha(1f);
                image_button_black_bottom.setAlpha(1f);

                display_bottom.setBackgroundResource(R.drawable.gradient_green_bottom);
                image_button_green_bottom.setAlpha(0.5f);
                image_button_history.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
                image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
                image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
                image_button_30.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
                image_button_40.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
                image_button_50.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
                image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
                image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);
                image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_green_menu_button_theme);

                bottomTheme = "GREEN";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.BOTTOM_THEME), bottomTheme);
                editor.apply();
            }
        });

        button_white_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_bottom.setAlpha(1f);
                image_button_green_bottom.setAlpha(1f);
                image_button_white_bottom.setAlpha(1f);
                image_button_blue_bottom.setAlpha(1f);
                image_button_black_bottom.setAlpha(1f);

                display_bottom.setBackgroundResource(R.drawable.gradient_white_bottom);
                image_button_white_bottom.setAlpha(0.5f);
                image_button_history.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
                image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
                image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
                image_button_30.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
                image_button_40.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
                image_button_50.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
                image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
                image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);
                image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_white_menu_button_theme);

                bottomTheme = "WHITE";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.BOTTOM_THEME), bottomTheme);
                editor.apply();
            }
        });

        button_blue_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_bottom.setAlpha(1f);
                image_button_green_bottom.setAlpha(1f);
                image_button_white_bottom.setAlpha(1f);
                image_button_blue_bottom.setAlpha(1f);
                image_button_black_bottom.setAlpha(1f);

                display_bottom.setBackgroundResource(R.drawable.gradient_blue_bottom);
                image_button_blue_bottom.setAlpha(0.5f);
                image_button_history.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
                image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
                image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
                image_button_30.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
                image_button_40.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
                image_button_50.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
                image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
                image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);
                image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_blue_menu_button_theme);

                bottomTheme = "BLUE";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.BOTTOM_THEME), bottomTheme);
                editor.apply();
            }
        });

        button_black_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                image_button_red_bottom.setAlpha(1f);
                image_button_green_bottom.setAlpha(1f);
                image_button_white_bottom.setAlpha(1f);
                image_button_blue_bottom.setAlpha(1f);
                image_button_black_bottom.setAlpha(1f);

                display_bottom.setBackgroundResource(R.drawable.gradient_black_bottom);
                image_button_black_bottom.setAlpha(0.5f);
                image_button_history.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
                image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
                image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
                image_button_30.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
                image_button_40.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
                image_button_50.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
                image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
                image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);
                image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_black_menu_button_theme);

                bottomTheme = "BLACK";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.BOTTOM_THEME), bottomTheme);
                editor.apply();
            }
        });
    }

    public void poisonButtonClicked() {

        button_poison_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                if (!poisonFClicked) {
                    image_button_poison_top.setAlpha(0.5f);
                    LinearLayout_poison_top.setVisibility(View.VISIBLE);
                    poisonFClicked = true;
                } else {
                    image_button_poison_top.setAlpha(1f);
                    LinearLayout_poison_top.setVisibility(View.GONE);
                    poisonFClicked = false;
                }
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.POISON_TOP_OPEN), poisonFClicked);
                editor.apply();
            }
        });
        button_poison_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                if (!poisonClicked) {
                    image_button_poison_bottom.setAlpha(0.5f);
                    LinearLayout_poison_bottom.setVisibility(View.VISIBLE);
                    poisonClicked = true;
                } else {
                    image_button_poison_bottom.setAlpha(1f);
                    LinearLayout_poison_bottom.setVisibility(View.GONE);
                    poisonClicked = false;
                }

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.POISON_BOTTOM_OPEN), poisonClicked);
                editor.apply();
            }
        });
    }

    public void seekbarUpdate() {

        poisonBarTop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                //Rounds progress value to the nearest integer.
                i = Math.round(i / 10) * 10;
                seekBar.setProgress(i);

                poisonTopText.setText("Poison: " + i / 10);
                //Used for database entry.
                poisonTopUpdate = i / 10;

                //Haptic Feedback
                if (i / 10 != previousTopi && allowVibration) {
                    myVib.vibrate(5);
                }

                //Used to see if haptic feedback is appropriate (if statement above).
                previousTopi = i / 10;

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.TOP_POISON_COUNT), poisonTopUpdate);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        poisonBarBottom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                //Rounds progress value to the nearest integer.
                i = Math.round(i / 10) * 10;
                seekBar.setProgress(i);

                poisonBottomText.setText("Poison: " + i / 10);
                //Used for database entry.
                poisonBottomUpdate = i / 10;


                //Haptic Feedback
                if (i / 10 != previousBottomi && allowVibration) {
                    myVib.vibrate(5);
                }

                //Used to see if haptic feedback is appropriate (if statement above).
                previousBottomi = i / 10;

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.BOTTOM_POISON_COUNT), poisonBottomUpdate);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void scoreReset() {
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                globalSeconds = System.currentTimeMillis();

                if (interstitialAd.isLoaded() && (globalSeconds - previousGlobalSeconds >= 60000)) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            interstitialAd.show();
                            previousGlobalSeconds = globalSeconds;
                        }
                    }, 1000);
                }


                //Clicked Button Effect
                image_button_reset.setAlpha(0.5f);

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                //Toast Message
                if (allowToast) {
                    Toast.makeText(MainActivity.this, "Game Reset", Toast.LENGTH_SHORT).show();
                }

                //Resets all display items to their initial values.
                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);
                poisonTopText.setText(R.string.initial_poison_count);
                poisonBottomText.setText(R.string.initial_poison_count);
                poisonBarTop.setProgress(0);
                poisonBarBottom.setProgress(0);
                text_view_top_player_time.setText(R.string.initial_timer_text);
                text_view_bottom_player_time.setText(R.string.initial_timer_text);
                button_top_player_turn.setText(R.string.initial_turn_button_text);
                button_bottom_player_turn.setText(R.string.initial_turn_button_text);
                topPlayerSeconds = 0;
                bottomPlayerSeconds = 0;
                turnNumber = 0;
                currentTurnName = "";
                currentTurnTimeInterval = "0:00";
                lastTopTurnTime = "0:00";
                lastBottomTurnTime = "0:00";

                //Ends players' turn if it is currently in session.
                if (topTurnActive) {
                    button_top_player_turn.performClick();
                } else if (bottomTurnActive) {
                    button_bottom_player_turn.performClick();
                }

                //Resets all theme backgrounds and button backgrounds.
                display_top.setBackgroundResource(R.drawable.gradient_default_bottom);
                display_bottom.setBackgroundResource(R.drawable.gradient_default_bottom);

                image_button_reset.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
                image_button_numbers.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
                image_button_name.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
                image_button_history.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
                image_button_dice.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
                image_button_settings.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);

                image_button_numbers_back.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
                image_button_10.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
                image_button_20.setBackgroundResource(R.drawable.gradient_top_default_menu_button_theme);
                image_button_30.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
                image_button_40.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
                image_button_50.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);

                image_button_name_menu_back.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
                image_button_name_menu_reset.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);
                image_button_name_menu_confirm.setBackgroundResource(R.drawable.gradient_bottom_default_menu_button_theme);

                image_button_red_top.setAlpha(1f);
                image_button_green_top.setAlpha(1f);
                image_button_white_top.setAlpha(1f);
                image_button_blue_top.setAlpha(1f);
                image_button_black_top.setAlpha(1f);
                image_button_red_bottom.setAlpha(1f);
                image_button_green_bottom.setAlpha(1f);
                image_button_white_bottom.setAlpha(1f);
                image_button_blue_bottom.setAlpha(1f);
                image_button_black_bottom.setAlpha(1f);

                topTheme = "DEFAULT";
                bottomTheme = "DEFAULT";
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TOP_THEME), topTheme);
                editor.putString(getString(R.string.BOTTOM_THEME), bottomTheme);
                editor.putString(getString(R.string.TOP_SCORE), text_number_top.getText().toString());
                editor.putString(getString(R.string.BOTTOM_SCORE), text_number_bottom.getText().toString());
                editor.putString(getString(R.string.LAST_TOP_TURN_TIME), lastTopTurnTime);
                editor.putString(getString(R.string.LAST_BOTTOM_TURN_TIME), lastBottomTurnTime);
                editor.putString(getString(R.string.CURRENT_TIME_INTERVAL), currentTurnTimeInterval);
                editor.putString(getString(R.string.TOP_PLAYER_TIME), text_view_top_player_time.getText().toString());
                editor.putInt(getString(R.string.TOP_PLAYER_SECONDS), topPlayerSeconds);
                editor.putString(getString(R.string.BOTTOM_PLAYER_TIME), text_view_bottom_player_time.getText().toString());
                editor.putInt(getString(R.string.BOTTOM_PLAYER_SECONDS), bottomPlayerSeconds);
                editor.putInt(getString(R.string.TURN_NUMBER), turnNumber);
                editor.apply();

                //Shakes top/bottom display items to indicate that a reset has taken place.
                Animation vibrate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_vibrate);

                text_view_top_player_time.startAnimation(vibrate);
                text_number_top.startAnimation(vibrate);
                button_plus_top.startAnimation(vibrate);
                button_minus_top.startAnimation(vibrate);
                text_view_top_player_name.startAnimation(vibrate);
                poisonBarTop.startAnimation(vibrate);
                poisonTopText.startAnimation(vibrate);

                text_view_bottom_player_time.startAnimation(vibrate);
                text_number_bottom.startAnimation(vibrate);
                button_plus_bottom.startAnimation(vibrate);
                button_minus_bottom.startAnimation(vibrate);
                text_view_bottom_player_name.startAnimation(vibrate);
                poisonBarBottom.startAnimation(vibrate);
                poisonBottomText.startAnimation(vibrate);

                //Sets reset button's effect state to un-clicked.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        image_button_reset.setAlpha(1f);
                    }
                }, 150);

                //Sends reset data to database.
                AddData();
            }
        });
    }

    public void lifeCountMenuOpen() {
        button_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                menu_numbers_center.setVisibility(View.VISIBLE);
                menuAnimation(menu_numbers_center, 0f, 0f, 1f, 150);
                menuAnimation(RelativeLayout_menu_center, 0f, 0f, 0.0005f, 150);
                lifeCountMenuOpen = true;


            }
        });
    }

    public void startingLifeCountUpdate() {

        button_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTextNumber = "10";
                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }
                if (allowToast) {
                    Toast.makeText(MainActivity.this, "Default Score Updated to " + currentTextNumber, Toast.LENGTH_SHORT).show();
                }
                image_button_10.setAlpha(1f);
                image_button_20.setAlpha(1f);
                image_button_30.setAlpha(1f);
                image_button_40.setAlpha(1f);
                image_button_50.setAlpha(1f);
                image_button_10.setAlpha(0.5f);

                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.STARTING_LIFE_COUNT), currentTextNumber);
                editor.putString(getString(R.string.TOP_SCORE), text_number_top.getText().toString());
                editor.putString(getString(R.string.BOTTOM_SCORE), text_number_bottom.getText().toString());
                editor.apply();

                AddData();
            }
        });

        button_20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTextNumber = "20";
                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }
                if (allowToast) {
                    Toast.makeText(MainActivity.this, "Default Score Updated to " + currentTextNumber, Toast.LENGTH_SHORT).show();
                }

                image_button_10.setAlpha(1f);
                image_button_20.setAlpha(1f);
                image_button_30.setAlpha(1f);
                image_button_40.setAlpha(1f);
                image_button_50.setAlpha(1f);
                image_button_20.setAlpha(0.5f);

                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.STARTING_LIFE_COUNT), currentTextNumber);
                editor.putString(getString(R.string.TOP_SCORE), text_number_top.getText().toString());
                editor.putString(getString(R.string.BOTTOM_SCORE), text_number_bottom.getText().toString());
                editor.apply();

                AddData();
            }
        });

        button_30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTextNumber = "30";
                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }
                if (allowToast) {
                    Toast.makeText(MainActivity.this, "Default Score Updated to " + currentTextNumber, Toast.LENGTH_SHORT).show();
                }

                image_button_10.setAlpha(1f);
                image_button_20.setAlpha(1f);
                image_button_30.setAlpha(1f);
                image_button_40.setAlpha(1f);
                image_button_50.setAlpha(1f);
                image_button_30.setAlpha(0.5f);

                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.STARTING_LIFE_COUNT), currentTextNumber);
                editor.putString(getString(R.string.TOP_SCORE), text_number_top.getText().toString());
                editor.putString(getString(R.string.BOTTOM_SCORE), text_number_bottom.getText().toString());
                editor.apply();

                AddData();
            }
        });

        button_40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTextNumber = "40";
                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }
                if (allowToast) {
                    Toast.makeText(MainActivity.this, "Default Score Updated to " + currentTextNumber, Toast.LENGTH_SHORT).show();
                }

                image_button_10.setAlpha(1f);
                image_button_20.setAlpha(1f);
                image_button_30.setAlpha(1f);
                image_button_40.setAlpha(1f);
                image_button_50.setAlpha(1f);
                image_button_40.setAlpha(0.5f);

                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.STARTING_LIFE_COUNT), currentTextNumber);
                editor.putString(getString(R.string.TOP_SCORE), text_number_top.getText().toString());
                editor.putString(getString(R.string.BOTTOM_SCORE), text_number_bottom.getText().toString());
                editor.apply();

                AddData();

            }
        });

        button_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTextNumber = "50";
                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }
                if (allowToast) {
                    Toast.makeText(MainActivity.this, "Default Score Updated to " + currentTextNumber, Toast.LENGTH_SHORT).show();
                }

                image_button_10.setAlpha(1f);
                image_button_20.setAlpha(1f);
                image_button_30.setAlpha(1f);
                image_button_40.setAlpha(1f);
                image_button_50.setAlpha(1f);
                image_button_50.setAlpha(0.5f);

                text_number_top.setText(currentTextNumber);
                text_number_bottom.setText(currentTextNumber);

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.STARTING_LIFE_COUNT), currentTextNumber);
                editor.putString(getString(R.string.TOP_SCORE), text_number_top.getText().toString());
                editor.putString(getString(R.string.BOTTOM_SCORE), text_number_bottom.getText().toString());
                editor.apply();

                AddData();
            }
        });
    }

    public void nameMenuOpen() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        final float yMove = 40f * displayMetrics.density;


        button_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }


                menu_names.setVisibility(View.VISIBLE);
                menuAnimation(menu_names, 0f, 0f, 1f, 150);
                menuAnimation(RelativeLayout_menu_center, 0f, 0f, 0.0005f, 150);
                menuAnimation(RelativeLayout_theme_menu_top, 0f, -yMove, 1f, 150);
                menuAnimation(RelativeLayout_theme_menu_bottom, 0f, yMove, 1f, 150);
                nameMenuOpen = true;

                button_name_menu_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Haptic Feedback
                        if (allowVibration) {
                            myVib.vibrate(5);
                        }

                        image_button_name_menu_confirm.setAlpha(0.5f);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                image_button_name_menu_confirm.setAlpha(1f);
                            }
                        }, 150);

                        Animation vibrate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_vibrate);

                        text_view_top_player_name.setText(edit_text_top_player_name.getText());
                        text_view_bottom_player_name.setText(edit_text_bottom_player_name.getText());
                        history_first_name_heading.setText(text_view_top_player_name.getText());
                        history_second_name_heading.setText(text_view_bottom_player_name.getText());

                        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.TOP_NAME), text_view_top_player_name.getText().toString());
                        editor.putString(getString(R.string.BOTTOM_NAME), text_view_bottom_player_name.getText().toString());
                        editor.apply();


                        if (allowToast) {
                            Toast.makeText(MainActivity.this, "Names Updated", Toast.LENGTH_SHORT).show();
                        }
//                        edit_text_top_player_name.setText("");
//                        edit_text_bottom_player_name.setText("");
                        text_view_top_player_name.startAnimation(vibrate);
                        text_view_bottom_player_name.startAnimation(vibrate);

                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }
                });
                button_name_menu_reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Haptic Feedback
                        if (allowVibration) {
                            myVib.vibrate(5);
                        }

                        image_button_name_menu_reset.setAlpha(0.5f);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                image_button_name_menu_reset.setAlpha(1f);
                            }
                        }, 150);

                        Animation vibrate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_vibrate);

                        text_view_top_player_name.setText("Opponent");
                        text_view_bottom_player_name.setText("User");
                        edit_text_top_player_name.setText("");
                        edit_text_bottom_player_name.setText("");
                        history_first_name_heading.setText(text_view_top_player_name.getText());
                        history_second_name_heading.setText(text_view_bottom_player_name.getText());


                        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.TOP_NAME), text_view_top_player_name.getText().toString());
                        editor.putString(getString(R.string.BOTTOM_NAME), text_view_bottom_player_name.getText().toString());
                        editor.apply();

                        if (allowToast) {
                            Toast.makeText(MainActivity.this, "Names Reset", Toast.LENGTH_SHORT).show();
                        }

                        text_view_top_player_name.startAnimation(vibrate);
                        text_view_bottom_player_name.startAnimation(vibrate);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

                    }
                });
                button_name_menu_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Haptic Feedback
                        if (allowVibration) {
                            myVib.vibrate(5);
                        }

                        menuAnimation(menu_names, 0f, 0f, 0.0005f, 150);
                        menuAnimation(RelativeLayout_menu_center, 0f, 0f, 1f, 150);
                        menuAnimation(RelativeLayout_theme_menu_top, 0f, yMove, 1f, 150);
                        menuAnimation(RelativeLayout_theme_menu_bottom, 0f, -yMove, 1f, 150);
                        nameMenuOpen = false;
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

                    }
                });


            }
        });
    }

    public void historyMenuOpen() {

        button_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalSeconds = System.currentTimeMillis();

                if (interstitialAd.isLoaded() && (globalSeconds - previousGlobalSeconds >= 60000)) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            interstitialAd.show();
                            previousGlobalSeconds = globalSeconds;
                        }
                    }, 3000);
                }

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                button_blue_top.setEnabled(false);
                button_black_top.setEnabled(false);
                button_poison_top.setEnabled(false);


                RelativeLayout_menu_history.setVisibility(View.VISIBLE);
                RelativeLayout_menu_history.bringToFront();

                menuAnimation(RelativeLayout_menu_history, 0f, 0f, 1f, 150);
                menuHistoryOpen = true;
                populateListView();
            }
        });

    }

    public void historyButtonBack() {
        button_history_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                button_blue_top.setEnabled(true);
                button_black_top.setEnabled(true);
                button_poison_top.setEnabled(true);

                menuAnimation(RelativeLayout_menu_history, 0f, 0f, 0.0005f, 200);
                menuHistoryOpen = false;

            }
        });
        button_blue_top.setEnabled(true);
        button_black_top.setEnabled(true);
        button_poison_top.setEnabled(true);

        menuAnimation(RelativeLayout_menu_history, 0f, 0f, 0.0005f, 200);
        menuHistoryOpen = false;


    }

    public void AddData() {


        Cursor scores = myDb.lastRow();

        ArrayList<String> columnArray1 = new ArrayList<String>();
        ArrayList<String> columnArray2 = new ArrayList<String>();
        ArrayList<String> columnArray3 = new ArrayList<String>();
        ArrayList<String> columnArray4 = new ArrayList<String>();
        ArrayList<String> columnArray6 = new ArrayList<String>();

        for (scores.moveToFirst(); !scores.isAfterLast(); scores.moveToNext()) {
            columnArray1.add(scores.getString(1));
            columnArray2.add(scores.getString(2));
            columnArray3.add(scores.getString(3));
            columnArray4.add(scores.getString(4));
            columnArray6.add(scores.getString(5));
        }
        String[] colStrArr1 = columnArray1.toArray(new String[columnArray1.size()]);
        String[] colStrArr2 = columnArray2.toArray(new String[columnArray2.size()]);
        String[] colStrArr3 = columnArray3.toArray(new String[columnArray3.size()]);
        String[] colStrArr4 = columnArray4.toArray(new String[columnArray4.size()]);
        String[] colStrArr6 = columnArray4.toArray(new String[columnArray6.size()]);


        if (colStrArr1 != null && colStrArr1.length > 0) {

            if (colStrArr1[0].equals(text_number_top.getText().toString()) && colStrArr2[0].equals(Integer.toString(poisonTopUpdate)) &&
                    colStrArr3[0].equals(text_number_bottom.getText().toString()) && colStrArr4[0].equals(Integer.toString(poisonBottomUpdate)) &&
                    colStrArr6[0].equals(Integer.toString(turnNumber))) {
                return;
            } else {
                myDb.insertData(text_number_top.getText().toString(), Integer.toString(poisonTopUpdate),
                        text_number_bottom.getText().toString(), Integer.toString(poisonBottomUpdate),
                        currentTurnName, Integer.toString(turnNumber), currentTurnTimeInterval);
            }

        } else
            myDb.insertData(text_number_top.getText().toString(), Integer.toString(poisonTopUpdate),
                    text_number_bottom.getText().toString(), Integer.toString(poisonBottomUpdate),
                    currentTurnName, Integer.toString(turnNumber), currentTurnTimeInterval);

    }

    public void populateListView() {

        Cursor scores = myDb.getAllData();

        String[] columns = new String[]{
                myDb.COL_2,
                myDb.COL_3,
                myDb.COL_4,
                myDb.COL_5,
                myDb.COL_6,
                myDb.COL_7,
                myDb.COL_8
        };

        int[] boundTo = new int[]{
                R.id.topScore,
                R.id.topCount,
                R.id.bottomScore,
                R.id.bottomCount,
                R.id.currentTurnName,
                R.id.turn_number,
                R.id.turn_time
        };

        simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.list_view_score_history,
                scores,
                columns,
                boundTo,
                0);

        list_view_history.setAdapter(simpleCursorAdapter);
    }

    public void showTrashDialog(View view) {

        //Haptic Feedback
        if (allowVibration) {
            myVib.vibrate(5);
        }

        turnNumber = 0;
        currentTurnName = text_view_bottom_player_name.getText().toString();

        FragmentManager manager = getFragmentManager();
        TrashDialog trashDialog = new TrashDialog();
        trashDialog.show(manager, "TrashDialog");


    }

    @Override
    public void onDialogMessage(String message) {

        //Haptic Feedback
        if (allowVibration) {
            myVib.vibrate(5);
        }

        if (allowToast) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        if (message.equals("Deleted")) {

            poisonTopText.setText("Poison: 0");
            poisonBottomText.setText("Poison: 0");
            poisonBarTop.setProgress(0);
            poisonBarBottom.setProgress(0);


            myDb.deleteAll();
            button_reset.performClick();
            RelativeLayout_menu_history.performClick();
            simpleCursorAdapter.notifyDataSetChanged();
            populateListView();

        }

    }

    public void showHistoryInformationDialog(View view) {

        //Haptic Feedback
        if (allowVibration) {
            myVib.vibrate(5);
        }

        FragmentManager manager = getFragmentManager();
        HistoryInformationDialog dialog = new HistoryInformationDialog();
        dialog.show(manager, "HistoryInformationDialog");

    }

    public void diceSpinAnimation() {
        button_dice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                button_resume_dice.setEnabled(false);

                backPressEnabled = false;
                button_clear_center.setEnabled(false);

                button_reset.setEnabled(false);
                button_numbers.setEnabled(false);
                button_history.setEnabled(false);
//                picture.setEnabled(false);
                button_dice.setEnabled(false);

                image_button_dice.setAlpha(0.5f);

                final float redTopAlpha = image_button_red_top.getAlpha();
                final float greenTopAlpha = image_button_green_top.getAlpha();
                final float whiteTopAlpha = image_button_white_top.getAlpha();
                final float blueTopAlpha = image_button_blue_top.getAlpha();
                final float blackTopAlpha = image_button_black_top.getAlpha();
                final float poisonTopAlpha = image_button_poison_top.getAlpha();

                final float redBottomAlpha = image_button_red_bottom.getAlpha();
                final float greenBottomAlpha = image_button_green_bottom.getAlpha();
                final float whiteBottomAlpha = image_button_white_bottom.getAlpha();
                final float blueBottomAlpha = image_button_blue_bottom.getAlpha();
                final float blackBottomAlpha = image_button_black_bottom.getAlpha();
                final float poisonBottomAlpha = image_button_poison_bottom.getAlpha();


                button_dice.setAlpha(0.5f); //make image button

                image_button_red_top.setEnabled(false);
                image_button_green_top.setEnabled(false);
                image_button_white_top.setEnabled(false);
                image_button_blue_top.setEnabled(false);
                image_button_black_top.setEnabled(false);
                image_button_poison_top.setEnabled(false);
                image_button_red_bottom.setEnabled(false);
                image_button_green_bottom.setEnabled(false);
                image_button_white_bottom.setEnabled(false);
                image_button_blue_bottom.setEnabled(false);
                image_button_black_bottom.setEnabled(false);
                image_button_poison_bottom.setEnabled(false);

                button_red_top.setEnabled(false);
                button_green_top.setEnabled(false);
                button_white_top.setEnabled(false);
                button_blue_top.setEnabled(false);
                button_black_top.setEnabled(false);
                button_poison_top.setEnabled(false);
                button_red_bottom.setEnabled(false);
                button_green_bottom.setEnabled(false);
                button_white_bottom.setEnabled(false);
                button_blue_bottom.setEnabled(false);
                button_black_bottom.setEnabled(false);
                button_poison_bottom.setEnabled(false);

                RelativeLayout_menu_center.bringToFront();
                RelativeLayout_theme_menu_top.animate().rotation(1620).setDuration(1500);
                RelativeLayout_theme_menu_bottom.animate().rotation(1440).setDuration(1500);

                Random r = new Random();
                i = r.nextInt(6) + 1;
                j = r.nextInt(6) + 1;
                while (i == j) {
                    i = r.nextInt(6) + 1;
                    j = r.nextInt(6) + 1;
                }

                final Handler handler = new Handler(); //Wait for Animation to Complete
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        image_button_red_top.setAlpha(1f);
                        image_button_green_top.setAlpha(1f);
                        image_button_white_top.setAlpha(1f);
                        image_button_blue_top.setAlpha(1f);
                        image_button_black_top.setAlpha(1f);
                        image_button_poison_top.setAlpha(1f);

                        image_button_red_bottom.setAlpha(1f);
                        image_button_green_bottom.setAlpha(1f);
                        image_button_white_bottom.setAlpha(1f);
                        image_button_blue_bottom.setAlpha(1f);
                        image_button_black_bottom.setAlpha(1f);
                        image_button_poison_bottom.setAlpha(1f);


                        image_button_red_top.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_white_top.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_blue_top.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_black_top.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_green_top.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_poison_top.setImageResource(R.drawable.icon_dice_dot_center);
                        icon_dice_dot_top.setImageResource(R.drawable.icon_dice_dot_center);

                        image_button_red_top.setVisibility(View.VISIBLE);
                        image_button_white_top.setVisibility(View.VISIBLE);
                        image_button_blue_top.setVisibility(View.VISIBLE);
                        image_button_black_top.setVisibility(View.VISIBLE);
                        image_button_green_top.setVisibility(View.VISIBLE);
                        image_button_poison_top.setVisibility(View.VISIBLE);
                        icon_dice_dot_top.setVisibility(View.VISIBLE);

                        image_button_red_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_white_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_blue_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_black_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_green_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                        image_button_poison_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                        icon_dice_dot_bottom.setImageResource(R.drawable.icon_dice_dot_center);

                        image_button_red_bottom.setVisibility(View.VISIBLE);
                        image_button_white_bottom.setVisibility(View.VISIBLE);
                        image_button_blue_bottom.setVisibility(View.VISIBLE);
                        image_button_black_bottom.setVisibility(View.VISIBLE);
                        image_button_green_bottom.setVisibility(View.VISIBLE);
                        image_button_poison_bottom.setVisibility(View.VISIBLE);
                        icon_dice_dot_bottom.setVisibility(View.VISIBLE);

                        icon_dice_dot_top.bringToFront();
                        icon_dice_dot_bottom.bringToFront();

                        switch (i) {
                            case 1:
                                image_button_red_top.setVisibility(View.INVISIBLE);
                                image_button_white_top.setVisibility(View.INVISIBLE);
                                image_button_blue_top.setVisibility(View.INVISIBLE);
                                image_button_black_top.setVisibility(View.INVISIBLE);
                                image_button_green_top.setVisibility(View.INVISIBLE);
                                image_button_poison_top.setVisibility(View.INVISIBLE);
                                icon_dice_dot_top.setVisibility(View.VISIBLE);

                                break;

                            case 2:
                                image_button_red_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_top.setVisibility(View.INVISIBLE);
                                image_button_blue_top.setVisibility(View.INVISIBLE);
                                image_button_black_top.setVisibility(View.INVISIBLE);
                                image_button_green_top.setVisibility(View.INVISIBLE);
                                image_button_poison_top.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_top.setVisibility(View.INVISIBLE);
                                break;

                            case 3:
                                image_button_red_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_top.setVisibility(View.INVISIBLE);
                                image_button_blue_top.setVisibility(View.INVISIBLE);
                                image_button_black_top.setVisibility(View.INVISIBLE);
                                image_button_green_top.setVisibility(View.INVISIBLE);
                                image_button_poison_top.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_top.setVisibility(View.VISIBLE);
                                break;

                            case 4:
                                image_button_red_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_blue_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_black_top.setVisibility(View.INVISIBLE);
                                image_button_green_top.setVisibility(View.INVISIBLE);
                                image_button_poison_top.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_top.setVisibility(View.INVISIBLE);
                                break;

                            case 5:
                                image_button_red_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_blue_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_black_top.setVisibility(View.INVISIBLE);
                                image_button_green_top.setVisibility(View.INVISIBLE);
                                image_button_poison_top.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_top.setVisibility(View.VISIBLE);
                                break;

                            case 6:
                                image_button_red_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_blue_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_black_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_green_top.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_poison_top.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_top.setVisibility(View.INVISIBLE);
                                break;

                        }

                        switch (j) {
                            case 1:
                                image_button_red_bottom.setVisibility(View.INVISIBLE);
                                image_button_white_bottom.setVisibility(View.INVISIBLE);
                                image_button_blue_bottom.setVisibility(View.INVISIBLE);
                                image_button_black_bottom.setVisibility(View.INVISIBLE);
                                image_button_green_bottom.setVisibility(View.INVISIBLE);
                                image_button_poison_bottom.setVisibility(View.INVISIBLE);
                                icon_dice_dot_bottom.setVisibility(View.VISIBLE);
                                break;

                            case 2:
                                image_button_red_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_bottom.setVisibility(View.INVISIBLE);
                                image_button_blue_bottom.setVisibility(View.INVISIBLE);
                                image_button_black_bottom.setVisibility(View.INVISIBLE);
                                image_button_green_bottom.setVisibility(View.INVISIBLE);
                                image_button_poison_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_bottom.setVisibility(View.INVISIBLE);
                                break;

                            case 3:
                                image_button_red_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_bottom.setVisibility(View.INVISIBLE);
                                image_button_blue_bottom.setVisibility(View.INVISIBLE);
                                image_button_black_bottom.setVisibility(View.INVISIBLE);
                                image_button_green_bottom.setVisibility(View.INVISIBLE);
                                image_button_poison_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_bottom.setVisibility(View.VISIBLE);
                                break;

                            case 4:
                                image_button_red_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_blue_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_black_bottom.setVisibility(View.INVISIBLE);
                                image_button_green_bottom.setVisibility(View.INVISIBLE);
                                image_button_poison_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_bottom.setVisibility(View.INVISIBLE);
                                break;

                            case 5:
                                image_button_red_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_blue_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_black_bottom.setVisibility(View.INVISIBLE);
                                image_button_green_bottom.setVisibility(View.INVISIBLE);
                                image_button_poison_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_bottom.setVisibility(View.VISIBLE);
                                break;

                            case 6:
                                image_button_red_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_white_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_blue_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_black_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_green_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                image_button_poison_bottom.setImageResource(R.drawable.icon_dice_dot_center);
                                icon_dice_dot_bottom.setVisibility(View.INVISIBLE);
                                break;

                        }
                        button_resume_dice.setEnabled(true);

                        if (allowToast) {
                            Toast.makeText(MainActivity.this, "Tap to Resume", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 750);
                button_resume_dice.setVisibility(View.VISIBLE);
                button_resume_dice.bringToFront();


                button_resume_dice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        button_resume_dice.setEnabled(false);

                        RelativeLayout_theme_menu_top.animate().rotation(180).setDuration(500);
                        RelativeLayout_theme_menu_bottom.animate().rotation(0).setDuration(500);

                        image_button_red_top.setImageResource(R.drawable.icon_red);
                        image_button_white_top.setImageResource(R.drawable.icon_white);
                        image_button_blue_top.setImageResource(R.drawable.icon_blue);
                        image_button_black_top.setImageResource(R.drawable.icon_black);
                        image_button_green_top.setImageResource(R.drawable.icon_green);
                        image_button_poison_top.setImageResource(R.drawable.icon_poison_front);
                        icon_dice_dot_top.setVisibility(View.INVISIBLE);

                        image_button_red_top.setVisibility(View.VISIBLE);
                        image_button_white_top.setVisibility(View.VISIBLE);
                        image_button_blue_top.setVisibility(View.VISIBLE);
                        image_button_black_top.setVisibility(View.VISIBLE);
                        image_button_green_top.setVisibility(View.VISIBLE);
                        image_button_poison_top.setVisibility(View.VISIBLE);

                        image_button_red_bottom.setImageResource(R.drawable.icon_red);
                        image_button_white_bottom.setImageResource(R.drawable.icon_white);
                        image_button_blue_bottom.setImageResource(R.drawable.icon_blue);
                        image_button_black_bottom.setImageResource(R.drawable.icon_black);
                        image_button_green_bottom.setImageResource(R.drawable.icon_green);
                        image_button_poison_bottom.setImageResource(R.drawable.icon_poison_front);
                        icon_dice_dot_bottom.setVisibility(View.INVISIBLE);

                        image_button_red_bottom.setVisibility(View.VISIBLE);
                        image_button_white_bottom.setVisibility(View.VISIBLE);
                        image_button_blue_bottom.setVisibility(View.VISIBLE);
                        image_button_black_bottom.setVisibility(View.VISIBLE);
                        image_button_green_bottom.setVisibility(View.VISIBLE);
                        image_button_poison_bottom.setVisibility(View.VISIBLE);

                        final Handler handler3 = new Handler(); //Wait for Animation to Complete
                        handler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                button_dice.setAlpha(1f);

                                image_button_red_top.setEnabled(true);
                                image_button_blue_top.setEnabled(true);
                                image_button_white_top.setEnabled(true);
                                image_button_green_top.setEnabled(true);
                                image_button_black_top.setEnabled(true);
                                image_button_poison_top.setEnabled(true);
                                image_button_red_bottom.setEnabled(true);
                                image_button_blue_bottom.setEnabled(true);
                                image_button_white_bottom.setEnabled(true);
                                image_button_green_bottom.setEnabled(true);
                                image_button_black_bottom.setEnabled(true);
                                image_button_poison_bottom.setEnabled(true);

                                button_red_top.setEnabled(true);
                                button_blue_top.setEnabled(true);
                                button_white_top.setEnabled(true);
                                button_green_top.setEnabled(true);
                                button_black_top.setEnabled(true);
                                button_poison_top.setEnabled(true);
                                button_red_bottom.setEnabled(true);
                                button_blue_bottom.setEnabled(true);
                                button_white_bottom.setEnabled(true);
                                button_green_bottom.setEnabled(true);
                                button_black_bottom.setEnabled(true);
                                button_poison_bottom.setEnabled(true);
                            }
                        }, 500);

                        final Handler handler4 = new Handler();
                        handler4.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                image_button_red_top.setAlpha(redTopAlpha);
                                image_button_blue_top.setAlpha(blueTopAlpha);
                                image_button_white_top.setAlpha(whiteTopAlpha);
                                image_button_green_top.setAlpha(greenTopAlpha);
                                image_button_black_top.setAlpha(blackTopAlpha);
                                image_button_poison_top.setAlpha(poisonTopAlpha);

                                image_button_red_bottom.setAlpha(redBottomAlpha);
                                image_button_blue_bottom.setAlpha(blueBottomAlpha);
                                image_button_white_bottom.setAlpha(whiteBottomAlpha);
                                image_button_green_bottom.setAlpha(greenBottomAlpha);
                                image_button_black_bottom.setAlpha(blackBottomAlpha);
                                image_button_poison_bottom.setAlpha(poisonBottomAlpha);

                                image_button_dice.setAlpha(1f);
                                backPressEnabled = true;
                                button_clear_center.setEnabled(true);
                                button_reset.setEnabled(true);
                                button_numbers.setEnabled(true);
                                button_history.setEnabled(true);
//                        picture.setEnabled(true);
                                button_dice.setEnabled(true);
//                        poisonButton.setEnabled(true);

                                button_resume_dice.setVisibility(View.GONE);
                            }
                        }, 650);

                    }
                });
            }
        });
    }

    public void settingMenuOpen() {
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                button_rate_app.setVisibility(View.VISIBLE);
                RelativeLayout_menu_settings.setVisibility(View.VISIBLE);
                RelativeLayout_menu_settings.bringToFront();

                menuAnimation(RelativeLayout_menu_settings, 0f, 0f, 1f, 150);
                menuSettingsOpen = true;

                image_button_red_top.setEnabled(false);
                image_button_green_top.setEnabled(false);
                image_button_white_top.setEnabled(false);
                image_button_blue_top.setEnabled(false);
                image_button_black_top.setEnabled(false);
                image_button_poison_top.setEnabled(false);
                image_button_red_bottom.setEnabled(false);
                image_button_green_bottom.setEnabled(false);
                image_button_white_bottom.setEnabled(false);
                image_button_blue_bottom.setEnabled(false);
                image_button_black_bottom.setEnabled(false);
                image_button_poison_bottom.setEnabled(false);

                button_red_top.setEnabled(false);
                button_green_top.setEnabled(false);
                button_white_top.setEnabled(false);
                button_blue_top.setEnabled(false);
                button_black_top.setEnabled(false);
                button_poison_top.setEnabled(false);
                button_red_bottom.setEnabled(false);
                button_green_bottom.setEnabled(false);
                button_white_bottom.setEnabled(false);
                button_blue_bottom.setEnabled(false);
                button_black_bottom.setEnabled(false);
                button_poison_bottom.setEnabled(false);

                button_reset.setEnabled(false);
                button_numbers.setEnabled(false);
                button_name.setEnabled(false);
                button_history.setEnabled(false);
                button_dice.setEnabled(false);
                button_settings.setEnabled(false);


            }
        });
    }

    public void settings() {

        toggle_button_toast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                allowToast = b;

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.TOAST_TOGGLE), allowToast);
                editor.apply();
            }
        });

        toggle_button_turn_timer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showTurnTimer = b;

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.TURN_TIMER_TOGGLE), showTurnTimer);
                editor.apply();

                if (b) {
                    button_top_player_turn.setVisibility(View.VISIBLE);
                    button_bottom_player_turn.setVisibility(View.VISIBLE);
                    text_view_top_player_time.setVisibility(View.VISIBLE);
                    text_view_bottom_player_time.setVisibility(View.VISIBLE);

                } else {
                    button_top_player_turn.setVisibility(View.GONE);
                    button_bottom_player_turn.setVisibility(View.GONE);
                    text_view_top_player_time.setVisibility(View.GONE);
                    text_view_bottom_player_time.setVisibility(View.GONE);
                }


            }
        });

        toggle_button_names.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showPlayerNames = b;

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.PLAYER_NAMES_TOGGLE), showPlayerNames);
                editor.apply();

                if (b) {
                    text_view_top_player_name.setVisibility(View.VISIBLE);
                    text_view_bottom_player_name.setVisibility(View.VISIBLE);
                } else {
                    text_view_top_player_name.setVisibility(View.INVISIBLE);
                    text_view_bottom_player_name.setVisibility(View.INVISIBLE);
                }
            }
        });

        toggle_button_vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                allowVibration = b;

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE),
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.VIBRATION_TOGGLE), allowVibration);
                editor.apply();

            }
        });

        button_settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }

                menuAnimation(RelativeLayout_menu_settings, 0f, 0f, 0.000f, 150);
                menuSettingsOpen = false;
                button_rate_app.setVisibility(View.GONE);

                image_button_red_top.setEnabled(true);
                image_button_green_top.setEnabled(true);
                image_button_white_top.setEnabled(true);
                image_button_blue_top.setEnabled(true);
                image_button_black_top.setEnabled(true);
                image_button_poison_top.setEnabled(true);
                image_button_red_bottom.setEnabled(true);
                image_button_green_bottom.setEnabled(true);
                image_button_white_bottom.setEnabled(true);
                image_button_blue_bottom.setEnabled(true);
                image_button_black_bottom.setEnabled(true);
                image_button_poison_bottom.setEnabled(true);

                button_red_top.setEnabled(true);
                button_green_top.setEnabled(true);
                button_white_top.setEnabled(true);
                button_blue_top.setEnabled(true);
                button_black_top.setEnabled(true);
                button_poison_top.setEnabled(true);
                button_red_bottom.setEnabled(true);
                button_green_bottom.setEnabled(true);
                button_white_bottom.setEnabled(true);
                button_blue_bottom.setEnabled(true);
                button_black_bottom.setEnabled(true);
                button_poison_bottom.setEnabled(true);

                button_reset.setEnabled(true);
                button_numbers.setEnabled(true);
                button_name.setEnabled(true);
                button_history.setEnabled(true);
                button_dice.setEnabled(true);
                button_settings.setEnabled(true);

            }
        });

        button_settings_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Settings Restored to Default", Toast.LENGTH_SHORT).show();

                globalSeconds = System.currentTimeMillis();

                if (interstitialAd.isLoaded() && (globalSeconds - previousGlobalSeconds >= 60000)) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            interstitialAd.show();
                            previousGlobalSeconds = globalSeconds;
                        }
                    }, 1000);
                }

                //Haptic Feedback
                if (allowVibration) {
                    myVib.vibrate(5);
                }
                image_button_settings_restore.setAlpha(0.5f);

                toggle_button_toast.setChecked(true);
                toggle_button_vibration.setChecked(true);
                toggle_button_names.setChecked(true);
                toggle_button_turn_timer.setChecked(true);

                //Sets restore button's effect state to un-clicked.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        image_button_settings_restore.setAlpha(1f);
                    }
                }, 150);

                button_20.performClick();


            }
        });
    }

    @Override
    public void onBackPressed() {

        if (backPressEnabled) {
            if (lifeCountMenuOpen) {
                button_numbers_back.performClick();
            } else if (menuHistoryOpen) {
                button_history_back.performClick();
            } else if (nameMenuOpen) {
                button_name_menu_back.performClick();
            } else if (menuSettingsOpen) {
                button_settings_back.performClick();
            } else if (menuCenterOpen) {
                button_clear_center.performClick();
            }

        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        interstitialAd.loadAd(adRequest);
    }

    private void adListener() {
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

            }
        });
    }

    private void rateApp() {
        final String packageName = this.getPackageName();
        button_rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + packageName);
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                }
            }
        });
    }


}


