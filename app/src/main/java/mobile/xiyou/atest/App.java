package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.PathClassLoader;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static mobile.xiyou.atest.Rf.*;

/**
 * Created by admin on 2017/3/1.
 */

public class App {

    public static final String EXTRA_TARGET_ACTIVITY="an";
    public static final String EXTRA_ID="id";

    private PkgInfo info;
    private PathClassLoader loader;
    private HashMap<String,Activity> activities;
    private Application application;
    private Resources res;
    private AssetManager am;
    private Resources.Theme theme;
    private HashMap<Integer,Resources.Theme> themes;
    private int id=0;
    private boolean appAttached=false;

    public App(Context c,String packageName)
    {
        try {
            Context cc=c.createPackageContext(packageName, CONTEXT_IGNORE_SECURITY);
            String apkPath=cc.getPackageResourcePath();
            info=PkgInfo.getPackageArchiveInfo(apkPath, 1);

            //Init classes:
            loader=new PathClassLoader(apkPath,cc.getApplicationInfo().nativeLibraryDir,ClassLoader.getSystemClassLoader());
            //activities=new HashMap<>();
            if (info.info.applicationInfo.name!=null)
                application=(Application) loader.loadClass(info.info.applicationInfo.name).newInstance();
            else
            application=new Application();
            themes=new HashMap<>();

            //Load resources:
            am = AssetManager.class.newInstance();
            Method add = am.getClass().getMethod("addAssetPath", String.class);
            add.invoke(am, apkPath);
            res = new Resources(am, c.getResources().getDisplayMetrics(), c.getResources().getConfiguration());
            theme = res.newTheme();
            theme.setTo(cc.getTheme());
            theme.applyStyle(info.info.applicationInfo.theme, true);
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (InstantiationException e) {
            Log.e("xx",e.toString());
        } catch (InvocationTargetException e) {
            Log.e("xx",e.toString());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }
    }

    public void launch()
    {

    }

    public ClassLoader getLoader()
    {
        return loader;
    }

    public ActivityInfo getActInfo(String name)
    {
        ActivityInfo[] x=info.info.activities;
        for (int i=0;i<x.length;i++)
        {
            if (x[i].name.equals(name))
                return x[i];
        }

        return null;
    }

    public Resources getRes()
    {
        return res;
    }

    public AssetManager getAm()
    {
        return am;
    }

    public Resources.Theme getTheme()
    {
        return theme;
    }

    public Resources.Theme getTheme(int i)
    {
        return themes.get(i);
    }

    public void newTheme(int t,int i)
    {
        Resources.Theme x=res.newTheme();
        x.setTo(theme);
        x.applyStyle(t, true);
        themes.put(i,x);
    }

    public void setTheme(Resources.Theme t)
    {
        theme=t;
    }

    public void setId(int i)
    {
        id=i;
    }

    public int getId()
    {
        return id;
    }

    public Intent startActivityIntent(Context c,String name)
    {
        id++;
        Intent i=new Intent(c,TestActivity.class);
        i.putExtra("an",name);
        i.putExtra("id",id);
        return i;
    }

    public static String getIntentClassName(Intent i)
    {
        return i.getStringExtra("an");
    }

    public void solveIntent(ActivityBase c)
    {
        Intent i=c.getIntent();
        if (!appAttached)
            attachApplication(c);
        String x;
        if ((x=i.getStringExtra(EXTRA_TARGET_ACTIVITY))!=null) {
            Activity a=getActivity(x);
            c.setRealActivity(a);
            attachActivity(c,a);
        }
    }

    public Activity getActivity(String name)
    {
        try {
            return (Activity)loader.loadClass(name).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Application getApplication()
    {
        return application;
    }

    public PkgInfo getInfo()
    {
        return info;
    }

    public void attachApplication(Context c)
    {
        try {
            Method m = Application.class.getDeclaredMethod("attach", Context.class);
            m.setAccessible(true);
            m.invoke(application, c);
            appAttached=true;
        }catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (InvocationTargetException e) {
            Log.e("xx",e.toString());
        }
    }

    public void attachActivity(Activity base,Activity target)
    {
        try{
            Object thread=readField(Activity.class,base,"mMainThread"),token=readField(Activity.class,base,"mToken"),ident=readField(Activity.class,base,"mIdent"),intent=readField(Activity.class,base,"mIntent"),
                    mLastNonConfigurationInstances=readField(Activity.class,base,"mLastNonConfigurationInstances"),mCurrentConfig=readField(Activity.class,base,"mCurrentConfig"),mReferrer=readField(Activity.class,base,"mReferrer"),
                    mVoiceInteractor=readField(Activity.class,base,"mVoiceInteractor");
            ContextBase.init(thread,info.info.applicationInfo);
            Method ms[]=Activity.class.getDeclaredMethods();
            Method m=null;
            for (int i=0;i<ms.length;i++)
            {
                if (ms[i].getName().equals("attach"))
                {
                    m=ms[i];
                    m.setAccessible(true);
                    break;
                }
            }
            //m=Activity.class.getDeclaredMethod("attach",Context.class,thread.getClass(), Instrumentation.class, IBinder.class,
             //       int.class,Application.class,Intent.class, ActivityInfo.class,CharSequence.class,Activity.class,String.class,
             //       mLastNonConfigurationInstances.getClass(), Configuration.class,String.class,mVoiceInteractor.getClass());
            m.invoke(target,base,thread,new PachInstr((Instrumentation) readField(Activity.class,base,"mInstrumentation")),token,(int)ident,application,base.getIntent(),getActInfo(getIntentClassName(base.getIntent())),
                   "title",null,"id",mLastNonConfigurationInstances,mCurrentConfig,mReferrer,mVoiceInteractor);

            //m.invoke(target, new Object[]{base, null, new Instrumentation(), null, 0, getApplication(), base.getIntent(), info.activities[0], "xxx", getParent(), "00", null, null, "", null});
            //setField(Activity.class,target,"mWindow", readField(Activity.class,base,"mWindow"));
            //base.getWindow().setUiOptions(getActInfo(getIntentClassName(base.getIntent())).uiOptions);
            setField(Activity.class,target,"mWindow", base.getWindow());
            //setField(ContextThemeWrapper.class,target,"mTheme",getTheme());
            //setField(Activity.class,base,"mInstrumentation",new PachInstr(new Instrumentation()));
        } catch (InvocationTargetException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }
    }


}
