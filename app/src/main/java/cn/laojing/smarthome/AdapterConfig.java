package cn.laojing.smarthome;

/**
 * Created by laojing on 5/22/16.
 */

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AdapterConfig extends BaseAdapter {

    private String[] lightNames;
    private int intcur = 0;
    private static MainActivity act;

    public AdapterConfig(MainActivity c) {
        //lightNames = (String[])bundle.getSerializable("names");

        act = c;
        Resources res = act.getResources();
        lightNames = res.getStringArray(R.array.config_names);
    }

    public int getCount() {
        return lightNames.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {

        RelativeLayout v;

        if(convertView == null) {
            v = (RelativeLayout)View.inflate(act, R.layout.switch_button, null);



            SutSwitchButton sb = (SutSwitchButton) v.findViewById(R.id.iv_item);
            sb.setOnChangeListener(new SutSwitchButton.OnChangeListener() {

                @Override
                public void onChange(SutSwitchButton sb, boolean state) {
                    // TODO Auto-generated method stub
                    Toast.makeText(act, state ? "开":"关", Toast.LENGTH_SHORT).show();
                    act.myBinder.ParameterChange( position, state );
                }
            });


        } else {
            v = (RelativeLayout)convertView;
        }
        //ImageView imageView = (ImageView)v.findViewById(R.id.iv_item);
        TextView textView = (TextView)v.findViewById(R.id.tv_item);
        SutSwitchButton sb = (SutSwitchButton) v.findViewById(R.id.iv_item);

        //imageView.setImageResource(R.drawable.cartclose);
        textView.setText(lightNames[position]);

        return v;

    }

}