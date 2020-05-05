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
