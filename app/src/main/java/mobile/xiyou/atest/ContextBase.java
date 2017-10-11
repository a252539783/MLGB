package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Instrumentation;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ReceiverCallNotAllowedException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import static mobile.xiyou.atest.Rf.*;

/**
 * Created by admin on 2017/3/2.
 */

public class ContextBase extends ContextWrapper {

    public static void patchService()
    {
        Class ServiceFetcher=null;
        Method regist=null;
        HashMap featchers=null;
        try {
            ServiceFetcher=Class.forName("android.app.SystemServiceRegistry$ServiceFetcher");
            //regist=Class.forName("android.app.SystemServiceRegistry").getDeclaredMethod("registerService",new Class[]{String.class,Class.class,ServiceFetcher});
            featchers=(HashMap)readField(Class.forName("android.app.SystemServiceRegistry"),null,"SYSTEM_SERVICE_FETCHERS");
            //regist.setAccessible(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx",e.toString());
        }

        featchers.put(Context.LAYOUT_INFLATER_SERVICE, Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{ServiceFetcher},new LF(featchers.get(Context.LAYOUT_INFLATER_SERVICE))));
    }

    private static class LF implements InvocationHandler{

        Object base=null;
        public LF(Object o){base=o;}

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return Class.forName("com.android.internal.policy.PhoneLayoutInflater").getDeclaredConstructor(Context.class).newInstance(App.context2);
            //return null;
            //return method.invoke(base,args);

        }
    }

    /*
        r.packageInfo = ActivityThread.getPackageInfoNoCheck(
                                r.activityInfo.applicationInfo, r.compatInfo);
        ams.CompatibilityInfo compatibilityInfoForPackageLocked(ApplicationInfo ai) {
            return mCompatModePackages.compatibilityInfoForPackageLocked(ai);
        }
         */
    public static void init(Object thread,ApplicationInfo info)
    {
        try {
            //thread=readField(Class.forName("android.app.ActivityThread"),null,"currentActivityThread");
            Object compat=readField(Class.forName("android.content.res.CompatibilityInfo"),null,"DEFAULT_COMPATIBILITY_INFO");
            invoke(thread.getClass(),thread,"getPackageInfoNoCheck",new Class[]{ApplicationInfo.class,Class.forName("android.content.res.CompatibilityInfo")},info,compat);   //LoadedApk
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }
    }

    public static Object loadApk(Object thread,ApplicationInfo info)
    {
        Object loaded=null;
        try {
            //thread=readField(Class.forName("android.app.ActivityThread"),null,"currentActivityThread");
            Object compat=readField(Class.forName("android.content.res.CompatibilityInfo"),null,"DEFAULT_COMPATIBILITY_INFO");
            loaded=invoke(thread.getClass(),thread,"getPackageInfoNoCheck",new Class[]{ApplicationInfo.class,Class.forName("android.content.res.CompatibilityInfo")},info,compat);   //LoadedApk
            setField(loaded,"mResDir",info.sourceDir);
            setField(loaded,"mSplitResDirs",info.splitSourceDirs);
            Log.e("xx","dir:"+info.sourceDir+":"+info.splitSourceDirs);
            //loaded=invoke(thread,"getPackageInfo",new Class[]{ApplicationInfo.class,Class.forName("android.content.res.CompatibilityInfo"),int.class},info,compat, Context.CONTEXT_INCLUDE_CODE + Context.CONTEXT_IGNORE_SECURITY);
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }

        return loaded;
    }

    public static Context createActivityContext(Object thread,Object la)
    {
        Context c=null;
        try {
            c=(Context)invoke(Class.forName("android.app.ContextImpl"),null,"createActivityContext",new Class[]{thread.getClass(),la.getClass(),int.class, Configuration.class},thread,la, Display.DEFAULT_DISPLAY,new Configuration());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }


        return c;
    }

    public static Context createActivityContext(Object thread,ApplicationInfo info)
    {
        Context c=null;
        Object la=loadApk(thread,info);
        try {
            c=(Context)invoke(Class.forName("android.app.ContextImpl"),null,"createActivityContext",new Class[]{thread.getClass(),la.getClass(),int.class, Configuration.class},thread,la, Display.DEFAULT_DISPLAY,new Configuration());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }

        return c;
    }

/*
class ReceiverRestrictedContext extends ContextWrapper {
   private Context base;

    ReceiverRestrictedContext(Context base) {
        super(base);
        this.base=base;
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return registerReceiver(receiver, filter, null, null);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
                                   String broadcastPermission, Handler scheduler) {
        if (receiver == null) {
            // Allow retrieving current sticky broadcast; this is safe since we
            // aren't actually registering a receiver.
            return super.registerReceiver(null, filter, broadcastPermission, scheduler);
        } else {
            throw new ReceiverCallNotAllowedException(
                    "BroadcastReceiver components are not allowed to register to receive intents");
        }
    }


    public Intent registerReceiverAsUser(BroadcastReceiver receiver, UserHandle user,
                                         IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if (receiver == null) {
            // Allow retrieving current sticky broadcast; this is safe since we
            // aren't actually registering a receiver.
            return base.registerReceiverAsUser(null, user, filter, broadcastPermission, scheduler);
        } else {
            throw new ReceiverCallNotAllowedException(
                    "BroadcastReceiver components are not allowed to register to receive intents");
        }
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        throw new ReceiverCallNotAllowedException(
                "BroadcastReceiver components are not allowed to bind to services");
    }
}
*/
    private final static String TAG = "ContextImpl";
    private final static boolean DEBUG = false;

    /**
     * Map from package name, to preference name, to cached preferences.
     */
    private static ArrayMap<String, ArrayMap<String, Object>> sSharedPrefs;

    Object mMainThread;     //ActivityThread
    Object mPackageInfo;    //LoadedAPK

    private IBinder mActivityToken;

    private UserHandle mUser;

    private ContentResolver mContentResolver;

    private String mBasePackageName;
    private String mOpPackageName;

    private Object mResourcesManager;     //ResourcesManager
    private Resources mResources;
    private Display mDisplay; // may be null if default display
//    private final DisplayAdjustments mDisplayAdjustments = new DisplayAdjustments();    //DisplayAdjustments

    private boolean mRestricted;

    private App app;
    private Context mOuterContext;
    private Context mMainContext;
    private int mThemeResource = 0;
    private Resources.Theme mTheme = null;
    private PackageManager mPackageManager;
    private Context mReceiverRestrictedContext = null;

    private final Object mSync = new Object();

    private String dataDir=null;
    private File mDatabasesDir;
    private File mPreferencesDir;
    private File mFilesDir;
    private File mNoBackupFilesDir;
    private File mCacheDir;
    private File mCodeCacheDir;

    private File[] mExternalObbDirs;
    private File[] mExternalFilesDirs;
    private File[] mExternalCacheDirs;
    private File[] mExternalMediaDirs;

    private static final String[] EMPTY_STRING_ARRAY = {};

//    final Object[] mServiceCache = SystemServiceRegistry.createServiceCache();

    static Object getImpl(Context context) {
        Context nextContext;
        while ((context instanceof ContextWrapper) &&
                (nextContext=((ContextWrapper)context).getBaseContext()) != null) {
            context = nextContext;
        }
        return context;
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    @Override
    public Resources getResources() {
        Log.e("xx",""+mResources.hashCode()+":"+mMainContext.hashCode());
        return mResources;
    }

    @Override
    public PackageManager getPackageManager() {
        return mMainContext.getPackageManager();
        /*
        if (mPackageManager != null) {
            return mPackageManager;
        }

        IPackageManager pm = ActivityThread.getPackageManager();
        if (pm != null) {
            // Doesn't matter if we make more than one instance.
            return (mPackageManager = new ApplicationPackageManager(this, pm));
        }

        return null;
        */
    }

    @Override
    public ContentResolver getContentResolver() {
        return mMainContext.getContentResolver();
    }

    @Override
    public Looper getMainLooper() {
        return (Looper)invoke(mMainThread.getClass(),mMainThread,"getLooper",new Class[]{});
    }

    @Override
    public Context getApplicationContext() {
        return app.getApplication();
    }

    @Override
    public void setTheme(int resId) {
        if (mThemeResource != resId) {
            mThemeResource = resId;
            initializeTheme();
        }
    }

    public int getThemeResId() {
        return mThemeResource;
    }

    @Override
    public Resources.Theme getTheme() {
        if (mTheme != null) {
            return mTheme;
        }

        mThemeResource =selectDefaultTheme(mThemeResource,
                getOuterContext().getApplicationInfo().targetSdkVersion);
        initializeTheme();

        return mTheme;
    }

    private static int selectDefaultTheme(int curTheme, int targetSdkVersion) {
        try {
             return (int)invoke(Class.forName("android.content.res.Resources"),null,"selectDefaultTheme",new Class[]{int .class,int.class},curTheme,targetSdkVersion);
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }
        return 0;
    }

    /** @hide */
    private static int selectSystemTheme(int curTheme, int targetSdkVersion, int orig, int holo,
                                        int dark, int deviceDefault) {
        if (curTheme != 0) {
            return curTheme;
        }
        if (targetSdkVersion < Build.VERSION_CODES.HONEYCOMB) {
            return orig;
        }
        if (targetSdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return holo;
        }
        if (targetSdkVersion < Build.VERSION_CODES.CUR_DEVELOPMENT) {
            return dark;
        }
        return deviceDefault;
    }

    private void initializeTheme() {
        if (mTheme == null) {
            mTheme = mResources.newTheme();
        }
        mTheme.applyStyle(mThemeResource, true);
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPackageInfo != null ?
                (ClassLoader) invoke(mPackageInfo.getClass(),mPackageInfo,"getClassLoader",new Class[]{}) : ClassLoader.getSystemClassLoader();
    }

    @Override
    public String getPackageName() {
        if (mPackageInfo != null) {
            //return mPackageInfo.getPackageName();
            mBasePackageName=mMainContext.getPackageName();
            mOpPackageName=mBasePackageName;
            //return (String)invoke(mPackageInfo,"getPackageName",new Class[]{});
            return mBasePackageName;
        }
        // No mPackageInfo means this is a Context for the system itself,
        // and this here is its name.
        return "android";
    }


    public String getBasePackageName() {
        return getPackageName();
    }


    public String getOpPackageName() {
        return getPackageName();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        if (mPackageInfo != null) {
            //return (ApplicationInfo) invoke(mPackageInfo,"getApplicationInfo",new Class[]{});
            return app.getInfo().info.applicationInfo;
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override
    public String getPackageResourcePath() {
        if (mPackageInfo != null) {
            return (String)invoke(mPackageInfo,"getResDir");
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override
    public String getPackageCodePath() {
        if (mPackageInfo != null) {
            return (String)invoke(mPackageInfo,"getAppDir");
        }
        throw new RuntimeException("Not supported in system context");
    }


    public File getSharedPrefsFile(String name) {
        return makeFilename(getPreferencesDir(), name + ".xml");
    }


    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        //return mMainContext.getSharedPreferences(name,mode);

        Class spcls=null;
        try {
            spcls=Class.forName("android.app.SharedPreferencesImpl");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx",e.toString());
        }

        Object sp;
        synchronized (this) {
            if (sSharedPrefs == null) {
                sSharedPrefs = new ArrayMap<String, ArrayMap<String, Object>>();
            }

            final String packageName = getPackageName();
            ArrayMap<String, Object> packagePrefs = sSharedPrefs.get(packageName);
            if (packagePrefs == null) {
                packagePrefs = new ArrayMap<String, Object>();
                sSharedPrefs.put(packageName, packagePrefs);
            }

            // At least one application in the world actually passes in a null
            // name.  This happened to work because when we generated the file name
            // we would stringify it to "null.xml".  Nice.
            if (app.getInfo().info.applicationInfo.targetSdkVersion <
                    Build.VERSION_CODES.KITKAT) {
                if (name == null) {
                    name = "null";
                }
            }

            sp = packagePrefs.get(name);
            if (sp == null) {
                File prefsFile = getSharedPrefsFile(name);
                try {
                    Constructor c=spcls.getDeclaredConstructor(File.class,int.class);
                    c.setAccessible(true);
                    sp = c.newInstance(prefsFile,mode);
                } catch (InstantiationException e) {
                    Log.e("xx",e.toString());
                } catch (IllegalAccessException e) {
                    Log.e("xx",e.toString());
                } catch (InvocationTargetException e) {
                    Rf.p("sp newinstance",e);
                } catch (NoSuchMethodException e) {
                    Log.e("xx",e.toString());
                }
                packagePrefs.put(name, sp);
                return (SharedPreferences) sp;
            }
        }
        if ((mode & Context.MODE_MULTI_PROCESS) != 0 ||
                getApplicationInfo().targetSdkVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
            // If somebody else (some other process) changed the prefs
            // file behind our back, we reload it.  This has been the
            // historical (if undocumented) behavior.
           // sp.startReloadIfChangedUnexpectedly();
            invoke(sp,"startReloadIfChangedUnexpectedly");
        }
        return (SharedPreferences) sp;

    }


    private File getPreferencesDir() {
        synchronized (mSync) {
            if (mPreferencesDir == null) {
                mPreferencesDir = new File(getDataDirFile(), "shared_prefs");
            }
            return mPreferencesDir;
        }
    }

    @Override
    public FileInputStream openFileInput(String name)
            throws FileNotFoundException {
        File f = makeFilename(getFilesDir(), name);
        return new FileInputStream(f);
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode)
            throws FileNotFoundException {
        final boolean append = (mode&MODE_APPEND) != 0;
        File f = makeFilename(getFilesDir(), name);
        try {
            FileOutputStream fos = new FileOutputStream(f, append);
            //setFilePermissionsFromMode(f.getPath(), mode, 0);
            return fos;
        } catch (FileNotFoundException e) {
        }

        File parent = f.getParentFile();
        parent.mkdir();
        /*
        FileUtils.setPermissions(
                parent.getPath(),
                FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
                -1, -1);
                */
        FileOutputStream fos = new FileOutputStream(f, append);
        //setFilePermissionsFromMode(f.getPath(), mode, 0);
        return fos;
    }

    @Override
    public boolean deleteFile(String name) {
        File f = makeFilename(getFilesDir(), name);
        return f.delete();
    }

    // Common-path handling of app data dir creation
    private static File createFilesDirLocked(File file) {
        if (!file.exists()) {
            if (!file.mkdirs()) {
                if (file.exists()) {
                    // spurious failure; probably racing with another process for this app
                    return file;
                }
                Log.w(TAG, "Unable to create files subdir " + file.getPath());
                return null;
            }
            /*
            FileUtils.setPermissions(
                    file.getPath(),
                    FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
                    -1, -1);
                    */
        }
        return file;
    }

    @Override
    public File getFilesDir() {
        synchronized (mSync) {
            if (mFilesDir == null) {
                mFilesDir = new File(getDataDirFile(), "files");
            }
            return createFilesDirLocked(mFilesDir);
        }
    }

    @Override
    public File getNoBackupFilesDir() {
        synchronized (mSync) {
            if (mNoBackupFilesDir == null) {
                mNoBackupFilesDir = new File(getDataDirFile(), "no_backup");
            }
            return createFilesDirLocked(mNoBackupFilesDir);
        }
    }

    @Override
    public File getExternalFilesDir(String type) {
        // Operates on primary external storage
        return getExternalFilesDirs(type)[0];

        //return new File(mMainContext.getExternalFilesDir(type)+"/Android/data/"+app.getInfo().info.packageName+"/files/");
    }

    @Override
    public File[] getExternalFilesDirs(String type) {
        File [] dirs=new File[]{
                new File(mMainContext.getExternalFilesDir(type)+"/Android/data/"+app.getInfo().info.packageName+"/files/")
        };
        if (!dirs[0].exists())
        {
            dirs[0].mkdirs();
        }
        /*synchronized (mSync) {
            if (mExternalFilesDirs == null) {
                mExternalFilesDirs = Environment.buildExternalStorageAppFilesDirs(getPackageName());
            }

            // Splice in requested type, if any
            File[] dirs = mExternalFilesDirs;
            if (type != null) {
                dirs = Environment.buildPaths(dirs, type);
            }

            // Create dirs if needed
            return ensureDirsExistOrFilter(dirs);
        }
        */
        return dirs;
    }

    @Override
    public File getObbDir() {
        // Operates on primary external storage
        return getObbDirs()[0];
    }

    @Override
    public File[] getObbDirs() {

        File [] dirs=new File[]{
                new File(mMainContext.getObbDir()+"/Android/obb/"+app.getInfo().info.packageName+"/")
        };
        if (!dirs[0].exists())
        {
            dirs[0].mkdirs();
        }

        /*synchronized (mSync) {
            if (mExternalObbDirs == null) {
                mExternalObbDirs = Environment.buildExternalStorageAppObbDirs(getPackageName());
            }

            // Create dirs if needed
            return ensureDirsExistOrFilter(mExternalObbDirs);
        }
        */

        return dirs;
    }

    @Override
    public File getCacheDir() {
        synchronized (mSync) {
            if (mCacheDir == null) {
                mCacheDir = new File(getDataDirFile(), "cache");
            }
            return createFilesDirLocked(mCacheDir);
        }
    }

    @Override
    public File getCodeCacheDir() {
        synchronized (mSync) {
            if (mCodeCacheDir == null) {
                mCodeCacheDir = new File(getDataDirFile(), "code_cache");
            }
            return createFilesDirLocked(mCodeCacheDir);
        }
    }

    @Override
    public File getExternalCacheDir() {
        // Operates on primary external storage
        return getExternalCacheDirs()[0];
    }

    @Override
    public File[] getExternalCacheDirs() {

        File [] dirs=new File[]{
                new File(mMainContext.getExternalCacheDir()+"/Android/data/"+app.getInfo().info.packageName+"/cache/")
        };
        if (!dirs[0].exists())
        {
            dirs[0].mkdirs();
        }

        /*
        synchronized (mSync) {
            if (mExternalCacheDirs == null) {
                mExternalCacheDirs = Environment.buildExternalStorageAppCacheDirs(getPackageName());
            }

            // Create dirs if needed
            return ensureDirsExistOrFilter(mExternalCacheDirs);
        }
        */

        return dirs;
    }

    @Override
    public File[] getExternalMediaDirs() {

        /*File [] dirs=new File[]{
                new File(mMainContext.getExternalFilesDir(type)+"/Android/data/"+app.getInfo().info.packageName+"/files/")
        };
        if (!dirs[0].exists())
        {
            dirs[0].mkdirs();
        }*/

        /*
        synchronized (mSync) {
            if (mExternalMediaDirs == null) {
                mExternalMediaDirs = Environment.buildExternalStorageAppMediaDirs(getPackageName());
            }

            // Create dirs if needed
            return ensureDirsExistOrFilter(mExternalMediaDirs);
        }
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mMainContext.getExternalMediaDirs();
        }

        return null;
    }

    @Override
    public File getFileStreamPath(String name) {
        return makeFilename(getFilesDir(), name);
    }

    @Override
    public String[] fileList() {
        final String[] list = getFilesDir().list();
        return (list != null) ? list : EMPTY_STRING_ARRAY;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return openOrCreateDatabase(name, mode, factory, null);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        File f = validateFilePath(name, true);
        int flags = SQLiteDatabase.CREATE_IF_NECESSARY;
        if ((mode & MODE_ENABLE_WRITE_AHEAD_LOGGING) != 0) {
            flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(f.getPath(), factory, flags, errorHandler);
        //setFilePermissionsFromMode(f.getPath(), mode, 0);
        return db;
    }

    @Override
    public boolean deleteDatabase(String name) {
        try {
            File f = validateFilePath(name, false);
            return f.delete();
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public File getDatabasePath(String name) {
        return validateFilePath(name, false);
    }

    @Override
    public String[] databaseList() {
        final String[] list = getDatabasesDir().list();
        return (list != null) ? list : EMPTY_STRING_ARRAY;
    }


    private File getDatabasesDir() {
        synchronized (mSync) {
            if (mDatabasesDir == null) {
                mDatabasesDir = new File(getDataDirFile(), "databases");
            }
            if (mDatabasesDir.getPath().equals("databases")) {
                mDatabasesDir = new File("/data/system");
            }
            return mDatabasesDir;
        }
    }

    @Override
    @Deprecated
    public Drawable getWallpaper() {
        return getWallpaperManager().getDrawable();
    }

    @Override
    @Deprecated
    public Drawable peekWallpaper() {
        return getWallpaperManager().peekDrawable();
    }

    @Override
    @Deprecated
    public int getWallpaperDesiredMinimumWidth() {
        return getWallpaperManager().getDesiredMinimumWidth();
    }

    @Override
    @Deprecated
    public int getWallpaperDesiredMinimumHeight() {
        return getWallpaperManager().getDesiredMinimumHeight();
    }

    @Override
    @Deprecated
    public void setWallpaper(Bitmap bitmap) throws IOException {
        getWallpaperManager().setBitmap(bitmap);
    }

    @Override
    @Deprecated
    public void setWallpaper(InputStream data) throws IOException {
        getWallpaperManager().setStream(data);
    }

    @Override
    @Deprecated
    public void clearWallpaper() throws IOException {
        getWallpaperManager().clear();
    }

    private WallpaperManager getWallpaperManager() {
        return (WallpaperManager)invoke(getImpl(mMainContext).getClass(),mMainContext,"getWallpaperManager");
    }

    @Override
    public void startActivity(Intent intent) {
        warnIfCallingFromSystemProcess();
        startActivity(intent, null);
    }

    /** @hide */
    public void startActivityAsUser(Intent intent, UserHandle user) {
        startActivityAsUser(intent, null, user);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        intent=app.startActivityIntent(intent.getComponent().getClassName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMainContext.startActivity(intent,options);
        }
    }

    /** @hide */
    public void startActivityAsUser(Intent intent, Bundle options, UserHandle user) {
        intent=app.startActivityIntent(intent.getComponent().getClassName());
        invoke(getImpl(mMainContext).getClass(),mMainContext,"startActivityAsUser",new Class[]{Intent.class,Bundle.class,UserHandle.class},
                intent,options,user);
    }

    @Override
    public void startActivities(Intent[] intents) {
        warnIfCallingFromSystemProcess();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivities(intents, null);
        }
    }


    public void startActivitiesAsUser(Intent[] intents, Bundle options, UserHandle userHandle) {
        mMainContext.startActivities(intents, options);
        /*
        if ((intents[0].getFlags()&Intent.FLAG_ACTIVITY_NEW_TASK) == 0) {
            throw new AndroidRuntimeException(
                    "Calling startActivities() from outside of an Activity "
                            + " context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent."
                            + " Is this really what you want?");
        }
        mMainThread.getInstrumentation().execStartActivitiesAsUser(
                getOuterContext(), mMainThread.getApplicationThread(), null,
                (Activity) null, intents, options, userHandle.getIdentifier());
                */
    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {

        mMainContext.startActivities(intents, options);
        /*

        warnIfCallingFromSystemProcess();
        if ((intents[0].getFlags()&Intent.FLAG_ACTIVITY_NEW_TASK) == 0) {
            throw new AndroidRuntimeException(
                    "Calling startActivities() from outside of an Activity "
                            + " context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent."
                            + " Is this really what you want?");
        }
        mMainThread.getInstrumentation().execStartActivities(
                getOuterContext(), mMainThread.getApplicationThread(), null,
                (Activity) null, intents, options);
                */
    }


    @Override
    public void startIntentSender(IntentSender intent,
                                  Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags)
            throws IntentSender.SendIntentException {
        startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent,
                                  int flagsMask, int flagsValues, int extraFlags, Bundle options)
            throws IntentSender.SendIntentException {
        /*try {
            String resolvedType = null;
            if (fillInIntent != null) {
                fillInIntent.migrateExtraStreamToClipData();
                fillInIntent.prepareToLeaveProcess();
                resolvedType = fillInIntent.resolveTypeIfNeeded(getContentResolver());
            }
            int result = ActivityManagerNative.getDefault()
                    .startActivityIntentSender(mMainThread.getApplicationThread(), intent,
                            fillInIntent, resolvedType, null, null,
                            0, flagsMask, flagsValues, options);
            if (result == ActivityManager.START_CANCELED) {
                throw new IntentSender.SendIntentException();
            }
            Instrumentation.checkStartActivityResult(result, null);
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMainContext.startIntentSender(intent,fillInIntent,flagsMask,flagsValues,extraFlags,options);
        }
    }

    @Override
    public void sendBroadcast(Intent intent) {
        mMainContext.sendBroadcast(intent);
        /*
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, null, AppOpsManager.OP_NONE, null, false, false,
                    getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    public void sendBroadcast(Intent intent, String receiverPermission) {
        mMainContext.sendBroadcast(intent,receiverPermission);
        /*
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        String[] receiverPermissions = receiverPermission == null ? null
                : new String[] {receiverPermission};
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, receiverPermissions, AppOpsManager.OP_NONE,
                    null, false, false, getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }


    public void sendBroadcastMultiplePermissions(Intent intent, String[] receiverPermissions) {
        invoke(getImpl(mMainContext).getClass(),mMainContext,"sendBroadcastMultiplePermissions",
                new Class[]{Intent.class,String[].class},intent,receiverPermissions);
        /*
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, receiverPermissions, AppOpsManager.OP_NONE,
                    null, false, false, getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }


    public void sendBroadcast(Intent intent, String receiverPermission, Bundle options) {
        invoke(getImpl(mMainContext).getClass(),mMainContext,"sendBroadcast",
                new Class[]{Intent.class,String.class,Bundle.class},intent,receiverPermission,options);
        /*
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        String[] receiverPermissions = receiverPermission == null ? null
                : new String[] {receiverPermission};
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, receiverPermissions, AppOpsManager.OP_NONE,
                    options, false, false, getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    public void sendBroadcast(Intent intent, String receiverPermission, int appOp) {
        invoke(getImpl(mMainContext).getClass(),mMainContext,"sendBroadcast",
                new Class[]{Intent.class,String.class,int.class},intent,receiverPermission,appOp);
        /*
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        String[] receiverPermissions = receiverPermission == null ? null
                : new String[] {receiverPermission};
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, receiverPermissions, appOp, null, false, false,
                    getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }


    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        mMainContext.sendOrderedBroadcast(intent,receiverPermission);
        /*
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        String[] receiverPermissions = receiverPermission == null ? null
                : new String[] {receiverPermission};
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, receiverPermissions, AppOpsManager.OP_NONE,
                    null, true, false, getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    public void sendOrderedBroadcast(Intent intent,
                                     String receiverPermission, BroadcastReceiver resultReceiver,
                                     Handler scheduler, int initialCode, String initialData,
                                     Bundle initialExtras) {
        mMainContext.sendOrderedBroadcast(intent, receiverPermission,
                resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }


    public void sendOrderedBroadcast(Intent intent,
                                     String receiverPermission, Bundle options, BroadcastReceiver resultReceiver,
                                     Handler scheduler, int initialCode, String initialData,
                                     Bundle initialExtras) {
       // sendOrderedBroadcast(intent, receiverPermission, AppOpsManager.OP_NONE,
        //        resultReceiver, scheduler, initialCode, initialData, initialExtras, options);
    }


    public void sendOrderedBroadcast(Intent intent,
                                     String receiverPermission, int appOp, BroadcastReceiver resultReceiver,
                                     Handler scheduler, int initialCode, String initialData,
                                     Bundle initialExtras) {
        sendOrderedBroadcast(intent, receiverPermission, appOp,
                resultReceiver, scheduler, initialCode, initialData, initialExtras, null);
    }

    void sendOrderedBroadcast(Intent intent,
                              String receiverPermission, int appOp, BroadcastReceiver resultReceiver,
                              Handler scheduler, int initialCode, String initialData,
                              Bundle initialExtras, Bundle options) {
        invoke(getImpl(mMainContext).getClass(),mMainContext,"sendOrderedBroadcast",
                new Class[]{String.class,int.class,BroadcastReceiver.class,Handler.class,int.class,String.class,Bundle.class,Bundle.class},
                intent,receiverPermission,appOp,resultReceiver,scheduler,initialCode,initialData,initialExtras,options);
        /*
        warnIfCallingFromSystemProcess();
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = mPackageInfo.getReceiverDispatcher(
                        resultReceiver, getOuterContext(), scheduler,
                        mMainThread.getInstrumentation(), false);
            } else {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(
                        resultReceiver, getOuterContext(), scheduler, null, false).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        String[] receiverPermissions = receiverPermission == null ? null
                : new String[] {receiverPermission};
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, rd,
                    initialCode, initialData, initialExtras, receiverPermissions, appOp,
                    options, true, false, getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMainContext.sendBroadcastAsUser(intent,user);
        }
        /*
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(mMainThread.getApplicationThread(),
                    intent, resolvedType, null, Activity.RESULT_OK, null, null, null,
                    AppOpsManager.OP_NONE, null, false, false, user.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user,
                                    String receiverPermission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMainContext.sendBroadcastAsUser(intent,user,receiverPermission);
        }
    }


    public void sendBroadcastAsUser(Intent intent, UserHandle user,
                                    String receiverPermission, int appOp) {
        invoke(getImpl(mMainContext).getClass(),mMainContext,"sendBroadcastAsUser",
                new Class[]{Intent.class,UserHandle.class,String.class,int.class},
                intent,user,receiverPermission,appOp);
        /*
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        String[] receiverPermissions = receiverPermission == null ? null
                : new String[] {receiverPermission};
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, receiverPermissions, appOp, null, false, false,
                    user.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user,
                                           String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler,
                                           int initialCode, String initialData, Bundle initialExtras) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMainContext.sendOrderedBroadcastAsUser(intent,user,receiverPermission,resultReceiver,scheduler,initialCode,initialData,initialExtras);
        }

       // sendOrderedBroadcastAsUser(intent, user, receiverPermission, AppOpsManager.OP_NONE,
        //        null, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }


    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user,
                                           String receiverPermission, int appOp, BroadcastReceiver resultReceiver,
                                           Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        sendOrderedBroadcastAsUser(intent, user, receiverPermission, appOp,
                null, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }


    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user,
                                           String receiverPermission, int appOp, Bundle options, BroadcastReceiver resultReceiver,
                                           Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        invoke(getImpl(mMainContext).getClass(),mMainContext,"sendOrderedBroadcastAsUser",
                new Class[]{Intent.class,UserHandle.class,String.class,int.class,Bundle.class,BroadcastReceiver.class,Handler.class,int.class,String.class,Bundle.class},
                intent,user,receiverPermission,appOp,options,resultReceiver,scheduler,initialCode,initialData,initialExtras);
        /*
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = mPackageInfo.getReceiverDispatcher(
                        resultReceiver, getOuterContext(), scheduler,
                        mMainThread.getInstrumentation(), false);
            } else {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(resultReceiver, getOuterContext(),
                        scheduler, null, false).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        String[] receiverPermissions = receiverPermission == null ? null
                : new String[] {receiverPermission};
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, rd,
                    initialCode, initialData, initialExtras, receiverPermissions,
                    appOp, options, true, false, user.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    @Deprecated
    public void sendStickyBroadcast(Intent intent) {
        mMainContext.sendStickyBroadcast(intent);
        /*
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, null, AppOpsManager.OP_NONE, null, false, true,
                    getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    @Deprecated
    public void sendStickyOrderedBroadcast(Intent intent,
                                           BroadcastReceiver resultReceiver,
                                           Handler scheduler, int initialCode, String initialData,
                                           Bundle initialExtras) {
        mMainContext.sendStickyOrderedBroadcast(intent,resultReceiver,scheduler,initialCode,initialData,initialExtras);
        /*
        warnIfCallingFromSystemProcess();
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = mPackageInfo.getReceiverDispatcher(
                        resultReceiver, getOuterContext(), scheduler,
                        mMainThread.getInstrumentation(), false);
            } else {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(
                        resultReceiver, getOuterContext(), scheduler, null, false).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, rd,
                    initialCode, initialData, initialExtras, null,
                    AppOpsManager.OP_NONE, null, true, true, getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    @Deprecated
    public void removeStickyBroadcast(Intent intent) {
        mMainContext.removeStickyBroadcast(intent);
        /*
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        if (resolvedType != null) {
            intent = new Intent(intent);
            intent.setDataAndType(intent.getData(), resolvedType);
        }
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().unbroadcastIntent(
                    mMainThread.getApplicationThread(), intent, getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    @Deprecated
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMainContext.sendStickyBroadcastAsUser(intent,user);
        }
        /*
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, null,
                    Activity.RESULT_OK, null, null, null, AppOpsManager.OP_NONE, null, false, true, user.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    @Deprecated
    public void sendStickyOrderedBroadcastAsUser(Intent intent,
                                                 UserHandle user, BroadcastReceiver resultReceiver,
                                                 Handler scheduler, int initialCode, String initialData,
                                                 Bundle initialExtras) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMainContext.sendStickyOrderedBroadcastAsUser(intent,user,resultReceiver,scheduler,initialCode,initialData,initialExtras);
        }
        /*
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = mPackageInfo.getReceiverDispatcher(
                        resultReceiver, getOuterContext(), scheduler,
                        mMainThread.getInstrumentation(), false);
            } else {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(
                        resultReceiver, getOuterContext(), scheduler, null, false).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(
                    mMainThread.getApplicationThread(), intent, resolvedType, rd,
                    initialCode, initialData, initialExtras, null,
                    AppOpsManager.OP_NONE, null, true, true, user.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    @Deprecated
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMainContext.removeStickyBroadcastAsUser(intent,user);
        }
        /*
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        if (resolvedType != null) {
            intent = new Intent(intent);
            intent.setDataAndType(intent.getData(), resolvedType);
        }
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().unbroadcastIntent(
                    mMainThread.getApplicationThread(), intent, user.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return mMainContext.registerReceiver(receiver, filter);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
                                   String broadcastPermission, Handler scheduler) {
        return mMainContext.registerReceiver(receiver, filter,broadcastPermission,scheduler);
    }


    public Intent registerReceiverAsUser(BroadcastReceiver receiver, UserHandle user,
                                         IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return (Intent)invoke(getImpl(mMainContext).getClass(),mMainContext,"registerReceiverAsUser",
                new Class[]{BroadcastReceiver.class,UserHandle.class,IntentFilter.class,String.class,Handler.class},
                receiver,user,filter,broadcastPermission,scheduler);
        /*
        return registerReceiverInternal(receiver, user.getIdentifier(),
                filter, broadcastPermission, scheduler, getOuterContext());
    */
    }

    private Intent registerReceiverInternal(BroadcastReceiver receiver, int userId,
                                            IntentFilter filter, String broadcastPermission,
                                            Handler scheduler, Context context) {
        return (Intent)invoke(getImpl(mMainContext).getClass(),mMainContext,"registerReceiverInternal",
                new Class[]{BroadcastReceiver.class,IntentFilter.class,String.class,Handler.class,Context.class},
                receiver,userId,filter,broadcastPermission,scheduler,context);

        /*
        IIntentReceiver rd = null;
        if (receiver != null) {
            if (mPackageInfo != null && context != null) {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = mPackageInfo.getReceiverDispatcher(
                        receiver, context, scheduler,
                        mMainThread.getInstrumentation(), true);
            } else {
                if (scheduler == null) {
                    scheduler = mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(
                        receiver, context, scheduler, null, true).getIIntentReceiver();
            }
        }
        try {
            return ActivityManagerNative.getDefault().registerReceiver(
                    mMainThread.getApplicationThread(), mBasePackageName,
                    rd, filter, broadcastPermission, userId);
        } catch (RemoteException e) {
            return null;
        }
        */
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        mMainContext.unregisterReceiver(receiver);
        /*
        if (mPackageInfo != null) {
            IIntentReceiver rd = mPackageInfo.forgetReceiverDispatcher(
                    getOuterContext(), receiver);
            try {
                ActivityManagerNative.getDefault().unregisterReceiver(rd);
            } catch (RemoteException e) {
            }
        } else {
            throw new RuntimeException("Not supported in system context");
        }
        */
    }

    private void validateServiceIntent(Intent service) {

        if (service.getComponent() == null && service.getPackage() == null) {
            if (getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.LOLLIPOP) {
                IllegalArgumentException ex = new IllegalArgumentException(
                        "Service Intent must be explicit: " + service);
                throw ex;
            } else {
                Log.w(TAG, "Implicit intents with startService are not safe: " + service
                        + " " );
            }
        }
    }

    @Override
    public ComponentName startService(Intent service) {     //undefined
        service=app.startServiceIntent(service);
        //service=new Intent(mMainContext,TestService.class);
        ComponentName cn=mMainContext.startService(service);
        Log.e("xx","startSer"+cn.toString());
        return cn;
    }

    @Override
    public boolean stopService(Intent service) {        //undefined

        return mMainContext.stopService(service);
    }

    public ComponentName startServiceAsUser(Intent service, UserHandle user) {
        return startServiceCommon(service, user);
    }

    private ComponentName startServiceCommon(Intent service, UserHandle user) {
        return (ComponentName) invoke(getImpl(mMainContext).getClass(),mMainContext,"startServiceCommon",
                new Class[]{Intent.class,UserHandle.class},
                service,user);
        /*
        try {
            validateServiceIntent(service);
            service.prepareToLeaveProcess();
            ComponentName cn = ActivityManagerNative.getDefault().startService(
                    mMainThread.getApplicationThread(), service, service.resolveTypeIfNeeded(
                            getContentResolver()), getOpPackageName(), user.getIdentifier());
            if (cn != null) {
                if (cn.getPackageName().equals("!")) {
                    throw new SecurityException(
                            "Not allowed to start service " + service
                                    + " without permission " + cn.getClassName());
                } else if (cn.getPackageName().equals("!!")) {
                    throw new SecurityException(
                            "Unable to start service " + service
                                    + ": " + cn.getClassName());
                }
            }
            return cn;
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }


    public boolean stopServiceAsUser(Intent service, UserHandle user) {
        return stopServiceCommon(service, user);
    }

    private boolean stopServiceCommon(Intent service, UserHandle user) {
        return (boolean)invoke(getImpl(mMainContext).getClass(),mMainContext,"stopServiceCommon",
                new Class[]{Intent.class,UserHandle.class},
                service,user);
        /*
        try {
            validateServiceIntent(service);
            service.prepareToLeaveProcess();
            int res = ActivityManagerNative.getDefault().stopService(
                    mMainThread.getApplicationThread(), service,
                    service.resolveTypeIfNeeded(getContentResolver()), user.getIdentifier());
            if (res < 0) {
                throw new SecurityException(
                        "Not allowed to stop service " + service);
            }
            return res != 0;
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn,
                               int flags) {

        service=app.startServiceIntent(service);
        return mMainContext.bindService(service, conn, flags);
        /*
        warnIfCallingFromSystemProcess();
        return bindServiceCommon(service, conn, flags, Process.myUserHandle());
        */
    }

    /** @hide */
    public boolean bindServiceAsUser(Intent service, ServiceConnection conn, int flags,
                                     UserHandle user) {
        return bindServiceCommon(service, conn, flags, user);
    }

    private boolean bindServiceCommon(Intent service, ServiceConnection conn, int flags,
                                      UserHandle user) {
        return (boolean)invoke(getImpl(mMainContext).getClass(),mMainContext,"bindServiceCommon",
                new Class[]{Intent.class,ServiceConnection.class,int.class,UserHandle.class},
                service,conn,flags,user);
        /*
        IServiceConnection sd;
        if (conn == null) {
            throw new IllegalArgumentException("connection is null");
        }
        if (mPackageInfo != null) {
            sd = mPackageInfo.getServiceDispatcher(conn, getOuterContext(),
                    mMainThread.getHandler(), flags);
        } else {
            throw new RuntimeException("Not supported in system context");
        }
        validateServiceIntent(service);
        try {
            IBinder token = getActivityToken();
            if (token == null && (flags&BIND_AUTO_CREATE) == 0 && mPackageInfo != null
                    && mPackageInfo.getApplicationInfo().targetSdkVersion
                    < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                flags |= BIND_WAIVE_PRIORITY;
            }
            service.prepareToLeaveProcess();
            int res = ActivityManagerNative.getDefault().bindService(
                    mMainThread.getApplicationThread(), getActivityToken(), service,
                    service.resolveTypeIfNeeded(getContentResolver()),
                    sd, flags, getOpPackageName(), user.getIdentifier());
            if (res < 0) {
                throw new SecurityException(
                        "Not allowed to bind to service " + service);
            }
            return res != 0;
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        */
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        mMainContext.unbindService(conn);
        /*
        if (conn == null) {
            throw new IllegalArgumentException("connection is null");
        }
        if (mPackageInfo != null) {
            IServiceConnection sd = mPackageInfo.forgetServiceDispatcher(
                    getOuterContext(), conn);
            try {
                ActivityManagerNative.getDefault().unbindService(sd);
            } catch (RemoteException e) {
            }
        } else {
            throw new RuntimeException("Not supported in system context");
        }
        */
    }

    @Override
    public boolean startInstrumentation(ComponentName className,
                                        String profileFile, Bundle arguments) {
        return (boolean) invoke(getImpl(mMainContext).getClass(),mMainContext,"startInstrumentation",
                new Class[]{ComponentName.class,String.class,Bundle.class},
                className,profileFile,arguments);
        /*
        try {
            if (arguments != null) {
                arguments.setAllowFds(false);
            }
            return ActivityManagerNative.getDefault().startInstrumentation(
                    className, profileFile, 0, arguments, null, null, getUserId(),
                    null );
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
    */
    }

    @Override
    public Object getSystemService(String name) {
        return mMainContext.getSystemService(name);
    }

    @Override
    public String getSystemServiceName(Class<?> serviceClass) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mMainContext.getSystemServiceName(serviceClass);
        }

        return null;
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return mMainContext.checkPermission(permission,pid,uid);
        /*
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        try {
            return ActivityManagerNative.getDefault().checkPermission(
                    permission, pid, uid);
        } catch (RemoteException e) {
            return PackageManager.PERMISSION_DENIED;
        }
        */
    }

    /** @hide */

    public int checkPermission(String permission, int pid, int uid, IBinder callerToken) {
        return (int)invoke(getImpl(mMainContext).getClass(),mMainContext,"checkPermission",
                new Class[]{String.class,int.class,int.class,IBinder.class},
                permission,pid,uid,callerToken);
        /*
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        try {
            return ActivityManagerNative.getDefault().checkPermissionWithToken(
                    permission, pid, uid, callerToken);
        } catch (RemoteException e) {
            return PackageManager.PERMISSION_DENIED;
        }
        */
    }

    @Override
    public int checkCallingPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        int pid = Binder.getCallingPid();
        if (pid != Process.myPid()) {
            return checkPermission(permission, pid, Binder.getCallingUid());
        }
        return PackageManager.PERMISSION_DENIED;
    }

    @Override
    public int checkCallingOrSelfPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        return checkPermission(permission, Binder.getCallingPid(),
                Binder.getCallingUid());
    }

    @Override
    public int checkSelfPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        return checkPermission(permission, Process.myPid(), Process.myUid());
    }

    private void enforce(
            String permission, int resultOfCheck,
            boolean selfToo, int uid, String message) {

        if (resultOfCheck != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException(
                    (message != null ? (message + ": ") : "") +
                            (selfToo
                                    ? "Neither user " + uid + " nor current process has "
                                    : "uid " + uid + " does not have ") +
                            permission +
                            ".");
        }
    }

    @Override
    public void enforcePermission(
            String permission, int pid, int uid, String message) {
        mMainContext.enforcePermission(permission,pid,uid,message);
        /*
        enforce(permission,
                checkPermission(permission, pid, uid),
                false,
                uid,
                message);
                */
    }

    @Override
    public void enforceCallingPermission(String permission, String message) {
        mMainContext.enforceCallingPermission(permission, message);
/*
        enforce(permission,
                checkCallingPermission(permission),
                false,
                Binder.getCallingUid(),
                message);
                */
    }

    @Override
    public void enforceCallingOrSelfPermission(
            String permission, String message) {
        mMainContext.enforceCallingOrSelfPermission(permission,message);

        /*
        enforce(permission,
                checkCallingOrSelfPermission(permission),
                true,
                Binder.getCallingUid(),
                message);
                */
    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        mMainContext.grantUriPermission(toPackage, uri, modeFlags);
        /*
        try {
            ActivityManagerNative.getDefault().grantUriPermission(
                    mMainThread.getApplicationThread(), toPackage,
                    ContentProvider.getUriWithoutUserId(uri), modeFlags, resolveUserId(uri));
        } catch (RemoteException e) {
        }
        */
    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {
        mMainContext.revokeUriPermission(uri,modeFlags);

        /*
        try {
            ActivityManagerNative.getDefault().revokeUriPermission(
                    mMainThread.getApplicationThread(),
                    ContentProvider.getUriWithoutUserId(uri), modeFlags, resolveUserId(uri));
        } catch (RemoteException e) {
        }
        */
    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return mMainContext.checkUriPermission(uri, pid, uid, modeFlags);

        /*
        try {
            return ActivityManagerNative.getDefault().checkUriPermission(
                    ContentProvider.getUriWithoutUserId(uri), pid, uid, modeFlags,
                    resolveUserId(uri), null);
        } catch (RemoteException e) {
            return PackageManager.PERMISSION_DENIED;
        }
        */
    }

    /** @hide */

    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags, IBinder callerToken) {
        return (int)invoke(getImpl(mMainContext).getClass(),mMainContext,"checkUriPermission",
                new Class[]{Uri.class,int.class,int.class,int.class,IBinder.class},
                uri,pid,uid,modeFlags,callerToken);
        /*
        try {
            return ActivityManagerNative.getDefault().checkUriPermission(
                    ContentProvider.getUriWithoutUserId(uri), pid, uid, modeFlags,
                    resolveUserId(uri), callerToken);
        } catch (RemoteException e) {
            return PackageManager.PERMISSION_DENIED;
        }
        */
    }

    private int resolveUserId(Uri uri) {
        //return ContentProvider.getUserIdFromUri(uri, getUserId());
        return (int)invoke(getImpl(mMainContext).getClass(),mMainContext,"resolveUserId",new Class[]{Uri.class},uri);
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        int pid = Binder.getCallingPid();
        if (pid != Process.myPid()) {
            return checkUriPermission(uri, pid,
                    Binder.getCallingUid(), modeFlags);
        }
        return PackageManager.PERMISSION_DENIED;
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return checkUriPermission(uri, Binder.getCallingPid(),
                Binder.getCallingUid(), modeFlags);
    }

    @Override
    public int checkUriPermission(Uri uri, String readPermission,
                                  String writePermission, int pid, int uid, int modeFlags) {
        if (DEBUG) {
            Log.i("foo", "checkUriPermission: uri=" + uri + "readPermission="
                    + readPermission + " writePermission=" + writePermission
                    + " pid=" + pid + " uid=" + uid + " mode" + modeFlags);
        }
        if ((modeFlags&Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0) {
            if (readPermission == null
                    || checkPermission(readPermission, pid, uid)
                    == PackageManager.PERMISSION_GRANTED) {
                return PackageManager.PERMISSION_GRANTED;
            }
        }
        if ((modeFlags&Intent.FLAG_GRANT_WRITE_URI_PERMISSION) != 0) {
            if (writePermission == null
                    || checkPermission(writePermission, pid, uid)
                    == PackageManager.PERMISSION_GRANTED) {
                return PackageManager.PERMISSION_GRANTED;
            }
        }
        return uri != null ? checkUriPermission(uri, pid, uid, modeFlags)
                : PackageManager.PERMISSION_DENIED;
    }

    private String uriModeFlagToString(int uriModeFlags) {
        StringBuilder builder = new StringBuilder();
        if ((uriModeFlags & Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0) {
            builder.append("read and ");
        }
        if ((uriModeFlags & Intent.FLAG_GRANT_WRITE_URI_PERMISSION) != 0) {
            builder.append("write and ");
        }
        if ((uriModeFlags & Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION) != 0) {
            builder.append("persistable and ");
        }
        if ((uriModeFlags & Intent.FLAG_GRANT_PREFIX_URI_PERMISSION) != 0) {
            builder.append("prefix and ");
        }

        if (builder.length() > 5) {
            builder.setLength(builder.length() - 5);
            return builder.toString();
        } else {
            throw new IllegalArgumentException("Unknown permission mode flags: " + uriModeFlags);
        }
    }

    private void enforceForUri(
            int modeFlags, int resultOfCheck, boolean selfToo,
            int uid, Uri uri, String message) {
        if (resultOfCheck != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException(
                    (message != null ? (message + ": ") : "") +
                            (selfToo
                                    ? "Neither user " + uid + " nor current process has "
                                    : "User " + uid + " does not have ") +
                            uriModeFlagToString(modeFlags) +
                            " permission on " +
                            uri +
                            ".");
        }
    }

    @Override
    public void enforceUriPermission(
            Uri uri, int pid, int uid, int modeFlags, String message) {
        enforceForUri(
                modeFlags, checkUriPermission(uri, pid, uid, modeFlags),
                false, uid, uri, message);
    }

    @Override
    public void enforceCallingUriPermission(
            Uri uri, int modeFlags, String message) {
        enforceForUri(
                modeFlags, checkCallingUriPermission(uri, modeFlags),
                false,
                Binder.getCallingUid(), uri, message);
    }

    @Override
    public void enforceCallingOrSelfUriPermission(
            Uri uri, int modeFlags, String message) {
        enforceForUri(
                modeFlags,
                checkCallingOrSelfUriPermission(uri, modeFlags), true,
                Binder.getCallingUid(), uri, message);
    }

    @Override
    public void enforceUriPermission(
            Uri uri, String readPermission, String writePermission,
            int pid, int uid, int modeFlags, String message) {
        enforceForUri(modeFlags,
                checkUriPermission(
                        uri, readPermission, writePermission, pid, uid,
                        modeFlags),
                false,
                uid,
                uri,
                message);
    }

    /**
     * Logs a warning if the system process directly called a method such as
     * {@link #startService(Intent)} instead of {@link #startServiceAsUser(Intent, UserHandle)}.
     * The "AsUser" variants allow us to properly enforce the user's restrictions.
     */
    private void warnIfCallingFromSystemProcess() {
    }

    /*
    @Override
    public Context createApplicationContext(ApplicationInfo application, int flags)
            throws PackageManager.NameNotFoundException {
        LoadedApk pi = mMainThread.getPackageInfo(application, mResources.getCompatibilityInfo(),
                flags | CONTEXT_REGISTER_PACKAGE);
        if (pi != null) {
            final boolean restricted = (flags & CONTEXT_RESTRICTED) == CONTEXT_RESTRICTED;
            ContextImpl c = new ContextImpl(this, mMainThread, pi, mActivityToken,
                    new UserHandle(UserHandle.getUserId(application.uid)), restricted,
                    mDisplay, null, Display.INVALID_DISPLAY);
            if (c.mResources != null) {
                return c;
            }
        }

        throw new PackageManager.NameNotFoundException(
                "Application package " + application.packageName + " not found");
    }
*/
    @Override
    public Context createPackageContext(String packageName, int flags)
            throws PackageManager.NameNotFoundException {
        return mMainContext.createPackageContext(packageName, flags);

   //     return createPackageContextAsUser(packageName, flags,
    //            mUser != null ? mUser : Process.myUserHandle());
    }
/*
    @Override
    public Context createPackageContextAsUser(String packageName, int flags, UserHandle user)
            throws PackageManager.NameNotFoundException {
        final boolean restricted = (flags & CONTEXT_RESTRICTED) == CONTEXT_RESTRICTED;
        if (packageName.equals("system") || packageName.equals("android")) {
            return new ContextImpl(this, mMainThread, mPackageInfo, mActivityToken,
                    user, restricted, mDisplay, null, Display.INVALID_DISPLAY);
        }

        LoadedApk pi = mMainThread.getPackageInfo(packageName, mResources.getCompatibilityInfo(),
                flags | CONTEXT_REGISTER_PACKAGE, user.getIdentifier());
        if (pi != null) {
            ContextImpl c = new ContextImpl(this, mMainThread, pi, mActivityToken,
                    user, restricted, mDisplay, null, Display.INVALID_DISPLAY);
            if (c.mResources != null) {
                return c;
            }
        }

        // Should be a better exception.
        throw new PackageManager.NameNotFoundException(
                "Application package " + packageName + " not found");
    }
    */

    @Override
    public Context createConfigurationContext(Configuration overrideConfiguration) {

        return mMainContext.createConfigurationContext(overrideConfiguration);
        /*
        if (overrideConfiguration == null) {
            throw new IllegalArgumentException("overrideConfiguration must not be null");
        }

        return new ContextImpl(this, mMainThread, mPackageInfo, mActivityToken,
                mUser, mRestricted, mDisplay, overrideConfiguration, Display.INVALID_DISPLAY);
                */
    }

    @Override
    public Context createDisplayContext(Display display) {
        return mMainContext.createDisplayContext(display);
        /*
        if (display == null) {
            throw new IllegalArgumentException("display must not be null");
        }

        return new ContextImpl(this, mMainThread, mPackageInfo, mActivityToken,
                mUser, mRestricted, display, null, Display.INVALID_DISPLAY);*/
    }

/*
    Display getDisplay() {
        if (mDisplay != null) {
            return mDisplay;
        }
        return ResourcesManager.getInstance().getAdjustedDisplay(
                Display.DEFAULT_DISPLAY, mDisplayAdjustments);
    }

    private int getDisplayId() {
        return mDisplay != null ? mDisplay.getDisplayId() : Display.DEFAULT_DISPLAY;
    }

    @Override
    public boolean isRestricted() {
        return mRestricted;
    }

    @Override
    public DisplayAdjustments getDisplayAdjustments(int displayId) {
        return mDisplayAdjustments;
    }
*/
    private File getDataDirFile() {
        if (dataDir== null) {
            dataDir=((File)invoke(mPackageInfo,"getDataDirFile")).getAbsolutePath();

        }
        return new File(mMainContext.getFilesDir().getAbsolutePath()+dataDir);
        //return new File(dataDir);
        //throw new RuntimeException("Not supported in system context");
    }


    @Override
    public File getDir(String name, int mode) {
        name = "app_" + name;
        File file = makeFilename(getDataDirFile(), name);
        if (!file.exists()) {
            file.mkdirs();
         //   setFilePermissionsFromMode(file.getPath(), mode,
            //          FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH);
        }
        return file;
    }

    /** {@hide} */
    public int getUserId() {
        return (int)invoke(mUser,"getIdentifier");
        //return app.getInfo().info.applicationInfo.uid;
    }

    public ContextBase(Context base)
    {
        super(base);
    }

    public ContextBase(Context container, Object mainThread,
                       Object packageInfo,App app, boolean restricted,Activity activity
    ){
        this(container, mainThread, packageInfo, app, restricted);
        mOuterContext=activity;
    }

    public ContextBase(Context container, Object mainThread,
                        Object packageInfo,App app, boolean restricted
                        ){
        super(container);
        mOuterContext = this;

        mMainContext=container;
        mMainThread = mainThread;
        mRestricted = restricted;

        mUser=null;
        if (mUser == null) {
            mUser = Process.myUserHandle();
        }

        this.app=app;
        mResources=app.getRes();

        mPackageInfo = packageInfo;
        try {
            mResourcesManager = invoke(Class.forName("android.app.ResourcesManager"),null,"getInstance");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("xx","context init:"+e.toString());
        }

       // final int displayId = (createDisplayWithId != Display.INVALID_DISPLAY) ? createDisplayWithId : (display != null) ? display.getDisplayId() : Display.DEFAULT_DISPLAY;

   /*     Object compatInfo = null;
        if (container != null) {
            compatInfo = container.getDisplayAdjustments(displayId).getCompatibilityInfo();
        }
        if (compatInfo == null) {
            compatInfo = (displayId == Display.DEFAULT_DISPLAY)
                    ? packageInfo.getCompatibilityInfo()
                    : CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        }
   //     mDisplayAdjustments.setCompatibilityInfo(compatInfo);
   //     mDisplayAdjustments.setConfiguration(overrideConfiguration);

    //    mDisplay = (createDisplayWithId == Display.INVALID_DISPLAY) ? display
    //            : ResourcesManager.getInstance().getAdjustedDisplay(displayId, mDisplayAdjustments);

        Resources resources = packageInfo.getResources(mainThread);
        if (resources != null) {
            if (displayId != Display.DEFAULT_DISPLAY
                    || overrideConfiguration != null
                    || (compatInfo != null && compatInfo.applicationScale
                    != resources.getCompatibilityInfo().applicationScale)) {
                resources = mResourcesManager.getTopLevelResources(packageInfo.getResDir(),
                        packageInfo.getSplitResDirs(), packageInfo.getOverlayDirs(),
                        packageInfo.getApplicationInfo().sharedLibraryFiles, displayId,
                        overrideConfiguration, compatInfo);
            }
        }
        */
  /*      mResources = resources;

        if (container != null) {
            mBasePackageName = container.mBasePackageName;
            mOpPackageName = container.mOpPackageName;
        } else {
            mBasePackageName = packageInfo.mPackageName;
            ApplicationInfo ainfo = packageInfo.getApplicationInfo();
            if (ainfo.uid == Process.SYSTEM_UID && ainfo.uid != Process.myUid()) {
                // Special case: system components allow themselves to be loaded in to other
                // processes.  For purposes of app ops, we must then consider the context as
                // belonging to the package of this process, not the system itself, otherwise
                // the package+uid verifications in app ops will fail.
                mOpPackageName = ActivityThread.currentPackageName();
            } else {
                mOpPackageName = mBasePackageName;
            }
        }

        mContentResolver = new ApplicationContentResolver(this, mainThread, user);

        */

        mBasePackageName=null;
        mOpPackageName=null;
        mResources=app.getRes();

    }

    void installSystemApplicationInfo(ApplicationInfo info, ClassLoader classLoader) {
        invoke(mPackageInfo,"installSystemApplicationInf",new Class[]{ApplicationInfo.class,ClassLoader.class},info, classLoader);
    }

    /*
    final void scheduleFinalCleanup(String who, String what) {
        mMainThread.scheduleContextCleanup(this, who, what);
    }


    final void performFinalCleanup(String who, String what) {

        mPackageInfo.removeContextRegistrations(getOuterContext(), who, what);
    }
    */

    final Context getReceiverRestrictedContext() {
        Object x;
        return (x=invoke(getImpl(mMainContext).getClass(),mMainContext,"getReceiverRestrictedContext"))==null ? null:(Context) x;

        /*
        if (mReceiverRestrictedContext != null) {
            return mReceiverRestrictedContext;
        }
        return mReceiverRestrictedContext = new ReceiverRestrictedContext(getOuterContext());
        */
    }

    final void setOuterContext(Context context) {
        mOuterContext = context;
    }

    final Context getOuterContext() {
        return mOuterContext;
    }

    final IBinder getActivityToken() {
        return (IBinder) invoke(getImpl(mMainContext).getClass(),mMainContext,"getActivityToken");
    }

    /*

    @SuppressWarnings("deprecation")
    static void setFilePermissionsFromMode(String name, int mode,
                                           int extraPermissions) {

        int perms = FileUtils.S_IRUSR|FileUtils.S_IWUSR
                |FileUtils.S_IRGRP|FileUtils.S_IWGRP
                |extraPermissions;
        if ((mode&MODE_WORLD_READABLE) != 0) {
            perms |= FileUtils.S_IROTH;
        }
        if ((mode&MODE_WORLD_WRITEABLE) != 0) {
            perms |= FileUtils.S_IWOTH;
        }
        if (DEBUG) {
            Log.i(TAG, "File " + name + ": mode=0x" + Integer.toHexString(mode)
                    + ", perms=0x" + Integer.toHexString(perms));
        }
        FileUtils.setPermissions(name, perms, -1, -1);

    }*/

    private File validateFilePath(String name, boolean createDirectory) {
        File dir;
        File f;

        if (name.charAt(0) == File.separatorChar) {
            String dirPath = name.substring(0, name.lastIndexOf(File.separatorChar));
            dir = new File(dirPath);
            name = name.substring(name.lastIndexOf(File.separatorChar));
            f = new File(dir, name);
        } else {
            dir = getDatabasesDir();
            f = makeFilename(dir, name);
        }

        if (createDirectory && !dir.isDirectory() && dir.mkdir()) {
            //FileUtils.setPermissions(dir.getPath(),
             //       FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
             //       -1, -1);
        }

        return f;
    }


    private File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException(
                "File " + name + " contains a path separator");
    }

    /**
     * Ensure that given directories exist, trying to create them if missing. If
     * unable to create, they are filtered by replacing with {@code null}.
     */

    /*
    private File[] ensureDirsExistOrFilter(File[] dirs) {
        File[] result = new File[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            File dir = dirs[i];
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    // recheck existence in case of cross-process race
                    if (!dir.exists()) {
                        // Failing to mkdir() may be okay, since we might not have
                        // enough permissions; ask vold to create on our behalf.
                        final IMountService mount = IMountService.Stub.asInterface(
                                ServiceManager.getService("mount"));
                        try {
                            final int res = mount.mkdirs(getPackageName(), dir.getAbsolutePath());
                            if (res != 0) {
                                Log.w(TAG, "Failed to ensure " + dir + ": " + res);
                                dir = null;
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Failed to ensure " + dir + ": " + e);
                            dir = null;
                        }
                    }
                }
            }
            result[i] = dir;
        }
        return result;
    }

    */

    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------

   /* private static final class ApplicationContentResolver extends ContentResolver {
        private final Object mMainThread;
        private final UserHandle mUser;

        public ApplicationContentResolver(
                Context context, Object mainThread, UserHandle user) {
            super(context);
            mMainThread = Preconditions.checkNotNull(mainThread);
            mUser = Preconditions.checkNotNull(user);
        }

        @Override
        protected IContentProvider acquireProvider(Context context, String auth) {
            return mMainThread.acquireProvider(context,
                    ContentProvider.getAuthorityWithoutUserId(auth),
                    resolveUserIdFromAuthority(auth), true);
        }

        @Override
        protected IContentProvider acquireExistingProvider(Context context, String auth) {
            return mMainThread.acquireExistingProvider(context,
                    ContentProvider.getAuthorityWithoutUserId(auth),
                    resolveUserIdFromAuthority(auth), true);
        }

        @Override
        public boolean releaseProvider(IContentProvider provider) {
            return mMainThread.releaseProvider(provider, true);
        }

        @Override
        protected IContentProvider acquireUnstableProvider(Context c, String auth) {
            return mMainThread.acquireProvider(c,
                    ContentProvider.getAuthorityWithoutUserId(auth),
                    resolveUserIdFromAuthority(auth), false);
        }

        @Override
        public boolean releaseUnstableProvider(IContentProvider icp) {
            return mMainThread.releaseProvider(icp, false);
        }

        @Override
        public void unstableProviderDied(IContentProvider icp) {
            mMainThread.handleUnstableProviderDied(icp.asBinder(), true);
        }

        @Override
        public void appNotRespondingViaProvider(IContentProvider icp) {
            mMainThread.appNotRespondingViaProvider(icp.asBinder());
        }

        /**
         * @hide
         */

/*
        protected int resolveUserIdFromAuthority(String auth) {
            return ContentProvider.getUserIdFromAuthority(auth, mUser.getIdentifier());
        }
    }*/
}
