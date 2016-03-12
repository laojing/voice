package cn.laojing.smarthome;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class FragmentCart extends SutFragment {

    private GestureDetector gesture;
    private static MainActivity act;

    public static final FragmentCart newInstance(MainActivity activity)
    {
        FragmentCart f = new FragmentCart();
        f.act = activity;
        f.page = 1;
        f.title = R.string.toolbar_title_cart;
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
        final View view = inflater.inflate(R.layout.fragment_cart, container, false);

        GridView gv = (GridView) view.findViewById(R.id.gridviewCart);
        gv.setAdapter(new AdapterCart(act));
        gv.setPadding(8,8,8, 308);

        //根据父窗体getActivity()为fragment设置手势识别
        gesture = new GestureDetector(act, new SutSwipeListener(act));
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
