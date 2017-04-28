package mobile.xiyou.atest;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by admin on 2017/3/7.
 */

public class PachInstr extends Instrumentation {

    private Instrumentation base;
    private App app;

    public PachInstr(Instrumentation x,App app)
    {
        base=x;
        this.app=app;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        intent=app.startActivityIntent(intent);
        Object x=null;
        try {
            Method m = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            x=m.invoke(base,app.getMContext(), contextThread, token, target, intent, requestCode, options);
        }catch (InvocationTargetException e)
        {
            Log.e("xx","in startActivity:"+e.getCause().toString());
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }

        if (x!=null) {
            Log.e("xx","start for result");
            return (ActivityResult) x;

        }

        Log.e("xx","start no result");
        return null;
    }

    @Override
    public String toString() {
        return "sb";
    }

    //public void execStartActivities(Context who, IBinder contextThread,IBinder token, Activity target, Intent[] intents, Bundle options) {

    //public void execStartActivitiesAsUser(Context who, IBinder contextThread, IBinder token, Activity target, Intent[] intents, Bundle options, int userId) {

    //public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, String target,Intent intent, int requestCode, Bundle options) {

    //public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,Intent intent, int requestCode, Bundle options, UserHandle user) {

    //public ActivityResult execStartActivityAsCaller(Context who, IBinder contextThread, IBinder token, Activity target,Intent intent, int requestCode, Bundle options, boolean ignoreTargetSecurity,int userId) {

    //public void execStartActivityFromAppTask(Context who, IBinder contextThread, IAppTask appTask,Intent intent, Bundle options) {



    @Override
    public void onCreate(Bundle arguments) {
        base.onCreate(arguments);
    }

    @Override
    public void start() {
        base.start();
    }

    @Override
    public void onStart() {
        base.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        return base.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
        base.sendStatus(resultCode, results);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        base.finish(resultCode, results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
        base.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
        base.startPerformanceSnapshot();
    }

    @Override
    public void endPerformanceSnapshot() {
        base.endPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {
        base.onDestroy();
    }

    @Override
    public Context getContext() {
        return base.getContext();
    }

    @Override
    public ComponentName getComponentName() {
        return base.getComponentName();
    }

    @Override
    public Context getTargetContext() {
        return base.getTargetContext();
    }

    @Override
    public boolean isProfiling() {
        return base.isProfiling();
    }

    @Override
    public void startProfiling() {
        base.startProfiling();
    }

    @Override
    public void stopProfiling() {
        base.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {
        base.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {
        base.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {
        base.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {
        base.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        return base.startActivitySync(intent);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
        return base.addMonitor(cls, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
        return base.addMonitor(filter, result, block);
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
        base.addMonitor(monitor);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
        return base.checkMonitorHit(monitor, minHits);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
        return base.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
        return base.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
        base.removeMonitor(monitor);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
        return base.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        return base.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void sendStringSync(String text) {
        base.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
        base.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
        base.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
        base.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
        base.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
        base.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return base.newApplication(cl, className, context);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        base.callApplicationOnCreate(app);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return base.newActivity(cl, className, intent);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
        return base.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
    }

    //private void prePerformCreate(Activity activity) {}

    //private void postPerformCreate(Activity activity) {


    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        base.callActivityOnCreate(activity, icicle);
    }

    /*@Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        base.callActivityOnCreate(activity, icicle, persistentState);
    }*/

    @Override
    public void callActivityOnDestroy(Activity activity) {
        base.callActivityOnDestroy(activity);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        base.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    /*@Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
        base.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }*/

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
        base.callActivityOnPostCreate(activity, icicle);
    }

    /*@Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        base.callActivityOnPostCreate(activity, icicle, persistentState);
    }*/

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        base.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        base.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        base.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        base.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        base.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        base.callActivityOnSaveInstanceState(activity, outState);
    }

    /*@Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
        base.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }*/

    @Override
    public void callActivityOnPause(Activity activity) {
        base.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        base.callActivityOnUserLeaving(activity);
    }

    @Override
    public void startAllocCounting() {
        base.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
        base.stopAllocCounting();
    }

    //private void addValue(String key, int value, Bundle results) {


    @Override
    public Bundle getAllocCounts() {
        return base.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
        return base.getBinderCounts();
    }

    /*@Override
    public UiAutomation getUiAutomation() {
        return base.getUiAutomation();
    }*/
}
