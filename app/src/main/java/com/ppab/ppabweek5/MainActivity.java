package com.ppab.ppabweek5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;

    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;

    private TextView sensorAzimuth;
    private TextView sensorPitch;
    private TextView sensorRoll;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnometerData = new float[3];

    private static final float VALUE_DRIFT = 0.05f;

    private ImageView spotTop;
    private ImageView spotLeft;
    private ImageView spotRight;
    private ImageView spotBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sensorAzimuth = findViewById(R.id.value_azimuth);
        sensorPitch = findViewById(R.id.value_pitch);
        sensorRoll = findViewById(R.id.value_roll);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        spotTop = findViewById(R.id.spot_top);
        spotBottom = findViewById(R.id.spot_bottom);
        spotLeft = findViewById(R.id.spot_left);
        spotRight = findViewById(R.id.spot_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensorAccelerometer != null){
            sensorManager.registerListener(this, sensorAccelerometer,
                    sensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorMagnetometer != null){
            sensorManager.registerListener(this, sensorMagnetometer,
                    sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnometerData = event.values.clone();
                break;
        }

        float [] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null,
                mAccelerometerData, mMagnometerData);
        float orientationValues[] = new float[3];
        if (rotationOK){
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        sensorRoll.setText(getResources().getString(R.string.value_format, roll));
        sensorPitch.setText(getResources().getString(R.string.value_format, pitch));
        sensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));

        if (Math.abs(pitch) < VALUE_DRIFT){
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT){
            roll = 0;
        }

        spotTop.setAlpha(0f);
        spotLeft.setAlpha(0f);
        spotRight.setAlpha(0f);
        spotBottom.setAlpha(0f);

        if (pitch > 0){
            spotBottom.setAlpha(pitch);
        }else{
            spotTop.setAlpha(Math.abs(pitch));
        }

        if (roll > 0){
            spotLeft.setAlpha(roll);
        }else{
            spotRight.setAlpha(Math.abs(roll));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}