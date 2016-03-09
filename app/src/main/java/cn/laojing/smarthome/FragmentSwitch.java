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

/**
 * Created by laojing on 3/10/16.
 */
public class FragmentSwitch extends Fragment {

    private GestureDetector gesture;

    public static final FragmentSwitch newInstance()
    {
        FragmentSwitch f = new FragmentSwitch();
        return f;
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
        gv.setAdapter(new ImageAdapter(getActivity()));


        //根据父窗体getActivity()为fragment设置手势识别
        gesture = new GestureDetector(this.getActivity(), new SutSwipeListener(getActivity(),0));
        //为fragment添加OnTouchListener监听器
        gv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);//返回手势识别触发的事件
            }
        });

        return view;
    }

}
