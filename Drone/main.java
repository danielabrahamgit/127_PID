import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {

/* Variables for setting the roll and the height */
float setRoll = 0;
float setHeight = 0;
float throttleLeft = 0;
float throttleRight = 0;

/* Drone instance and PID controllers */
Drone drone;
PID rollController;
PID heightController;

public void setup() {
  // Set up the size of the frame (in pixels)
  
  // Set up the frame rate
  frameRate(60);
  // Initialize new drone with lenth 60, height 10, and mass 10
  drone = new Drone(60, 10, 10);
  // Initialize a height controller with PID values shown
  heightController = new PID(.01f, 0.00005f, 3.2f, "Height Controller");
  // Initialize a roll controller  with PID values shown
  rollController = new PID(.5f, 0.001f, 3, "Roll Controller");
}

public void draw() {
  // Draw background white (255)
  background(255); 
  
  // Get user input for the setRoll and setHeight values
  userInput();
  
  // Find control height using PID controller and set point
  float controlHeight = heightController.getControl(setHeight, drone.getY());
  // Find control roll using PID controller and set point
  float controlRoll = rollController.getControl(setRoll, drone.getAngle());
  
  // Calculate left and right throttle values accordingly
  throttleLeft = controlHeight + controlRoll;
  throttleRight = controlHeight - controlRoll;
  
  // Update the drone 
  drone.updateDrone(throttleLeft, throttleRight);
  
  // draw the drone
  drone.drawDrone(width, height);
}

public void userInput() {
  // Use mouse to control set points
  setHeight = (height/2 - mouseY);
  setRoll = map(mouseX, 0, width, -5, 5);
}
class Drone {
    
  /* State variables for the drone */
  float _theta, _omega;
  PVector _pos, _vel;
 
  /* Physical characteristics */
  int _len, _wid, _mass;
 
  /* Gravity constant */
  final PVector gravF;
 
  /* Drag parameter */
  final float drag = 0.92f;
  
  /* Maximum/minimum throttle values */
  final static int MAX_THROTTLE = 50;
  final static int MIN_THROTTLE = -50;
 
  /* Constructor sets the length, width and mass. */
  public Drone(int l, int w, int m) {
    _len = l; _wid = w; _mass = m;
    _theta = 0; _omega = 0;
    _pos = new PVector(0, 0); _vel = new PVector(0, 0);
    gravF = new PVector(0, -_mass);
  }
  
  /* Getter methods */
  public int getX() { return (int) _pos.x; }
  public int getY() { return (int) _pos.y; }
  public int getLen() { return (int) _len; }
  public int getWid() { return (int) _wid; }
  public float getMass() { return _mass; }
  public float getGravMag() { return  gravF.mag(); }
  public float getAngle() { return _theta * 180 / PI; }
 
  /* Main physics update method */
  public void updateDrone(float throttleLeft, float throttleRight) {
    
    // Assert that the throttles do not exceed the maximum
    if (throttleLeft > MAX_THROTTLE) {
      throttleLeft = MAX_THROTTLE;
    } else if (throttleLeft < MIN_THROTTLE) {
      throttleLeft = MIN_THROTTLE;
    }
    if (throttleRight > MAX_THROTTLE) {
      throttleRight = MAX_THROTTLE;
    } else if (throttleRight < MIN_THROTTLE) {
      throttleRight = MIN_THROTTLE;
    }
        
    // Find alpha angular acceleration
    float alpha = (throttleLeft - throttleRight) * 6 / (_mass * _len);    
    
    // update omega and theta accordingly
    _omega += alpha;  
    _theta += _omega;
    
    // Find net force on drone
    PVector force = new PVector(sin(_theta), cos(_theta));
    force.mult(throttleLeft + throttleRight);
    force = PVector.add(force, gravF);
    
    // Update velocity and position accordingly
    _vel.add(force);
    _vel.mult(drag);
    _pos.add(_vel); 
  }
 
  /* Draw to screen */
  public void drawDrone(int windowWidth, int windowHeight) {
    
    // Check boarders X direction and recenter drone
    if (_pos.x > windowWidth/2) {
      _pos.x = -windowWidth/2;
    } else if (_pos.x < -windowWidth/2) {
      _pos.x = windowWidth/2;
    }
    // Check boarders Y direction and recenter drone
    if (_pos.y > windowHeight/2) {
      //_pos.y = -windowHeight/2;
    } else if (_pos.y < -windowHeight/2) {
      //_pos.y = windowHeight/2;
    }
    
    // Save reference frame
    pushMatrix();
    
    // Move reference frame to center of window
    translate(windowWidth/2, windowHeight/2);
    
    // Rotate reference frame by THETA
    rotate(_theta);
    
    // Draw the drone with color 0 (black)
    fill(0);
    rect(_pos.x - (_len/2), (-_pos.y + (_wid/2)), _len, _wid);
    
    // Go back to old reference frame that we pushed
    popMatrix();
  }
}
class PID {
  
  /* Constant gains for PID controller */
  float _kp;
  float _ki; 
  float _kd;
  
  /* Instance variables to keep track of */
  float _prevError; 
  float _integralTot;
  int   _time;
  
  /* These are for debugging purposes. */
  private float _state;
  private float _control;
  private String _name;
 
  /* Constructor which takes in PID gains */
  public PID(float p, float i, float d, String n) {
    _kp = p; 
    _ki = i; 
    _kd = d;
    _name = n;
  }
  
  /* Gets the next control value given a set value and the state of the drone */
  public float getControl(float desiredState, float state) {
    
    //Store state for debugging
    _state = state;
    // Error value defined as the desired state - current state
    float error =  desiredState - state;
    // find how much time elapsed since the last function call
    int dt = (millis() - _time) % MAX_INT;
    // update the current time
    _time = millis();
    // update integral variable
    _integralTot += error;
    
    // Proportional error
    float proportional = error;
    
    // Integral of error
    float integral = _integralTot * dt; 
    
    // Derivative of error
    float derivative = (error - _prevError) / dt;
    
    // Final control value, stored in variable for debugging
    _control = (_kp * proportional) + (_ki * integral) + (_kd * derivative);
    
    // Update previous error to the current error
    _prevError = error;
    
    // return final control value
    return _control;
  }
  
  public String toString() {
    return _name + ": " + "State - " + _state + "\tControl - " + _control;
  }
 
 
}
  public void settings() {  size(800, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
