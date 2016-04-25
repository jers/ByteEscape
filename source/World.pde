class World {
  
  int numZones;
  Zone [] zones;
  int currZone;
 
  public World (int nZones) {
   
    numZones = nZones;
    zones = new Zone [numZones];
    currZone = 0;
   
  }
 
  void loadZones() {
   
    for (int i = 0; i < numZones; i++) {
      zones[i] = new Zone(i);
      zones[i].loadZone(); 
      zones[i].loadZoneData();
    }
   
  }
  
  void scrambleColours() {
    
    zones[currZone].scrambleColours();
    
  }
  
  void setPeaceful(int x, int y) {
    
    zones[currZone].setPeaceful(x, y);
    
  }
  
  void setDialog(int progress) {
    
    zones[currZone].setDialog(progress);
    
  }
  
  boolean isPuzzle(int x, int y) {
    
    return zones[currZone].isPuzzle(x, y);
    
  }
  
  void doPuzzleOperation(int x, int y) {
    
    zones[currZone].doPuzzleOperation(x, y);
    
  }
 
  void loadLevel(int level) {
    
    zones[currZone].resetLiveGrid();
    currZone = level;
   
  }
  
  boolean setNextDialog(Player Stella) {
    
    return zones[currZone].setNextDialog(Stella);
    
  }
  
  boolean isObstruction(int x, int y) {
    
    return zones[currZone].isObstruction(x, y);
    
  }
  
  boolean isTrigger(int x, int y) {
  
    return zones[currZone].isTrigger(x, y);

  
  }
  
  boolean checkPuzzle() {
  
    return zones[currZone].checkPuzzle();
    
  }
  
  int solvePuzzle() {
  
    return zones[currZone].solvePuzzle();
  
  }
 
  int getCurrentXDimention() {
    return zones[currZone].getXDim(); 
  }
  int getCurrentYDimention() {
    return zones[currZone].getYDim();
  }
 
  Zone getcurrentZone() {
 
    return zones[currZone];
   
  }
 
  void changeBlock( int x, int y, int e ) {
    zones[currZone].changeBlock(x, y, e); 
  }
 
 
  int getBackgroundTileType(int x, int y) {
    
    return zones[currZone].getBackgroundTileType(x, y);
    
  }
  
  int getBackgroundTileData(int x, int y) {
    
    return zones[currZone].getBackgroundTileData(x, y);
    
  }
  
  int getBackgroundTileColour(int x, int y) {
    
    return zones[currZone].getBackgroundTileColour(x, y);
    
  }


 
  
  void moveEnemyUP(int x, int y) {
    zones[currZone].moveEnemyUP(x,y);
    
  }
  
  void moveEnemyDOWN(int x, int y) {
    zones[currZone].moveEnemyDOWN(x,y);
    
    
  }
  
  void setEnemy(int x, int y) {
    zones[currZone].setEnemy(x, y);
    
  }
  
  void moveEnemyLEFT(int x, int y) {
    zones[currZone].moveEnemyLEFT(x,y);
    
    
  }
  
  int testBoundaries(int x, int y) {
    return zones[currZone].testBoundaries(x, y);
  }
  void moveEnemyRIGHT(int x, int y) {
    zones[currZone].moveEnemyRIGHT(x,y);
    
    
  }
  void removeBullet(int x, int y) {
    
    zones[currZone].removeBullet(x, y);
    
  }
  
  void updateBullets(int x, int y, int nx, int ny) {
   
    zones[currZone].updateLiveGridBullets(x, y, nx, ny);
    
  }
  
  void spawnBulletUP(int x, int y) {

    zones[currZone].spawnBulletUP(x,y);
    
  }
  void spawnBulletDOWN(int x, int y) {

    zones[currZone].spawnBulletDOWN(x,y);
    
  }
  void spawnBulletLEFT(int x, int y) {

    zones[currZone].spawnBulletLEFT(x,y);

  }
  void spawnBulletRIGHT(int x, int y) {
    zones[currZone].spawnBulletRIGHT(x,y);

    
  }
  
  void resetZone() {
    zones[currZone].loadZone();
    
  }
  
  boolean hasEnemy(int x, int y) {
    
    return zones[currZone].hasEnemy(x, y);
    
  }
  
  boolean hasBullet(int x, int y) {
    
    return zones[currZone].hasBullet(x, y);
    
  }
  
  boolean hasItem(int x, int y) {
    
    return zones[currZone].hasItem(x, y);
    
  }

  String getNextDialog() {
  
    return zones[currZone].getNextDialog();
    
  }
  
  void flipBackgroundTile(int x, int y) {
    
    zones[currZone].flip( x,  y);
    
  }
  
  void normalizeTile(int x, int y) {
    
    zones[currZone].changeBlock( x,  y, 0);
    zones[currZone].setLeveledUp();

  }
  
  boolean isLeveled() {
    
    return zones[currZone].isLeveledUp();
    
  }
  
  void removeCoin(int x, int y) {
    
    zones[currZone].removeCoin(x, y);
    
  }
  
  boolean isAuthorized(int x, int y, int level) {
    
    return zones[currZone].isAuthorized(x, y, level);
    
  }
  
  int getLiveElement(int x, int y) {
   
    return zones[currZone].getLiveElement(x, y);
   
  }
}