package mobile.xiyou.atest;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

/**
 * Created by admin on 2017/3/1.
 */

public class AppManager {
    private HashMap<Integer,App> apps;
    private static AppManager mgr=null;

    private AppManager()
    {
        this.apps=new HashMap<>();
    }

    public App getApp(int id)
    {
        return apps.get(id);
    }

    public void startApp(Context c,String packageName,int i)
    {
        App x=null;
            if (!apps.containsKey(i))
            {
                x=new App(c,packageName);
                apps.put(i,x);
                //x.setId(i);
            }else
            x=apps.get(i);
        c.startActivity(x.startActivityIntent(c,x.getInfo().mainClass));
    }

    public static AppManager get()
    {
        synchronized (AppManager.class)
        {
            if (mgr==null)
                mgr=new AppManager();
        }
        return mgr;
    }
}
