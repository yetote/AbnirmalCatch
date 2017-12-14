package com.example.admin.abnirmalcatch;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * com.example.admin.abnirmalcatch
 *
 * @author Swg
 * @date 2017/12/14 14:23
 */
public interface UpLoadFile {
    @Multipart
    @POST("new_file.php")
    Call<Result> uploadFile(@Part RequestBody file);
}
