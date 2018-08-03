package com.example.cheng.testcontact;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DataUtils dataUtils;
    private ListView contact_lv;
    private List<ContactInfo> lists = new ArrayList<>();
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataUtils = new DataUtils(this);
        contact_lv = findViewById(R.id.contact_lv);
        adapter = new ContactAdapter(this, lists);
        contact_lv.setAdapter(adapter);
        if(checkPermission(this, Manifest.permission.READ_CONTACTS)){
            getData();
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            }
        }
    }

    private void getData(){
        new Thread(){
            public void run() {
                List<ContactInfo> allPhoneContact = dataUtils.getAllPhoneContact();
                Log.i("111", "allPhoneContact.size() = " + allPhoneContact.size());
                if(allPhoneContact!=null && allPhoneContact.size()>0) lists.addAll(allPhoneContact);
                handler.sendEmptyMessage(0);
            };
        }.start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Log.i("111", "lists.size() = " + lists.size());
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    //权限获取回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (permissions.length > 0 && permissions[0].equals(permissions) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//同意使用
                getData();
            }else{
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    if (checkPermission(this, Manifest.permission.READ_CONTACTS)) {
                        getData();
                    }
                }
            }
        }
    }

    /**
     * 权限检查
     * @param permission
     * @return
     */
    public boolean checkPermission(Context context, String permission) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (geTargetSdkVersion(context) >= Build.VERSION_CODES.M) {
                result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }
        Log.i("111", "checkPermission = " + result);
        return result;
    }

    /**
     *  获取版本号
     *  @param context
     *  @return 当前应用的版本号
     */
    public static int geTargetSdkVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
