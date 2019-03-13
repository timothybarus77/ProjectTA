package com.example.compressfile;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath + "/instinctcoder/zipunzip/data/" ; //akses data yang ingin dicompress
    private String zipPath = SDPath + "/instinctcoder/zipunzip/zip/" ; //create file yang berisis file yang sudah di compress
    final static String TAG = MainActivity.class.getName();

    Button btnZip;
    CheckBox checkParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkParent = (CheckBox)findViewById(R.id.chkParent);
        btnZip = (Button) findViewById(R.id.btnZip);
        btnZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(if (FileZipper.zip(dataPath, zipPath, "dummy.zip", checkParent.isChecked())){
                    Toast.makeText(MainActivity.this,"Zip successfully.", Toast.LENGTH_LONG).show();) //proses compress file
            }
            }
        });
    }
}
