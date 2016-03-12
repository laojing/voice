package cn.laojing.smarthome;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.percent.PercentRelativeLayout;
import android.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.speech.VoiceRecognitionService;
import com.baidu.tts.answer.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private LinearLayout mNavDrawerEntriesRootView;
    private PercentRelativeLayout mFrameLayout_AccountView;
    private FrameLayout mFrameLayout_Switch, mFrameLayout_cart, mFrameLayout_infra,
            mFrameLayout_monitor, mFrameLayout_About;
    private Context mContext;
    private SutFragment fragments[] = {null, null, null, null, null};
    public SutFragment fragCur = null;

    private SpeechRecognizer speechRecognizer;
    private long speechEndTime = -1;
    private static final int EVENT_ERROR = 11;

    private SutActionButton btnOpen, btnClose;

    public CommandService.MyBinder myBinder;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (CommandService.MyBinder) service;
        }
    };

    private CommandReceiver receiver=null;
    private class CommandReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            fragCur.updateLight(bundle.getString("lights"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove(Constant.EXTRA_INFILE).commit();

        mContext = this;

        LinearLayout linear = (LinearLayout)findViewById(R.id.btnBoxBottom);
        //根据父窗体getActivity()为fragment设置手势识别
        final GestureDetector gesture = new GestureDetector(this, new SutSwipeListener(this));
        //为fragment添加OnTouchListener监听器
        linear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);//返回手势识别触发的事件
            }
        });


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initialise();

        btnOpen = (SutActionButton)findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBinder.LightOn(17);
            }
        });

        startService(new Intent(MainActivity.this, CommandService.class));
        Intent bindIntent = new Intent(this, CommandService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        receiver=new CommandReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("cn.laojing.smarthome.CommandService");
        MainActivity.this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        //结束服务
        unbindService(connection);
        stopService(new Intent(MainActivity.this, CommandService.class));
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_voice:
                //Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:10086"));
                //startActivity(intent);
                //startActivity(new Intent(mContext, Test.class));
                startActivity(new Intent(mContext, VoiceActivity.class));

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void activeFragment ( int index, int right ) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if ( right == 0 ) {
            ft.setCustomAnimations(
                    R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out,
                    R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out);
        } else {
            ft.setCustomAnimations(
                    R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out,
                    R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out);
        }

        if ( fragments[index] == null ) {
            switch ( index ) {
                case 0:
                    fragments[index] = (SutFragment)FragmentSwitch.newInstance(this);
                    break;
                case 1:
                    fragments[index] = (SutFragment)FragmentCart.newInstance(this);
                    break;
                case 2:
                    fragments[index] = (SutFragment)FragmentInfra.newInstance(this);
                    break;
                case 3:
                    fragments[index] = (SutFragment)FragmentMonitor.newInstance(this);
                    break;
                case 4:
                    fragments[index] = (SutFragment)FragmentAbout.newInstance(this);
            }
            ft.add(R.id.main_activity_content_frame, fragments[index]);
            ft.hide(fragments[index]);
        }

        // Set the first item as selected for the first time
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle((fragments[index]).title);
        }
        if ( fragCur != null ) {
            ft.hide(fragCur);
        }
        ft.show(fragments[index]);
        ft.commit();
        fragCur = fragments[index];
    }
    public void changePage ( int right ) {
        int page = fragCur.page;
        if ( right == 1 ) page--;
        else page++;
        if ( page<0 ) page = 4;
        if ( page>4 ) page = 0;
        activeFragment(page,right);
    }

    private void initialise()
    {
        // Toolbar
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setUpIcons();

        // Layout resources
        mFrameLayout_AccountView = (PercentRelativeLayout) findViewById
                (R.id.navigation_drawer_account_view);

        mNavDrawerEntriesRootView = (LinearLayout)findViewById
                (R.id.navigation_drawer_linearLayout_entries_root_view);

        mFrameLayout_Switch = (FrameLayout) findViewById
                (R.id.navigation_drawer_items_list_linearLayout_switch);

        mFrameLayout_cart = (FrameLayout) findViewById
                (R.id.navigation_drawer_items_list_linearLayout_cart);

        mFrameLayout_infra = (FrameLayout) findViewById
                (R.id.navigation_drawer_items_list_linearLayout_infra);

        mFrameLayout_monitor = (FrameLayout) findViewById
                (R.id.navigation_drawer_items_list_linearLayout_monitor);

        mFrameLayout_About = (FrameLayout) findViewById
                (R.id.navigation_drawer_items_list_linearLayout_about);

        // Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_DrawerLayout);

        final SutFrameLayout mScrimInsetsFrameLayout = (SutFrameLayout)
                findViewById(R.id.main_activity_navigation_drawer_rootLayout);

        final ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        mDrawerLayout,
                        mToolbar,
                        R.string.navigation_drawer_opened,
                        R.string.navigation_drawer_closed
                )
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                // Disables the burger/arrow animation by default
                super.onDrawerSlide(drawerView, 0);
            }
        };

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mActionBarDrawerToggle.syncState();

        // Navigation Drawer layout width
        final int possibleMinDrawerWidth =
                UtilsDevice.getScreenWidth(this) -
                        UtilsMiscellaneous
                                .getThemeAttributeDimensionSize(this, android.R.attr.actionBarSize);

        final int maxDrawerWidth =
                getResources().getDimensionPixelSize(R.dimen.navigation_drawer_max_width);

        mScrimInsetsFrameLayout.getLayoutParams().width =
                Math.min(possibleMinDrawerWidth, maxDrawerWidth);

        // Nav Drawer item click listener
        mFrameLayout_Switch.setOnClickListener(this);
        mFrameLayout_cart.setOnClickListener(this);
        mFrameLayout_infra.setOnClickListener(this);
        mFrameLayout_monitor.setOnClickListener(this);
        mFrameLayout_About.setOnClickListener(this);



        mFrameLayout_Switch.setSelected(true);

        activeFragment ( 0,0 );
    }

    @Override
    public void onClick(View view)
    {
        if (!view.isSelected())
        {
            onRowPressed((FrameLayout) view);


            view.setSelected(true);
            if (view == mFrameLayout_Switch)
            {
                activeFragment(0,0);
            }
            else if (view == mFrameLayout_cart)
            {
                activeFragment ( 1,0 );
            }
            else if (view == mFrameLayout_infra)
            {
                activeFragment(2,0);
            }
            else if (view == mFrameLayout_monitor)
            {
                activeFragment(3,0);
            }
            else if (view == mFrameLayout_About) {
                activeFragment ( 4,0 );
            }
        }
        else
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Set up the rows when any is pressed
     *
     * @param pressedRow is the pressed row in the drawer
     */
    private void onRowPressed(@NonNull final FrameLayout pressedRow)
    {
        if (pressedRow.getTag() != getResources().getString(R.string.tag_nav_drawer_special_entry))
        {
            for (int i = 0; i < mNavDrawerEntriesRootView.getChildCount(); i++)
            {
                final View currentView = mNavDrawerEntriesRootView.getChildAt(i);

                final boolean currentViewIsMainEntry = currentView.getTag() ==
                        getResources().getString(R.string.tag_nav_drawer_main_entry);

                if (currentViewIsMainEntry)
                {
                    currentView.setSelected(currentView == pressedRow);
                }
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Sets a tint list to the icons
     *
     * Uses DrawableCompat to make it work before SKD 21
     */
    private void setUpIcons()
    {
        // Icons tint list
        final ImageView homeImageView =
                (ImageView) findViewById(R.id.navigation_drawer_items_list_icon_switch);
        final Drawable homeDrawable = DrawableCompat.wrap(homeImageView.getDrawable());
        DrawableCompat.setTintList
                (
                        homeDrawable.mutate(),
                        ContextCompat.getColorStateList(this, R.color.nav_drawer_icon)
                );

        homeImageView.setImageDrawable(homeDrawable);


        final ImageView cartImageView =
                (ImageView) findViewById(R.id.navigation_drawer_items_list_icon_cart);
        final Drawable cartDrawable = DrawableCompat.wrap(cartImageView.getDrawable());
        DrawableCompat.setTintList
                (
                        cartDrawable.mutate(),
                        ContextCompat.getColorStateList(this, R.color.nav_drawer_icon)
                );

        cartImageView.setImageDrawable(cartDrawable);


        final ImageView infraImageView =
                (ImageView) findViewById(R.id.navigation_drawer_items_list_icon_infra);
        final Drawable infraDrawable = DrawableCompat.wrap(infraImageView.getDrawable());
        DrawableCompat.setTintList
                (
                        infraDrawable.mutate(),
                        ContextCompat.getColorStateList(this, R.color.nav_drawer_icon)
                );

        infraImageView.setImageDrawable(infraDrawable);

        final ImageView monitorImageView =
                (ImageView) findViewById(R.id.navigation_drawer_items_list_icon_monitor);
        final Drawable monitorDrawable = DrawableCompat.wrap(monitorImageView.getDrawable());
        DrawableCompat.setTintList
                (
                        monitorDrawable.mutate(),
                        ContextCompat.getColorStateList(this, R.color.nav_drawer_icon)
                );

        monitorImageView.setImageDrawable(monitorDrawable);
    }


}
