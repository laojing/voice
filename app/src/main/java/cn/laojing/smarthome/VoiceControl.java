package cn.laojing.smarthome;

import android.os.Message;
import android.util.Log;

import com.baidu.tts.client.SpeechSynthesizer;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by laojing on 3/11/16.
 */
public class VoiceControl {

    private SpeechSynthesizer mSpeechSynthesizer;
    private VoiceActivity mActivity;
    private String[] jokes;

    public VoiceControl ( VoiceActivity act, SpeechSynthesizer speech ) {
        mActivity = act;
        mSpeechSynthesizer = speech;
        jokes = act.getResources().getStringArray(R.array.jokes);
    }

    public void runCommand ( String cmd ) {
        if ( cmd.equals(mActivity.getResources().getString(R.string.cmd_open)) ) {
            this.mSpeechSynthesizer.speak(mActivity.getResources().getString(R.string.cmd_response));
        } else if ( cmd.indexOf(mActivity.getResources().getString(R.string.cmd_joke)) >= 0 ) {
            this.mSpeechSynthesizer.speak(jokes[(int) (Math.random() * jokes.length)]);
        } else if ( cmd.indexOf(mActivity.getResources().getString(R.string.cmd_date)) >= 0 ) {
            Date cur = new Date(System.currentTimeMillis());

            Calendar c = Calendar.getInstance();
            this.mSpeechSynthesizer.speak("报告主人"
                    + c.get(Calendar.YEAR) + "年"
                    + c.get(Calendar.MONTH) + "月"
                    + c.get(Calendar.DAY_OF_MONTH) + "日");
        } else if ( cmd.indexOf(mActivity.getResources().getString(R.string.cmd_time)) >= 0 ) {
            Date cur = new Date(System.currentTimeMillis());

            Calendar c = Calendar.getInstance();
            this.mSpeechSynthesizer.speak("报告主人"
                    + c.get(Calendar.HOUR_OF_DAY) + "时"
                    + c.get(Calendar.MINUTE) + "分"
                    + c.get(Calendar.SECOND) + "秒");
        } else if ( cmd.equals("打开客厅灯") ) {
            //mActivity.myBinder.LightOn(0);
            this.mSpeechSynthesizer.speak("报告主人,任务完成");
        } else if ( cmd.equals("关闭客厅灯") ) {
            //mActivity.myBinder.LightOn(0);
            this.mSpeechSynthesizer.speak("报告主人,任务完成");
        } else if ( cmd.indexOf("关闭所有") >= 0 ) {
            mActivity.myBinder.LightCloseAll();
            this.mSpeechSynthesizer.speak("报告主人,任务完成");
        } else {
            Message msg = new Message();
            msg.what = 0x12;
            mActivity.myHandler.sendMessage(msg);
        }
    }
}
