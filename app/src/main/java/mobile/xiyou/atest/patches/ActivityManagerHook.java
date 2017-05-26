package mobile.xiyou.atest.patches;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import mobile.xiyou.atest.Rf;

/**
 * Created by user on 2017/5/11.
 */

public class ActivityManagerHook implements InvocationHandler {

    private static final String PKG="mobile.xiyou.atest";

    private Object mBase;
    private ActivityManagerHook(Object base)
    {
        mBase=base;
    }

    public static void patch()
    {
        try {
            Class AMN=Class.forName("android.app.ActivityManagerNative");
            Class ST=Class.forName("android.util.Singleton");

            Object amp= Rf.invoke(AMN,null,"getDefault");
            Object gDefault=Rf.readField(AMN,null,"gDefault");
            Object proxy= Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},new ActivityManagerHook(Rf.readField(ST,gDefault,"mInstance")));
            Rf.setField(ST,gDefault,"mInstance",proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx",e.toString());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //Log.e("xx","exec "+method.getName());
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
