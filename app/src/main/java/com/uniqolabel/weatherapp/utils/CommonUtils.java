package com.uniqolabel.weatherapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.uniqolabel.weatherapp.BuildConfig;
import com.uniqolabel.weatherapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.uniqolabel.weatherapp.utils.AppConstants.LOCATION_REQUEST_CODE;

public class CommonUtils {
    public static String getDayFromDate(String input) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date;
        try {
            date = inFormat.parse(input);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return outFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDayFromDateTime(String input) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date;
        try {
            date = inFormat.parse(input);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return outFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void showPermissionDeniedDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.app_location_permission_warning_msg))
                .setCancelable(false)
                .setNegativeButton(activity.getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(activity.getString(R.string.msg_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
                        activity.startActivityForResult(intent, LOCATION_REQUEST_CODE);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
