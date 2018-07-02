package com.example.cheng.testcontact;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DataUtils dataUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataUtils = new DataUtils(this);
        if(checkPermission(this, Manifest.permission.READ_CONTACTS)){
            Log.i("111", "一切正常--------");
            getData();
        }else{
            Log.i("111", "无法查找数据--------");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            }
        }
    }

    private void getData(){
        new Thread(){
            public void run() {
                List<ContactInfo> allPhoneContact = dataUtils.getAllPhoneContact();
                for (int i = 0; i < allPhoneContact.size(); i++) {
                    Log.i("111", allPhoneContact.get(i).toString());
                }
                Log.i("111", "查找数据完毕--------");
            };
        }.start();
    }

    //权限获取回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("111", "获取权限回调----------");
        if (requestCode == 100) {
            if (permissions.length > 0 && permissions[0].equals(permissions) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//同意使用
                Log.i("111", "获取权限成功");
                getData();
            }else{
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    if (checkPermission(this, Manifest.permission.READ_CONTACTS)) {
                        Log.i("111", "获取权限成功");
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
