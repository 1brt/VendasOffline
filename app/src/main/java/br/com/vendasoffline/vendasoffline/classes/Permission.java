package br.com.vendasoffline.vendasoffline.classes;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;

/**
 * Created by home on 25/06/17.
 */

public class Permission {

    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 4;

    private Activity activity;
    private SharedPreferences prefs;

    public Permission(Activity activity, SharedPreferences prefs){
        this.activity = activity;
        this.prefs = prefs;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestContentPermissions(){
        String permis = "Você precisa permitir o acesso ao(s) seguinte(s) recurso(s): \n";
        Boolean comPermis = true;
        Boolean firstRun = false;

        vibrate();

        if (prefs.getBoolean("firstrunContent", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            firstRun = true;

            prefs.edit().putBoolean("firstrunContent", false).commit();

        }

        if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) && !hasExternalPermission() && !firstRun) {
            permis = permis + "Armazenamento Externo \n";
            comPermis = false;
        }

        if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && !hasCameraPermission() && !firstRun){
            permis = permis + "Câmera \n";
            comPermis = false;
        }

        if (!comPermis){
            showMessage(permis);
            return;
        }

        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST);
            return;
        }

        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestLocationPermissions(){
        String permis = "Você precisa permitir o acesso ao(s) seguinte(s) recurso(s): \n";
        Boolean comPermis = true;
        Boolean firstRun = false;

        if (prefs.getBoolean("firstrunLocation", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            firstRun = true;

            prefs.edit().putBoolean("firstrunLocation", false).commit();

        }

        if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && !hasLocationPermission() && !firstRun) {
            permis = permis + "Localização \n";
            comPermis = false;
        }

        if (!comPermis){
            showMessage(permis);
            return;
        }

        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasCameraPermission(){
        boolean retorno = true;

        if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        return retorno;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasExternalPermission(){
        boolean retorno = true;

        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        return retorno;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasLocationPermission(){
        boolean retorno = true;

        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        return retorno;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasContentPermissions(){
        boolean retorno = true;

        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
        }

        return retorno;
    }

    public void vibrate(){
        // Utilizado para vibrar o celular.
        Vibrator vibe = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(50);
    }

    private void showMessage(String message){
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
