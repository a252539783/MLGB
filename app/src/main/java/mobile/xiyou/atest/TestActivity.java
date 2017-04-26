package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

/**
 * Created by admin on 2017/3/1.
 */

public class TestActivity extends Activity {

    public TestActivity()
    {
        super();
        //requestPermissions();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTaskDescription(new ActivityManager.TaskDescription("aaaa", BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)));
    }
}
