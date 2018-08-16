package com.libstudio.projectojekonline.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.libstudio.projectojekonline.MapsActivity;
import com.libstudio.projectojekonline.R;
import com.libstudio.projectojekonline.helper.HeroHelper;
import com.libstudio.projectojekonline.helper.MyContants;
import com.libstudio.projectojekonline.helper.SessionManager;
import com.libstudio.projectojekonline.model.ResponseCheckBooking;
import com.libstudio.projectojekonline.model.ResponseRegister;
import com.libstudio.projectojekonline.network.InitRetrofit;
import com.libstudio.projectojekonline.network.RestApi;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindDriverActivity extends AppCompatActivity {

    @BindView(R.id.pulsator)
    PulsatorLayout pulsator;
    @BindView(R.id.buttoncancel)
    Button buttoncancel;
    private int id;
    private Timer timer;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_driver);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        pulsator.start();

        id = intent.getIntExtra(MyContants.IDBOOKING,0);
        checkbooking();

        timer = new Timer();
        manager = new SessionManager(FindDriverActivity.this);
    }

    private void checkbooking() {
        RestApi api = InitRetrofit.getInstance();
        Call<ResponseCheckBooking> responseCheckBookingCall = api.checkBooking((id));
        responseCheckBookingCall.enqueue(new Callback<ResponseCheckBooking>() {
            @Override
            public void onResponse(Call<ResponseCheckBooking> call, Response<ResponseCheckBooking> response) {
                if (response.isSuccessful()){
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")){
                        String iddriver = response.body().getDriver();
                        Log.i("isi id driver",iddriver);
                        Intent intent = new Intent(FindDriverActivity.this, DetailLokasiDriver.class );
                        intent.putExtra(MyContants.IDDRIVER, iddriver);
                        startActivity(intent);
                        Toast.makeText(FindDriverActivity.this,msg,Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(FindDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.i("else",msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCheckBooking> call, Throwable t) {
                Toast.makeText(FindDriverActivity.this,"GAGAL KONEKSI!! " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.buttoncancel)
    public void onViewClicked() {
        final ProgressDialog dialog = ProgressDialog.show(FindDriverActivity.this,"Process cancel booking","Loading ...");
        String device = HeroHelper.getDeviceUUID(FindDriverActivity.this);
        String token = manager.getToken();
        RestApi api = InitRetrofit.getInstance();
        Call<ResponseRegister> responseCancelCall = api.cancelBooking(
                id,
                device,
                token);
        responseCancelCall.enqueue(new Callback<ResponseRegister>() {
            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                if (response.isSuccessful()){
                    dialog.dismiss();
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")){
                        Toast.makeText(FindDriverActivity.this,msg,Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(FindDriverActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(FindDriverActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseRegister> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(FindDriverActivity.this,"Gagal "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkbooking();
//                Toast.makeText(FindDriverActivity.this,"Refresh",Toast.LENGTH_SHORT).show();
            }
        },0,3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}
