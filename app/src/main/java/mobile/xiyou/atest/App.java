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
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
    private ClassLoader loader,defaultLoader;
    private HashMap<String,Activity> activities;
    private Application application;
    private Resources res;
    private AssetManager am;
    private Resources.Theme theme;
    private HashMap<Integer,Resources.Theme> themes;
    private Context context;
    private int id=0;
    private boolean appAttached=false;
    private Object mThread=null,loadedApk=null;

    public App(Context c,String packageName)
    {
        context=c;
        defaultLoader=c.getClassLoader();
        try {
            mThread=invoke(Class.forName("android.app.ActivityThread"),null,"currentActivityThread",new Class[]{});

            Context cc=c.createPackageContext(packageName, CONTEXT_IGNORE_SECURITY);

            String apkPath=cc.getPackageResourcePath();
            info=PkgInfo.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES|PackageManager.GET_UNINSTALLED_PACKAGES|PackageManager.GET_META_DATA|PackageManager.GET_SHARED_LIBRARY_FILES);


            //Init
            info.info.applicationInfo.dataDir="/data/data/"+info.info.packageName+"";
            loadedApk=ContextBase.loadApk(mThread,info.info.applicationInfo);
            //Log.e("xx","xx"+(readField(loadedApk.getClass(),loadedApk,"mPackageName")==null));
            //invoke(loadedApk.getClass(),loadedApk,"makeApplication",new Class[]{boolean.class,Instrumentation.class},false,null);
            //cc=ContextBase.createActivityContext(mThread,loadedApk);
            //context=cc;

            loader=new PathClassLoader(apkPath,cc.getApplicationInfo().nativeLibraryDir,ClassLoader.getSystemClassLoader());
            ClassLoader old=c.getClassLoader();
            setField(ClassLoader.class,loader,"parent",old.getParent());
            setField(ClassLoader.class,old,"parent",loader);
            //loader=c.getClassLoader();
            Class AT=Class.forName("android.app.ActivityThread");
            Map packages=(Map)readField(AT,mThread,"mPackages");
            packages.put(info.info.packageName,new WeakReference<Object>(loadedApk));
            setField(loadedApk.getClass(),loadedApk,"mClassLoader",loader);

            if (info.info.applicationInfo.className!=null) {
                application = (Application) loader.loadClass(info.info.applicationInfo.className).newInstance();
                Log.e("xx","application:"+info.info.applicationInfo.className);
            }
            else
            application=new Application();
            setField(loadedApk.getClass(),loadedApk,"mApplication",application);
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
            Log.e("xx","in app init:"+e.getCause().toString());
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

    public void solveIntent(ActivityBase c){
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
            ClassLoader l=(ClassLoader) invoke(loadedApk.getClass(),loadedApk,"getClassLoader",new Class[]{});
            //l=context.getClassLoader();
            return (Activity) l.loadClass(name).newInstance();
        } catch (InstantiationException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
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
            context=new ContextBase(c,mThread,loadedApk,this,false);
            Method m = Application.class.getDeclaredMethod("attach", Context.class);
            m.setAccessible(true);
            m.invoke(application, context);
            setField(Application.class,application,"mLoadedApk",loadedApk);
            invoke(Application.class,application,"onCreate",new Class[]{});
            appAttached=true;
            if (loader!=readField(loadedApk.getClass(),loadedApk,"mClassLoader"))
            {
                //loader=defaultLoader;
                Log.e("xx","change");
            }
        }catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (InvocationTargetException e) {
            Log.e("xx","in application attach"+e.getCause().toString());
        }
    }

    public void attachActivity(Activity base,Activity target){
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
            m.invoke(target,context,thread,new PachInstr((Instrumentation) readField(Activity.class,base,"mInstrumentation")),token,(int)ident,application,base.getIntent(),getActInfo(getIntentClassName(base.getIntent())),
                   "title",null,"id",mLastNonConfigurationInstances,mCurrentConfig,mReferrer,mVoiceInteractor);

            //m.invoke(target, new Object[]{base, null, new Instrumentation(), null, 0, getApplication(), base.getIntent(), info.activities[0], "xxx", getParent(), "00", null, null, "", null});
            //setField(Activity.class,target,"mWindow", readField(Activity.class,base,"mWindow"));
            //base.getWindow().setUiOptions(getActInfo(getIntentClassName(base.getIntent())).uiOptions);
            setField(Activity.class,target,"mWindow", base.getWindow());
            //setField(ContextThemeWrapper.class,target,"mTheme",getTheme());
            //setField(Activity.class,base,"mInstrumentation",new PachInstr(new Instrumentation()));
        }catch (InvocationTargetException e) {
            Log.e("xx","in acitivity attach:"+e.getCause().toString());
        }  catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }
    }


}
