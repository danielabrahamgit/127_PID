class Drone {
    
  /* State variables for the drone */
  float _theta, _omega;
  PVector _pos, _vel;
 
  /* Physical characteristics */
  int _len, _wid, _mass;
 
  /* Gravity constant */
  final PVector gravF;
 
  /* Drag parameter */
  final float drag = 0.92;
  
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
