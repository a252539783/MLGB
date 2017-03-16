package mobile.xiyou.atest;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by admin on 2017/3/2.
 */

public class Rf {

    public static Object readField(Object o,String name)
    {
        try {
            Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(o);
        }catch (Exception e)
        {
            Log.e("xx",e.toString());
        }
        return null;
    }

    public static Object readField(Class r,Object o,String name)
    {
        try {
            Field f = r.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(o);
        }catch (Exception e)
        {
            Log.e("xx",e.toString());
        }
        return null;
    }

    public static void setField(Object o,String name,Object x)
    {
        try {
            Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(o,x);
        }catch (Exception e)
        {
            Log.e("xx",e.toString());
        }
    }

    public static void setField(Class r,Object o,String name,Object x)
    {
        try {
            Field f = r.getDeclaredField(name);
            f.setAccessible(true);
            f.set(o,x);
        }catch (Exception e)
        {
            Log.e("xx",e.toString());
        }
    }

    public static Object invoke(Class r,Object o,String name,Class []vp,Object ...params)
    {
        try {
            Method m = r.getDeclaredMethod(name,vp);
            m.setAccessible(true);
            return m.invoke(o,params);
        }catch (InvocationTargetException e)
        {
            Log.e("xx",e.getCause().toString());
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }
        return null;
    }

    public static Object invoke(Object o,String name,Class []vp,Object ...params)
    {
        Class r=o.getClass();
        try {
            Method m = r.getDeclaredMethod(name,vp);
            m.setAccessible(true);
            return m.invoke(o,params);
        }catch (InvocationTargetException e)
        {
            Log.e("xx",e.getCause().toString());
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }
        return null;
    }

    public static Object invoke(Object o,String name)
    {
        Class r=o.getClass();
        try {
            Method m = r.getDeclaredMethod(name,new Class[]{});
            m.setAccessible(true);
            return m.invoke(o);
        }catch (InvocationTargetException e)
        {
            Log.e("xx",e.getCause().toString());
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }
        return null;
    }

    public static Object invoke(Class r,Object o,String name)
    {
        try {
            Method m = r.getDeclaredMethod(name,new Class[]{});
            m.setAccessible(true);
            return m.invoke(o);
        }catch (InvocationTargetException e)
        {
            Log.e("xx",e.getCause().toString());
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }
        return null;
    }

}
