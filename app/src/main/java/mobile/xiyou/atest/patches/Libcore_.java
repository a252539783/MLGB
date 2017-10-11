package mobile.xiyou.atest.patches;

import android.content.Context;
import android.system.ErrnoException;
import android.system.StructStat;
import android.system.StructStatVfs;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;

import mobile.xiyou.atest.App;
import mobile.xiyou.atest.Rf;
import mobile.xiyou.hook.ArtHook;
import mobile.xiyou.hook.OriginalMethod;

/**
 * Created by user on 2017/5/17.
 */

public class Libcore_ {

    private static OriginalMethod open =null,mkdir=null,access=null,chmod=null,chown=null,lchown,link=null,lstat=null,mkfifo,readlink,remove,rename,stat,statvfs,symlink,nativeLoad,load;
    private static Object os =null;
    private static String path_=null;
    private static String mPath=null;
    private static final String path_o="/data/data/";

    public static void patch(Context c)
    {
        mPath=c.getFilesDir().getPath();
        path_=mPath.substring(0,mPath.indexOf(c.getPackageName()));

        try {

            open = ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("open",new Class[]{String.class,int.class,int.class})
                    ,Libcore_.class.getDeclaredMethod("open",String.class,int.class,int.class),null);
            mkdir= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("mkdir",new Class[]{String.class,int.class})
                    ,Libcore_.class.getDeclaredMethod("mkdir",String.class,int.class),null);
            access= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("access",new Class[]{String.class,int.class})
                    ,Libcore_.class.getDeclaredMethod("access",String.class,int.class),null);
            chmod= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("chmod",new Class[]{String.class,int.class})
                    ,Libcore_.class.getDeclaredMethod("chmod",String.class,int.class),null);
            chown= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("chown",new Class[]{String.class,int.class,int.class})
                    ,Libcore_.class.getDeclaredMethod("chown",String.class,int.class,int.class),null);
            lchown= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("lchown",new Class[]{String.class,int.class,int.class})
                    ,Libcore_.class.getDeclaredMethod("lchown",String.class,int.class,int.class),null);
            link= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("link",new Class[]{String.class,String.class})
                    ,Libcore_.class.getDeclaredMethod("link",String.class,String.class),null);
            lstat= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("lstat",new Class[]{String.class})
                    ,Libcore_.class.getDeclaredMethod("lstat",String.class),null);
            mkfifo= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("mkfifo",new Class[]{String.class,int.class})
                    ,Libcore_.class.getDeclaredMethod("mkfifo",String.class,int.class),null);
            readlink= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("readlink",new Class[]{String.class})
                    ,Libcore_.class.getDeclaredMethod("readlink",String.class),null);
            remove= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("remove",new Class[]{String.class})
                    ,Libcore_.class.getDeclaredMethod("remove",String.class),null);
            rename= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("rename",new Class[]{String.class,String.class})
                    ,Libcore_.class.getDeclaredMethod("rename",String.class,String.class),null);
            symlink= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("symlink",new Class[]{String.class,String.class})
                    ,Libcore_.class.getDeclaredMethod("symlink",String.class,String.class),null);
            stat= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("stat",new Class[]{String.class})
                    ,Libcore_.class.getDeclaredMethod("stat",String.class),null);
            statvfs= ArtHook.hook(Class.forName("libcore.io.BlockGuardOs").getDeclaredMethod("statvfs",new Class[]{String.class})
                    ,Libcore_.class.getDeclaredMethod("statvfs",String.class),null);


            //nativeLoad=ArtHook.hook(Runtime.class.getDeclaredMethod("nativeLoad",new Class[]{String.class,ClassLoader.class,String .class}),
             //       Libcore_.class.getDeclaredMethod("nativeLoad",new Class[]{String.class,ClassLoader.class,String .class}),null);
            //load=ArtHook.hook(System.class.getDeclaredMethod("load",new Class[]{String.class})
            //        ,Libcore_.class.getDeclaredMethod("load",new Class[]{String.class}),null);
            os =Rf.readField(Class.forName("libcore.io.Libcore"),null,"os");
        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx",e.toString());
        }

    }

    public static void load(String path)
    {
        Log.e("xx","load:"+path);
        //path=solvePath(path);
        load.invoke(null,path);
    }

    public static String nativeLoad(String file,ClassLoader loader,String ldlib)
    {
        //file=solvePath(file);
        Log.e("xx","nativeLoad:"+file+":"+ldlib);
        return nativeLoad.invoke(null,file,loader,ldlib);
    }

    public StructStat stat(String path)   {
        path=solvePath(path);
        Log.e("xx","stat:"+path);
        return stat.invoke(os,path);
    }

    public StructStatVfs statvfs(String path) {
        path=solvePath(path);
        Log.e("xx","statvfs:"+path);
        return statvfs.invoke(os,path);
    }

    public void symlink(String oldPath, String newPath) {
        oldPath = solvePath(oldPath);
        newPath = solvePath(newPath);
        Log.e("xx", "symlink:" + oldPath + "---" + newPath);
        symlink.invoke(os, oldPath, newPath);
    }

    public void rename(String oldPath, String newPath) {
        oldPath = solvePath(oldPath);
        newPath = solvePath(newPath);
        Log.e("xx", "rename:" + oldPath + "---" + newPath);
        rename.invoke(os, oldPath, newPath);
    }

    public void remove(String path)  {
        path=solvePath(path);
        Log.e("xx","remove:"+path);
        remove.invoke(os,path);
    }

    public String readlink(String path) {
        path=solvePath(path);
        Log.e("xx","readlink:"+path);
        return readlink.invoke(os,path);
    }

    public void mkfifo(String path, int mode) {
        path=solvePath(path);
        Log.e("xx","mkfifo:"+path);
        mkfifo.invoke(os,path,mode);
    }

    public StructStat lstat(String path) {
        path=solvePath(path);
        Log.e("xx","lstat:"+path);
        return lstat.invoke(os,path);
    }

    private FileDescriptor open(String path, int offset, int byteCount)
    {
        path=solvePath(path);
        Log.e("xx","open:"+path);
        return open.invoke(os,path,offset,byteCount);
    }

    private void mkdir(String path, int mode)
    {
        path=solvePath(path);
        Log.e("xx","mkdir:"+path);
        mkdir.invoke(os,path,mode);
    }

    private boolean access(String path, int mode) {
        path=solvePath(path);
        Log.e("xx","access:"+path);
        return access.invoke(os,path,mode);
    }

    private void chmod(String path, int mode) {
        path=solvePath(path);
        Log.e("xx","chmod:"+path);
        chmod.invoke(os,path,mode);
    }

    private void chown(String path, int uid, int gid) {
        path=solvePath(path);
        Log.e("xx","chown:"+path);
        chown.invoke(os,path,uid,gid);
    }

    public void lchown(String path, int uid, int gid) {
        path=solvePath(path);
        Log.e("xx","lchown:"+path);
        lchown.invoke(os,path,uid,gid);
    }

    public void link(String oldPath, String newPath) {
        oldPath = solvePath(oldPath);
        newPath = solvePath(newPath);
        Log.e("xx", "link:" + oldPath + "---" + newPath);
        link.invoke(os, oldPath, newPath);
    }

    private static String solvePath(String path)
    {
        if (path.length()<path_.length()||path.length()<path_o.length())
            return path;
        if (path.charAt(0)!=File.separatorChar)
            path=File.separator+path;

        if (path.substring(0,path_.length()).equals(path_))
        {
            String pkgName=path.substring(path_.length()).split(File.separator)[0];

            if (pkgName.equals(App.get().getInfo().info.packageName))
            {
                path=mPath+path;
            }
        }else if(path.substring(0,path_o.length()).equals(path_o))
        {
            String pkgName=path.substring(path_o.length()).split(File.separator)[0];
            if (pkgName.equals(App.get().getInfo().info.packageName))
            {
                path=mPath+path;
            }
        }

        return path;
    }
}
