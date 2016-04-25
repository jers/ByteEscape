class Bullet {
  
  int dir;
  int nSteps;
  int PosX;
  int PosY;
  int remainingLife;
  boolean isAlive;
  
  public Bullet (int direction, int steps, int rl, int x, int y) {
    dir = direction;
    nSteps = steps;
    PosX = x;
    PosY = y;
    remainingLife = rl;
    if (rl > 0)
      isAlive = true;
    else
      isAlive = false;
  }
  
  boolean isAlive() {
    return isAlive;
    
  }
  
  int moveBullet() {
   if (remainingLife <= 0) {
     
     isAlive = false;
     return 0;

   }
   
   switch(dir) {
     
    case 0:
      PosX -= nSteps;
      break;
    case 1:
      PosY -= nSteps;
      break;    
    case 2:
      PosX += nSteps;
      break;    
    case 3:
      PosY += nSteps;
      break;
      
   }
   
   remainingLife--;  
   return 1; 
    
  }
  
  int getX() {
   
    return PosX;
    
  }
  
  int getY() {
    
    return PosY; 
    
  }
  
}