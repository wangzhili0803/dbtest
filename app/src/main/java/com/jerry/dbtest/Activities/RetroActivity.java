package com.jerry.dbtest.Activities;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jerry.dbtest.R;
import com.jerry.dbtest.entity.Repo;
import com.jerry.dbtest.entity.User;
import com.jerry.dbtest.interfaces.GitHubService;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetroActivity extends Activity {
    private final static String TAG = RetroActivity.class.getSimpleName();
    private GitHubService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retro);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(GitHubService.class);
    }

    public void sendRequest(View view) {
//        sendGetByPath();

//        sendGetRequest();

//        sendObjRequest();

//        uploadFile();

//        uploadFiles();
        Toast.makeText(this,"before change",Toast.LENGTH_SHORT).show();
    }

    private void uploadFiles() {
        File file1 = new File(Environment.getExternalStorageDirectory(), "ic_launcher1.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "ic_launcher2.png");
        File file3 = new File(Environment.getExternalStorageDirectory(), "ic_launcher3.png");
        Map<String,RequestBody> map = new HashMap<>();

        //创建文件体
        RequestBody photoRequestBody1 = RequestBody.create(MediaType.parse("image/png"), file1);
        RequestBody photoRequestBody2 = RequestBody.create(MediaType.parse("image/png"), file2);
        RequestBody photoRequestBody3 = RequestBody.create(MediaType.parse("image/png"), file3);

        map.put("1",photoRequestBody1);
        map.put("2",photoRequestBody2);
        map.put("3",photoRequestBody3);

        //添加描述
        RequestBody descriptionRequestBody = RequestBody.create(null, "this is photo.");
        Call<User> call = service.updateUser(map,descriptionRequestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e(TAG,response.body().toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG,t.toString());
            }
        });
    }

    /**
     * 上传文件
     */
    private void uploadFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "ic_launcher.png");
        //创建文件体
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/png"), file);
        //添加描述
        RequestBody descriptionRequestBody = RequestBody.create(null, "this is photo.");
        Call<User> call = service.updateUser(photoRequestBody, descriptionRequestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e(TAG,response.body().toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG,t.toString());
            }
        });
    }

    /**
     * 发送对象
     */
    private void sendObjRequest() {
        Call<User> userCall = service.createUser(new User(1,"Jerry","28","http://write.blog.csdn.net/postlist"));
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e(TAG,response.body().toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG,t.toString());
            }
        });
    }

    /**
     * GET请求
     */
    private void sendGetRequest() {
        Call<String> repos = service.get();
        repos.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG,response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG,t.toString());
            }
        });
    }

    /**
     * 替换@path
     */
    private void sendGetByPath() {
        Call<List<Repo>> repos = service.listRepos("octocat");
        repos.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                Log.e(TAG,response.body().toString());

            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {
                Log.e(TAG,t.toString());
            }
        });
    }
}
