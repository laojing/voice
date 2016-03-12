package cn.laojing.smarthome;

import android.app.Fragment;

/**
 * Created by laojing on 3/11/16.
 */
public abstract class SutFragment extends Fragment {
    public int page;
    public int title;

    public abstract void updateLight(String buffer);
}
