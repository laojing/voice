package cn.laojing.smarthome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by laojing on 3/10/16.
 */
public class FragmentSwitch extends SutFragment {

    private GestureDetector gesture;
    private static MainActivity act;
    private ImageAdapter imgAdapter;

    public static final FragmentSwitch newInstance(MainActivity activity)
    {
        FragmentSwitch f = new FragmentSwitch();
        act = activity;
        f.page = 0;
        f.title = R.string.toolbar_title_home;
        return f;
    }
    public void updateLight(String buffer) {
        byte[] b = buffer.getBytes();
        for ( int i=0; i<17; i++ ) {
            if ( b[i] == '1' ) imgAdapter.lights[i] = true;
            else imgAdapter.lights[i] = false;
        }
        imgAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView
            (
                    LayoutInflater inflater,
                    @Nullable ViewGroup container,
                    @Nullable Bundle savedInstanceState
            )
    {
        // The last two arguments ensure LayoutParams are inflated properly
        final View view = inflater.inflate(R.layout.fragment_switch, container, false);

        GridView gv = (GridView) view.findViewById(R.id.gridviewSwitch);
        imgAdapter = new ImageAdapter(act);
        gv.setAdapter(imgAdapter);

        return view;
    }
}
