package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentController;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

import static android.view.Window.FEATURE_ACTION_BAR;
import static android.view.Window.FEATURE_ACTION_BAR_OVERLAY;

public class MainActivity extends Activity implements Runnable,View.OnClickListener {

    public MainActivity() {
        super();
    }

    TextView test;

    private Resources res;
    private AssetManager am;
    private Activity activity;
    private Resources.Theme theme;
    private PackageInfo info;
    private Context cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bbb).setOnClickListener(this);
        //Log.e("xx",getWindow().hasFeature(Window.FEATURE_ACTION_BAR)+"xx");
    }



    @Override
    public void onClick(View v) {
       // AppManager.get().startApp(this,"xiyou.mobile.android.elisten",0);
        AppManager.get().startApp(this,"com.example.wyz.xiyoug",0);
    }
/*
    public class Queue
    {
        var login = {name
        :'',num:'',class:'',sex:'',tel:'',message:'',group:'',session:'',vercode:'',password:'',type:'',mail:'',state:'',init:

        function() {
            this.checkInfHandle(login.checkInf)
        }

        ,checkInfHandle:

        function(callback) {
            var iCookie = "";
            var url = location.search;
            login.num = url.split("=")[1];
            callback()
        }

        ,checkInf:

        function() {
            $.ajax({url:'http://www.tjoe18.cn:3000/check', dataType:"jsonp", data:{
                num:
                login.num,},headers:
            {
                withCredentials:
                true
            },success:
            function(data) {
                if (data.result == "without sign") {
                    login.signOut();
                    login.setSwiper();
                    login.setBtnStyle();
                    login.setForm();
                    login.setBack();
                    login.setHomeHandle();
                    login.setBtnHandle()
                } else if (data.result == "sign up") {
                    login.setThenAnimated();
                    login.signOut()
                } else if (data.result == "without login") {
                    window.location.href = "index.html"
                }
            }})}

        ,signOut:

        function() {
            var btn = document.getElementById('quit');
            btn.onclick = function() {
                $.cookie("session_from_qd", null);
                alert("宸查€€鍑虹櫥褰�");
                window.location.href = "index.html"
            }
        }

        ,setBtnHandle:

        function() {
            var btn = document.getElementById('login-btn');
            btn.onclick = login.setBtn
        }

        ,removeSetBtn:

        function() {
            var btn = document.getElementById('login-btn');
            btn.removeEventListener('click', this.setBtn)
        }

        ,setBtn:

        function() {
            login.name = document.getElementById('name').value;
            login.num = document.getElementById('num').value;
            login.tel = document.getElementById('tel').value;
            login.message = document.getElementById('message').value;
            login.group = document.getElementById('group').value;
            login.mail = document.getElementById('mail').value;
            var telRe =/^(13[0 - 9] | 15[0 | 1 | 3 | 6 | 7 | 8 | 9] | 17[0 - 9] | 18[0 - 9])\d {
                8
            } $ /; var mailRe =/^(\w -*\.*)+ @(\w- ?) +(\.\w {
                2,})+$ /; var nameRe =/[\u4E00 -\u9FA5]{
                2, 5
            } ( ?:路[\u4E00 -\u9FA5]{
                2, 5
            })
            var numRe =/^[0 - 9]*$ /; if (!nameRe.test(login.name)) {
                alert("璇锋鏌ュ鍚嶆槸鍚﹁緭鍏ユ纭�")
            } else if (!numRe.test(login.num)) {
                alert("璇锋鏌ュ鍙锋槸鍚﹁緭鍏ユ纭�")
            } else if (!mailRe.test(login.mail)) {
                alert("璇锋鏌ラ偖绠辨槸鍚﹁緭鍏ユ纭�")
            } else if (login.message.length > 30) {
                alert("鐣欒█鍐呭瓒呰繃闄愬埗")
            } else if (!login.tel) {
                alert("杩樻病鍐欑數璇濆憪")
            } else if (!login.mail) {
                alert("鑰侀搧锛岄兘鍟ユ椂浠ｄ簡杩樻病閭锛�")
            } else if (!login.group) {
                alert("涓€瀹氳閫変釜鏂瑰悜鍛€~!")
            } else {
                $.ajax({url:'http://www.tjoe18.cn:3000/add', dataType:"jsonp", headers:{
                    withCredentials:
                    true
                },data:
                {
                    newData:
                    {
                        name:
                        login.name, num:login.num,class:login.class, sex:login.sex, tel:
                        login.tel, message:login.message, mail:login.mail, group:login.group,},},
                success:
                function(data) {
                    if (!data.error) {
                        alert("鎶ュ悕鎴愬姛鍟");
                        login.setThenAnimated()
                    } else if (data.result == "wrong name") {
                        alert("璇锋鏌ュ鍚嶆槸鍚︽纭�");
                        setBtn()
                    } else if (data.result == "wrong num") {
                        alert("璇锋鏌ュ鍙锋槸鍚︽纭�");
                        setBtn()
                    }
                },})}
        }

        ,setThenAnimated:

        function() {
            clearInterval(login.timeId);
            var form = document.getElementsByClassName('login-form')[0];
            var btn = document.getElementById('login-btn');
            form.setAttribute("class", "login-form");
            form.setAttribute("class", "login-form animated bounceOut");
            btn.setAttribute("class", "login-form animated bounceOut");
            $.ajax({url:'http://www.tjoe18.cn:3000/returnInf', dataType:"jsonp", data:{
                num:
                login.num,},headers:
            {
                withCredentials:
                true
            },success:
            function(data) {
                var message = document.getElementById('iMessage');
                if (data.result[0].state == "宸插彂鍏�") {
                    iMessage.value = data.result[0].name + "鍚屽浣犲ソ锛佹槸閲戝瓙鎬讳細鍙戝厜鐨勶紝璇风户缁姫鍔涳紒"
                } else {
                    if (!data.result[0].state) {
                        login.state = "鏈潰璇曟垨鐘舵€佹湭鏇存柊"
                    } else {
                        login.state = data.result[0].state
                    }
                    iMessage.value = data.result[0].name + "鍚屽浣犲ソ锛佷綘宸茬粡鎶曢€掍簡" + data.result[0].group + "缁勫暒~~浣犵幇鍦ㄧ殑鐘舵€佹槸锛�" + login.state
                }
            }});
            setTimeout(function() {
                var lis = document.getElementsByClassName('check-li');
                lis[0].style.display = "block";
                lis[0].setAttribute("class", "check-li animated bounceInRight");
                setTimeout(function() {
                    var lis = document.getElementsByClassName('check-li');
                    lis[1].style.display = "block";
                    lis[1].setAttribute("class", "check-li animated bounceInRight");
                    var iSwiper = new Swiper(".swiper-container", {initialSlide:1, parallax:
                    true, touchRatio:0,})},200)},20)}

        ,setSwiper:

        function() {
            var iSwiper = new Swiper(".swiper-container", {initialSlide:0, parallax:
            true, touchRatio:0,})}

        ,setBack:

        function() {
            var userAgentInfo = navigator.userAgent;
            var Agents =["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];
            var flag = true;
            for (var v = 0; v < Agents.length; v++) {
                if (userAgentInfo.indexOf(Agents[v]) > 0) {
                    flag = false;
                    break
                }
            } if (!flag) {
                var back = document.getElementById("login");
                var html = document.getElementsByTagName("html")[0];
                var body = document.getElementsByTagName("body")[0];
                body.style.width = html.style.width = document.body.clientWidth + "px";
                body.style.height = html.style.height = document.body.clientHeight + "px";
                back.style.backgroundSize = document.body.clientWidth + "px " + document.body.clientHeight + "px"
            }
        }

        ,timeId:'',setBtnStyle:

        function() {
            var btn = document.getElementById('login-btn');
            login.timeId = setInterval(function() {
                btn.setAttribute("class", "animated swing");
                setTimeout(function() {
                    btn.setAttribute("class", "")
                },1000)},3000)}

        ,setForm:

        function() {
            var lis = document.getElementsByClassName('login-li');
            var i = 0;
            setTimeout(function() {
                lis[i].setAttribute("class", "login-li animated bounceInRight");
                lis[i].style.display = "block";
                i++;
                setTimeout(function() {
                    lis[i].setAttribute("class", "login-li animated bounceInRight");
                    lis[i].style.display = "block";
                    i++;
                    setTimeout(function() {
                        lis[i].setAttribute("class", "login-li animated bounceInRight");
                        lis[i].style.display = "block";
                        i++;
                        setTimeout(function() {
                            lis[i].setAttribute("class", "login-li animated bounceInRight");
                            lis[i].style.display = "block";
                            i++;
                            setTimeout(function() {
                                lis[i].setAttribute("class", "login-li animated bounceInRight");
                                lis[i].style.display = "block";
                                i++;
                                setTimeout(function() {
                                    lis[i].setAttribute("class", "login-li animated bounceInRight");
                                    lis[i].style.display = "block"
                                },100)},100)},100)},100)},100)},100)}

        ,setHomeHandle:

        function() {
        }

        ,
    }

    ;login.init();
    }*/



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);




/*        Method attach= null;
        try {
            attach =ContextWrapper.class.getDeclaredMethod("attachBaseContext",new Class[] { Context.class });
            attach.setAccessible(true);
        } catch (NoSuchMethodException e) {
            //Log.e("xx",e.toString());
        }
        try {
                attach.invoke(activity,new Object[]{newBase});
        } catch (IllegalAccessException e) {
            //Log.e("xx",e.toString());
        } catch (InvocationTargetException e) {
            //Log.e("xx",e.toString());
        }*/
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Instrumentation x=new Instrumentation();
        x.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN,500,500,0));
        x.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_UP,500,500,0));
    }
}
