package cn.laojing.smarthome;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class AdapterInfra extends BaseAdapter {
    private MainActivity act;
    private String[] lightNames;
    public boolean lights[] = new boolean[6];

    public AdapterInfra(MainActivity c) {
        act = c;
        Resources res = act.getResources();
        lightNames = res.getStringArray(R.array.infra_names);
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
    public View getView(int position, View convertView, ViewGroup parent) {

        RelativeLayout v;

        if(convertView == null) {
            v = (RelativeLayout)View.inflate(act, R.layout.switch_item, null);
        } else {
            v = (RelativeLayout)convertView;
        }
        ImageView imageView = (ImageView)v.findViewById(R.id.iv_item);
        TextView textView = (TextView)v.findViewById(R.id.tv_item);

        if ( lights[position] )
            imageView.setImageResource(R.drawable.infraclose);
        else
            imageView.setImageResource(R.drawable.infraopen);
        textView.setText(lightNames[position]);

        return v;

    }

}