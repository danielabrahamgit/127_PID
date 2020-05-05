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

Car car;
PID carController;

public void setup() {
  
  frameRate(30);
  car = new Car();
  carController = new PID(3, 0, 30, "Car Controller ");
}


public void draw() {
  background(255);
  float deg = car.getState();
  float control = -carController.getControl(0, deg); 
  car.updateCar(control);
  car.drawCar(width, height);
}
class Car {
  
  // State variables
  float _theta;
  float _omega;
  float _pos;
  float _vel;
  
  // Physical parameters
  final float ballMass = 1;
  final float ballRad = 10;
  final float len = 300;
  final float blockMass = 1;
  final float blockWidth = 200;
  final float blockHeight = 100;
  final float grav = 1;
  final float maxForce = 20;
  
  public Car() {
    _theta = PI/12;
    _omega = 0;
    _pos = 0;
    _vel = 0;
  }
  
  public float getState() {
    return  _theta * 180 / PI;
  }
  
  public void updateCar(float inputForce) {
    if (inputForce > maxForce) {
      inputForce = maxForce;
    } else if (inputForce < -maxForce) {
      inputForce = -maxForce;
    }
    
    float constant = 1 / ((blockMass / ballMass) + sq(sin(_theta)));
    
    float alpha = ((-inputForce * cos(_theta) / ballMass) -
    (sq(_omega) * len * sin(2 * _theta) / 2) + 
    ((ballMass + blockMass) * grav * sin(_theta) / ballMass)) / (constant * len);
    _omega += alpha;
    _theta += _omega;
    
    float acc = ((inputForce / ballMass) + 
    (sq(_omega) * len * sin(_theta)) -
    (grav * sin(2 * _theta) / 2)) / constant;
    _vel += acc;
    _pos += _vel;
      
}
  
  public void drawCar(int windowWidth, int windowHeight) {    
    pushMatrix();
    translate(windowWidth / 2, windowHeight / 2);
    if (_pos > windowWidth / 2) {
      _pos = 100 - windowWidth / 2;
    } else if (_pos < -windowWidth / 2) {
      _pos = windowWidth / 2 - 100;
    }
    fill(0);
    rect(_pos - blockWidth / 2, -blockHeight / 2, blockWidth, blockHeight);
    stroke(0);
    float endLineY = - ((blockHeight / 2) + (len * cos(_theta)));
    float endLineX = _pos + len * sin(_theta);
    line(_pos, -blockHeight / 2, endLineX, endLineY);
    ellipse(endLineX, endLineY, ballRad * 2, ballRad * 2);
    line(-windowWidth/2, 0, windowWidth/2, 0);
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
