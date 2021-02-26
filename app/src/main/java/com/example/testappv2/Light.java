package com.example.testappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Light extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    ImageView bulb;
    // Individual light and proximity sensors.
    private Sensor mSensorLight;

    // TextViews to display current sensor values
    private TextView mTextSensorLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        getSupportActionBar().setTitle("Light sensor");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTextSensorLight = findViewById(R.id.label_light);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        bulb = findViewById(R.id.bulbon);
        String sensor_error = getResources().getString(R.string.error_no_sensor);
        if (mSensorLight == null) {
            mTextSensorLight.setText(sensor_error);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorLight,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float currentValue[] = sensorEvent.values;
        // Event came from the light sensor.
        if (sensorType == Sensor.TYPE_LIGHT) {
            mTextSensorLight.setText(getResources().getString(
                    R.string.label_light, currentValue[0]));
            if (currentValue[0] >= 0 && currentValue[0] < 1) bulb.setImageAlpha(0);
            else if (currentValue[0] >= 1 && currentValue[0] < 5) bulb.setImageAlpha(50);
            else if (currentValue[0] >= 5 && currentValue[0] < 10) bulb.setImageAlpha(100);
            else if (currentValue[0] >= 10 && currentValue[0] < 20) bulb.setImageAlpha(150);
            else if (currentValue[0] >= 20 && currentValue[0] < 100) bulb.setImageAlpha(200);
            else if (currentValue[0] >= 100) bulb.setImageAlpha(255);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}