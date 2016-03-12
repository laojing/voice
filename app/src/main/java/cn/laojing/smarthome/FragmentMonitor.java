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


public class FragmentMonitor extends SutFragment {

    private GestureDetector gesture;
    private static MainActivity act;

    public static final FragmentMonitor newInstance(MainActivity activity)
    {
        FragmentMonitor f = new FragmentMonitor();
        f.act = activity;
        f.page = 3;
        f.title = R.string.toolbar_title_monitor;
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
        final View view = inflater.inflate(R.layout.fragment_monitor, container, false);

        //根据父窗体getActivity()为fragment设置手势识别
        gesture = new GestureDetector(this.getActivity(), new SutSwipeListener(act));
        //为fragment添加OnTouchListener监听器
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);//返回手势识别触发的事件
            }
        });

        return view;
    }
}
