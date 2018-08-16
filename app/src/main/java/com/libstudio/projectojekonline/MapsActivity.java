package com.libstudio.projectojekonline;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.libstudio.projectojekonline.activity.FindDriverActivity;
import com.libstudio.projectojekonline.activity.HistoryBookingActivity;
import com.libstudio.projectojekonline.helper.GPSTracker;
import com.libstudio.projectojekonline.helper.HeroHelper;
import com.libstudio.projectojekonline.helper.MyContants;
import com.libstudio.projectojekonline.helper.SessionManager;
import com.libstudio.projectojekonline.model.modelinsertbooking.ResponseInsertBooking;
import com.libstudio.projectojekonline.model.modelwaypoints.Distance;
import com.libstudio.projectojekonline.model.modelwaypoints.Duration;
import com.libstudio.projectojekonline.model.modelwaypoints.Leg;
import com.libstudio.projectojekonline.model.modelwaypoints.ModelWaypoints;
import com.libstudio.projectojekonline.model.modelwaypoints.Route;
import com.libstudio.projectojekonline.network.InitRetrofit;
import com.libstudio.projectojekonline.network.RestApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.libstudio.projectojekonline.helper.MyContants.LOKASIAWAL;
import static com.libstudio.projectojekonline.helper.MyContants.LOKASITUJUAN;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    @BindView(R.id.imgpick)
    ImageView imgpick;
    @BindView(R.id.lokasiawal)
    TextView lokasiawal;
    @BindView(R.id.lokasitujuan)
    TextView lokasitujuan;
    @BindView(R.id.txtharga)
    TextView txtharga;
    @BindView(R.id.txtjarak)
    TextView txtjarak;
    @BindView(R.id.txtdurasi)
    TextView txtdurasi;
    @BindView(R.id.requestorder)
    Button requestorder;
    @BindView(R.id.edtcatatan)
    EditText edtcatatan;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private double latawal;
    private double lotawal;
    private String namaLokasiAwal;
    private double latakhir;
    private double lotakhir;
    private String namaLokasiAkhir;
    private GPSTracker gpstrack;
    private double lonawal;
    private double lonakhir;
    private String name_location;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        //TODO 12 GENERATE BUTTER GOOGLE MAP
        cekgps();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        polylines = new ArrayList<Polyline>();
        manager = new SessionManager(MapsActivity.this);
    }

    private void cekgps() {
        // TODO 13 CEK GPS
        // cek sttus gps aktif atau tidak
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            //     finish();
        }
        // Todo Location Already on  ... end
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        }
    }

    private void enableLoc() {
        // TODO 14 CEK ENABLELOC
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, MyContants.REQUEST_LOCATION);

                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // TODO 18 MENAMBAHKAN GPS TRACK
        gpstrack = new GPSTracker(MapsActivity.this);

        // TODO 17 MENMBAHKAN PERMISSION
        // UNTUK MARSHMELLOW KE ATAS PERLU SETTING PERMISSION ACCESS LOCATION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        110);


            }
            return;
        }
        // TODO 19 MENGAMBIL DATA LOKASI
        if (gpstrack.canGetLocation()) {
            //getkoordinat jika gps aktif
            latawal = gpstrack.getLatitude();
            lonawal = gpstrack.getLongitude();
            //ubah koordinat jadi nama lokasi
            name_location = posisiku(latawal, lonawal);
            lokasiawal.setText(name_location);

        }

        // TODO 20 MENAMBAHKAN MARK KE PETA DENGAN LOKASI YANG SUDAH DI DAPATKAN
        // Add a marker in Sydney and move the camera
        LatLng lokasiku = new LatLng(latawal, lonawal);
        mMap.addMarker(new MarkerOptions().position(lokasiku).title(name_location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasiku));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 16));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private String posisiku(double latawal, double lonawal) {
        name_location = null;
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(latawal, lonawal, 1);
            if (list != null && list.size() > 0) {
                name_location = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();

                //fetch data from addresses
            } else {
                Toast.makeText(this, "kosong", Toast.LENGTH_SHORT).show();
                //display Toast message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name_location;
    }

    @OnClick({R.id.imgpick, R.id.lokasiawal, R.id.lokasitujuan, R.id.requestorder})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgpick:
                break;
            case R.id.lokasiawal:
                setLokasi(LOKASIAWAL);
                break;
            case R.id.lokasitujuan:
                setLokasi(MyContants.LOKASITUJUAN);
                break;
            case R.id.requestorder:
                requestorderan();
                break;
        }
    }

    private void requestorderan() {
        // TODO 25 MEMBUAT BOOKING OJEK
        int iduser = Integer.parseInt(manager.getIdUser().toString());
        String token = manager.getToken();
        String awal = lokasiawal.getText().toString();
        String akhir = lokasitujuan.getText().toString();
        String ltawal = String.valueOf(latawal);
        String ltakhir = String.valueOf(latakhir);
        String lnawal = String.valueOf(lonawal);
        String lnakhir = String.valueOf(lonakhir);
        String catatan = edtcatatan.getText().toString();
        float jarak = Float.valueOf(txtjarak.getText().toString().substring(0, txtjarak.getText().toString().indexOf("km")));

        String device = HeroHelper.getDeviceUUID(MapsActivity.this);

        final ProgressDialog alert = ProgressDialog.show(MapsActivity.this, "Proses request order", "Loading...");

        RestApi api = InitRetrofit.getInstance();
        Call<ResponseInsertBooking> insertBookingCall = api.insertbooking(
                device,
                token,
                jarak,
                catatan,
                akhir,
                lnakhir,
                ltakhir,
                awal,
                lnawal,
                ltawal,
                iduser
        );
        insertBookingCall.enqueue(new Callback<ResponseInsertBooking>() {
            @Override
            public void onResponse(Call<ResponseInsertBooking> call, Response<ResponseInsertBooking> response) {
                Log.i("APA", "masuk onresponse");
                if (response.isSuccessful()) {

                    Log.i("APA", "masuk isusuccess");

                    alert.dismiss();
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    Log.i("APA", "result,msg" + msg + result);
                    Toast.makeText(MapsActivity.this, msg + "," + result, Toast.LENGTH_SHORT).show();

                    if (result.equals("true")) {
                        Log.i("APA", "result true");
                        int idbooking = response.body().getIdBooking();
                        Snackbar.make(rootlayout, msg, Snackbar.LENGTH_SHORT).show();
                        Intent i = new Intent(MapsActivity.this, FindDriverActivity.class);
                        i.putExtra(MyContants.IDBOOKING, idbooking);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseInsertBooking> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Gagal " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("APA", t.getMessage());
                alert.dismiss();
            }
        });

    }

    private void setLokasi(int lokasi) {
        // TODO 15 SEARCH FILTER BY LOCATION
        // set lokasi hanya untuk indonesia saja
        AutocompleteFilter filternegara = new AutocompleteFilter.Builder()
                .setCountry("ID")
                .build();

        Intent i = null;
        try {
            i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filternegara)
                    .build(MapsActivity.this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        // request
        startActivityForResult(i, lokasi);
    }

    // Menangkap data
    //TODO 16 MEMBUAT AGAR DAPAT MENANGKAP DATA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOKASIAWAL) {
            if (resultCode == RESULT_OK && data != null) {
                Place p = PlaceAutocomplete.getPlace(this, data);
                latawal = p.getLatLng().latitude;
                lonawal = p.getLatLng().longitude;
                LatLng lokasi = new LatLng(latawal, lonawal);
                mMap.clear();
                namaLokasiAwal = p.getAddress().toString();
                if (lokasitujuan.getText().toString().length() != 0) {
                    Log.i("Masuk", "tambah tujuan mark");
                    LatLng lokasiAkhir = new LatLng(latakhir, lonakhir);
                    mMap.addMarker(new MarkerOptions().position(lokasiAkhir).title(namaLokasiAkhir));
                    mMap.addMarker(new MarkerOptions().position(lokasi).title(namaLokasiAwal));
                    getRouteToMarker(lokasi, lokasiAkhir);

                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15));
                // DATA NAMA LOKASI DI SIMPAN KE TEXTVIEW
                lokasiawal.setText(namaLokasiAwal);
                aksesrute();
            }
        } else if (requestCode == LOKASITUJUAN) {
            if (resultCode == RESULT_OK && data != null) {
                Place p = PlaceAutocomplete.getPlace(this, data);
                latakhir = p.getLatLng().latitude;
                lonakhir = p.getLatLng().longitude;
                LatLng lokasi = new LatLng(latakhir, lonakhir);
                mMap.clear();
                erasePylines();
                namaLokasiAkhir = p.getAddress().toString();
                if (lokasiawal.getText().toString().length() != 0) {
                    Log.i("Masuk", "tambah awal mark");
                    LatLng lokasiAwal = new LatLng(latawal, lonawal);
                    mMap.addMarker(new MarkerOptions().position(lokasiAwal).title(namaLokasiAwal));
                    mMap.addMarker(new MarkerOptions().position(lokasi).title(namaLokasiAkhir));
                    getRouteToMarker(lokasiAwal, lokasi);
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15));
                // DATA NAMA LOKASI DI SIMPAN KE TEXTVIEW
                lokasitujuan.setText(namaLokasiAkhir);
                aksesrute();
            }
        }
    }

    private void getRouteToMarker(LatLng lokasiA, LatLng lokasiB) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(lokasiA, lokasiB)
                .alternativeRoutes(false)
                .build();
        routing.execute();
    }

    private void aksesrute() {
        // TODO 23 MENGUKUR JARAK PADA MAP
        // GET KORDINAT
        String origin = String.valueOf(latawal) + "," + String.valueOf(lonawal);
        String destination = String.valueOf(latakhir) + "," + String.valueOf(lonakhir);

        LatLngBounds.Builder bound = LatLngBounds.builder();
        bound.include(new LatLng(latawal, lonakhir));
        bound.include(new LatLng(latakhir, lonakhir));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 16));

        LatLng awal = new LatLng(latawal, lonawal);
        LatLng akhir = new LatLng(latakhir, lonakhir);


//        Routing routing = new Routing.Builder()
//                .travelMode(AbstractRouting.TravelMode.DRIVING)
//                .withListener(this)
//                .waypoints(awal,akhir)
//                .alternativeRoutes(true)
//                .build();
//        routing.execute();

        // Menjalankan api get rute lokasi
        RestApi api = InitRetrofit.getInstanceGoogle();
        Call<ModelWaypoints> call = api.getRuteLokasi(
                origin, destination
        );
        call.enqueue(new Callback<ModelWaypoints>() {
            @Override
            public void onResponse(Call<ModelWaypoints> call, Response<ModelWaypoints> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    // TODO 24 MENGAMBIL DATA DARI JSON LOKASI
                    if (status.equals("OK")) {
                        List<Route> routes = response.body().getRoutes();
                        List<Leg> legs = routes.get(0).getLegs();
                        Distance distance = legs.get(0).getDistance();
                        Duration duration = legs.get(0).getDuration();
                        txtdurasi.setText(duration.getText().toString());
                        txtjarak.setText(distance.getText().toString());
                        //hitung harga
                        double nilaijarak = Double.valueOf(distance.getValue());
                        double harga = Math.ceil(nilaijarak / 1000);
                        double total = harga * 1000;
                        txtharga.setText("Rp." + HeroHelper.toRupiahFormat2(String.valueOf(total)));

                        String points = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();

//                        DirectionMapsV2 mapsV2 = new DirectionMapsV2(MapsActivity.this);
//                        mapsV2.gambarRoute(mMap,points);
                    } else {
                        Toast.makeText(MapsActivity.this, "invalid key api", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Response gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelWaypoints> call, Throwable t) {

            }
        });
    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorPrimary};


    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<com.directions.route.Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<Polyline>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.history) {
            startActivity(new Intent(MapsActivity.this, HistoryBookingActivity.class));
        } else if (id == R.id.logout) {
            manager.logout();
            finish();
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
