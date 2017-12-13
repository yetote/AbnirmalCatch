package com.example.admin.abnirmalcatch;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.SystemClock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * com.example.admin.abnirmalcatch
 *
 * @author Swg
 * @date 2017/12/13 15:52
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 文件夹目录
     */
    public static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/crash_log";

    /**
     * 文件名
     */
    public static final String FILE_NAME = "crash";

    /**
     * 文件名后缀
     */
    public static final String FILE_NAME_SUFFIX = ".trace";

    private Context mContext;

    //单例模式
    private static final CrashHandler ourInstance = new CrashHandler();

    public static CrashHandler getInstance() {
        return ourInstance;
    }

    private CrashHandler() {
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        //保存错误信息到SD卡中
        dumpExceptionToSDCard(throwable);

        //上传错误信息到服务器
        uploadExceptionToServer(throwable);

        SystemClock.sleep(1000);
        //结束App
        Process.killProcess(Process.myPid());
    }

    public void init(Context context) {
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = context.getApplicationContext();
    }

    /**
     * 保存错误信息到SD卡中
     *
     * @param throwable
     */
    private void dumpExceptionToSDCard(Throwable throwable) {
        //如果未挂载，返回
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) return;

        File dir=new File(PATH);
        //如果不存在，创建文件夹
        if (dir.exists()){
            dir.mkdirs();
        }
        //获取当前时间
        long current=System.currentTimeMillis();
        String time=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(current));

        //以当前时间创建log文件
        File file=new File(PATH+FILE_NAME+time+FILE_NAME_SUFFIX);
        try {
            PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(file)));

            PackageManager pm=mContext.getPackageManager();
            PackageInfo pi=pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
            pw.println("发生异常时间"+time);
            pw.println("应用版本：" + pi.versionName);
            pw.println("应用版本号：" + pi.versionCode);
            pw.println("android版本号：" + Build.VERSION.RELEASE);
            pw.println("android版本号API：" + Build.VERSION.SDK_INT);
            pw.println("手机制造商:" + Build.MANUFACTURER);
            pw.println("手机型号：" + Build.MODEL);
            throwable.printStackTrace(pw);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传错误信息到服务器
     *
     * @param throwable
     */
    private void uploadExceptionToServer(Throwable throwable) {
//        Error error = new Error(throwable.getMessage());
//        error.(new SaveListener<String>() {
//            @Override
//            public void done(String objectId, BmobException e) {
//
//            }
//        });
    }
}
