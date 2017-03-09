package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentController;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

import static android.view.Window.FEATURE_ACTION_BAR;
import static android.view.Window.FEATURE_ACTION_BAR_OVERLAY;

public class MainActivity extends Activity implements Runnable,View.OnClickListener {

    public MainActivity() {
        super();
    }

    TextView test;

    private Resources res;
    private AssetManager am;
    private Activity activity;
    private Resources.Theme theme;
    private PackageInfo info;
    private Context cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bbb).setOnClickListener(this);
        Log.e("xx",getWindow().hasFeature(Window.FEATURE_ACTION_BAR)+"xx");
    }



    @Override
    public void onClick(View v) {
        AppManager.get().startApp(this,"com.example.share4_15",0);
    }

    public void test(Bundle savedInstanceState)
    {
        try {

            cc = createPackageContext("a.a.zzz", CONTEXT_IGNORE_SECURITY | CONTEXT_INCLUDE_CODE);
            //  test.setText(c.getClassLoader().toString()+"\n");
            ClassLoader c1;
            String apkpath = createPackageContext("a.a.zzz", CONTEXT_IGNORE_SECURITY).getPackageResourcePath();
            info = getPackageManager().getPackageArchiveInfo(apkpath, 1);
            String className = info.activities[0].name;
            c1 = new DexClassLoader(apkpath, "/data/data/mobile.xiyou.atest/files", info.applicationInfo.nativeLibraryDir, ClassLoader.getSystemClassLoader());
            activity = (Activity) c1.loadClass(className).getConstructor(new Class[]{}).newInstance(new Object[]{});


            am = AssetManager.class.newInstance();
            Method add = am.getClass().getMethod("addAssetPath", String.class);
            add.invoke(am, apkpath);
            res = new Resources(am, getResources().getDisplayMetrics(), getResources().getConfiguration());
            theme = res.newTheme();
            theme.setTo(cc.getTheme());
            theme.applyStyle(info.applicationInfo.theme, true);
        } catch (ClassNotFoundException e) {
            //test.setText(test.getText()+e.toString()+"\n");
            Log.e("xx", e.toString());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("xx", e.toString());
        } catch (NoSuchMethodException e) {
            Log.e("xx", e.toString());
        } catch (InstantiationException e) {
            Log.e("xx", e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx", e.toString());
        } catch (InvocationTargetException e) {
            Log.e("xx", e.toString());
        }


        Method onc = null, attach = null, start = null;
        Field thread = null, mLastNonConfigurationInstances = null, mVoiceInteractor = null, window = null;
        try {
            thread = Activity.class.getDeclaredField("mMainThread");
            thread.setAccessible(true);
            window = Activity.class.getDeclaredField("mWindow");
            window.setAccessible(true);
            mLastNonConfigurationInstances = Activity.class.getDeclaredField("mLastNonConfigurationInstances");
            mLastNonConfigurationInstances.setAccessible(true);
            mVoiceInteractor = Activity.class.getDeclaredField("mVoiceInteractor");
            mVoiceInteractor.setAccessible(true);
            //attach=Activity.class.getDeclaredMethod("attach",new Class[]{Context.class,field.get(this).getClass(),Instrumentation.class,
            //        IBinder.class,Object.class,Application.class, Intent.class,ActivityInfo.class,CharSequence.class,Activity.class,
            //        String.class,Object.class,
            //        Configuration.class,String.class,Object.class});
            Method[] xxx = Activity.class.getDeclaredMethods();
            for (int i = 0; i < xxx.length; i++) {
                if (xxx[i].getName().equals("attach")) {
                    attach = xxx[i];
                    attach.setAccessible(true);
                    break;
                }
            }
            onc = Activity.class.getDeclaredMethod("performCreate", new Class[]{Bundle.class, PersistableBundle.class});
            onc.setAccessible(true);
            start = Activity.class.getDeclaredMethod("performStart", new Class[]{});
            start.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.e("xx", e.toString());
        } catch (NoSuchFieldException e) {
            Log.e("xx", e.toString());
        }
        try {
            attach.invoke(activity, new Object[]{this, null, new Instrumentation(), null, 0, getApplication(), getIntent(), info.activities[0], "xxx", getParent(), "00", null, null, "", null});
            window.set(activity, window.get(this));

            onc.invoke(activity, new Object[]{savedInstanceState, null});
            start.invoke(activity, new Object[]{});
        } catch (IllegalAccessException e) {
            Log.e("xx", e.toString());
        } catch (InvocationTargetException e) {
            Log.e("xx", e.getCause().toString());
        }

    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);




/*        Method attach= null;
        try {
            attach =ContextWrapper.class.getDeclaredMethod("attachBaseContext",new Class[] { Context.class });
            attach.setAccessible(true);
        } catch (NoSuchMethodException e) {
            //Log.e("xx",e.toString());
        }
        try {
                attach.invoke(activity,new Object[]{newBase});
        } catch (IllegalAccessException e) {
            //Log.e("xx",e.toString());
        } catch (InvocationTargetException e) {
            //Log.e("xx",e.toString());
        }*/
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Instrumentation x=new Instrumentation();
        x.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN,500,500,0));
        x.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_UP,500,500,0));
    }
}
