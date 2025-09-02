package com.example.shakeapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity{

    // glöm inte att skriva kommentarer till inlämning
    // och rapport på 100 ord
    // eller tre bilder

    // fall jag vill ha samma TAG på mer än en sak
    // private static final String TAG = "Matilda";

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, proximity;
    private SensorEventListener listener;

    private TextView accelTextView, gyroTextView, proxTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        // TextView till alla sensorer
        accelTextView = findViewById(R.id.accelTextView);
        gyroTextView = findViewById(R.id.gyroTextView);
        proxTextView = findViewById(R.id.proxTextView);

        // starta alla sensorer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.i("SENSOR", "Accuracy Changed: "
                        + sensor.getName() + " -> " + accuracy);

                // för att debugga
                // Sensor accelerator, gyroscope, proximity;

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                // Log.i("MATILDA", "onSensorChanged: " + event.values[0] + " y: " + event.values[1]);
                // Log.i("MATILDA", "onSensorChanged: " + event.values[0] + " accel z: " + event.values[2]);

                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        Log.i("ACCELEROMETER", "X: " + event.values[0] +
                                " Y: " + event.values[1] +
                                " Z: " + event.values[2]);
                        tv.setText("Accelerometer" +
                                "\nX: " + event.values[0] +
                                "\nY: " + event.values[1] +
                                "\nZ: " + event.values[2]);

                        break;

                    case Sensor.TYPE_GYROSCOPE:
                        Log.i("GYROSCOPE", "X: " + event.values[0] +
                                " Y: " + event.values[1] +
                                " Z: " + event.values[2]);
                        tv.setText("Gyroskop\nX: " + event.values[0] +
                                "\nY: " + event.values[1] +
                                "\nZ: " + event.values[2]);

                        break;

                    case Sensor.TYPE_PROXIMITY:
                        Log.i("PROXIMITY", "Proximity: " + event.values[0]);
                        tv.setText("Proximity: " + event.values[0]);

                        break;

                }

            }
        };

        if (accelerometer != null)
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (gyroscope != null)
            sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        if (proximity != null)
            sensorManager.registerListener(listener, proximity, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    @Override
            protected void onResume() {
        super.onResume();

        if (accelerometer != null)
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if(gyroscope != null)
            sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        if(proximity != null)
            sensorManager.registerListener(listener, proximity, SensorManager.SENSOR_DELAY_NORMAL);

    }

}