package com.libstudio.projectojekonline.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.libstudio.projectojekonline.R;
import com.libstudio.projectojekonline.helper.CustomRecycler;
import com.libstudio.projectojekonline.helper.HeroHelper;
import com.libstudio.projectojekonline.helper.SessionManager;
import com.libstudio.projectojekonline.model.DataHistory;
import com.libstudio.projectojekonline.model.ResponseGetBooking;
import com.libstudio.projectojekonline.network.InitRetrofit;
import com.libstudio.projectojekonline.network.RestApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProsesFragment extends Fragment {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    Unbinder unbinder;
    private SessionManager manager;

    public ProsesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.libstudio.projectojekonline.fragment
        View view = inflater.inflate(R.layout.fragment_proses, container, false);
        unbinder = ButterKnife.bind(this, view);
        getdatahistory();
        return view;
    }

    private void getdatahistory() {
        manager = new SessionManager(getActivity());
        RestApi api = InitRetrofit.getInstance();
        String device = HeroHelper.getDeviceUUID(getActivity());
        String token = manager.getToken();
        int idUser = Integer.parseInt(manager.getIdUser());
        int status = 2;
        retrofit2.Call<ResponseGetBooking> getBookingCall = api.getBooking(
                status,
                idUser,
                token,
                device
        );
        getBookingCall.enqueue(new Callback<ResponseGetBooking>() {
            @Override
            public void onResponse(Call<ResponseGetBooking> call, Response<ResponseGetBooking> response) {
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        List<DataHistory> dataHistories = response.body().getData();
                        CustomRecycler adapter = new CustomRecycler(dataHistories, getActivity());
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetBooking> call, Throwable t) {
                Toast.makeText(getActivity(), "Cek koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
