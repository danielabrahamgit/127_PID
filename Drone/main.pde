/* Variables for setting the roll and the height */
float setRoll = 0;
float setHeight = 0;
float throttleLeft = 0;
float throttleRight = 0;

/* Drone instance and PID controllers */
Drone drone;
PID rollController;
PID heightController;

void setup() {
  // Set up the size of the frame (in pixels)
  size(800, 800);
  // Set up the frame rate
  frameRate(60);
  // Initialize new drone with lenth 60, height 10, and mass 10
  drone = new Drone(60, 10, 10);
  // Initialize a height controller with PID values shown
  heightController = new PID(.01, 0.00005, 3.2, "Height Controller");
  // Initialize a roll controller  with PID values shown
  rollController = new PID(.5, 0.001, 3, "Roll Controller");
}

void draw() {
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

void userInput() {
  // Use mouse to control set points
  setHeight = (height/2 - mouseY);
  setRoll = map(mouseX, 0, width, -5, 5);
}
