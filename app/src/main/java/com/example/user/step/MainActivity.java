package com.example.user.step;

import java.util.Arrays;


import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.step.AccelSensorEventListener;
import com.example.user.step.R;
import com.example.user.step.sensortoy.LineGraphView;

public class MainActivity extends Activity {

    //Sensor variables
    public SensorManager sensorManager;
    public Sensor accelSensor;
    public SensorEventListener a;
    public TextView accelOutput;
    public TextView maxAccelOutput;

    //Button variables
    Button reset;
    Button pause;
    Button resume;

    //Graph variables
    LinearLayout ll;
    LineGraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up vertical layout
        ll = (LinearLayout) findViewById(R.id.label1);
        ll.setOrientation(LinearLayout.VERTICAL);

        //Assigning a sensor manger to sensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Assigning buttons with the tasks: pause, resume, and reset
        reset = (Button) findViewById(R.id.resetSteps);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((AccelSensorEventListener) a).zeroSteps();
            }
        });

        pause = (Button) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPause();
            }
        });

        resume = (Button) findViewById(R.id.resume);
        resume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onResume();
            }
        });

        //Accelerometer
        accelOutput = new TextView(getApplicationContext());
        maxAccelOutput = new TextView(getApplicationContext());

        //Creating a action listener
        a = new AccelSensorEventListener(accelOutput, maxAccelOutput);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(a, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);

        //Adding the accelerometer output to layout
        ll.addView(accelOutput);
        ll.addView(maxAccelOutput);

        //Graphing vectors on the line graph
        graph = new LineGraphView(getApplicationContext(),
                100,Arrays.asList("x", "y", "z"));

        ((AccelSensorEventListener) a).setGraph(graph);
        graph = ((AccelSensorEventListener) a).getUpdatedGraph();
        ll.addView(graph);
    }

    @Override
    //This method unregisters the accelerometer
    public void onPause() {

        super.onPause();
        sensorManager.unregisterListener(a, accelSensor);
    }

    //This method checks to see if the application is resumed by user,
    //if it is then the sensors are re-registered (listened to)
    public void onResume() {

        super.onResume();
        sensorManager.registerListener(a, accelSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
