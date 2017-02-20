package com.jerry.dbtest.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestServes {
    @POST("Mobile/GetMobileOwnership")
    Call<String> getString(@Query("mobile") String mobile,
                           @Query("authkey") String authkey);

    @GET("Mobile/GetMobileOwnership")
    Call<String> getCallString(@Query("mobile") String mobile,
                           @Query("authkey") String authkey);
}
