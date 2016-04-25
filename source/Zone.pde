class Zone {

  int zoneId;
  int xDim, yDim;
  gridElement [][] grid;
  Hashtable <Integer, Queue <String>> dialog;
  Queue <String> functionalDialog;
  
  String currDialog;
  boolean hasFlipRestriction;
  boolean hasRescueRestriction;
  boolean hasSolveRestriction;
  String zoneName;
  int avatarId;
  boolean leveledUp;
  boolean firstPass;
  
  
  public Zone (int zId) {
    zoneId = zId;
    currDialog = "";
    zoneName = "";
    hasFlipRestriction = hasRescueRestriction = hasSolveRestriction = false;
    avatarId = 0;
    leveledUp = false;
    firstPass = true;
  
  }
  
  
  boolean isLeveledUp() {
    
    return leveledUp;
    
  }
  
  void setLeveledUp() {
    
    leveledUp = true;
    
  }
  
  
  void scrambleColours() {
    
   for (int i = 0; i < yDim; i++) {
     
     for (int j = 0; j < xDim; j++) {
       
       grid[i][j].setColor();
       
     }
     
     
   }
    
  }
  
  boolean isPuzzle(int x, int y) {
    
    if (testBoundaries(x,y) == 0)
      return false;
    return grid[y][x].isPuzzle();
    
  }
  
  boolean isObstruction(int x, int y) {
    
    if (testBoundaries(x,y) == 0)
      return true;

    return grid[y][x].isObstruction();
    
  }
  
  boolean isTrigger(int x, int y) {

    if (testBoundaries(x,y) == 0)
      return false;

    return grid[y][x].isTrigger();

  
  }
  
  boolean checkPuzzle() {
  
    boolean complete = true;
    for(int i = 0; i < yDim; i++) {
      
      for(int j = 0; j < xDim; j++) {
        
        if(grid[i][j].isPuzzle() && grid[i][j].getBackgroundTileData() != 0)
          complete = false;
        
      }
      
    }
    return complete;
    
  }
  
  int solvePuzzle() {
  
    int count = 0; 
    
    for(int i = 0; i < yDim; i++) {
      
      for(int j = 0; j < xDim; j++) {
        
        if(grid[i][j].isPuzzle()) {
          
          grid[i][j].solvePuzzlePiece();
          count++;
        }

        
      }
      
    }
    return count;
  
  }
  
  void doPuzzleOperation(int x, int y) {
    
    if (testBoundaries(x,y) == 0)
      return;
    if (!hasSolveRestriction)
      grid[y][x].doPuzzleOperation();
    
  }
  
  boolean isPeaceful(int x, int y) {

    if (testBoundaries(x,y) == 0)
      return false;

    return grid[y][x].isPeaceful();
    
  }
  
  
  
  void loadZoneData() {
    
    String line;
    BufferedReader reader;
    String levelName = "data/zones/level_text/zone_" + zoneId + ".cool";
    int temp;
    reader = createReader(levelName);
    dialog = new Hashtable();
    try {
      line = reader.readLine();
      temp = Integer.parseInt(line);
      hasFlipRestriction = (temp == 1);
      line = reader.readLine();
      temp = Integer.parseInt(line);
      hasRescueRestriction = (temp == 1);
      line = reader.readLine();
      temp = Integer.parseInt(line);
      hasSolveRestriction = (temp == 1);
      zoneName = reader.readLine();
      line = reader.readLine();
      int nStates = Integer.parseInt(line);
      for(int i =0; i < nStates; i++) {
        line = reader.readLine();
        int State = Integer.parseInt(line);
        line = reader.readLine();
        avatarId = Integer.parseInt(line);
        line = reader.readLine();
        int nLines = Integer.parseInt(line);
        functionalDialog = new LinkedList <String> ();
        for (int j = 0; j < nLines; j++) {
          line = reader.readLine();
          functionalDialog.add(line);
        }
        dialog.put(State, functionalDialog);
        
      }

      //functionalDialog = dialog.get(0);
      
      
    } catch (IOException e) {}
    
  }
  
  
  void setDialog(int progress) {
  
    Enumeration e = dialog.keys();
    int value;
    int currValue = 0;
    while (e.hasMoreElements()) {
      value = (int) e.nextElement();
      println(value);
      if ((progress > value) && value > currValue) {
        functionalDialog = dialog.get(value); 
        currValue = value;
      }
    }
  }
  
  boolean setNextDialog(Player Stella) {
   
    if (functionalDialog != null && functionalDialog.size() != 0) {
      String temp = functionalDialog.poll();
      int amount;
      if (temp.equals("levelup")) {
        temp = functionalDialog.poll();
        amount = Integer.parseInt(temp);
        Stella.increaseSkill(amount);
        gameScreen.initializeLevelUpAnimation();
        return false;

      } else if (temp.equals("teach")) {
        temp = functionalDialog.poll(); 
        amount = Integer.parseInt(temp);
        switch (amount) {
          case 0:
            Stella.addStealthAbility();
            Stella.addSolveAbility();
            gameScreen.initializeLearnSolveAnimation();
            achievement.trigger();
            break;          
          case 1:
            Stella.addFlipAbility();
            achievement.trigger();
            gameScreen.initializeLearnFlipAnimation();
            break;
          case 2:
            Stella.addRescueAbility();
            currDialog = "---GOT RESCUE ABILITY!";            
            break;
          case 3: //outdated
            currDialog = "---GOT SOLVE ABILITY!";                      
            Stella.addSolveAbility();
            break;
          
        }
        return false;
      } else {
        currDialog = temp;  
      }
      return true;
    } else {
            
      currDialog = "[NO COMMS]";
     
    }
    
     return false;
    

    
    
  }
  
  String getCurrDialog () {
    
    return currDialog; 
    
  }
  
  String getNextDialog() {
    if (functionalDialog.size() != 0)
      return functionalDialog.poll();

     return null;
  }
  
  void loadZone() {

    String [] sgrid;

    BufferedReader reader;
    String line; 
    String levelName = "data/zones/level_layout/zone_" + zoneId + ".cool";
    reader = createReader(levelName);
    
    try {

      line = reader.readLine();
      xDim = Integer.parseInt(line);
      line = reader.readLine();
      yDim = Integer.parseInt(line);
      grid = new gridElement [yDim][xDim];

      for (int i = 0; i < yDim; i++) {
        line = reader.readLine();  
        sgrid = line.split(",");
        for ( int j = 0; j < xDim; j++) {
          
          int backgroundType = 0;
          int backgroundData = 0;
          int gridE = Integer.parseInt(sgrid[j]);
          
          if(gridE < 10) {
            
            backgroundType = 0; 
            backgroundData = gridE;
                 
          } else if (gridE < 30) {
            if (gridE < 20) {
              backgroundType = 1;
              backgroundData = gridE % 10;
            } else {
              if (!firstPass)
                backgroundType = 0;
              else 
                backgroundType = 2;

              backgroundData = gridE % 10;

            }
            
          } else if (gridE < 40) {
            
            backgroundType = 3;
            
          } else if (gridE < 50) {
            
           backgroundType = 4;
           backgroundData = gridE % 10;
            
          } else if (gridE < 60) {
           
            backgroundType = 5;
            backgroundData = gridE % 10;
            
            if (backgroundData == 2 && leveledUp)
              backgroundData = 0;
            
          } else if (gridE < 100) {
            backgroundType = 6;
            backgroundData = gridE - 60;
            
          }


          grid[i][j] = new gridElement(0,backgroundType,backgroundData);

        }
        
      }
      
    } catch (IOException e) {}
    firstPass = false;

  }
  
  int getXDim() {
    return xDim; 
  }
  
  int getYDim() {
    return yDim; 
  }
  gridElement [][] getGrid() {
    
    return grid; 
    
  }
  
  String getLevelName() {
    
    return zoneName;
    
  }
  
  void flip(int x, int y) {
    if (!hasFlipRestriction)
      grid[y][x].flip();
    
  }
  
  boolean hasEnemy(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return false;
    return grid[y][x].hasEnemy();
    
  }
  
  boolean hasBullet(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return false;
    return grid[y][x].hasBullet();
    
  }
  
  boolean hasItem(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return false;    
    return grid[y][x].hasItem();
    
  }
  
  void setPeaceful(int x, int y) {

    if (testBoundaries(x,y) == 0)
      return;    
    
    grid[y][x].setPeaceful();
    
    
  }
  
  int getLiveElement(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return -1;

    return grid[y][x].getLiveEntityType();
    
  }
 
  int get_xDim() {
    
    return xDim;
    
  }
  int get_yDim() {
    
    return yDim;
    
  }
  
  
  int getBackgroundTileType(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return -1;
    return grid[y][x].getBackgroundTileType();
  }
  
  int getBackgroundTileData(int x, int y) {

    if (testBoundaries(x,y) == 0)
      return -1;
    return grid[y][x].getBackgroundTileData();    
    
  }
  
  int getBackgroundTileColour(int x, int y) {
    
    if (testBoundaries(x,y) == 0)
      return -1;
    return grid[y][x].getBackgroundTileColour();    

    
  }
  
  boolean isAuthorized(int x, int y, int level) {
    
    int backgroundType = grid[y][x].getBackgroundTileType();
    
    if (backgroundType > 4) {
      
      return true;
      
    } else if (backgroundType < 3) {
      
      int backgroundData = grid[y][x].getBackgroundTileData();
      
      if (backgroundData == level) {
      
        return true;
        
      }
        
    }
    
    return false; 
  }
  
  void removeCoin(int x, int y) {
    
    grid[y][x].removeCoin(); 
    
  }
  
  void updateLiveGridBullets(int x, int y, int nx, int ny) {
    
    grid[y][x].removeBullet();
    grid[ny][nx].addBullet();
    
  }
  
  void resetLiveGrid() {
    
    for(int j = 0; j < xDim; j++) {
      for(int i = 0; i < yDim; i++) {
        grid[i][j].setLiveEntityType(0);  
      }
    }
  }
  
  void removeBullet(int x, int y) {
    
    grid[y][x].removeBullet();
    
  }
  void moveEnemyUP(int x, int y) {
    
    grid[y][x].removeEnemy();
    grid[y-1][x].addEnemy();
      
  }
  
  void moveEnemyDOWN(int x, int y) {

    grid[y][x].removeEnemy();
    grid[y+1][x].addEnemy();

  }
  
  int testBoundaries(int x, int y) {
    if (x < 0 || x >= xDim || y < 0 || y >= yDim) {
      return 0; 
    }
    return 1;
    
  }
  
  void moveEnemyLEFT(int x, int y) {
    
    grid[y][x].removeEnemy();
    grid[y][x-1].addEnemy();
    
  }
  
  void moveEnemyRIGHT(int x, int y) {

    grid[y][x].removeEnemy();
    grid[y][x+1].addEnemy();
      
    
  }
  
  void spawnBulletUP(int x, int y) {
    
    if ((y-1) >= 0)
      grid[y-1][x].addBullet();
    
  }
  
  void spawnBulletDOWN(int x, int y) {
    
    if ((y+1) < yDim)
      grid[y+1][x].addBullet();
    
  }
  void spawnBulletLEFT(int x, int y) {

    if ((x-1) >= 0)
      grid[y][x-1].addBullet();

  }
  void spawnBulletRIGHT(int x, int y) {
    if ((x+1) < xDim)
      grid[y][x+1].addBullet();
      
    
  }
  
  void setEnemy(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return;

    grid[y][x].addEnemy();
    
    
  }
  
  void changeBlock(int x, int y, int e) {
    
    grid[y][x].setBackgroundTileData(e);
    
  }
  
}