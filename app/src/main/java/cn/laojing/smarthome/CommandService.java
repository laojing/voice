package cn.laojing.smarthome;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by laojing on 3/12/16.
 */
public class CommandService extends Service {

    public static final String TAG  = "cn.laojing.smarthome";

    private String ip               = "115.28.215.22";
    private int port                = 8997;
    private int timeout             = 10000;
    private boolean threadDisable   = false;

    private Socket client = null;
    private PrintWriter out;
    private BufferedReader in;
    private boolean sendWait = false;
    private boolean lights[] = new boolean[26];
    private boolean cmds[] = new boolean[18];

    private MyBinder mBinder        = new MyBinder();

    public void conn() {
        try {
            Log.i(TAG, "连接中……");
            client = new Socket(ip, port);
            client.setSoTimeout(timeout);// 设置阻塞时间
            Log.e(TAG, "连接成功");
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream())), true);
            Log.e(TAG, "输入输出流获取成功");
        } catch (UnknownHostException e) {
            Log.e(TAG, "连接错误UnknownHostException 重新获取");
            e.printStackTrace();
            conn();
        } catch (IOException e) {
            Log.e(TAG, "连接服务器io错误");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "连接服务器错误Exception" + e.getMessage());
            e.printStackTrace();
        }
    }
    public void send(boolean last) {
        String mess = "";
        try {
            if ( last )  mess = "haha";
            else {
                for ( int i=0; i<17; i++ ) {
                    if ( cmds[i] ) {
                        mess = String.valueOf(i);
                        cmds[i] = false;
                        break;
                    }
                }
            }
            if (client != null) {
                out.println(mess);
                out.flush();
            } else {
                conn();
            }
        } catch (Exception e) {
            Log.e(TAG, "send error");
        } finally {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Command Serivce onCreate() executed");

        new Thread(new Runnable() {
            @Override
            public void run() {
                conn();

                char[] buffer = new char[30];
                while (!threadDisable) {
                    try {
                        if (client != null) {
                            int res = 0;
                            send(false);
                            while ((res = in.read(buffer,0,26)) > 0 ) {
                                if ( res == 26 ) {
                                    String line = "";
                                    for ( int i=0; i<26; i++ ) {
                                        if ( buffer[i] == '1' ) line += "1";
                                        else line += "0";
                                        lights[i] = buffer[i] == '1';
                                    }

                                    Intent intent=new Intent();
                                    intent.putExtra("lights", line);
                                    intent.setAction("cn.laojing.smarthome.CommandService");
                                    sendBroadcast(intent);
                                }
                                Thread.sleep(2000);
                                send(false);
                            }

                        } else {
                            Log.e(TAG, "没有可用连接");
                            conn();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "数据接收错误" + e.getMessage());
                        send(false);
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadDisable = true;
        try {
            if (client != null) {
                send(true);
                in.close();
                out.close();
                client.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "close err");
        }
        Log.e(TAG, "Command Service onDestroy() executed");
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        Log.e(TAG, "Command Service onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class MyBinder extends Binder {

        public void LightOn ( int index ) {
            cmds[index] = true;
            //send(String.valueOf(index));
        }
        public void LightCloseAll () {
            for ( int i=0; i<17; i++ ) {
                if ( lights[i] ) cmds[i] = true;
                else cmds[i] = false;
            }
        }

    }
}
