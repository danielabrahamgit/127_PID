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
