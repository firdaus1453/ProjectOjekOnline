package com.libstudio.projectojekonline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.libstudio.projectojekonline.helper.HeroHelper;
import com.libstudio.projectojekonline.helper.SessionManager;
import com.libstudio.projectojekonline.model.Data;
import com.libstudio.projectojekonline.model.ResponseLogin;
import com.libstudio.projectojekonline.model.ResponseRegister;
import com.libstudio.projectojekonline.network.InitRetrofit;
import com.libstudio.projectojekonline.network.RestApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txt_rider_app)
    TextView txtRiderApp;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO 1 PASTE LAYOUT DAN RESOURCE DAN MEMBUAT PACKAGE/FOLDER BARU UNTUK RESOURCE
        // TODO 4 GENERATE BUTTERKNIFE + ONCLIK
        ButterKnife.bind(this);

        // UNTUK MARSHMELLOW KE ATAS PERLU SETTING PERMISSION ACCESS PHONE UUID
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        110);
            }
            return;
        }

        // TODO 12 MEMANGGIL SESSION
        manager = new SessionManager(MainActivity.this);
    }

    @OnClick({R.id.btnSignIn, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                login();
                break;
            case R.id.btnRegister:
                register();
                break;
        }
    }

    // TODO 5 MEMBUAT METHOD REGISTER DAN LOGIN
    private void register() {
        final AlertDialog.Builder dialogRegis = new AlertDialog.Builder(this);
        dialogRegis.setTitle(R.string.title_register);
        dialogRegis.setMessage(R.string.message_register);
        LayoutInflater inflater = LayoutInflater.from(this);
        View tampilanregis = inflater.inflate(R.layout.layout_register, null);
        // TODO 6 MEMASUKKAN CARDVIEW DEPENDENCIES
        // TODO 7 PASTE DRI TELEGRAM DAN SESUAIKAN
        // CTRL+ALT F untuk membuat variable global
        // CTRL+R UNTUK REPLACE ALL
        final EditText edtEmail = tampilanregis.findViewById(R.id.edtEmail);
        final EditText edtPassword = tampilanregis.findViewById(R.id.edtPassword);
        final EditText edtName = tampilanregis.findViewById(R.id.edtName);
        final EditText edtPhone = tampilanregis.findViewById(R.id.edtPhone);
        dialogRegis.setView(tampilanregis);
        dialogRegis.setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
                //check validasi
                // UNTUK SNACK BAR TAMBAHKAN LIBRARY SUPPORT:DESIGN
                // CEK ISI
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    // ALT+ENTER UNTUK MENAMBAHKAN KE STRING
                    Snackbar.make(rootlayout, R.string.requireemail, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.enterpassword, Snackbar.LENGTH_SHORT).show();
                } else if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootlayout, R.string.passwordshort, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtName.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.entername, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.enterphone, Snackbar.LENGTH_SHORT).show();
                } else {
                    // APABILA TERISI SEMUA JALANKAN PERINTAH DI BAWAH INI
                    // TODO 9 INSTANCE ATAU INISIALISASI RETROFIT
                    RestApi restApi = InitRetrofit.getInstance();
                    // MEMASUKKAN DATA KE WEB SERVICE
                    Call<ResponseRegister> registerCall = restApi.registerUser(
                            edtName.getText().toString(),
                            edtPassword.getText().toString(),
                            edtEmail.getText().toString(),
                            edtPhone.getText().toString()
                            );
                    // TODO 10 UNTUK MENANGKAP CALLBACK ATAU RESPONSE SERVER
                    registerCall.enqueue(new Callback<ResponseRegister>() {
                        @Override
                        public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                            // TODO 11 CEK RESPON SUKSES ATAU TIDAK
                            if (response.isSuccessful()) {
                                String result = response.body().getResult();
                                String msg = response.body().getMsg();
                                if (result.equals("true")) {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseRegister> call, Throwable t) {
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Check your connection " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
        dialogRegis.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogRegis.show();
    }

    private void login() {
        final AlertDialog.Builder dialogLogin = new AlertDialog.Builder(this);
        dialogLogin.setTitle("Login");
        dialogLogin.setMessage("Masukkan Email dan Password");
        LayoutInflater inflater = LayoutInflater.from(this);
        View tampilanlogin = inflater.inflate(R.layout.layout_login, null);
        final EditText edtEmail = tampilanlogin.findViewById(R.id.edtEmail);
        final EditText edtPassword = tampilanlogin.findViewById(R.id.edtPassword);
        dialogLogin.setView(tampilanlogin);
        dialogLogin.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    // ALT+ENTER UNTUK MENAMBAHKAN KE STRING
                    Snackbar.make(rootlayout, R.string.requireemail, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.enterpassword, Snackbar.LENGTH_SHORT).show();
                } else if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootlayout, R.string.passwordshort, Snackbar.LENGTH_SHORT).show();
                } else {
                    // APABIL TERISI SEMUA JALANKAN PERINTAH DI BAWAH INI
                    // INSTANCE ATAU INISIALISASI RETROFIT
                    RestApi restApi = InitRetrofit.getInstance();
                    String device = HeroHelper.getDeviceUUID(MainActivity.this);
                    Log.i("device","kode device"+device);
                    // MEMASUKKAN DATA KE WEB SERVICE
                    Call<ResponseLogin> loginCall = restApi.loginUser(
                            device,
                            edtEmail.getText().toString(),
                            edtPassword.getText().toString()
                    );
                    loginCall.enqueue(new Callback<ResponseLogin>() {
                        @Override
                        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                            dialogInterface.dismiss();
                            if (response.isSuccessful()) {
                                String result = response.body().getResult();
                                String msg = response.body().getMsg();
                                if (result.equals("true")) {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    // TODO 25 MEMBUAT CREATE SESSION
                                    Data d = response.body().getData();
                                    manager.setIduser(d.getIdUser());
                                    manager.setEmail(d.getUserEmail());
                                    manager.setPhone(d.getUserHp());
                                    String token = response.body().getToken();
                                    manager.createLoginSession(token);
                                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseLogin> call, Throwable t) {
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Check your connection " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        dialogLogin.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogLogin.show();
    }
}
