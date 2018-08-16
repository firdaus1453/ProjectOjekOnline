package com.libstudio.projectojekonline.network;

import com.libstudio.projectojekonline.helper.MyContants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitRetrofit {

    //inizialisasi sebuah library untuk accesss data dari server
    //library yang digunakan adalah retrofit dari square
    // class ini digunakan agar inisiasi retrofit tidak terulang

    public static Retrofit setInit() {
        // TODO 8 MEMBUAT MYCONSTANT BASE URL DI HELPER
        return new Retrofit.Builder().baseUrl(MyContants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static RestApi getInstance(){

        return setInit().create(RestApi.class);
    }


    // TODO 21 MEMBUAT METHOD RETROFIT BARU
    public static Retrofit setInitGoogle() {
        // TODO 8 MEMBUAT MYCONSTANT BASE URL DI HELPER
        return new Retrofit.Builder().baseUrl(MyContants.BASE_MAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static RestApi getInstanceGoogle(){

        return setInitGoogle().create(RestApi.class);
    }
}
