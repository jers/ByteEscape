final color RESTRICTED_RED = #6B1414;
final color ENEMY_RED = #B50042;
final color VALID_GREEN = #1D7B5E;
final int STD_GRAY = 100;
final color HUMAN_COLOUR = #7BC8E1;
final color ROBOT_COLOUR = #89DDE1;
//final color ROBOT_COLOUR = #7CC9E2;
final color STELLA1 = #FAC864;
final color STELLA2 = #E9E445;

final color BLOCKT1 = #C8C8C8;
final color BLOCKT2 = #353535;
final color BLOCKT3 = #7BC8E1;

final color BLOCKT4 = #DC91D2;
final color BACKGROUND = #001E3A;
final color UI_BACKGROUND = #002555;
final color SUBROUTINE_BACKGROUND = #7BC8E1;
final color OBSTRUCTION = #003366;
final color PEACEFUL_GREEN = #00fa9a; 
final color FLASH = #f0ffff;
final color COIN = #ffa500;
final color BRIGHT_YELLOW = #ffd700;

class Canvas{
  
  //what do we NEED?
  
  
  Zone currZone;
  int gridDimention;
 
  
  int randomizerAnimationCount, respawnAnimationCount, colourAnimationCount, screenshakeCount, flashCount;
  int bonusCount;
  int coinsCount;
  boolean levelupAnimation;
  boolean learnSolveAnimation;
  boolean learnFlipAnimation;
  boolean backgroundFlash;
  boolean colourAnimation;
  boolean respawnAnimation;
  boolean randomizerAnimation;
  boolean BONUS_Animation;
  boolean COINS_Animation;
  boolean screenShake;
  int windowX, windowY;
  
  int dopamine; 
  
  
  Player Stella;
  
  //int [] charSymbols = {91, 93, 123, 125, 187, 164, 161, 165, 167, 182, 191, 94, 126, 162, 215, 223, 230, 163, 69, 63};
  int [] displayRow_startPosition = {-4, -1, -1, 0, 0, 1, 1};
  int [] displayRow_gridWidth = {8, 10, 12, 12, 12, 10, 8};
  String [] levels = {"Ambitious Amoeba", "Macho Mosquito", "Curious Centipede", "Majestic Moth", "Superstitious Spider", "Gentle Goldfish", "Sinister Scorpion", "Cosmic Chipmunk", "Easygoing Eel", "Ferral Fox", "Reclusive Racoon", "Evasive Eagle" ,"Dastarly Dog", "Suggestive Sloth", "Lanky Lion", "Daredevil Dolphin", "Zesty Zebra", "Odourless Orangatan", "Benevolent Bear", "Earnest Elephant", "Weeping Whale"};
  int display_gridHeight = 7;
  String levelName;
  int backgroundColour;
  
  public Canvas(int dim, int window_x, int window_y, Player s) {
    
    gridDimention = dim;
    Stella = s;
    windowX = window_x;
    windowY = window_y;
    randomizerAnimationCount = respawnAnimationCount = colourAnimationCount = coinsCount = bonusCount = 0;
    levelupAnimation = colourAnimation = respawnAnimation = learnSolveAnimation = learnFlipAnimation = randomizerAnimation = screenShake = BONUS_Animation = COINS_Animation = false;
    textFont(body, 72);
    backgroundColour = 255;
    dopamine = STELLA1;

  }
  
  public void loadLevel(Zone z) {
   
    currZone = z;
    randomizerAnimationCount = respawnAnimationCount = colourAnimationCount = 0;
    levelupAnimation = colourAnimation = respawnAnimation = learnSolveAnimation = learnFlipAnimation = randomizerAnimation = false;
    levelName = currZone.getLevelName();
    
  }
    
  public void initializeLevelUpAnimation() {
    
    randomizerAnimationCount = 20;
    levelupAnimation = true;
    randomizerAnimation = true;
    dopamine = BRIGHT_YELLOW;
    
    
  }
  public void initializeLearnSolveAnimation() {
    
    randomizerAnimationCount = 20;
    learnSolveAnimation = true;
    randomizerAnimation = true;
    
  }
  
  public void initializeLearnFlipAnimation() {
    
    randomizerAnimationCount = 20;
    learnFlipAnimation = true;
    randomizerAnimation = true;
    
  }
  
  
  public void initializeRespawnAnimation() {
    
    respawnAnimationCount = 12;
    respawnAnimation = true;
    
    
  }
  
  public void gotCoin() {
    
    COINS_Animation = true;
    coinsCount = 10;
    dopamine = BRIGHT_YELLOW;
    
  }
  
  public void solvedPuzzle() {
    
    BONUS_Animation = true;
    bonusCount = 10;
    
  }
  
  public void backgroundFlash() {
    
    backgroundColour = FLASH;
    backgroundFlash = true;
    flashCount = 10;
    
  }
  
  public void initializeColourAnimation() {
    
    colourAnimationCount = 40; 
    colourAnimation = true;
    
  }
  

  
  public void doColourAnimation() {
   
    if (colourAnimationCount > 0) {
      gWorld.scrambleColours();
      colourAnimationCount--;
    }
    else 
      colourAnimation = false;
    
  }
  
  public void startScreenShake() {
    
    screenShake = true;
    screenshakeCount = 10;
    
  }
  
  public void doLevelupBanner() {

    doBanner(levels[Stella.getSkillLevel() - 1]);

    
  }
  
  public void doLearnFlipBanner() {
    
    doBanner("LEARNT FLIP");

  }
  
  public void doLearnSolveBanner() {
    
    doBanner("LEARNT SOLVE");

  }
  
  public void doLevelBanner() {
    
    doBanner(levelName);

    
  }
  
  public void doBanner(String line) {
    textAlign(CENTER);
    textSize(80);
    fill(HUMAN_COLOUR);
    text(line, (width - gridDimention * 3)/2, gridDimention * 4/3);
  }
  
  public void render(int runState) {
    background(backgroundColour);
    
    renderPlayerView();
    
    
    if (backgroundFlash) {
     
      if (flashCount > 0) {
        
        flashCount--; 
      } else {
        
        backgroundFlash = false;
        backgroundColour = 255;
      }
      
    }
    
    
    if (!respawnAnimation && !randomizerAnimation)
      renderData(runState);
    
    if (colourAnimation)
      doColourAnimation();
      
    if (randomizerAnimation) {
      
      if (levelupAnimation)
        doLevelupBanner();
      else if (learnSolveAnimation) 
        doLearnSolveBanner();
      else if (learnFlipAnimation) 
        doLearnFlipBanner();
  
      
    } else if(!COINS_Animation)
      dopamine = STELLA1;

    
    if (respawnAnimation)
      doLevelBanner();
    
  }
  
  public void renderPlayerView() {
    
    noStroke();
    
    int StellaX = Stella.getX();
    int StellaY = Stella.getY();
    
    int gridX = StellaX;
    int gridY = StellaY - (display_gridHeight - 1) / 2;
    
    int playerViewX = width / 2 - gridDimention * 2; 
    int playerViewY = height / 2 - ((display_gridHeight - 1) / 2) * gridDimention;
    
    int gridWidth = 0;

    int element = 0; 
    int offset = 0;
    int screenOffsetX = 0;
    int screenOffsetY = 0;
    
    if (screenShake) {
      if (screenshakeCount > 0) {
        
        screenshakeCount--;
        screenOffsetX = 2 - (int) random (0, 4);
        playerViewX += screenOffsetX;
        screenOffsetY = 1 - (int) random (0, 2);
        playerViewY += screenOffsetY;
        
      } else
        screenShake = false;
      
    }
    
    if (respawnAnimation) {
      if (respawnAnimationCount > 0) {
        offset = respawnAnimationCount;
        respawnAnimationCount--;
      } else
        respawnAnimation = false; 
    }
    
    if (randomizerAnimation) {
      if (randomizerAnimationCount > 0) 
        randomizerAnimationCount--;
      else {
        randomizerAnimation = false;
        
        if (levelupAnimation)
          levelupAnimation = false;
        if (learnFlipAnimation)
          learnFlipAnimation = false;
        if (learnSolveAnimation)
          learnSolveAnimation = false;
        
        
      }
    }

    
    
    for(int i = 0; i < display_gridHeight; i++) {
      gridX = gridX + displayRow_startPosition[i];
      playerViewX = playerViewX + displayRow_startPosition[i] * gridDimention;
      gridWidth = displayRow_gridWidth[i];
      if (respawnAnimation && gridWidth >= offset)
        gridWidth = gridWidth-offset;
      for(int j = 0; j < gridWidth; j++) {
        
        if (randomizerAnimation && (int) random(0,50) > 46) {
          gridX++;
          playerViewX += gridDimention;
          continue;
          
        }
        
        element = currZone.getLiveElement(gridX, gridY);
        if (element > 0)
          renderLiveElement(playerViewX, playerViewY, gridX, gridY);
        else if (element == 0)
          renderBackground (gridX, gridY, playerViewX, playerViewY);
        else 
          renderDot(playerViewX, playerViewY);
        gridX++;
        playerViewX += gridDimention;
        
      }
      
      gridY++;
      playerViewX -= gridWidth * gridDimention; 
      gridX -= gridWidth;
      playerViewY += gridDimention;
      
    }
    
    renderPlayer(screenOffsetX, screenOffsetY);

    
  }
  
  public void renderDot(int playerViewX, int playerViewY) {
    fill(OBSTRUCTION);
    rect(playerViewX, playerViewY, gridDimention, gridDimention);
    overlayDot(STD_GRAY, playerViewX, playerViewY);
    
    
  }
  
  public void overlayMidsizeDot(int colour, int x, int y) {
    
    fill(colour);
    ellipse(x + gridDimention / 2,y + gridDimention / 2, gridDimention / 4,gridDimention / 4);

    
  }
  
  public void overlayDot(int colour, int X, int Y) {
    
    fill(colour);
    ellipse(X + gridDimention / 2,Y + gridDimention / 2, gridDimention / 8,gridDimention / 8);

  }
  
  public void renderBackground(int gridX, int gridY, int playerViewX, int playerViewY ) {
  
    int type = currZone.getBackgroundTileType(gridX, gridY); 
    
    int colour = getColour(type, gridX, gridY);    
    fill(colour);
    rect(playerViewX, playerViewY, gridDimention, gridDimention);
    
    renderOverlay(type, gridX, gridY, playerViewX, playerViewY);

  
  }
  
  public void overlayMiniBox(int colour, int viewX, int viewY) {
    
    fill(colour);
    rect(viewX + gridDimention / 8, viewY + gridDimention / 8, gridDimention * 3/4, gridDimention * 3/4);

    
  }
  
  public void overlayMiniCircle(int colour, int viewX, int viewY) {

    fill(colour);
    ellipse(viewX + gridDimention / 2,viewY + gridDimention / 2, gridDimention * 2 / 3,gridDimention * 2 / 3);

    
  }
  
  public void overlayText(int colour, String character, int viewX, int viewY) {
    

    textAlign(CENTER);
    textSize(30);
    fill(colour);
    text(character, viewX + gridDimention / 2,viewY + gridDimention * 15 / 24);
    textAlign(LEFT);
    
    
  }
  
  public void overlayZone(int colour, int element, int playerViewX, int playerViewY) {
    
    fill(colour);
    
    if ((element & 1) != 0) {
      rect(playerViewX + gridDimention / 8,playerViewY + gridDimention / 8,gridDimention * 3/8,gridDimention * 1/4);        
    }
    
    if ((element & 2) != 0) {
      rect(playerViewX + gridDimention / 2,playerViewY + gridDimention / 8,gridDimention * 3/8,gridDimention * 1/4);                  
    }
    if ((element & 4) != 0) {
      rect(playerViewX + gridDimention / 8,playerViewY + gridDimention * 3/8,gridDimention * 3/8,gridDimention * 1/4);                  
       
    }
    if ((element & 8) != 0) {
      rect(playerViewX + gridDimention / 2,playerViewY + gridDimention * 3/8,gridDimention * 3/8,gridDimention * 1/4);                  
        
    }
    
    if ((element & 16) != 0) {
      rect(playerViewX + gridDimention / 2,playerViewY +  gridDimention * 5/8,gridDimention * 3/8,gridDimention * 1/4);                  
        
    }
    if ((element & 32) != 0) {
      rect(playerViewX + gridDimention / 2,playerViewY +  gridDimention * 5/8,gridDimention * 3/8,gridDimention * 1/4);                  
        
    }

    
    
  }
  
  public void renderOverlay(int type, int gridX, int gridY, int playerViewX, int playerViewY) {
    
    int element;
    
    switch(type) {
      case 0:
        break;
      case 1:
        overlayMiniBox(255, playerViewX, playerViewY);
        overlayDot(STD_GRAY,playerViewX, playerViewY);
        break;
      case 2:
        overlayMidsizeDot(STELLA1, playerViewX, playerViewY);
        //no longer used
        //overlayMiniBox(255, playerViewX, playerViewY);
        //overlayDot(RESTRICTED_RED,playerViewX, playerViewY);
        break;
      case 3:
        overlayDot(STD_GRAY, playerViewX, playerViewY);
        break;
      case 4:
        overlayMiniCircle(255, playerViewX, playerViewY);
        element = currZone.getBackgroundTileData(gridX, gridY);
        overlayText(BACKGROUND, element + "", playerViewX, playerViewY);
        break;
      case 5:
        
        element = currZone.getBackgroundTileData(gridX, gridY);
        if (element == 1) {
          overlayMiniCircle(255, playerViewX, playerViewY);
          overlayText(BACKGROUND, "=", playerViewX, playerViewY);

        } else if (element == 2) {
          overlayMiniCircle(255, playerViewX, playerViewY);
          overlayDot(VALID_GREEN,playerViewX, playerViewY);

          
          
        }
        break;
      case 6:
        overlayMiniBox(STD_GRAY * 2, playerViewX, playerViewY);
        element = currZone.getBackgroundTileData(gridX, gridY);;
        overlayZone(55, element, playerViewX, playerViewY);
        break;

     
      
    }
    
    
    
    
  }
  
  public int getColour(int type, int gridX, int gridY) {
    
    int colour = 0x0;
    
    switch(type) {
      
      case 0:
        colour = currZone.getBackgroundTileColour(gridX, gridY);
        break;
      case 1:
        colour = currZone.getBackgroundTileColour(gridX, gridY);  
        break;
      case 2:
        colour = currZone.getBackgroundTileColour(gridX, gridY);
        break;
      case 3:
        colour = OBSTRUCTION;
        break;
      case 4:
        colour = OBSTRUCTION;
        break;
      case 5:
        colour = VALID_GREEN;
        break;
      case 6:
        int element = currZone.getBackgroundTileData(gridX, gridY);
        if (Stella.getSkillLevel() >= element)
          colour = VALID_GREEN;
        else 
          colour = RESTRICTED_RED;
        break;
      case 7:
        colour = RESTRICTED_RED;
        break;
      
     }
     
     return colour;
    
  }
  
  public void renderLiveElement(int playerViewX, int playerViewY, int gridX, int gridY) {
    
  
    if (currZone.hasEnemy(gridX, gridY)) {
       if (currZone.isPeaceful(gridX, gridY)) 
         fill(PEACEFUL_GREEN);
       else 
         fill(ENEMY_RED);
    } else if (currZone.hasBullet(gridX, gridY))
      fill(RESTRICTED_RED);
 
    rect(playerViewX, playerViewY, gridDimention, gridDimention);
    
    
  }
  
  
  
  public void renderPlayer(int offsetX, int offsetY) {
    int type = Stella.getType();
    
    if (type == 0) {
      fill(dopamine);    
    } else
      fill(STELLA2);
      
    rect(width / 2 - gridDimention * 2 + offsetX, height / 2 + offsetY, gridDimention, gridDimention);
//    stroke(0);    

    if (type == 0) {
      switch(Stella.getAuthorizedLevel()) {
        case 0:
          fill(BLOCKT1);
          break;
        case 1:
          fill(BLOCKT2);
          break;
        case 2:
          fill(BLOCKT3);
          break;
        case 3:
          fill(BLOCKT4);
          break;
        default:
          fill(0);
          break;     
      
      }
    } else {
      fill(HUMAN_COLOUR);
    }
    rect(width / 2 + gridDimention / 3 - gridDimention * 2 + offsetX ,height / 2 + gridDimention / 3 + offsetY, gridDimention / 3, gridDimention / 3);
  }
  
  
  
  public void renderData(int runState) {
    
    if (runState == PROGRAMMING || runState == ROBOT)
      renderSubroutineUI();
    else 
      renderPlayerData();
    
    
  }
  
  void renderPlayerData() {
    
    int offset = gridDimention * 5;
    
    //fill(BACKGROUND);
    //rect(0,0, gridDimention * 6, gridDimention * 2);
    

    textAlign(CENTER);
    textSize(40);
    fill(HUMAN_COLOUR);
    text(levelName, (width - gridDimention * 3)/2, gridDimention * 2/3);
    fill(ROBOT_COLOUR);
    text("Rank: " + levels[Stella.getSkillLevel() - 1], (width - gridDimention * 3)/2, gridDimention * 5/3);
   
   // if (runState == DIALOG) {
    String dialog = "-[END OF-COMMUNICATIONS]-----controls--space = start/stop-programming--r = reset level";   
      
    if (levelDialog)
      dialog = currZone.getCurrDialog();
      
    if (dialog == "")
      text("...", width - offset / 2, height / 2);
    else {
      textSize(25);
      String [] lines = dialog.split("-");
      //a little pythony
      int count = 0;
      int rem = 0;
      for(String line : lines) {
        rem = (count % 3);
        if (rem == 0)
          fill(OBSTRUCTION);
        else if (rem == 1)
          fill(BACKGROUND);
        else if (rem == 2)
          fill(BLOCKT2);
        text(line, width - offset / 2, gridDimention * 3 + count * gridDimention / 2);
        count++;
      }

    }     


    fill(ROBOT_COLOUR);
    textAlign(CENTER);
    textSize(25);

    text("COINS", width - gridDimention * 4, gridDimention * 5/3);
    text("BONUS", width - gridDimention * 4, gridDimention * 2/3);

    textSize(25);
    fill(OBSTRUCTION);
    if (BONUS_Animation) {
      if (bonusCount > 0) {
        bonusCount--;
        
      } else {
        BONUS_Animation = false; 
      }
      textSize(60);
      text(Stella.getbytes(), width - gridDimention * 2 + 2 - (int) random(0,4), gridDimention * 2/3 + 2 - (int) random(0,4));
      
    } else {
      textSize(20); 
      text(Stella.getbytes(), width - gridDimention * 2, gridDimention * 2/3);

    }
    
    if (COINS_Animation) {
      if (coinsCount > 0) {
        coinsCount--;

        
      } else {
        COINS_Animation = false; 
      }
      
      textSize(60);
      text(Stella.getCoins(), width - gridDimention * 2 + 2 - (int) random(0,4), gridDimention * 5/3 + 2 - (int) random(0,4));
      
    } else {
      textSize(20); 
      text(Stella.getCoins(), width - gridDimention * 2, gridDimention * 5/3);

    }    
    
    
    textAlign(LEFT);
  }
  
  void renderSubroutineUI() {
    
    textAlign(CENTER);
    textSize(30);
    fill(OBSTRUCTION);
    text("PROGRAMMING", width - gridDimention * 3 + gridDimention, gridDimention * 2 / 3);
    text("z = start loop(s)     arrow keys = movement     x = finish loop", (width- gridDimention * 3) / 2, gridDimention * 2/3);
    //fill(ROBOT_COLOUR);
    text("Space = Run Program                 r = reset level", (width - gridDimention * 3) / 2, gridDimention * 5/3);

    
    
    
    
    textAlign(LEFT);
    
    int [] subroutine;
    subroutine = Stella.getMovement();
    int subLength = Stella.getMovementLength();
    int wPos = 0;
    int vPos = 1;
    String dir; 
    int textX = gridDimention / 5;
    int textY = gridDimention / 3;
    float offset = gridDimention * 4.5;
    textSize(20);
    

    
    int currentMove = Stella.getCurrentMove();
    
    for(int i = 0; i < subLength; i++) {
      
      if (i == currentMove) {
        fill(0);
        ellipse(width-offset + (wPos - 2) * gridDimention / 2 + gridDimention / 4, vPos * gridDimention / 2 + gridDimention / 4, gridDimention / 4, gridDimention / 4);
      }
      if (subroutine[i] > 0) {
        fill(255, 200, 100);
        wPos++;
        rect(width - offset + wPos * gridDimention / 2, vPos * gridDimention / 2, gridDimention/2, gridDimention/2);
        fill(0);
        text(subroutine[i], width - offset + wPos * gridDimention / 2 + textX, vPos * gridDimention / 2 + textY);
        wPos--;
      } else {
        switch(subroutine[i]) {
          case -1:
            fill(200);
            wPos--;
            vPos++;
            rect(width - offset + wPos * gridDimention / 2, vPos * gridDimention / 2, gridDimention/2, gridDimention/2);
            fill(0);
            text("E", width - offset + wPos * gridDimention / 2 + textX, vPos * gridDimention / 2 + textY);
            break;
          case -2:
            fill(200);
            vPos++;
            rect(width - offset + wPos * gridDimention / 2, vPos * gridDimention / 2, gridDimention/2, gridDimention/2);
            fill(0);
            text("S", width - offset + wPos * gridDimention / 2 + textX, vPos * gridDimention / 2 + textY);
            fill(255, 200, 100);
            wPos++;
            i++;
            rect(width - offset + wPos * gridDimention / 2, vPos * gridDimention / 2, gridDimention/2, gridDimention/2);
            fill(0);
            text(subroutine[i], width - offset + wPos * gridDimention / 2 + textX, vPos * gridDimention / 2 + textY);
            break;
          case -7:
            vPos++;
            fill(RESTRICTED_RED); 
            rect(width - offset + wPos * gridDimention / 2, vPos * gridDimention / 2, gridDimention/2, gridDimention/2);
            fill(0);
            text("Q", width - offset + wPos * gridDimention / 2 + textX, vPos * gridDimention / 2 + textY);
            break;
          default:
            vPos++;
            fill(HUMAN_COLOUR);
            rect(width - offset + wPos * gridDimention / 2, vPos * gridDimention / 2, gridDimention/2, gridDimention/2);
            fill(0);
            if (subroutine[i] == -6) 
              dir = "D";
            else if (subroutine[i] == -5) 
              dir = "U";
            else if (subroutine[i] == -4)
              dir = "R";
            else 
              dir = "L";
            text(dir, width - offset + wPos * gridDimention / 2 + textX, vPos * gridDimention / 2 + textY);
            break;  
        }

      }
    }    
    
    
  }
}