package com.example.admin.abnirmalcatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/crash_log";

    private static final String TAG = "MainActivity";
    public static final int PERMISSION_CRASH_CODE = 1;
    private int[] a = new int[]{1, 2, 3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CRASH_CODE);
        } else {
            upload();
        }


    }


    public void upload() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        UpLoadFile upLoadFile = retrofit.create(UpLoadFile.class);
        File file = new File(PATH + "/" + "1.txt");
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        Log.e(TAG, "uploadExceptionToServer: " + PATH + "/" + "1.txt");
        Call<Result> requestCall = upLoadFile.uploadFile(requestBody);
        requestCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.e(TAG, "onResponse: " + "success" + response.body().toString()  );
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, "onFailure: " + "error");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CRASH_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "onRequestPermissionsResult: " + "您需要授予该权限，否则将会导致部分功能无法使用");
                } else {
                    upload();
                }
                break;
            default:
                return;
        }
    }
}
