package mobile.xiyou.atest.patches;

import android.app.NotificationManager;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import mobile.xiyou.atest.Rf;

/**
 * Created by user on 2017/5/14.
 */

public class NotificationManagerHook implements InvocationHandler {

    private Object mBase;
    private NotificationManagerHook(Object base)
    {
        mBase=base;
    }

    public static void patch()
    {
            Object service= Rf.invoke(NotificationManager.class,null,"getService");
            //Object gDefault=Rf.readField(AMN,null,"gDefault");
        //Log.e("xx","patch nm start"+Rf.readField(NotificationManager.class,null,"sService").toString());
        Object proxy= null;
        try {
            proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{Class.forName("android.app.INotificationManager")},new NotificationManagerHook(service));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx",e.toString());
        }
        Rf.setField(NotificationManager.class,null,"sService",proxy);
        //Log.e("xx","patch nm finish"+Rf.readField(NotificationManager.class,null,"sService").toString());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //Log.e("xx","exec "+method.getName()+":"+args[0]+"XX"+args[1]);
        //args[0]=App.get().getInfo().info.packageName;
        //args[6]=10187;
       // return method.invoke(mBase,args);
        return null;
    }
}
