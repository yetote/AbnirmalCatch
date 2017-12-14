package com.example.admin.abnirmalcatch;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

/**
 * com.example.admin.abnirmalcatch
 *
 * @author Swg
 * @date 2017/12/13 15:52
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private String time;

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
    public static final String FILE_NAME_SUFFIX = ".txt";

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
        MyApplication.getContext().exitActivity();
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
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "dumpExceptionToSDCard: " + "未挂载");
            return;
        }

        File dir = new File(PATH);
        //如果不存在，创建文件夹
        if (!dir.exists()) {
            Log.e(TAG, "dumpExceptionToSDCard: " + PATH);
            dir.mkdirs();
        }
        //获取当前时间
        long current = System.currentTimeMillis();
        time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(current));

        //以当前时间创建log文件
        Log.e(TAG, "dumpExceptionToSDCard: " + PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            pw.println("发生异常时间" + time);
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        UpLoadFile upLoadFile = retrofit.create(UpLoadFile.class);
        File file = new File(PATH + "/" + "1.txt");
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        Log.e(TAG, "uploadExceptionToServer: " + PATH + "/" + "1.txt");
//        MultipartBody.Part body = MultipartBody.Part.createFormData("upload_file", file.getName(), requestBody);
        Call<Result> requestCall = upLoadFile.uploadFile(requestBody);
//        requestCall.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//                Log.e(TAG, "onResponse: " + "success"+response.toString());
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//                Log.e(TAG, "onFailure: " + "error");
//            }
//        });
        requestCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.e(TAG, "onResponse: " + "success" + response.toString());

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, "onFailure: " + "error");

            }
        });

//        uploadFile();
    }

    private void uploadFile() {
    }
}
