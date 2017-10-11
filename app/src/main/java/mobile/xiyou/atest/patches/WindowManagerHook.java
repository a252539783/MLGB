package mobile.xiyou.atest.patches;

import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import mobile.xiyou.atest.Rf;

/**
 * Created by Administrator on 2017/7/12.
 */

public class WindowManagerHook implements InvocationHandler {

    private static final String PKG="mobile.xiyou.atest";

    private Object mBase;
    private WindowManagerHook(Object base)
    {
        mBase=base;
    }

    public static void patch()
    {
        try {

            Class SM=Class.forName("android.os.ServiceManager");
            Object sm=Rf.invoke(SM,null,"getService",new Class[]{String.class},"window");
            HashMap<String,Object> sCache=(HashMap)Rf.readField(SM,null,"sCache");
            Object proxy= Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{Class.forName("android.os.IBinder")},new WindowManagerHook(sm));
            sCache.put("window",proxy);

            Class WMG=Class.forName("android.view.WindowManagerGlobal");

            Object wm= Rf.invoke(WMG,null,"getWindowManagerService");
            proxy= Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{Class.forName("android.view.IWindowManager")},new WindowManagerHook(wm));
            Rf.setField(WMG,null,"sWindowManagerService",proxy);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx",e.toString());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Log.e("xx","WindowManagerService exec "+method.getName());
        if (method.getName().equals("getIntentSender"))
        {
            args[1]="mobile.xiyou.atest";
        }else if (method.getName().equals("registerReceiver"))
        {
            Log.e("xx","registe rec:"+args[1]);
        }
/*
        String methodName=method.getName();
        switch(methodName)
        {
            case "getIntentSender":
                args[1]=PKG;
                break;
            case "getAppTasks":
                args[0]=PKG;
                break;
            case "getPackageProcessState":
                args[1]=PKG;
                break;
        }
*/

        return method.invoke(mBase,args);
    }
}
