package com.hzy.p7zip.app.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzy.p7zip.app.R;
import com.hzy.p7zip.app.activity.StorageActivity;
import com.hzy.p7zip.app.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class mainbluetooth extends AppCompatActivity {


    public static final int FILE_REQUEST_CODE = 10;
    //Create Objects-------------------------------------------------------
    TextView textFolder;
    EditText dataPath;
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;
    File root, curFolder;
    private List<String> fileList = new ArrayList<String>();
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    BluetoothAdapter btAdatper = BluetoothAdapter.getDefaultAdapter();

    //---------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbluetooth);
        dataPath = (EditText) findViewById(R.id.file_path);
        ButterKnife.bind(this);
    }

    @OnClick (R.id.Sendblue)
    public void sendViaBluetooth(){
        if (!dataPath.equals(null)) {
            if (btAdatper == null) {
                Toast.makeText(this, "Device not support bluetooth", Toast.LENGTH_LONG).show();
            } else {
                enableBluetooth();
            }
        } else {
            Toast.makeText(this, "Please select a file.", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.select_file)
    public  void btnSelectFile(){
        dataPath.setText("");
        Intent intent = new Intent(this, StorageActivity.class);
        startActivityForResult(intent, FILE_REQUEST_CODE);
        
    }

    /*
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(mainbluetooth.this);
                dialog.setContentView(R.layout.dailoglayout);
                dialog.setTitle("File Selector");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                textFolder = (TextView) dialog.findViewById(R.id.folder);
                buttonUp = (Button) dialog.findViewById(R.id.up);
                buttonUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListDir(curFolder.getParentFile());
                    }
                });
                dialog_ListView = (ListView) dialog.findViewById(R.id.dialoglist);
                dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File selected = new File(fileList.get(position));
                        if (selected.isDirectory()) {
                            ListDir(selected);
                        } else if (selected.isFile()) {
                            getselectedFile(selected);
                        } else {
                            dismissDialog(CUSTOM_DIALOG_ID);
                        }
                    }
                });
                break;
        }
        return dialog;
    }

    */

    /**
     * Prepare statement untuk membuka dialog file
     */
    /*
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case CUSTOM_DIALOG_ID:
                ListDir(curFolder);
                break;
        }
    }
    */


    public void getselectedFile(File f) {
        dataPath.setText(f.getAbsolutePath());
        fileList.clear();
        finishActivity(FILE_REQUEST_CODE);
    }

    /*
    public void ListDir(File f) {
        if (f.equals(root)) {
            buttonUp.setEnabled(false);
        } else {
            buttonUp.setEnabled(true);
        }
        curFolder = f;
        textFolder.setText(f.getAbsolutePath());
        dataPath.setText(f.getAbsolutePath());
        File[] files = f.listFiles();
        fileList.clear();

        for (File file : files) {
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
        dialog_ListView.setAdapter(directoryList);
    }

    //exit to application---------------------------------------------------------------------------
    public void Exit(View V) {
        btAdatper.disable();
        Toast.makeText(this, "*** Now Bluetooth is off... Thanks. ***", Toast.LENGTH_LONG).show();
        finish();
    }
    */

    //Method for send file via bluetooth------------------------------------------------------------

    public void enableBluetooth() {
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }

    //Override method for sending data via bluetooth availability--------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.setType("*/*");
            File file = new File(dataPath.getText().toString());

            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
            if (list.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : list) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }
                //CHECK BLUETOOTH available or not------------------------------------------------
                if (!found) {
                    Toast.makeText(this, "Bluetooth not been found", Toast.LENGTH_LONG).show();
                } else {
                    i.setClassName(packageName, className);
                    startActivity(i);
                }
            }
        } else {
            Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG).show();
        }
    }


}
