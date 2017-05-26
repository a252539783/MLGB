package mobile.xiyou.atest.patches;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mobile.xiyou.atest.App;

import static mobile.xiyou.atest.Rf.*;

/**
 * Created by user on 2017/5/8.
 */

public class PatchThreadHandler implements Handler.Callback {

    public int LAUNCH_ACTIVITY;

    private Handler mbase;
    private App app;

    private Method handleLaunch=null;

    public PatchThreadHandler(Object base,App app)
    {
        this.app=app;
        mbase=(Handler)base;
        LAUNCH_ACTIVITY=(int)readField(mbase,"LAUNCH_ACTIVITY");
        Method []ms=app.currentThread().getClass().getDeclaredMethods();
        for (int i=0;i<ms.length;i++)
        {
            if (ms[i].getName()=="handleLaunchActivity")
            {
                handleLaunch=ms[i];
                handleLaunch.setAccessible(true);
                break;
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what==LAUNCH_ACTIVITY) {
            Object r = msg.obj;
            Intent i = (Intent) readField(r, "intent");
            String cls = app.getIntentClassName(i);
            if (cls == null) {
                cls = app.getInfo().mainClass;
            }
            i.setComponent(new ComponentName(app.getInfo().info.packageName, cls));

            ActivityInfo ai = app.getActInfo(cls);
            setField(r, "activityInfo", ai);
            //Log.e("xx", "launch activity" + ai.name + ":" + ai.applicationInfo.packageName);

            setField(r, "packageInfo", app.getLoadedApk());
            try {
                handleLaunch.invoke(app.currentThread(),r,null);
            } catch (IllegalAccessException e) {
                Log.e("xx",e.toString());
            } catch (InvocationTargetException e) {
                p(handleLaunch,e);
            }

            Activity activity=(Activity)readField(r,"activity");

            //invoke(activity,"attachBaseContext",new Class[]{Context.class},app.createActivityContext(activity));
            return true;
        }else


        mbase.handleMessage(msg);

        return true;
    }
}
