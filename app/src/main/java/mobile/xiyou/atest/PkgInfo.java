package mobile.xiyou.atest;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static mobile.xiyou.atest.Rf.*;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class PkgInfo {

    public PackageInfo info;
    public String mainClass="";
    public Object pkg;
    public IntentFilters intents=null;

    public PkgInfo(PackageInfo i,String main,Object pkg) {
        this.info=i;
        this.mainClass=main;
        this.pkg=pkg;
    }

    public static class IntentFilters
    {
        public HashMap<String,List<IntentFilter>> activity,receiver;

        public IntentFilters(Object pkg)
        {
            activity=new HashMap<>();
            List acs= (List) readField(pkg,"activities");
            for (int i=0;i<acs.size();i++)
            {
                ActivityInfo ai= (ActivityInfo) readField(acs.get(i),"info");
                activity.put(ai.name,(List<IntentFilter>) readField(acs.get(i),"intents"));
            }

            receiver=new HashMap<>();
            List rec= (List) readField(pkg,"receivers");
            for (int i=0;i<rec.size();i++)
            {
                ActivityInfo ai= (ActivityInfo) readField(acs.get(i),"info");
                List xx=(List)readField(acs.get(i),"intents");
                Log.e("xx","if size:"+xx.size());
                receiver.put(ai.name,(List<IntentFilter>)xx );
            }
        }
    }

    public static PkgInfo getPackageArchiveInfo(String archiveFilePath, int flags) {
        Object pkg=null,state=null;
        Method generate=null;
        try {
            Class Parser = Class.forName("android.content.pm.PackageParser");
            final Object parser = Parser.newInstance();
            final File apkFile = new File(archiveFilePath);
            Method []ms=Parser.getDeclaredMethods();

            Method parse = Parser.getMethod("parseMonolithicPackage", new Class[]{File.class, int.class});
            parse.setAccessible(true);
            pkg = parse.invoke(parser, new Object[]{apkFile, flags});
            List acs=(List)pkg.getClass().getField("activities").get(pkg);
            String main=null;
            for (int i=0;i<acs.size();i++)
            {
                List<IntentFilter> intents=(List<IntentFilter>) acs.get(i).getClass().getField("intents").get(acs.get(i));
                for (int j=0;j<intents.size();j++)
                {
                    IntentFilter cc=intents.get(j);
                    for (int k=0;k<cc.countActions();k++)
                        if (cc.getAction(k).contains("MAIN"))
                        {
                            main=((ActivityInfo)acs.get(i).getClass().getField("info").get(acs.get(i))).name;
                        }
                }
            }
/*            if ((flags & GET_SIGNATURES) != 0) {
                parser.collectCertificates(pkg, 0);
                parser.collectManifestDigest(pkg);
            }*/
            state = Class.forName("android.content.pm.PackageUserState").newInstance();
            generate=Parser.getDeclaredMethod("generatePackageInfo",pkg.getClass(),int[].class,int .class,long.class,long.class,Set.class,state.getClass());
            PackageInfo i=(PackageInfo) generate.invoke(null,new Object[]{pkg, null, flags, 0, 0, null, state});
            PkgInfo pi= new PkgInfo(i,main,pkg);
            pi.intents=new IntentFilters(pkg);
            return pi;
        }
        catch (Exception e) {
            Log.e("xx",e.toString());
        }

        return null;
    }

    /*               get abi
    final File nativeLibraryRoot = new File(nativeLibraryRootStr);

            // Null out the abis so that they can be recalculated.
            pkg.applicationInfo.primaryCpuAbi = null;
            pkg.applicationInfo.secondaryCpuAbi = null;
            if (isMultiArch(pkg.applicationInfo)) {
                // Warn if we've set an abiOverride for multi-lib packages..
                // By definition, we need to copy both 32 and 64 bit libraries for
                // such packages.
                if (pkg.cpuAbiOverride != null
                        && !NativeLibraryHelper.CLEAR_ABI_OVERRIDE.equals(pkg.cpuAbiOverride)) {
                    Slog.w(TAG, "Ignoring abiOverride for multi arch application.");
                }

                int abi32 = PackageManager.NO_NATIVE_LIBRARIES;
                int abi64 = PackageManager.NO_NATIVE_LIBRARIES;
                if (Build.SUPPORTED_32_BIT_ABIS.length > 0) {
                    if (extractLibs) {
                        abi32 = NativeLibraryHelper.copyNativeBinariesForSupportedAbi(handle,
                                nativeLibraryRoot, Build.SUPPORTED_32_BIT_ABIS,
                                useIsaSpecificSubdirs);
                    } else {
                        abi32 = NativeLibraryHelper.findSupportedAbi(handle, Build.SUPPORTED_32_BIT_ABIS);
                    }
                }

                maybeThrowExceptionForMultiArchCopy(
                        "Error unpackaging 32 bit native libs for multiarch app.", abi32);

                if (Build.SUPPORTED_64_BIT_ABIS.length > 0) {
                    if (extractLibs) {
                        abi64 = NativeLibraryHelper.copyNativeBinariesForSupportedAbi(handle,
                                nativeLibraryRoot, Build.SUPPORTED_64_BIT_ABIS,
                                useIsaSpecificSubdirs);
                    } else {
                        abi64 = NativeLibraryHelper.findSupportedAbi(handle, Build.SUPPORTED_64_BIT_ABIS);
                    }
                }

                maybeThrowExceptionForMultiArchCopy(
                        "Error unpackaging 64 bit native libs for multiarch app.", abi64);

                if (abi64 >= 0) {
                    pkg.applicationInfo.primaryCpuAbi = Build.SUPPORTED_64_BIT_ABIS[abi64];
                }

                if (abi32 >= 0) {
                    final String abi = Build.SUPPORTED_32_BIT_ABIS[abi32];
                    if (abi64 >= 0) {
                        pkg.applicationInfo.secondaryCpuAbi = abi;
                    } else {
                        pkg.applicationInfo.primaryCpuAbi = abi;
                    }
                }
            } else {
                String[] abiList = (cpuAbiOverride != null) ?
                        new String[] { cpuAbiOverride } : Build.SUPPORTED_ABIS;

                // Enable gross and lame hacks for apps that are built with old
                // SDK tools. We must scan their APKs for renderscript bitcode and
                // not launch them if it's present. Don't bother checking on devices
                // that don't have 64 bit support.
                boolean needsRenderScriptOverride = false;
                if (Build.SUPPORTED_64_BIT_ABIS.length > 0 && cpuAbiOverride == null &&
                        NativeLibraryHelper.hasRenderscriptBitcode(handle)) {
                    abiList = Build.SUPPORTED_32_BIT_ABIS;
                    needsRenderScriptOverride = true;
                }

                final int copyRet;
                if (extractLibs) {
                    copyRet = NativeLibraryHelper.copyNativeBinariesForSupportedAbi(handle,
                            nativeLibraryRoot, abiList, useIsaSpecificSubdirs);
                } else {
                    copyRet = NativeLibraryHelper.findSupportedAbi(handle, abiList);
                }

                if (copyRet < 0 && copyRet != PackageManager.NO_NATIVE_LIBRARIES) {
                    throw new PackageManagerException(INSTALL_FAILED_INTERNAL_ERROR,
                            "Error unpackaging native libs for app, errorCode=" + copyRet);
                }

                if (copyRet >= 0) {
                    pkg.applicationInfo.primaryCpuAbi = abiList[copyRet];
                } else if (copyRet == PackageManager.NO_NATIVE_LIBRARIES && cpuAbiOverride != null) {
                    pkg.applicationInfo.primaryCpuAbi = cpuAbiOverride;
                } else if (needsRenderScriptOverride) {
                    pkg.applicationInfo.primaryCpuAbi = abiList[0];
                }
            }
     */

}
