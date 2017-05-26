package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import static mobile.xiyou.atest.Rf.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by admin on 2017/3/1.
 */

public class ActivityBase extends Activity{

    private Activity realActivity;
    private App app=null;
    private int id=0;

    public ActivityBase()
    {
        Log.e("xx","failed");
    }

    public void setRealActivity(Activity a)
    {
        realActivity=a;
    }

    private Object invoke(int method,Object ...params)
    {
        try {
            Method m=ActivityMethods.get(method);
            m.setAccessible(true);
            //Log.e("xx","invoke "+realActivity.toString()+":"+m.getName());
            return m.invoke(realActivity,params);
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (InvocationTargetException e) {
            Log.e("xx",e.getCause().toString()+":"+method);
        }
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        if (app!=null)
        return app.getLoader();

        return super.getClassLoader();
    }

    @Override
    public Resources getResources() {
        if (app!=null)
            return app.getRes();

        return super.getResources();
    }


    @Override
    public AssetManager getAssets() {
        if (app!=null)
            return app.getAm();

        return super.getAssets();
    }

    @Override
    public Resources.Theme getTheme() {
        Object xx=null;
        if ((xx=readField(ContextThemeWrapper.class,realActivity,"mTheme"))==null) {
            if (app != null)
                return app.getTheme(id);
        }

        return (Resources.Theme)xx;
    }

    @Override
    public Context getApplicationContext() {
        return app.getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTaskDescription(new ActivityManager.TaskDescription("aaaa"));
        app=((MainApp)getApplication()).getApp();
        app.solveIntent(this);
        Log.e("xxx","sbbbbbbbbbbbbbbbbb");
        id=getIntent().getIntExtra("id",0);

        //app.solveIntent(this);
        app.newTheme(app.getActInfo(app.getIntentClassName(getIntent())).theme,id);
        setField(ContextThemeWrapper.class,realActivity,"mTheme",app.getTheme(id));

        invoke(ActivityMethods.ONCREATE,savedInstanceState);
        setField(Activity.class,realActivity,"mParent",null);
        callSuper();


    }


    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {

        return realActivity.onCreatePanelMenu(featureId, menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("xx","onresult");
    }

    @Override
    public void finish() {
        Log.e("xx","finish");
        realActivity.finish();
    }

    @Override
    public void onBackPressed() {Log.e("xx","back");
        realActivity.onBackPressed();

    }


    @Override
    public void finishActivity(int requestCode) {
        realActivity.finishActivity(requestCode);
    }

    @Override
    public void finishAfterTransition() {
        realActivity.finishAfterTransition();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return realActivity.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        invoke(ActivityMethods.ONDESTROY);
        callSuper();
    }

    @Override
    protected void onStart() {
        invoke(ActivityMethods.ONSTART);
        callSuper();
    }

    @Override
    protected void onRestart() {
        invoke(ActivityMethods.ONRESTART);
        callSuper();
    }

    @Override
    protected void onResume() {
        invoke(ActivityMethods.ONRESUME);
        callSuper();
    }

    @Override
    protected void onPause() {
        invoke(ActivityMethods.ONPAUSE);
        callSuper();
    }

    @Override
    public void onDetachedFromWindow() {
        realActivity.onDetachedFromWindow();
    }

    @Override
    protected void onStop() {
        invoke(ActivityMethods.ONSTOP);
        callSuper();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return realActivity.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        realActivity.onContextMenuClosed(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        realActivity.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public CharSequence onCreateDescription() {
        return realActivity.onCreateDescription();
    }

    @Override
    public View onCreatePanelView(int featureId) {
        return realActivity.onCreatePanelView(featureId);
    }

    @Override
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return realActivity.onCreateThumbnail(outBitmap, canvas);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return realActivity.onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return realActivity.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return realActivity.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return realActivity.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return realActivity.onKeyUp(keyCode, event);
    }

    @Override
    public void onLowMemory() {
        realActivity.onLowMemory();
        callSuper();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        return realActivity.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return realActivity.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return realActivity.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        invoke(ActivityMethods.ONOPTIONSMENUCLOSED,menu);
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        invoke(ActivityMethods.ONPANELCLOSED,featureId,menu);
        super.onPanelClosed(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Object x=invoke(ActivityMethods.ONPREPAREOPTIONSMENU,menu);
        if (x!=null)
            return (boolean)x;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        Object x=invoke(ActivityMethods.ONPREPAREPANEL,featureId,view,menu);
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        Object x=invoke(ActivityMethods.ONRETAINNONCONFIG);
        if (x!=null)
            return x;
        return super.onRetainNonConfigurationInstance();
    }

    @Override
    public boolean onSearchRequested() {
        Object x=invoke(ActivityMethods.ONSEARCHREQ);
        if (x!=null)
            return (boolean)x;
        return super.onSearchRequested();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return realActivity.onTouchEvent(event);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        Object x=invoke(ActivityMethods.ONTRACKBALLEVENT,event);
        if (x!=null)
            return (boolean)x;
        return super.onTrackballEvent(event);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        invoke(ActivityMethods.ONWINDOWATTRIBUTESCHANGED,params);
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        invoke(ActivityMethods.ONWINDOWFOCUSCHANGED,hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onUserInteraction() {
        realActivity.onUserInteraction();
    }

    private void callSuper()
    {
        setField(Activity.class,this,"mCalled",true);
    }

    private static class ActivityMethods
    {
        public static final int ONCREATE=0;
        public static final int ONCREATEPANELMENU=1;
        public static final int ONCREATEOPTIONSMENU=2;
        public static final int ONDESTROY=3;
        public static final int ONSTART=4;
        public static final int ONRESTART=5;
        public static final int ONRESUME=6;
        public static final int ONPAUSE=7;
        public static final int ONSTOP=8;
        public static final int ONCONTEXTITEMSELECTED=9;
        public static final int ONCONTEXTMENUCLOSED=10;
        public static final int ONCREATECONTEXTMENU=11;
        public static final int ONCREATEDESCRIPTION=12;
        public static final int ONCREATEPANELVIEW=13;
        public static final int ONCREATETHUMBNAIL=14;
        public static final int ONCREATEVIEW1=15;
        public static final int ONKEYDOWN=16;
        public static final int ONKEYMULTIPLE=17;
        public static final int ONKEYUP=18;
        public static final int ONLOWMEMORY=19;
        public static final int ONMENUITEMSELECTED=20;
        public static final int ONMENUOPENED=21;
        public static final int ONOPTIONSITEMSELECTED=22;
        public static final int ONOPTIONSMENUCLOSED=23;
        public static final int ONPANELCLOSED=24;
        public static final int ONPREPAREOPTIONSMENU=25;
        public static final int ONPREPAREPANEL=26;
        public static final int ONRETAINNONCONFIG=27;
        public static final int ONSEARCHREQ=28;
        public static final int ONTOUCHEVENT=29;
        public static final int ONTRACKBALLEVENT=30;
        public static final int ONUSERINTERACTION=31;
        public static final int ONWINDOWATTRIBUTESCHANGED=32;
        public static final int ONWINDOWFOCUSCHANGED=33;
        public static final int ONCREATEVIEW2=34;

        private Method []methods;
        private static ActivityMethods am=new ActivityMethods();

        private ActivityMethods()
        {
            int num=4;
            String names[];
            Object params[];


            names=new String[]{"onCreate","onCreatePanelMenu","onCreateOptionsMenu",
                    "onDestroy","onStart","onRestart","onResume","onPause","onStop",
                    "onContextItemSelected","onContextMenuClosed","onCreateContextMenu",
                    "onCreateDescription","onCreatePanelView","onCreateThumbnail",
                    "onCreateView","onKeyDown",
                    "onKeyMultiple","onKeyUp","onLowMemory",
                    "onMenuItemSelected","onMenuOpened","onOptionsItemSelected","onOptionsMenuClosed",
                    "onPanelClosed","onPrepareOptionsMenu","onPreparePanel","onRetainNonConfigurationInstance",
                    "onSearchRequested","onTouchEvent","onTrackballEvent","onUserInteraction",
                    "onWindowAttributesChanged","onWindowFocusChanged","onCreateView"};
            params=new Object[]{new Class[]{Bundle.class},new Class[]{int.class,Menu.class},new Class[]{Menu.class},
                    new Class[]{},new Class[]{},new Class[]{},new Class[]{},new Class[]{},new Class[]{},
                    new Class[]{MenuItem.class},new Class[]{Menu.class},new Class[]{ContextMenu.class,View.class, ContextMenu.ContextMenuInfo.class},
                    new Class[]{},new Class[]{int.class},new Class[]{Bitmap.class,Canvas.class},
                    new Class[]{String.class,Context.class,AttributeSet.class},new Class[]{int.class,KeyEvent.class},
                    new Class[]{int.class,int.class,KeyEvent.class},new Class[]{int.class,KeyEvent.class},new Class[]{},
                    new Class[]{int.class,MenuItem.class},new Class[]{int.class,Menu.class},new Class[]{MenuItem.class},new Class[]{Menu.class},
                    new Class[]{int.class,Menu.class},new Class[]{Menu.class},new Class[]{int.class,View.class,Menu.class},new Class[]{},
                    new Class[]{},new Class[]{MotionEvent.class},new Class[]{MotionEvent.class},new Class[]{},
                    new Class[]{WindowManager.LayoutParams.class},new Class[]{boolean.class},new Class[]{View.class,String.class,Context.class,AttributeSet.class}};
            methods=new Method[names.length];

            for (int i=0;i<methods.length;i++)
            {
                try {
                    methods[i]=Activity.class.getDeclaredMethod(names[i],(Class[])params[i]);
                } catch (NoSuchMethodException e) {
                    Log.e("xx","activity:"+e.toString());
                }
            }
        }


        public static Method get(int index)
        {
            return am.methods[index];
        }
    }

    public static class A1 extends ActivityBase
    {
    }

    public static class A2 extends ActivityBase
    {
    }

    public static class A3 extends ActivityBase
    {
    }

    public static class A4 extends ActivityBase
    {
    }

    public static class A5 extends ActivityBase
    {
    }
}
