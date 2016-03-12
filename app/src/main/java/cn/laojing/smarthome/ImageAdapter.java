package cn.laojing.smarthome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by laojing on 3/9/16.
 */
public class ImageAdapter extends BaseAdapter {
    private String[] lightNames;
    private MainActivity act;
    public boolean lights[] = new boolean[18];

    public ImageAdapter(MainActivity c) {
        act = c;
        Resources res = act.getResources();
        lightNames = res.getStringArray(R.array.light_names);
        for ( int i=0; i<17; i++ ) {
            lights[i] = false;
        }
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

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout vv = (RelativeLayout)v;
                    TextView textView = (TextView)vv.findViewById(R.id.tv_item);


                    int find = 0;
                    for ( int i=0; i<lightNames.length; i++ ) {
                        if ( lightNames[i].equals(textView.getText()) ) {
                            find = i;
                            act.myBinder.LightOn(find);
                            break;
                        }
                    }
                    Log.e("cn.laojing.smarthome", "===" + find);
                }
            });

        } else {
            v = (RelativeLayout)convertView;
        }

        ImageView imageView = (ImageView)v.findViewById(R.id.iv_item);
        TextView textView = (TextView)v.findViewById(R.id.tv_item);

        if ( lights[position] )
            imageView.setImageResource(R.drawable.lightopen_dark);
        else
            imageView.setImageResource(R.drawable.lightclose_dark);
        textView.setText(lightNames[position]);


        return v;

    }

}