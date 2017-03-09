package mobile.xiyou.atest;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class PkgInfo {

    public PackageInfo info;
    public String mainClass="";

    public PkgInfo(PackageInfo i,String main) {
        this.info=i;
        this.mainClass=main;
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
            return new PkgInfo(i,main);
        }
        catch (Exception e) {
            Log.e("xx",e.toString());
        }

        return null;
    }

}
