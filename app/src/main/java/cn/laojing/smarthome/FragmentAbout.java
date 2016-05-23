package cn.laojing.smarthome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class FragmentAbout extends SutFragment {

    private GestureDetector gesture;
    private static MainActivity act;

    public static final FragmentAbout newInstance(MainActivity activity)
    {
        FragmentAbout f = new FragmentAbout();
        f.act = activity;
        f.page = 5;
        f.title = R.string.toolbar_title_about;
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
        final View view = inflater.inflate(R.layout.fragment_about, container, false);


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
