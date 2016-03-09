package cn.laojing.smarthome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private LinearLayout mNavDrawerEntriesRootView;
    private PercentRelativeLayout mFrameLayout_AccountView;
    private FrameLayout mFrameLayout_Switch, mFrameLayout_cart, mFrameLayout_infra,
            mFrameLayout_monitor, mFrameLayout_About;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove(Constant.EXTRA_INFILE).commit();

        mContext = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initialise();
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

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:10086"));
                startActivity(intent);
                //startActivity(new Intent(mContext, ApiActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates, binds and sets up the resources
     */
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

        // Set the first item as selected for the first time
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(R.string.toolbar_title_home);
        }

        mFrameLayout_Switch.setSelected(true);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out,
                R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out);
        ft.add(R.id.main_activity_content_frame, FragmentSwitch.newInstance()).commit();
    }

    @Override
    public void onClick(View view)
    {
        if (!view.isSelected())
        {
            onRowPressed((FrameLayout) view);

            if (view == mFrameLayout_Switch)
            {
                if (getSupportActionBar() != null)
                {
                    getSupportActionBar().setTitle(getString(R.string.toolbar_title_home));
                }

                view.setSelected(true);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(
                        R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out,
                        R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out);
                ft.replace(R.id.main_activity_content_frame, FragmentSwitch.newInstance()).commit();

            }
            else if (view == mFrameLayout_cart)
            {
                if (getSupportActionBar() != null)
                {
                    getSupportActionBar().setTitle(getString(R.string.toolbar_title_cart));
                }

                view.setSelected(true);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(
                        R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out,
                        R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out);
                ft.replace(R.id.main_activity_content_frame, FragmentCart.newInstance()).commit();


                Log.e("cn.laojing.smarthome", "childccccccccccccccccccccc");
            }
            else if (view == mFrameLayout_infra)
            {
                if (getSupportActionBar() != null)
                {
                    getSupportActionBar().setTitle(getString(R.string.toolbar_title_infra));
                }

                view.setSelected(true);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(
                        R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out,
                        R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out);
                ft.replace(R.id.main_activity_content_frame, FragmentInfra.newInstance()).commit();

            }
            else if (view == mFrameLayout_monitor)
            {
                if (getSupportActionBar() != null)
                {
                    getSupportActionBar().setTitle(getString(R.string.toolbar_title_monitor));
                }

                view.setSelected(true);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.setCustomAnimations(
                        R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out,
                        R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out);
                ft.replace(R.id.main_activity_content_frame, FragmentMonitor.newInstance()).commit();

            }
            else if (view == mFrameLayout_About)
            {
                if (getSupportActionBar() != null)
                {
                    getSupportActionBar().setTitle(getString(R.string.toolbar_title_about));
                }

                view.setSelected(true);


                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(
                        R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out,
                        R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out);
                ft.replace(R.id.main_activity_content_frame, FragmentAbout.newInstance()).commit();

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
