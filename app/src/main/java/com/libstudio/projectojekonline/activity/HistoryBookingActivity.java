package com.libstudio.projectojekonline.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.libstudio.projectojekonline.R;
import com.libstudio.projectojekonline.fragment.ProsesFragment;
import com.libstudio.projectojekonline.fragment.SelesaiFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryBookingActivity extends AppCompatActivity {

    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.pager)
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking);
        ButterKnife.bind(this);

        tablayout.addTab(tablayout.newTab().setText("Proses"));
        tablayout.addTab(tablayout.newTab().setText("Selesai"));
        PagerAdapter adapter = new CustomAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
    }

    private class CustomAdapter extends FragmentStatePagerAdapter {
        public CustomAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);

        }

        @Override
        public Fragment getItem(int position) {
            if (position==0){
                return new ProsesFragment();
            }else if (position==1){
                return new SelesaiFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
