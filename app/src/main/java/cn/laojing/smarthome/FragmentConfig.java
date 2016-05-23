package cn.laojing.smarthome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

public class FragmentConfig extends SutFragment {

    private GestureDetector gesture;
    private static MainActivity act;

    public static final FragmentConfig newInstance(MainActivity activity)
    {
        FragmentConfig f = new FragmentConfig();
        f.act = activity;
        f.page = 4;
        f.title = R.string.toolbar_title_config;
        return f;
    }

    public void updateLight(String buffer) {

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
        final View view = inflater.inflate(R.layout.fragment_config, container, false);

        GridView gv = (GridView) view.findViewById(R.id.gridviewConfig);
        gv.setAdapter(new AdapterConfig(act));
        gv.setPadding(8,8,8,8);

        return view;
    }
}
