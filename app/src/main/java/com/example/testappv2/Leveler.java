package com.example.testappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Leveler extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    TextView tvAngle;
    float[] orientations;
    ImageView ivArrow;
    float currentPosition = 0;
    float rotationModifier = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leveler);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        tvAngle = findViewById(R.id.tvAngle);
        orientations = new float[3];
        ivArrow = findViewById(R.id.ivArrow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(
                    rotationMatrix, sensorEvent.values);


            float[] remappedRotationMatrix = new float[16];
            SensorManager.remapCoordinateSystem(rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix);


            SensorManager.getOrientation(remappedRotationMatrix, orientations);
            for(int i = 0; i < 3; i++) {
                orientations[i] = (float)(Math.round(Math.toDegrees(orientations[i])*10)/10.0);
            }

            if(orientations[2] > 65) rotationModifier = -90;
            if(orientations[2] < -65) rotationModifier = 90;
            orientations[2] += rotationModifier;
            orientations[2] = (float) (Math.round(orientations[2]*10)/10.0);
            tvAngle.setText(Float.toString(orientations[2]) + "°");
            if(orientations[2]> 10) orientations[2] = 10;
            if(orientations[2]< -10) orientations[2] = -10;
            TranslateAnimation anim = new TranslateAnimation(currentPosition, (ivArrow.getWidth()*(orientations[2]/21)), 0, 0);
            currentPosition = (ivArrow.getWidth()*(orientations[2]/21));
            anim.setDuration(50);
            anim.setFillAfter(true);
            ivArrow.startAnimation(anim);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}