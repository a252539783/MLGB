package mobile.xiyou.atest;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by user on 2017/4/10.
 */

public class ServiceBase extends Service {

    private Intent intent;
    private Service realService;

    public ServiceBase()
    {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("asd","222222");
       // Log.e("xx",AppManager.get().getApp(0)+"a");
        attachService();
        realService.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
     //   this.intent=intent;
        super.onStart(intent, startId);
        realService.onStart(intent,startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return realService.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        realService.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realService.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return realService.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return realService.onBind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        realService.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        realService.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        realService.onTrimMemory(level);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        realService.onTaskRemoved(rootIntent);
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);

        Class ServiceClass= null;
        try {
            ServiceClass = Class.forName("android.app.Service");
            Method[] methods=ServiceClass.getMethods();
            Method dump=null;
            for(int i=0;i<methods.length;i++)
            {
                if(methods[i].getName().equals("attach"))
                {
                    dump=methods[i];
                    break;
                }
            }
            dump.setAccessible(true);

            dump.invoke(realService,fd,writer,args);
        } catch (ClassNotFoundException e) {
            Log.e("dump",e.toString());

        } catch (InvocationTargetException e) {
            Log.e("dump",e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e("dump",e.toString());
            e.printStackTrace();
        }
    }


    public void attachService()
    {
        try {
//            realService= (Service) getClassLoader().loadClass(ServiceProxy.servicename.get(ServiceProxy.ServiceNum)).newInstance();
            realService= (Service) getClassLoader().loadClass(intent.getComponent().getClassName()).newInstance();
            Log.e("service",realService+"--");
            Binder token=new Binder();

            Class ServiceClass=Class.forName("android.app.Service");
            Method[] methods=ServiceClass.getMethods();
            Method attach=null;
            for(int i=0;i<methods.length;i++)
            {
                if(methods[i].getName().equals("attach"))
                {
                    attach=methods[i];
                    break;
                }
            }

            Class ActivityThreadClass=Class.forName("android.app.ActivityThread");
            Field CurrentActivityThreadField=ActivityThreadClass.getDeclaredField("sCurrentActivityThread");
            CurrentActivityThreadField.setAccessible(true);
            Object currentActivityThread=CurrentActivityThreadField.get(null);

//            attach(
//            Context context,
//            ActivityThread thread, String className, IBinder token,
//            Application application, Object activityManager) {
//            attach.invoke(realService,TestApp.context,currentActivityThread,ServiceProxy.servicename.get(ServiceProxy.ServiceNum),token,getApplication(),TestApp.proxy);
            attach.invoke(realService,TestApp.context,currentActivityThread,intent.getComponent().getClassName(),token,getApplication(),TestApp.proxy);

        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.e("attachService",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("attachService",e.toString());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e("attachService",e.toString());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.e("attachService",e.toString());
            e.printStackTrace();
        }
    }

    public static class Service1 extends ServiceBase{
        public Service1() {
            super();
        }
    }

    public static class Service2 extends ServiceBase{
        public Service2() {
            super();
        }
    }

    public static class Service3 extends ServiceBase{
        public Service3() {
            super();
        }
    }

    public static class Service4 extends ServiceBase{
        public Service4() {
            super();
        }
    }

    public static class Service5 extends ServiceBase{
        public Service5() {
            super();
        }
    }

}


