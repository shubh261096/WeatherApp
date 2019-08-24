package com.uniqolabel.weatherapp.view.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.uniqolabel.weatherapp.R;
import com.uniqolabel.weatherapp.service.model.ForecastResponseModel;
import com.uniqolabel.weatherapp.service.model.ForecastdayItem;
import com.uniqolabel.weatherapp.service.repo.Events;
import com.uniqolabel.weatherapp.utils.CommonUtils;
import com.uniqolabel.weatherapp.view.adapter.MainActivityAdapter;
import com.uniqolabel.weatherapp.viewModel.MainActivityViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uniqolabel.weatherapp.utils.AppConstants.ACCESS_LOCATION_CODE;
import static com.uniqolabel.weatherapp.utils.AppConstants.GPS_REQUEST_CODE;
import static com.uniqolabel.weatherapp.utils.AppConstants.LOCATION_REQUEST_CODE;
import static com.uniqolabel.weatherapp.utils.CommonUtils.getDayFromDateTime;
import static com.uniqolabel.weatherapp.utils.CommonUtils.isLocationPermissionGranted;


public class MainActivity extends AppCompatActivity implements LocationListener {

    MainActivityViewModel mainActivityViewModel;
    private static final String TAG = "MainActivity";
    @BindView(R.id.rvForecast)
    RecyclerView rvForecast;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.conditionIcon)
    ImageView conditionIcon;
    @BindView(R.id.avgTempC)
    TextView avgTempC;
    @BindView(R.id.day)
    TextView day;
    @BindView(R.id.conditionText)
    TextView conditionText;
    @BindView(R.id.locationName)
    TextView locationName;
    @BindView(R.id.llMain)
    LinearLayout llMain;
    private String cityName;
    private List<ForecastdayItem> forecastdayItemList = new ArrayList<>();
    MainActivityAdapter mainActivityAdapter;
    private String currCity = "";
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        // Hi, All the very best :-)

        checkLocationPermission();
        observerForecast();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvForecast.setLayoutManager(layoutManager);
        mainActivityAdapter = new MainActivityAdapter(forecastdayItemList, this);
        rvForecast.setAdapter(mainActivityAdapter);
    }

    private void checkLocationPermission() {
        if (isLocationPermissionGranted(this)) {
            statusCheck();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            statusCheck();
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            checkLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: Permission Granted");
                statusCheck();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Log.i(TAG, "onRequestPermissionsResult: Permission Denied");
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    CommonUtils.showPermissionDeniedDialog(this);
                } else {
                    checkLocationPermission();
                }
            }
        }
    }

    private void subscribe() {
        llMain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mainActivityViewModel.sendRequest(this.cityName);
    }

    private void observerForecast() {
        mainActivityViewModel.getForecast().observe(this, new Observer<Events.ForecastResponseEvent>() {
            @Override
            public void onChanged(@Nullable Events.ForecastResponseEvent responseEvent) {
                if (responseEvent != null) {

                    clearResources();

                    progressBar.setVisibility(View.GONE);
                    if (responseEvent.isSuccess()) {
                        llMain.setVisibility(View.VISIBLE);
                        ForecastResponseModel forecastResponseModel = responseEvent.getForecastResponseModel();
                        List<ForecastdayItem> forecastdayItems = forecastResponseModel.getForecast().getForecastday();
                        for (int i = 1; i < forecastdayItems.size(); i++) { // Here skipping the first item since data is of the same date
                            forecastdayItemList.add(forecastdayItems.get(i));
                        }
                        mainActivityAdapter.notifyDataSetChanged();

                        if (!TextUtils.isEmpty(forecastResponseModel.getCurrent().getCondition().getIcon())) {
                            Picasso.get()
                                    .load("http:" + forecastResponseModel.getCurrent().getCondition().getIcon())
                                    .into(conditionIcon);
                        }
                        if (!TextUtils.isEmpty(forecastResponseModel.getCurrent().getCondition().getText())) {
                            conditionText.setText(forecastResponseModel.getCurrent().getCondition().getText());
                        }
                        if (!TextUtils.isEmpty(forecastResponseModel.getLocation().getLocaltime())) {
                            day.setText(getDayFromDateTime(forecastResponseModel.getLocation().getLocaltime()));
                        }

                        if (!TextUtils.isEmpty(forecastResponseModel.getLocation().getName())) {
                            locationName.setText(forecastResponseModel.getLocation().getName());
                        }

                        avgTempC.setText(String.format("%s%s", forecastResponseModel.getCurrent().getTempC(), getString(R.string.degree_celcius)));

                        Log.i(TAG, "onChanged: " + forecastResponseModel.toString());
                    } else {
                        showInformativeDialog(MainActivity.this, responseEvent.getErrorModel().getError().getMessage());
                    }
                }
            }
        });
    }

    public void showInformativeDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.msg_retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mainActivityAdapter != null) {
                            mainActivityAdapter.clearData();
                        }
                        subscribe();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("MissingPermission")
    public void statusCheck() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {

            clearResources();

            progressBar.setVisibility(View.VISIBLE);
            if (location == null) {
                if (locationManager != null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            500,
                            0, this);
                }
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        onLocationChanged(location);
                    }
                }
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                getString(R.string.msg_enable_gps))
                .setCancelable(false).setPositiveButton(getString(R.string.msg_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        startActivityForResult(new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                    }
                })
                .setNegativeButton(getString(R.string.msg_no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        Toast.makeText(MainActivity.this, getString(R.string.msg_gps_enable), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            cityName = addresses.get(0).getAddressLine(0);
            if (!currCity.equalsIgnoreCase(cityName)) {
                currCity = cityName;
                subscribe();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private void clearResources() {
        if (mainActivityAdapter != null) {
            mainActivityAdapter.clearData();
        }
    }

}
