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


public class FragmentInfra extends SutFragment {

    private GestureDetector gesture;
    private static MainActivity act;
    private AdapterInfra imgAdapter;

    public static final FragmentInfra newInstance(MainActivity activity)
    {
        FragmentInfra f = new FragmentInfra();
        f.act = activity;
        f.page = 2;
        f.title = R.string.toolbar_title_infra;
        return f;
    }
    public void updateLight(String buffer) {

        byte[] b = buffer.getBytes();
        for ( int i=0; i<6; i++ ) {
            if ( b[i+20] == '1' ) imgAdapter.lights[i] = true;
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
        final View view = inflater.inflate(R.layout.fragment_infra, container, false);

        GridView gv = (GridView) view.findViewById(R.id.gridviewInfra);

        imgAdapter = new AdapterInfra(act);
        gv.setPadding(8,8,8, 584);
        gv.setAdapter(imgAdapter);


        return view;
    }
}
