package cn.laojing.smarthome;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.speech.VoiceRecognitionService;
import com.baidu.tts.answer.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by laojing on 3/11/16.
 */
public class VoiceActivity extends ListActivity implements RecognitionListener,SpeechSynthesizerListener {

    private SpeechRecognizer speechRecognizer;

    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";

    private VoiceControl mVoiceControl;
    private ListViewAdapter mListViewAdapter = new ListViewAdapter();
    private long speechEndTime = -1;
    private static final int EVENT_ERROR = 11;
    private ImageView imgBtn;

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x12) {
                start();
            }
        }

    };


    public CommandService.MyBinder myBinder;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (CommandService.MyBinder) service;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);
        setListAdapter(mListViewAdapter);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);

        initialEnv();
        initialTts();

        mSpeechSynthesizer.speak("语音输入系统启动");
        mVoiceControl = new VoiceControl(this, mSpeechSynthesizer);

        imgBtn = (ImageView)findViewById(R.id.imageView);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        Intent bindIntent = new Intent(this, CommandService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        //start();
    }



    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void initialTts() {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(this);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
                + LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId("6738489");
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("wnhOVvQB4ByQOLmfPelT1guS", "94bdeadd1880b2905e368d1defb6666b");
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(可以不使用，只是验证授权是否成功)
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess()) {
            Log.e("cn.laojing.smarthome","auth success!");
        } else {
            Log.e("cn.laojing.smarthome","auth failed!");
        }
        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能）
        int result =
                mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
    }

    @Override
    public void onSpeechStart(String utteranceId) {
        //print ( "onSpeedStart" );
    }
    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
        //print ( "onSynthesizeDataArrived" );
    }
    @Override
    public void onSynthesizeStart(String utteranceId) {
        printe("onSynthesizeStart");
    }
    @Override
    public void onSynthesizeFinish(String utteranceId) {
        //print ( "onSynthesizeFinish" );
    }
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        //print ( "onSpeechProgress" );
    }

    @Override
    public void onSpeechFinish(String utteranceId) {
        printe("onSpeechFinish");

        Message msg = new Message();
        msg.what = 0x12;
        myHandler.sendMessage(msg);
    }

    @Override
    public void onError(String utteranceId, SpeechError error) {
        //print ( "onError" );
    }

    private void printe(String msg) {
        Log.e("cn.laojing.smarthome", "----" + msg);
    }




    private void start() {

        print("可以开始发音");
        Intent intent = new Intent();
        bindParams(intent);

        speechEndTime = -1;
        speechRecognizer.startListening(intent);

    }

    @Override
    protected void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        print("准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        print("检测到用户的已经开始说话");
    }
    @Override
    public void onBufferReceived(byte[] buffer) {

    }
    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        print("检测到用户的已经停止说话");
    }

    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        print("识别失败：" + sb.toString());
        start();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
        }
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        mListViewAdapter.setCmds(nbest.get(0));
        mVoiceControl.runCommand(nbest.get(0));
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                print("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }
    private void print(String msg) {
        Log.e("cn.laojing.smarthome", "----" + msg);
        mListViewAdapter.setStatus(msg);
    }


    class ListViewAdapter extends BaseAdapter {
        private String[] cmds = {"","","","","","","",""};
        int count = 8;

        public void setStatus ( String msg ) {
            cmds[0] = msg;
            notifyDataSetChanged();
        }
        public void setCmds ( String cmd ) {
            for(int i=6; i>=1; i-- ) {
                cmds[i+1] = cmds[i];
            }
            cmds[1] = cmd;
            notifyDataSetChanged();
        }

        public int getCount() {
            return count;
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View view, ViewGroup parent) {
            TextView mTextView;
            if (view == null) {
                mTextView = new TextView(VoiceActivity.this);
                mTextView.setTextSize(20f);
                mTextView.setHeight(80);
            } else {
                mTextView = (TextView) view;
            }
            mTextView.setText(cmds[position]);
            return mTextView;
        }
    }
}
