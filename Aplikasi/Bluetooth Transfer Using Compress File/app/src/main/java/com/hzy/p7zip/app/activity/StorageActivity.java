package com.hzy.p7zip.app.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.hzy.p7zip.app.R;
import com.hzy.p7zip.app.fragment.StorageFragment;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StorageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;



    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        mFragmentList = new ArrayList<>();
        mFragmentManager = getSupportFragmentManager();
        navigationView.setNavigationItemSelectedListener(this);
        showFragment(StorageFragment.class);
        /*
        Send = (Button) findViewById(R.id.Sendblue);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendViaBluetooth();
            }
        });
        */
    }


    /*
    @OnClick(R.id.selectfile)
    public void BtnOpenFile(){
        ButterKnife.bind(this);
        mFragmentList = new ArrayList<>();
        mFragmentManager = getSupportFragmentManager();
        navigationView.setNavigationItemSelectedListener(this);
        showFragment(StorageFragment.class);
    }


    // Masih mencari solusi bagaimana file dapat dipilih dan dikompres lalu ditampilkan ke TextVIew bluetooth
    //---------------------------------------------------------------------------------------------
    /*
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(StorageActivity.this);
                dialog.setContentView(R.layout.activity_main);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                //---------------------------------------------------------------------------------
                ButterKnife.bind(this);
                initToolbar();
                mFragmentList = new ArrayList<>();
                mFragmentManager = getSupportFragmentManager();
                navigationView.setNavigationItemSelectedListener(this);
                showFragment(StorageFragment.class); // tampilkan storageFragment class

        }
        return dialog;
    }
    */
    //---------------------------------------------------------------------------------------------
    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_storage:
                showFragment(StorageFragment.class);
                break;
            case R.id.nav_exit:
                finish();
                break;
        }
        return true;
    }

    private void showFragment(Class<? extends Fragment> fragmentCls) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mFragment != null) {
            transaction.hide(mFragment);
        }
        Fragment newFragment = null;
        for (Fragment f : mFragmentList) {
            if (fragmentCls.isInstance(f)) {
                newFragment = f;
                transaction.show(newFragment);
                break;
            }
        }
        if (newFragment == null) {
            try {
                newFragment = fragmentCls.newInstance();
                transaction.add(R.id.content_main_frame, newFragment);
                mFragmentList.add(newFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mFragment = newFragment;
        transaction.commitAllowingStateLoss();
    }

}