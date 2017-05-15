package mobile.xiyou.atest;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by user on 2017/5/11.
 */

public class ActivityManagerHook implements InvocationHandler {

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

            Object amp=Rf.invoke(AMN,null,"getDefault");
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
        }

        return method.invoke(mBase,args);
    }
}
