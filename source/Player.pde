class Player {
  
  int authorizedLevel;
  Movement movement;
  
  int HUMAN = 0;
  int ROBOT = 1;
  int playerType;
  int retX, retY;
  int skillLevel;
  int bytes;
  int coins;
  
  boolean hasStealthAbility;
  boolean hasFlipAbility;
  boolean hasRescueAbility;
  boolean hasSolveAbility;
  
  Stack <Integer> collectedBlocks;

  
  public Player (int x, int y) {
    hasFlipAbility = false;
    hasRescueAbility = false;
    hasSolveAbility = false;
    hasStealthAbility = false;
    authorizedLevel = 0;
    movement = new Movement();
    movement.setPos(x,y);
    playerType = HUMAN;
    retX = x;
    retY = y;
    collectedBlocks = new <Integer> Stack();
    skillLevel = 1;
    if (DEBUG_ON == 1) {
      skillLevel = 20;
      hasSolveAbility = true;
      hasFlipAbility = true;
      hasRescueAbility = true;
      hasStealthAbility = true;
    }
    bytes = 0;
  }
  
  
  void increasebytes(int newbytes) {
    
    bytes += newbytes;
    
  }
  
  void resetDepth() {
    
    movement.resetDepth();
    
  }
  
  int getCoins() {
    
    return coins;
    
  }
  
  void addCoin () {
    
    coins++;
    
  }
  
  int getbytes() {
    
    return bytes;
    
  }
  
  void increaseSkill(int toLevel) {
    
    if (toLevel > skillLevel)
      skillLevel = toLevel;
    
  }
  
  void levelUp() {
    
    if (skillLevel > 20)
      return;
    skillLevel++;
    
  }
  
  int getSkillLevel() {
    
    return skillLevel; 
    
  }
  
  int getCurrentMove() {
   
    return movement.getCurrentMove();
    
  }
  
  void addFlipAbility() {
   
    hasFlipAbility = true;
    
  }
  
  void addRescueAbility() {
    
    hasRescueAbility = true;
    
  }
  
  void addSolveAbility() {
    
    hasSolveAbility = true; 
    
  }
  
  boolean hasFlipAbility() {
    
    return hasFlipAbility;
    
  }
  
  void addStealthAbility() {
    
    hasStealthAbility = true; 
    
  }
  
  boolean hasStealthAbility() {
    
    return hasStealthAbility;
    
  }
  
  boolean hasRescueAbility() {
    
    return hasRescueAbility;
    
  }
  boolean hasSolveAbility() {
    
    return hasSolveAbility;
    
  }

  
  
  int dropBlock() {
    if (collectedBlocks.size() > 0)
      return collectedBlocks.pop();
    return -1;
  }
 
  void updatePosition(int newX, int newY) {

    movement.setPos(newX, newY);
    
  }
  
  void addBlock( int block ) {
    collectedBlocks.push(block); 
  }
  
  int [] getMovement () {
    return movement.getSubroutine();
  }
  
  void setMovementBoundaries(int x, int y) {
   movement.setMovementBoundaries(x, y); 
    
  }
  
  int getMovementLength () {
    return movement.getSubroutineLength(); 
  }
  
  int getType() {
    return playerType; 
  }
  void setHUMAN() {
    playerType = HUMAN;
    movement.setPos(retX, retY);
  }
  void setROBOT() {
    playerType = ROBOT;
    retX = movement.getX();
    retY = movement.getY();
  }
  
  int tryUP() {
    return movement.tryUP(); 
  }
  int tryDOWN() {
    return movement.tryDOWN(); 
  }
  int tryLEFT() {
    return movement.tryLEFT(); 
  }
  int tryRIGHT() {
    return movement.tryRIGHT();  
  }
  
  void goUP() {
    movement.goUP(); 
  }
  void goDOWN() {
    movement.goDOWN(); 
  }
  void goLEFT() {
    movement.goLEFT(); 
  }
  void goRIGHT() {
    movement.goRIGHT(); 
  }
  
  int getAuthorizedLevel() {
    return authorizedLevel;
  }
  
  
  
  void addRobotMove(int move) {
     movement.addMove(move);
  }
  
  int calculateRobotMove() {
    return movement.calculateMove();
  }
  
  int validateSubroutine() {
    return movement.validateMovement();
  }
  
  void doMove() {
    movement.doMove();
  }


  void reset() {
    movement.reset();
    retX = 1;
    retY = 1;
  }
  
  void setAuthorizedLevel(int l) {
    
    authorizedLevel = l; 
    
  }
  
  int getX() {
    
    return movement.getX();
    
  }
  int getY() {
    
    return movement.getY();
    
  }
  
  int getPotX() {
    return movement.get_potX();  
  }
  int getPotY() {
    return movement.get_potY(); 
  }
  
}