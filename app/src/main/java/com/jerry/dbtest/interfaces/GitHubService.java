package com.jerry.dbtest.interfaces;

import com.jerry.dbtest.entity.Repo;
import com.jerry.dbtest.entity.User;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface GitHubService {
    @GET("users/octocat/repos")
    Call<List<Repo>> listRepos();


    @GET("users/octocat/repos")
    Call<String> get();

    @GET("users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);

    @POST("users/new")
    Call<User> createUser(@Body User user);


    @FormUrlEncoded
    @POST("user/edit")
    Call<User> updateUser(@Field("first_name") String first, @Field("last_name") String last);

    @Multipart
    @PUT("user/photo")
    Call<User> updateUser(@Part("photo") RequestBody phot, @Part("description") RequestBody description);

    @Multipart
    @PUT("user/photo")
    Call<User> updateUser(@PartMap Map<String, RequestBody> photos, @Part("description") RequestBody description);
}