package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mobile.xiyou.hook.ArtHook;
import mobile.xiyou.hook.OriginalMethod;

/**
 * Created by user on 2017/3/29.
 */

public class MainApp extends Application {

    private static App app=null;
    private String appName=null;
    private String processName=null;
    private int appid=0;
    private Application realapp;

    private static Method attachBase;
    private static OriginalMethod om=null;

    public MainApp()
    {
        try {
            attachBase=ContextThemeWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
            attachBase.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        }

    }

    public static void testAAA()
    {
        Log.e("xx","vm version+"+System.getProperty("java.vm.version"));
    }


    @Override
    public void onCreate() {
        super.onCreate();



        startService(new Intent(this,AppManagerService.class));

        ActivityManager mActivityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == Process.myPid()) {

                processName=appProcess.processName;
                break;
            }
        }
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
            //Log.e("xx",r[0]+r[1]);
            appName=r[0];
            appid=Integer.parseInt(r[1]);
        } catch (IOException e) {
            Log.e("xx",e.toString());
        }
        app=new App(this,appName,appid);
        realapp=app.getApplication();

        app.patchThread();
        patchContext();
    }

    private void patchContext()
    {
        try {
            om=ArtHook.hook(attachBase,MainApp.class.getDeclaredMethod("attachBaseContextHook",Object.class,Context.class),null);
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        }
    }

    public static void attachBaseContextHook(Object recv,Context newBase) {

        if (recv instanceof Activity)
        {
            newBase=app.getContext();
        }
            om.invoke(recv,newBase);
    }

    public App getApp()
    {
        return app;
    }

}
