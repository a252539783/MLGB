package mobile.xiyou.atest;

import android.content.pm.ApplicationInfo;
import android.util.Log;

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
    public static void init(Object thread)
    {
        try {
            thread=readField(Class.forName("android.app.ActivityThread"),null,"currentActivityThread");
            Object compat=readField(Class.forName("android.content.res.CompatibilityInfo"),null,"DEFAULT_COMPATIBILITY_INFO");
            invoke(Class.forName("android.app.ActivityThread"),thread,"getPackageInfoNoCheck",new Class[]{ApplicationInfo.class,compat.getClass()},new ApplicationInfo(),compat);   //LoadedApk
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }
    }
}
