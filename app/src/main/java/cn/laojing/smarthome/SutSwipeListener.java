package cn.laojing.smarthome;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SutSwipeListener extends GestureDetector.SimpleOnGestureListener
{
    private Activity activity;
    private int page = 0;

    public SutSwipeListener(Activity act, int cur){
        activity = act;
        page = cur;
    }

    @Override//此方法必须重写且返回真，否则onFling不起效
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if((e1.getX()- e2.getX()>120)&&Math.abs(velocityX)>200){
            changePage(0, page);
            return true;
        }else if((e2.getX()- e1.getX()>120)&&Math.abs(velocityX)>200){
            changePage(1, page);
            return true;
        }
        return false;
    }

    public void changePage ( int right, int page ) {
        Fragment frag = null;
        int title = 0;
        if ( page == 0 && right == 0 ) {
            frag = FragmentInfra.newInstance();
            title = R.string.toolbar_title_infra;
        } else if ( page == 0 && right == 1 ) {
            frag = FragmentAbout.newInstance();
            title = R.string.toolbar_title_about;
        } else if ( page == 1 && right == 0 ) {
            frag = FragmentMonitor.newInstance();
            title = R.string.toolbar_title_monitor;
        } else if ( page == 1 && right == 1 ) {
            frag = FragmentSwitch.newInstance();
            title = R.string.toolbar_title_home;
        } else if ( page == 2 && right == 0 ) {
            frag = FragmentAbout.newInstance();
            title = R.string.toolbar_title_about;
        } else if ( page == 2 && right == 1 ) {
            frag = FragmentInfra.newInstance();
            title = R.string.toolbar_title_infra;
        } else if ( page == 3 && right == 0 ) {
            frag = FragmentSwitch.newInstance();
            title = R.string.toolbar_title_home;
        } else if ( page == 3 && right == 1 ) {
            frag = FragmentMonitor.newInstance();
            title = R.string.toolbar_title_monitor;
        }
        if ( frag != null ) {
            if ( right == 1 ) {
                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                ft.setCustomAnimations(
                        R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out,
                        R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out);
                ft.replace(R.id.main_activity_content_frame, frag).commit();
            } else {
                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                ft.setCustomAnimations(
                        R.anim.slide_fragment_horizontal_right_in, R.anim.slide_fragment_horizontal_left_out,
                        R.anim.slide_fragment_horizontal_left_in, R.anim.slide_fragment_horizontal_right_out);
                ft.replace(R.id.main_activity_content_frame, frag).commit();
            }


            if (title > 0 && ((AppCompatActivity)activity).getSupportActionBar() != null)
            {
                ((AppCompatActivity)activity).getSupportActionBar().setTitle(activity.getString(title));
            }

        }
    }
}