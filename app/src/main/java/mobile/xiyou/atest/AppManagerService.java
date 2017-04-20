package mobile.xiyou.atest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import static mobile.xiyou.atest.Rf.*;

/**
 * Created by user on 2017/4/13.
 */

public class AppManagerService extends Service{

    public static final String FILE_APPNAME="launchAppName";

    private String[] appList;
    private int launchIndex=-1;

    private AppManagerNative.Stub stub=new AppManagerNative.Stub() {
        @Override public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {}

        @Override
        public String getLaunchAppName() throws RemoteException {
            return handleGetLaunchAppName();
        }

        @Override
        public void startApp(String name) throws RemoteException {
            handleStartApp(name);
        }
    };

    public AppManagerService()
    {
        appList=new String[50];
        for (int i=0;i<appList.length;i++)
            appList[i]=null;
    }

    public String handleGetLaunchAppName()
    {
        if (launchIndex==-1)
            return null;

        return appList[launchIndex];
    }

    public void handleStartApp(String name)
    {
        for (int i=0;i<appList.length;i++)
        {
            if (name==appList[i])
            {
                return ;
            }
        }

        for (int i=0;i<appList.length;i++)
        {
            if (appList[i]==null)
            {
                launchIndex=i;
                appList[i]=name;
                break;
            }
        }

        try {
            FileOutputStream fos=openFileOutput(FILE_APPNAME,MODE_PRIVATE);
            fos.write((name+"/"+launchIndex).getBytes());
            //Log.e("xx",(name+"|"+launchIndex));
            fos.close();
            Intent i=new Intent(this,Class.forName("mobile.xiyou.atest.ActivityBase$A"+(launchIndex+1)));
            i.setFlags(0x10008000);
            startActivity(i);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx",e.toString());
        } catch (FileNotFoundException e) {
            Log.e("xx",e.toString());
        } catch (IOException e) {
            Log.e("xx",e.toString());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public static void createApp()
    {
        Class at=null;
        try {

        at=Class.forName("android.app.ActivityThread");
            setField(Looper.class,null,"sThreadLocal",new ThreadLocal<Looper>());
            setField(Looper.class,null,"sMainLooper",null);
            Log.e("xx","pid"+ invoke(at,null,"currentActivityThread"));
            Method []ms=at.getDeclaredMethods();
            for (int i=0;i<ms.length;i++)
            {
                if (ms[i].getName().equals("main"))
                {
                    ms[i].invoke(null,new Object[]{new String[]{}});
                    break;
                }
            }
    } catch (ClassNotFoundException e) {
        Log.e("xx",e.toString());
    } catch (InvocationTargetException e) {
            Log.e("xx",e.getCause().toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }
    }
}
