package com.dogukan.hizolcer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;



public class  MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    TextView tw;
    Button bt;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    float p1,p2,p3,p4;
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow(). addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toast.makeText(this, "Cloud sistemine başarıyla bağlanıldı. Hız bilginiz" +
                " anlık olarak raporlanıyor.", Toast.LENGTH_LONG).show();

        tw = (TextView) findViewById(R.id.editText);
        bt = (Button) findViewById(R.id.bt);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    public void Click(View v){
           moveTaskToBack(true);
           android.os.Process.killProcess(android.os.Process.myPid());
           System.exit(1);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            p1=(float)location.getLongitude();
                            p2= (float) location.getLatitude();
                            double dSpeed = location.getSpeed();
                            double a = 3.6 * (dSpeed);
                            int kmhSpeed = (int) (Math.round(a));

                            tw.setText("Enlem:" + location.getLatitude() + "  Boylam:"
                                    + location.getLongitude()+ "\n\nAnlık Hız= "+kmhSpeed + " km/h");
                            if(kmhSpeed>80 && kmhSpeed<100){
                                Toast.makeText(getApplicationContext() , "80 km/h hızdan yükseğe çıktınız. Dikkatli olunuz!", Toast.LENGTH_SHORT);
                            }
                            if(kmhSpeed>100){
                                Toast.makeText(getApplicationContext() , "100 km/h hızdan yükseğe çıktınız. Dikkatli olunuz!", Toast.LENGTH_SHORT);
                            }

                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://designproject-adec5-default-rtdb.firebaseio.com");
                            DatabaseReference myRef = database.getReference("Anlık Hız");
                            myRef.setValue(kmhSpeed);

                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int value = dataSnapshot.getValue(int.class);
                                    Log.i("Hız","Hedef kişinin anlık hızı: " +value);
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Log.i("Hız", "Bulut sisteminden hız verisi okunamadı." +
                                            "Bir sorun oluştu.", error.toException());
                                }
                            });
                            requestNewLocationData();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "GPS Ayarını Açın! Eğer sorun yine düzelmezde uygulama ayarlarından GPS iznini aktif edin!", Toast.LENGTH_LONG).show();
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            p3=(float)mLastLocation.getLongitude();
            p4= (float) mLastLocation.getLatitude();
            double dSpeed = mLastLocation.getSpeed();
            double a = 3.6 * (dSpeed);
            int kmhSpeed = (int) (Math.round(a));
            tw.setText("Enlem:" + mLastLocation.getLatitude() + " Boylam:"
                    + mLastLocation.getLongitude()+"\n\nAnlık Hız= "+kmhSpeed + " km/h");
            getLastLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}