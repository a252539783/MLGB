package mobile.xiyou.atest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

/**
 * Created by admin on 2017/3/1.
 */

public class TestActivity extends ActivityBase {

    public TestActivity()
    {
        super();
        //requestPermissions();
        Log.e("xx","process"+ Process.myPid());
    }
}
