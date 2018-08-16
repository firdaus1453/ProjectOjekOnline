package com.libstudio.projectojekonline.network;

import com.libstudio.projectojekonline.model.ResponseCheckBooking;
import com.libstudio.projectojekonline.model.ResponseDetailDriver;
import com.libstudio.projectojekonline.model.ResponseGetBooking;
import com.libstudio.projectojekonline.model.ResponseLogin;
import com.libstudio.projectojekonline.model.ResponseRegister;
import com.libstudio.projectojekonline.model.modelinsertbooking.ResponseInsertBooking;
import com.libstudio.projectojekonline.model.modelwaypoints.ModelWaypoints;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {

    // TODO 8 MEMBUAT ENDPOINT DAFTAR
    // ENDPOINT UNTUK REGISTER
    @FormUrlEncoded
    @POST("daftar")
    Call<ResponseRegister> registerUser(
            @Field("nama") String nama,
            @Field("password") String password,
            @Field("email") String email,
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("login")
    Call<ResponseLogin> loginUser(
            @Field("device") String device,
            @Field("f_email") String email,
            @Field("f_password") String password
    );

    @FormUrlEncoded
    @POST("insert_booking")
    Call<ResponseInsertBooking> insertbooking(
            @Field("f_device") String device,
            @Field("f_token") String token,
            @Field("f_jarak") float jarak,
            @Field("f_catatan") String catatan,
            @Field("f_akhir") String akhir,
            @Field("f_lngAkhir") String lngAkhir,
            @Field("f_latAkhir") String latAkhir,
            @Field("f_awal") String awal,
            @Field("f_lngAwal") String lngAwal,
            @Field("f_latAwal") String latAwal,
            @Field("f_idUser") int idUser
    );

    @FormUrlEncoded
    @POST("checkBooking")
    Call<ResponseCheckBooking> checkBooking(
            @Field("idbooking") int idUser
    );

    @FormUrlEncoded
    @POST("cancel_booking")
    Call<ResponseRegister> cancelBooking(
            @Field("idbooking") int idBooking,
            @Field("f_token") String token,
            @Field("f_device") String device
    );

    @FormUrlEncoded
    @POST("get_booking")
    Call<ResponseGetBooking> getBooking(
            @Field("status") int status,
            @Field("f_idUser") int idUser,
            @Field("f_token") String token,
            @Field("f_device") String device
    );

    @FormUrlEncoded
    @POST("get_driver")
    Call<ResponseDetailDriver> getDetailDriver(
            @Field("f_iddriver") String idDriver
    );

    // TODO 22 MEMBUAT ENDPOINT API GOOGLE MAP
    @GET("json")
    Call<ModelWaypoints> getRuteLokasi(
            @Query("origin") String alamatAsal,
            @Query("destination") String alamatTujuan
    );
}
