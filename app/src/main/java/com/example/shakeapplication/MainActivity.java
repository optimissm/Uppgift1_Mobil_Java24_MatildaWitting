package com.example.shakeapplication;

import android.app.AlertDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

    SensorManager sensorManager;
    Sensor accelerometer, gyroscope, proximity;
    SensorEventListener listener;

    TextView accelTextView, gyroTextView, proxTextView;

    Button startBtn;
    boolean sensorsActive = false;
    boolean shakeDetected = false;


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

        // hämtar knapp från xml
        startBtn = findViewById(R.id.startBtn);

        // starta alla sensorer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        listener = new SensorEventListener() {
            // denna kompilerar koden
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // gör det möjligt att debugga
                Log.i("SENSOR", "Accuracy Changed: "
                        + sensor.getName() + " -> " + accuracy);

                // för att debugga
                // Sensor accelerator, gyroscope, proximity;

            }

            // denna körs varje gång en sensor ändrar värde
            // så för att kunna få ut olika värden på telefonen
            // så behövs denna metoden
            @Override
            public void onSensorChanged(SensorEvent event) {
                // Log.i("MATILDA", "onSensorChanged: " + event.values[0] + " y: " + event.values[1]);
                // Log.i("MATILDA", "onSensorChanged: " + event.values[0] + " accel z: " + event.values[2]);

                // här sätter jag in en metod som berättar
                // vilken sensor som har ändrat värde
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        // dessa är de värdena vi har att jobba med
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        Log.i("ACCELEROMETER", "X: " + x + " Y: " + y + " Z: " + z);

                        // här räknas den totala accelerationen ut
                        double acceleration = Math.sqrt(x * x + y * y + z * z);

                        // vart ska gränsen för "earthquake" gå?
                        if (acceleration > 50 && !shakeDetected) {
                            shakeDetected = true;

                            runOnUiThread(() -> {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Shake It Baby!")
                                        .setMessage("You've got some sweet moves!")
                                        .setPositiveButton("THANKS",
                                                (dialog, which) -> {
                                            dialog.dismiss();
                                            shakeDetected = false;
                                                })
                                        .show();
                            });
                        }


                        // skriver ut värdena i loggen, så att jag kan se resultat i loggen
                        // vilket är bra till debugg
//                        Log.i("ACCELEROMETER", "X: " + event.values[0] +
//                                // behåller alla decimaler i loggen
//                                " Y: " + event.values[1] +
//                                " Z: " + event.values[2]);
//                        // detta ger mig värdena direkt, live, på skärmen
                        accelTextView.setText(String.format(
                                "Accelerometer" +
                                // begränsar det också till 2 decimaler på skärmen (lite snyggare)
                                "\nX: " + event.values[0] +
                                "\nY: " + event.values[1] +
                                "\nZ: " + event.values[2]));

//                                "Accelerometer\nX: %.2f\nY: %.2f\nZ: %.2f",
//                                event.values[0], event.values[1], event.values[2]));

                        break;

                    case Sensor.TYPE_GYROSCOPE:
                        Log.i("GYROSCOPE", "X: " + event.values[0] +
                                " Y: " + event.values[1] +
                                " Z: " + event.values[2]);
                        gyroTextView.setText(String.format(
                                "Gyroscope" +
                                "\nX: " + event.values[0] +
                                "\nY: " + event.values[1] +
                                "\nZ: " + event.values[2]));

                        break;

                    case Sensor.TYPE_PROXIMITY:
                        Log.i("PROXIMITY", "Proximity: " + event.values[0]);
                        proxTextView.setText("Proximity: " + event.values[0]);

                        break;

                }

            }
        };

        // knapp-lyssnare
        startBtn.setOnClickListener(v -> {
            if (sensorsActive) {
                sensorManager.unregisterListener(listener);
                startBtn.setText("Activate Sensors");
                sensorsActive = false;

            } else {
                // starta sensorerna
                if (accelerometer != null)
                    sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                if (gyroscope != null)
                    sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                if (proximity != null)
                    sensorManager.registerListener(listener, proximity, SensorManager.SENSOR_DELAY_NORMAL);

                startBtn.setText("Deactivate Sensors");
                sensorsActive = true;

            }
        });
    }

    // lägger till en onPause, så att appen inte körs hela tiden
    @Override
    protected void onPause() {
        super.onPause();
        // därför slutar jag lyssna
        sensorManager.unregisterListener(listener);
    }

    // och en funktion för att starta igen
    @Override
        protected void onResume() {
        super.onResume();

    }

}