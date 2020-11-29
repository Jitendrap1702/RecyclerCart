package com.example.recyclercart;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyApp extends Application {
    public FirebaseFirestore db;
    private AlertDialog dialog;
    private ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();

        setup();
    }

    // set Up

    private void setup() {
        db = FirebaseFirestore.getInstance();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void showLoadingDialog(Context context){
        dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Loading .....")
                .setMessage("fetch data from cloud.")
                .show();
    }

    public void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public Boolean isOffline(){
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return !(wifiNetworkInfo.isConnected() || dataNetworkInfo.isConnected());
    }

    public void hideLoadingDialog(){
        if(dialog != null)
            dialog.dismiss();
    }
}
