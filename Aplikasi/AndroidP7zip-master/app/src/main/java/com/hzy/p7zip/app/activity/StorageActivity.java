package com.hzy.p7zip.app.activity;


import android.app.Dialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hzy.p7zip.app.R;
import com.hzy.p7zip.app.fragment.AboutFragment;
import com.hzy.p7zip.app.fragment.HelpFragment;
import com.hzy.p7zip.app.fragment.StorageFragment;

import org.w3c.dom.Text;

import java.io.File;
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

    Button selectFile, Send, dialogStorage;
    EditText dataPath;
    TextView TextPathView;
    static final int CUSTOM_DIALOG_ID = 0;

    private static final int DISCOVER_DURATION = 200;
    private static final int REQUEST_BLUE = 1;
    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragmentList;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbluetooth);
        dataPath = (EditText) findViewById(R.id.file_path);
        Send = (Button) findViewById(R.id.Sendblue);
        dialogStorage = (Button) findViewById(R.id.selectfile);

        dialogStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPath.setText("");
                showDialog(CUSTOM_DIALOG_ID);

            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendViaBluetooth();
            }
        });
    }
    // Masih mencari solusi bagaimana file dapat dipilih dan dikompres lalu ditampilkan ke TextVIew bluetooth
    //-------------------------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(StorageActivity.this);
                dialog.setContentView(R.layout.activity_main);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                ButterKnife.bind(this);
                initToolbar();
                mFragmentList = new ArrayList<>();
                mFragmentManager = getSupportFragmentManager();
                navigationView.setNavigationItemSelectedListener(this);
                showFragment(StorageFragment.class); // tampilkan storageFragment class

        }
        return dialog;
    }
    //-----------------------------------------------------------------------------------------

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
            case R.id.nav_help:
                showFragment(HelpFragment.class);
                break;
            case R.id.nav_about:
                showFragment(AboutFragment.class);
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

    public void Exit (View V){
        btAdapter.disable();
        Toast.makeText(this, "Bluetooth Off", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Jika dataPath terisi dan Bluetooth tidak ada = Device tidak support
     * Jika dataPath terisi dan BLuetooth ada = Nyalakan Bluetooth
     * jika dataPath Kosong = Pilih file
     */
    public void sendViaBluetooth(){
        if(!dataPath.equals(null)){
            if (btAdapter ==null){
                Toast.makeText(this, "Device not support bluetooth", Toast.LENGTH_LONG).show();
            }else{
                enableBluetooth(); // Nyalakan Bluetooth
            }
        }else{
            Toast.makeText(this, "Please select a file.", Toast.LENGTH_LONG).show();
        }
    }

    public void enableBluetooth(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLUE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLUE){
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.setType("*/*");
            File file = new File(dataPath.getText().toString());

            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

            PackageManager pm = getPackageManager();
            List <ResolveInfo> list = pm.queryIntentActivities(i, 0);
            if(list.size() >0){
                String packageName = null;
                String className = null;
                boolean found = false;

                for(ResolveInfo info : list){
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")){
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }
                //CEK BLUETOOTH Ada atau Tidak
                if(!found){
                    Toast.makeText(this, "BLuetooth Not Been Found", Toast.LENGTH_LONG).show();
                }else{
                    i.setClassName(packageName, className);
                    startActivity(i);
                }
            }
        }else{
            Toast.makeText(this, "Bluetooth is Cancelled", Toast.LENGTH_LONG).show();
        }
    }

}
