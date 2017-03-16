package mobile.xiyou.atest;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Display;

import java.lang.ref.Reference;
import java.lang.reflect.Field;

import static mobile.xiyou.atest.Rf.*;

/**
 * Created by admin on 2017/3/2.
 */

public class ContextBase {

    /*
        r.packageInfo = ActivityThread.getPackageInfoNoCheck(
                                r.activityInfo.applicationInfo, r.compatInfo);
        ams.CompatibilityInfo compatibilityInfoForPackageLocked(ApplicationInfo ai) {
            return mCompatModePackages.compatibilityInfoForPackageLocked(ai);
        }
         */
    public static void init(Object thread,ApplicationInfo info)
    {
        try {
            //thread=readField(Class.forName("android.app.ActivityThread"),null,"currentActivityThread");
            Object compat=readField(Class.forName("android.content.res.CompatibilityInfo"),null,"DEFAULT_COMPATIBILITY_INFO");
            invoke(thread.getClass(),thread,"getPackageInfoNoCheck",new Class[]{ApplicationInfo.class,Class.forName("android.content.res.CompatibilityInfo")},info,compat);   //LoadedApk
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }
    }

    public static Object loadApk(Object thread,ApplicationInfo info)
    {
        Object loaded=null;
        try {
            //thread=readField(Class.forName("android.app.ActivityThread"),null,"currentActivityThread");
            Object compat=readField(Class.forName("android.content.res.CompatibilityInfo"),null,"DEFAULT_COMPATIBILITY_INFO");
            loaded=invoke(thread.getClass(),thread,"getPackageInfoNoCheck",new Class[]{ApplicationInfo.class,Class.forName("android.content.res.CompatibilityInfo")},info,compat);   //LoadedApk
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }

        return loaded;
    }

    public static Context createActivityContext(Object thread,Object la)
    {
        Context c=null;
        try {
            c=(Context)invoke(Class.forName("android.app.ContextImpl"),null,"createActivityContext",new Class[]{thread.getClass(),la.getClass(),int.class, Configuration.class},thread,la, Display.DEFAULT_DISPLAY,new Configuration());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }


        return c;
    }

    public static Context createActivityContext(Object thread,ApplicationInfo info)
    {
        Context c=null;
        Object la=loadApk(thread,info);
        try {
            c=(Context)invoke(Class.forName("android.app.ContextImpl"),null,"createActivityContext",new Class[]{thread.getClass(),la.getClass(),int.class, Configuration.class},thread,la, Display.DEFAULT_DISPLAY,new Configuration());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }

        return c;
    }
}
