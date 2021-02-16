package com.application.trafficlight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout backgroundParameter; //параметр фонового цвета
    private SensorManager mSensorManager; //сенсорный менеджер
    private float mAccel; // ускорение помимо силы тяжести
    private float mAccelCurrent; // текущее ускорение, включая силу тяжести
    private float mAccelLast; // последнее  ускорение, включая силу тяжести

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // выполнить фильтр низких частот
            if (mAccel > 22) {
                onShakeResetBackgroundGray();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        backgroundParameter = findViewById(R.id.background);
        backgroundParameter.setBackgroundColor(Color.GRAY);
    }

    /**
     * Вызывается при клике на красную кнопку
     *
     * @param view - вид кнопки
     */
    public void onClickSetBackgroundRed(View view){
        setBackgroundColor(Color.RED);
    }
    /**
     * Вызывается при клике на желтую кнопку
     *
     * @param view - вид кнопки
     */
    public void onClickSetBackgroundYellow(View view){
        setBackgroundColor(Color.YELLOW);
    }
    /**
     * Вызывается при клике на зеленую кнопку
     *
     * @param view - вид кнопки
     */
    public void onClickSetBackgroundGreen(View view){
        setBackgroundColor(Color.GREEN);
    }
    /**
     * Вызывается при встряхивании устройства
     *
     */
    public void onShakeResetBackgroundGray(){
        setBackgroundColor(Color.GRAY);
    }

    /**
     * Примечание: методы onPause() и onResume() используются для выключения датчика акселерометра.
     * Это позволяет уменьшить использование ресурсов устройства (батарея, ЦП, ОЗУ)
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ColorDrawable colorDrawable = (ColorDrawable) backgroundParameter.getBackground();
        outState.putInt("backgroundParameter", colorDrawable.getColor());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("backgroundParameter")) {
            backgroundParameter.setBackgroundColor(savedInstanceState.getInt("backgroundParameter"));
        }
    }

    /**
     * Задает цвет фону
     * @param color - id цвета
     */
    private void setBackgroundColor(int color){
        backgroundParameter.setBackgroundColor(color);
    }
}