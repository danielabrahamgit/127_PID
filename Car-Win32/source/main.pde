Car car;
PID carController;

void setup() {
  size(800, 800);
  frameRate(30);
  car = new Car();
  carController = new PID(3, 0, 30, "Car Controller ");
}


void draw() {
  background(255);
  float deg = car.getState();
  float control = -carController.getControl(0, deg); 
  car.updateCar(control);
  car.drawCar(width, height);
}
