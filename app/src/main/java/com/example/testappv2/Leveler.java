package com.example.testappv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Leveler extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    TextView tvAngle;
    float[] orientations;
    ImageView ivArrow;
    float currentPosition = 0;
    float rotationModifier = 0;
    private boolean dialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leveler);
        getSupportActionBar().setTitle("Leveler");
        if(savedInstanceState != null) dialogShown = savedInstanceState.getBoolean("alert");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        tvAngle = findViewById(R.id.tvAngle);
        orientations = new float[3];
        ivArrow = findViewById(R.id.ivArrow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dialogShown) regListener();
        if(!dialogShown){
            AlertDialog alert = new AlertDialog.Builder(Leveler.this)
                    .setTitle("Sensor calibration")
                    .setMessage("To increase accuracy it is advised to rotate your device around X, Y and Z axis before using leveler.")
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .show();
            Button okButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    regListener();
                    dialogShown = true;
                    alert.dismiss();
                }
            });
        }

    }

    private void regListener(){
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("alert", dialogShown);
    }
}