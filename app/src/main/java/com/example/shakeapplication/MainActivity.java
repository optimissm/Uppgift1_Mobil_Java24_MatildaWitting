package com.example.shakeapplication;

import android.app.AlertDialog;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity{

    // fall jag vill ha samma TAG på mer än en sak
    // private static final String TAG = "Matilda";

    // hanterar och gör det möjligt för användning av sensorerna
    SensorManager sensorManager;
    Sensor accelerometer, gyroscope, proximity;
    SensorEventListener listener;

    // lägger till TextView så att värdena på skärmen visas live
    TextView accelTextView, gyroTextView, proxTextView;
    // ska ha in en knapp för att sätta igång sensorerna
    ImageView imageFlip;
    Button startBtn;
    Switch lightSwitch;

    // är sensorerna på eller av?
    boolean sensorsActive = false;
    // så att bara en popup öppnas åt gången
    boolean shakeDetected = false;


    // detta är när appen startas så "skapas" den
    // och med EdgeToEdge tar appen upp hela skärmen
    // men inte nav/statusbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // laddar xml
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        // hämtar textView till alla sensorer från xml
        accelTextView = findViewById(R.id.accelTextView);
        gyroTextView = findViewById(R.id.gyroTextView);
        proxTextView = findViewById(R.id.proxTextView);

        // hämtar knapp, switch och image från xml
        startBtn = findViewById(R.id.startBtn);
        lightSwitch = findViewById(R.id.lightSwitch);
        imageFlip = findViewById(R.id.imageFlip);

        // starta/hämta alla sensorer från telefonen
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
            }

            // denna körs varje gång en sensor ändrar värde
            // så för att kunna få ut olika värden på telefonen
            // så behövs denna metoden
            @Override
            public void onSensorChanged(SensorEvent event) {
                // här sätter jag in en metod som berättar
                // vilken sensor som har ändrat värde
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:

                        handleAccelerometer(event);

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

        // knapp-lyssnare som startar/pausar sensorerna
        startBtn.setOnClickListener(v -> {
            if (sensorsActive) {
                sensorManager.unregisterListener(listener);
                startBtn.setText("Activate Sensors");
                sensorsActive = false;

            } else {
                registerSensors();

                startBtn.setText("Deactivate Sensors");
                sensorsActive = true;

            }
        });

        darkLight();

    }

    // metoden till accelerometern
    private void handleAccelerometer(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // vår totala acceleration
        Log.i("ACCELEROMETER", "X: " + x + " Y: " + y + " Z: " + z);

        // här räknas den totala accelerationen ut
        double acceleration = Math.sqrt(x * x + y * y + z * z);

        // vart ska gränsen för "earthquake" gå?
        if (acceleration > 50 && !shakeDetected) {
            shakeDetected = true;
            shakeAlert();

        }

        accelTextView.setText(String.format(
                "Accelerometer" +
                        // begränsar det också till 2 decimaler på skärmen (lite snyggare)
                        "\nX: " + event.values[0] +
                        "\nY: " + event.values[1] +
                        "\nZ: " + event.values[2]));

        if (imageFlip != null) {
            imageFlip.setRotation(x * 10);
        }

    }

    // metod för min popup
    private void shakeAlert() {
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

    private void darkLight() {
        lightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // darkmode
                findViewById(R.id.main).setBackgroundColor(Color.parseColor("#007A60"));
                accelTextView.setTextColor(Color.parseColor("#E36802"));
                gyroTextView.setTextColor(Color.parseColor("#E36802"));
                proxTextView.setTextColor(Color.parseColor("#E36802"));
                startBtn.setTextColor(Color.parseColor("#E36802"));
                lightSwitch.setTextColor(Color.parseColor("#58E0C6"));
            } else {
                // lightmode
                findViewById(R.id.main).setBackgroundColor(Color.parseColor("#FFE0F0"));
                accelTextView.setTextColor(Color.parseColor("#FF0F8E"));
                gyroTextView.setTextColor(Color.parseColor("#FF0F8E"));
                proxTextView.setTextColor(Color.parseColor("#FF0F8E"));
                startBtn.setTextColor(Color.parseColor("#FF0F8E"));
                lightSwitch.setTextColor(Color.parseColor("#FF0F8E"));
            }

        });

    }

    private void registerSensors() {
        if (accelerometer != null)
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (gyroscope != null)
            sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        if (proximity != null)
            sensorManager.registerListener(listener, proximity, SensorManager.SENSOR_DELAY_NORMAL);
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