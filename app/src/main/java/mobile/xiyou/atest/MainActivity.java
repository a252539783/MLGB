package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentController;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.content.pm.PackageManager.*;
import static mobile.xiyou.atest.Rf.*;

public class MainActivity extends Activity implements Runnable,View.OnClickListener {

    public MainActivity() {
        super();
    }

    Button test;
    private static boolean started=true;

    private Resources res;
    private AssetManager am;
    private Activity activity;
    private Resources.Theme theme;
    private PackageInfo info;
    private Context cc;
    private AppManagerNative ams=null;
    private ServiceConnection sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button a= (Button)findViewById(R.id.bbb);
        test=a;
        a.setOnClickListener(this);
        findViewById(R.id.aaa).setOnClickListener(this);
        //a.setText(""+getTaskId());
        //Log.e("xx","onCreate");

        if (started) {
            sc = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    ams = AppManagerNative.Stub.asInterface(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    ams = null;
                }
            };
            bindService(new Intent(this, AppManagerService.class), sc, BIND_AUTO_CREATE);
        }else
        {
            Intent i=new Intent(this,MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
            startActivity(i);
            started=true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);
    }

    @Override
    public void onClick(View v) {

if (v.getId()==R.id.aaa) {
    try {
            ams.startApp("com.example.wyz.xiyoug");
//           ams.startApp("com.example.share4_15");
        //ams.startApp("com.tencent.mobileqq");
        //ams.startApp("a.a.zzz");
    } catch (RemoteException e) {
        e.printStackTrace();
        Log.e("xx", e.toString());
    }
}
        else
    if (v.getId()==R.id.bbb)
    {
        try {
//            ams.startApp("com.example.wyz.xiyoug");
           ams.startApp("com.example.share4_15");
            //ams.startApp("com.tencent.mobileqq");
            //ams.startApp("a.a.zzz");
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("xx", e.toString());
        }
    }

        //AppManager.get().startApp(this,"xiyou.mobile.android.elisten",0);
        //AppManager.get().startApp(this,"com.example.wyz.xiyoug",0);
        //AppManager.get().startApp(this,"com.example.share4_15",0);
        //AppManager.get().startApp(this,"mobile.xiyou.join",0);
        //AppManager.get().startApp(this,"com.example.miaojie.login_test1",0);
        //startActivity(new Intent(this,TestActivity.class));
        //AppManager.get().startApp(this,"com.tencent.mobileqq",0);
        //AppManager.get().startApp(this,"xiyou.mobile.android.elisten",0);
        //AppManager.get().startApp(this,"ly.pp.justpiano2",0);
        //AppManager.get().startApp(this,"com.example.user.testbindservice",0);
        //AppManager.get().startApp(this,"com.estrongs.android.pop.pro",0);
            //Log.e("xx","fork"+JniTest.j_fork());
        //if (JniTest.j_fork()==0) {
            //setField(Looper.class,null,"sMainLooper",null);
            //setField(Looper.class,null,"sThreadLocal",new ThreadLocal<Looper>());
        //    AppManagerService.createApp();;
        //}
        //AppManager.get().startApp(this,"com.example.miaojie.sbbb",0);

            //startActivity(new Intent(this,TestActivity.class));

        //if (JniTest.j_fork()==0)
        //    AppManagerService.createApp();
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
