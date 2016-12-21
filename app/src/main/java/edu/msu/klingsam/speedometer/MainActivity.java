package edu.msu.klingsam.speedometer;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private static double MPH_MULTIPLIER = 2.23694;
    private static double MILES_MULTIPLIER = 0.000621371;

    private double oldLatitude = 0;
    private double oldLongitude = 0;
    private double newLatitude = 0;
    private double newLongitude = 0;

    private float distance = 0;
    private float odometer = 0;

    private long oldTime = 0;
    private long newTime = 0;
    private long time = 0;

    private double speed = 0;
    private double maxSpeed = 0;

    //shaky start to the gps speed calculations
    private int count = 0;


    float [] results = new float[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,1,this);

       // onLocationChanged(null);
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView speedView = (TextView)findViewById(R.id.speedView);
        TextView maxView = (TextView)findViewById(R.id.maxView);
        TextView odometerView = (TextView)findViewById(R.id.odometerView);

       // speedView.setTextColor(Color.WHITE);
       // maxView.setTextColor(Color.WHITE);

        oldLatitude = newLatitude;
        oldLongitude = newLongitude;

        if (location != null) {
            newLatitude = location.getLatitude();
            newLongitude = location.getLongitude();

            Location.distanceBetween(oldLatitude, oldLongitude, newLatitude, newLongitude, results);
            distance = results[0];


            if (oldTime == 0) {
                oldTime = location.getTime();
            } else {
                oldTime = newTime;
            }

            newTime = location.getTime();

            time = (newTime - oldTime) / 1000;

            speed = distance / time;

            if (speed > maxSpeed && count++ > 10) {
                maxSpeed = speed;
                odometer += (distance*MILES_MULTIPLIER);
            }


            if (speed < 100 && count > 5) {
                Locale l = Locale.US;
                speedView.setText(String.format(l,"%.1f", speed*MPH_MULTIPLIER));
                maxView.setText(String.format(l,"%.1f", maxSpeed*MPH_MULTIPLIER));
                odometerView.setText(String.format(l,"%.2f",odometer));

                ProgressBar speedometer = (ProgressBar)findViewById(R.id.speedBar);
                speedometer.setProgress((int)(speed*MPH_MULTIPLIER));



                ProgressBar maxSpeedometer = (ProgressBar)findViewById(R.id.maxBar);
                maxSpeedometer.setProgress((int)(maxSpeed*MPH_MULTIPLIER));

            }


        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
