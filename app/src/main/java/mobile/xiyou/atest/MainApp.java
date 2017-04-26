package mobile.xiyou.atest;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by user on 2017/3/29.
 */

public class MainApp extends Application {

    private App app=null;
    private String appName=null;
    private String processName=null;
    private int appid=0;

    public MainApp()
    {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ActivityManager mActivityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == Process.myPid()) {

                processName=appProcess.processName;
                break;
            }
        }

        startService(new Intent(this,AppManagerService.class));

        if (!processName.equals("mobile.xiyou.atest")&&!processName.equals("mobile.xiyou.atest:manager"))
        {
            Log.e("xx",processName);
            initApp();
        }
    }

    private void initApp()
    {
        byte[] cache=new byte[300];
        for (int i=0;i<300;i++)
            cache[i]=0;
        try {
            FileInputStream fis=openFileInput(AppManagerService.FILE_APPNAME);
            int n=fis.available();
            fis.read(cache,0,fis.available());
            String r[]=new String(cache,0,n).split("/");
            Log.e("xx",r[0]+r[1]);
            appName=r[0];
            appid=Integer.parseInt(r[1]);
        } catch (IOException e) {
            Log.e("xx",e.toString());
        }
        app=new App(this,appName,appid);
    }

    public App getApp()
    {
        return app;
    }
}
