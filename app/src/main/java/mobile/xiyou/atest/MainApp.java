package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import mobile.xiyou.atest.patches.ActivityManagerHook;
import mobile.xiyou.atest.patches.Libcore_;
import mobile.xiyou.atest.patches.NotificationManagerHook;
import mobile.xiyou.hook.ArtHook;
import mobile.xiyou.hook.OriginalMethod;

/**
 * Created by user on 2017/3/29.
 */

public class MainApp extends Application {

    private static App app=null;

    //loaded app's name
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



        startService(new Intent(this,AppManagerService.class));        //start the manager service

        //we should judge whether current process is our application's process by the processName
        ActivityManager mActivityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == Process.myPid()) {

                processName=appProcess.processName;
                break;
            }
        }

        //if current process isn't our application
        if (!processName.equals("mobile.xiyou.atest")&&!processName.equals("mobile.xiyou.atest:manager"))
        {
            //Log.e("xx",processName);
            initApp();
        }
    }

    //init a loaded app
    private void initApp()
    {

        //int the manage service,we hava already save the appinfo that will be loaded,so read it here
        byte[] cache=new byte[300];
        for (int i=0;i<300;i++)
            cache[i]=0;
        try {
            FileInputStream fis=openFileInput(AppManagerService.FILE_APPNAME);
            int n=fis.available();
            fis.read(cache,0,fis.available());
            String r[]=new String(cache,0,n).split("/");
            appName=r[0];
            appid=Integer.parseInt(r[1]);
        } catch (IOException e) {
            Log.e("xx",e.toString());
        }


        app=new App(this,appName,appid);
        realapp=app.getApplication();

        Libcore_.patch(this);
        ActivityManagerHook.patch();
        NotificationManagerHook.patch();
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
            //newBase=app.createActivityContext((Activity)recv);
            newBase=app.getContext();
            Log.e("xx","context set");
        }
            om.invoke(recv,newBase);
    }

    public App getApp()
    {
        return app;
    }

}
