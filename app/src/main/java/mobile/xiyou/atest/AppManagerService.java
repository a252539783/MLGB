package mobile.xiyou.atest;

import android.app.ActivityManager;
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
import java.util.List;

import static mobile.xiyou.atest.Rf.*;

/**
 * Created by user on 2017/4/13.
 */

public class AppManagerService extends Service implements Runnable{

    public static final String FILE_APPNAME="launchAppName";
    public static final int RUN_MAX=50;

    private HashMap<Integer,Integer> taskList,loadList;
    private HashMap<String,Integer> appList;
    private boolean []runList;
    private int launchIndex=-1;
    private String launchAppName=null;

    private ActivityManager am=null;

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

    @Override
    public void run() {
        while (true)
        {
            List<ActivityManager.AppTask> rapps=am.getAppTasks();

            for (int i=0;i<rapps.size();i++)
            {
                Log.e("xx","task"+rapps.get(i).getTaskInfo().id);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public AppManagerService()
    {
        appList=new HashMap<>();
        taskList=new HashMap<>();
        loadList=new HashMap<>();
        runList=new boolean[RUN_MAX];
        for (int i=0;i<RUN_MAX;i++)
        {
            runList[i]=false;
        }
    }

    public String handleGetLaunchAppName()
    {
        return launchAppName;
    }

    public void handleStartApp(String name)
    {
        updateAppList();

//taskid   launchId   pkg
        launchAppName=name;
        Integer taskId=appList.get(name);
        if (taskId!=null&&runList[taskList.get(taskId)])
        {
            startRunningApp(taskId);
            return ;
        }

        for (int i=0;i<RUN_MAX;i++)
        {
            if (!runList[i]) {
                launchIndex = i;
                break;
            }
        }



        try {
            FileOutputStream fos=openFileOutput(FILE_APPNAME,MODE_PRIVATE);
            fos.write((name+"/"+launchIndex).getBytes());
            Log.e("xx",(name+"|"+launchIndex));
            fos.close();
            Intent i=new Intent(this,Class.forName("mobile.xiyou.atest.ActivityBase$A"+(launchIndex+1)));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
            startActivity(i);
            //Log.e("xx",""+((ActivityManager)getSystemService(ACTIVITY_SERVICE)).getAppTasks().get(0).getTaskInfo().topActivity.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx",e.toString());
        } catch (FileNotFoundException e) {
            Log.e("xx",e.toString());
        } catch (IOException e) {
            Log.e("xx",e.toString());
        }
    }

    private void startRunningApp(int taskId)
    {
        List<ActivityManager.AppTask> rapps=am.getAppTasks();
        ActivityManager.AppTask target=null;
        for (int i=0;i<rapps.size();i++)
        {
            if (rapps.get(i).getTaskInfo().id==taskId)
            {
                target=rapps.get(i);
                break;
            }
        }
        startActivity(target.getTaskInfo().baseIntent);
        //am.moveTaskToFront(taskId,0);
    }

    private void updateAppList()
    {
        List<ActivityManager.AppTask> rapps=am.getAppTasks();

        for (int i=0;i<RUN_MAX;i++)
        {
            runList[i]=false;
        }

        for (int i=0;i<rapps.size();i++)
        {
            if (!taskList.containsKey(rapps.get(i).getTaskInfo().id))
            {
                taskList.put(rapps.get(i).getTaskInfo().id,launchIndex);
                appList.put(launchAppName,rapps.get(i).getTaskInfo().id);
                Log.e("xx","+  "+rapps.get(i).getTaskInfo().id);
            }

            int launchI=taskList.get(rapps.get(i).getTaskInfo().id);

            if (launchI!=-1)
                runList[launchI]=true;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (am==null) {
            am = ((ActivityManager) getSystemService(ACTIVITY_SERVICE));
            List<ActivityManager.AppTask> rapps = am.getAppTasks();

            for (int i = 0; i < rapps.size(); i++) {
                taskList.put(rapps.get(i).getTaskInfo().id, -1);
            }
        }
        //new Thread(this).start();
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
