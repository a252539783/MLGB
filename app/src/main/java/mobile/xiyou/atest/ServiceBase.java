package mobile.xiyou.atest;

import android.app.Service;
import android.content.ComponentName;
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
import java.util.HashMap;

import static mobile.xiyou.atest.Rf.*;

/**
 * Created by user on 2017/4/10.
 */

public class ServiceBase extends Service {

    private App app=null;
    private HashMap<String,Service> realServices=new HashMap<>();

    public ServiceBase()
    {
    }

    public Service getRealService(String name)
    {
        return realServices.get(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (app==null)
        {
            app=((MainApp)getApplication()).getApp();
        }
        Service r=app.solveIntent(this,intent);
        realServices.put(r.getClass().getName(),r);
        super.onStart(intent, startId);
        intent=intent.getParcelableExtra(App.EXTRA_OLD_INTENT);
        r.onStart(intent,startId);
        Log.e("xx","load service :"+realServices.size()+":"+r.getClass().getName());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //return realService.onUnbind(intent);

        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        //realService.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //realService.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (app==null)
        {
            app=((MainApp)getApplication()).getApp();
        }
        Service r=app.solveIntent(this,intent);
        realServices.put(r.getClass().getName(),r);
        //super.onStartCommand(intent, flags,startId);
        intent=intent.getParcelableExtra(App.EXTRA_OLD_INTENT);
        Log.e("xx","load service :"+realServices.size()+":"+r.getClass().getName());
        return r.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (app==null)
        {
            app=((MainApp)getApplication()).getApp();
        }
        Service r=app.solveIntent(this,intent);
        realServices.put(r.getClass().getName(),r);
        intent=intent.getParcelableExtra(App.EXTRA_OLD_INTENT);
        Log.e("xx","load service :"+realServices.size()+":"+r.getClass().getName());
        return r.onBind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
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


