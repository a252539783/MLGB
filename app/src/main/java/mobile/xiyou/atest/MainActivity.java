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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
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

import static android.content.pm.PackageManager.*;
import static mobile.xiyou.atest.Rf.*;

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
        setContentView(R.layout.activity_main);
        findViewById(R.id.bbb).setOnClickListener(this);

        //Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
//        ApplicationInfo ai=getPackageManager().getInstalledApplications(GET_SHARED_LIBRARY_FILES).get(0);
//        Log.e("xx",readField(ai,"primaryCpuAbi").toString());
    }



    @Override
    public void onClick(View v) {
        //AppManager.get().startApp(this,"xiyou.mobile.android.elisten",0);
        //AppManager.get().startApp(this,"com.example.wyz.xiyoug",0);
        AppManager.get().startApp(this,"com.example.share4_15",0);
        //AppManager.get().startApp(this,"a.a.zzz",0);
        //AppManager.get().startApp(this,"com.example.miaojie.login_test1",0);
        //startActivity(new Intent(this,TestActivity.class));
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
