package com.example.user.step;


import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.step.sensortoy.LineGraphView;

public class AccelSensorEventListener implements SensorEventListener {

	
	private TextView output;
	private float[] maxAccelF  = new float[3];
	private float[] values = {0,0,0};
	private TextView maxAccel;
	private LineGraphView graph;
	
	//Low pass filter variables
	private float previousLowValue = 0;
	private float previousHighValue = 0;
	
	//New vector which is the vector sum of x,y, and z
	private float vectorSum;
	private float steps = 0;
	
	//First stafe for state machine
	private int stage = 1;
	
	//State machine varibales
	private int iterator = 0;
	private float high = 0;
	
	//Constructor takes in a reference to the text view to allow changes
    public AccelSensorEventListener(TextView accelOutput, TextView maxAccelOutput)
    {
    	output = accelOutput;
    	maxAccel = maxAccelOutput;
    }
    //Returns values
    public float[] getValues()
	{
		return values;
	}
    // Set step count to zero
    public void zeroSteps()
	{
		steps = 0;
	}
    //Set up graph
    public void setGraph(LineGraphView graphIn)
	{
		graph = graphIn;
	}
	//Update graph
	public LineGraphView getUpdatedGraph()
	{
		return graph;
	}
    
    public void onAccuracyChanged(Sensor s, int i) {}
    
    //Low pass filter uses the previous accel Field and the current accel variable.
    float lowpass(float in) {
  					
  					float out = previousLowValue;
  					previousHighValue = previousLowValue;
  					float l = 0.24f;
  					
  						out = l * in + (1-l) *out;
  						previousLowValue = out;
  				return out;
  			}

    public void onSensorChanged(SensorEvent se) {
    	
       //Summation of vectors x,y, and z
       vectorSum = (float) Math.pow((Math.pow(se.values[0], 2) + Math.pow(se.values[1], 2) + Math.pow(se.values[2], 2)), 1);
      
       values = se.values;
       values[0] = lowpass(vectorSum);
       
       //This is to get rid of the y and z values on the graph
       values[1] = 0;
       values[2] = 0;
       
       graph.addPoint(values);  
        //State machine
       //Stage 2, this stage check to see if vectorSum is between 8 and 25.
        if((vectorSum > 8 && vectorSum < 25) || stage == 2)
        {
        	stage = 2;
        	//Lets high equal vectorSum if high is less than vectorSum
        	if(vectorSum > high) { 
        		high = vectorSum;
        		}
        	//Moves to stage 3
        	else {
        		stage = 3;
        		}
        }
        
        //Stage 3, the data must drop to below 18% of the max in stage 2 for atleast 18 iteratorations.
        //This is to stop one from simply shaking the device to generate steps.
        if(vectorSum < high*0.18 && stage == 3 )
        {
        	iterator++;
        }
        else if(iterator > 18) {
        	iterator = 0;
        	}
        
        //If the value in stage 3 stays below 18% of max for a sufficient time then we count the step and set stage back to 1.
        if(iterator == 18)
        {
        	steps++;
        	iterator = 0;
        	stage = 1;
        }
        //End of state machine
        
       //Displaying data
       if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
    	  
		     output.setText(String.format("\nAcceleration:\nVector Sum: (%f)\n steps:(%f)", se.values[0], steps));

		     output.setTextColor(Color.BLACK);
    	  
		if(Math.abs(se.values[0]) > Math.abs(maxAccelF[0])) {
          	maxAccelF[0] = se.values[0];
          }
          if(Math.abs(se.values[1]) > Math.abs(maxAccelF[1])) {
          	maxAccelF[1] = se.values[1];
          }
          if(Math.abs(se.values[2]) > Math.abs(maxAccelF[2])) {
          	maxAccelF[2] = se.values[2];
          }
          
        //Display formating  
		maxAccel.setText(String.format("Max Acceleration:\nMax Sum: (%f)",
                          				maxAccelF[0]));
		maxAccel.setTextColor(Color.MAGENTA);
      }
    }

	private Button findViewById(int button12) {
		// TODO Auto-generated method stub
		return null;
	}


}