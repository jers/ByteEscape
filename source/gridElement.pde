final int Enemy = 1;
final int Bullet = 2;
final int Item = 4;
final int PEACEFUL = 8;

class gridElement {
  
  int liveEntityType;
  int backgroundTileType;
  int backgroundTileData;
  color backgroundTileColour;
  
  public gridElement(int eType, int bType, int bData) {
  
    liveEntityType = eType;
    backgroundTileType = bType;
    backgroundTileData = bData;
    if (bType < 3)
      setColor();
    else 
      backgroundTileColour = color(0); 
      
    
  }
  
  void setColor() {

    int red, green, blue = 0;
      
    switch(backgroundTileData) {
      case 0:
      //variations on white
        red = blue = green = 225 - (int) random(15, 50);
        break;
      case 1 :
        //variations on gray / black
        red = blue = green = 35 + (int) random(10, 25);
        break;
      case 2 :
        //variations on yellow
        //19,246,46
        red = (int) random(5,35);
        green = (int) random (220,255);
        blue = (int) random (220,255);
        break;        
      case 3:
        //variations on purple
        red = (int) random(210, 230);
        green = (int) random (135, 155);
        blue = (int) random (200, 220);
        break;
      default:
        //pure black
        red = green = blue = 0;
        break;   
        
        
    }
    backgroundTileColour = color(red, green, blue);    
    
  }
  
  boolean isTrigger() {
    
    if (backgroundTileType == 5 && backgroundTileData == 1)
      return true;
      
    return false;
    
  }
  
  boolean isObstruction() {
    
    if(backgroundTileType == 3)
      return true;
      
    return false;
    
  }
  
  
  
  boolean isPuzzle() {
   
    if (backgroundTileType == 4)
      return true;
      
    return false;
    
  }

  void doPuzzleOperation() {
    
    if (backgroundTileType == 4) {
      
      backgroundTileData--;
      
    }
    
  }
  
  void removeCoin() {
    
   backgroundTileType = 0; 
    
  }
  
  
  void flip() {
    
    if (backgroundTileType != 0) 
      return;
     
    switch(backgroundTileData) {
   
      case 0: 
        backgroundTileData = 1;
        break;
      case 1:
        backgroundTileData = 0;
        break;
      case 2:
        backgroundTileData = 3;
        break;
      case 3:
        backgroundTileData = 2;
        break;
    
    }
  
    setColor();
     
   
  }
  
  color getBackgroundTileColour() {
    
    return backgroundTileColour;
    
  }
  
  void setBackgroundTileColour(color colour) {
   
    backgroundTileColour = colour;
    
  }
  
  int getBackgroundTileData() {
    
    return backgroundTileData;
    
  }
  
  void setBackgroundTileData(int data) {
   
    backgroundTileData = data;
    
  }  
  
  void solvePuzzlePiece() {
    
    backgroundTileType = 5;
    
  }

  int getBackgroundTileType() {
    
    return backgroundTileType;
    
  }
  
  void setBackgroundTileType(int type) {
   
    backgroundTileType = type;
    
  }
  
  void setPeaceful () {
   
    liveEntityType |= PEACEFUL;
    
  }
  
  int getLiveEntityType() {
    
    return liveEntityType;
    
  }
  
  boolean isPeaceful() {
 
   if ((liveEntityType & PEACEFUL) != 0)
     return true;
     
   return false;
    
    
  }
  
  boolean hasEnemy() {
    
   if ((liveEntityType & Enemy) != 0)
     return true;
     
   return false;
    
  }
  
  boolean hasBullet() {
    
   if ((liveEntityType & Bullet) != 0)
     return true;
     
   return false;
    
  }
  
  boolean hasItem() {
    
   if ((liveEntityType & Item) != 0)
     return true;
     
   return false;
    
  }

  void addEnemy () {
    
    liveEntityType |= Enemy;
    
  }

  void addBullet () {
    
    liveEntityType |= Bullet;
    
  }

  void addItem () {
    
    liveEntityType |= Item;
    
  }
  
  void removeEnemy() {
   
    liveEntityType -= liveEntityType & Enemy;
    
  }
  
  void removeBullet() {
   
    liveEntityType -= liveEntityType & Bullet;
    
  }

  void removeItem() {
   
    liveEntityType -= liveEntityType & Item;
    
  }


  
  void addLiveEntity(int entityToAdd) {
    
   liveEntityType |= entityToAdd;
    
  }
  
  void removeEntity(int entityToRemove) {
    
    liveEntityType -= liveEntityType & entityToRemove;
    
  }
 
  void setLiveEntityType(int type) {
   
    liveEntityType = type;
    
  }
  
  
  
}