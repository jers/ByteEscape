import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class game extends PApplet {

/*
  States 
  Intro
  Pause
  Reset 

*/
static final int INTRO_SCREEN = 0;
static final int GAMEPLAY = 1;
static final int PAUSE = 2;
static final int RESET = 3;

/*
  Runtime ie Gameplay states 

*/

static final int HUMAN = 0;
static final int ROBOT = 1;
static final int PROGRAMMING = 2;
static final int DIALOG = 3;
static final int CUTSCENE = 4;
static final int RESET_LEVEL = 5;

int DEBUG_ON; 


/*
  bonus when you save an enemy
*/
static final int saveBonus = 500;

/*
  when dialog is from the npc, dialog target indicates which entity (from the list of ai enemies) it is. 
*/


//import net.java.games.input.*;
//import org.gamecontrolplus.*;


//ControlIO control;
//Configuration config;
//ControlDevice device; 


Minim minim;
AudioSample puzzlePiece;
AudioSample puzzleSolved;
AudioSample loadtheLevel;
AudioSample switcher;
AudioSample rewind;
AudioSample startProgramming;
AudioSample failure;
AudioSample start;
AudioSample achievement;
AudioSample coin;
AudioSample computerBlip;

Player Stella; 
Canvas gameScreen;
World gWorld; 

boolean key_pressed, key_up, key_down, key_left, key_right; 
boolean key_r, key_spc;
boolean key_z, key_x;

int gameState;
int runState;
int currentLevel;

int playerStartX = 1;
int playerStartY = 1;
boolean levelDialog;
 
PFont title;
PFont body;

/*
  Setup

*/

public void setup () {

  strokeWeight(3);  

  levelDialog = false;
  int numZones = 21;
  int gridDimention = 80;
  
  runState = HUMAN;
  gameState = GAMEPLAY;
  minim = new Minim(this);
  
  DEBUG_ON = 0;
  
  if (DEBUG_ON == 1)
    currentLevel = 0;
  else 
    currentLevel = 1;
  //title = createFont("MISTV___.ttf", 72);
  //body = createFont("zekton rg.ttf", 24);
  body = loadFont("SegoeUIBlack-48.vlw");

  puzzlePiece = minim.loadSample("data/sounds/puzzleE2.aiff",512);
  puzzleSolved = minim.loadSample("data/sounds/puzzle_complete.mp3", 512);
  loadtheLevel = minim.loadSample("data/sounds/wooly-resonance.aif",512);
  switcher = minim.loadSample("data/sounds/double_beep.aif",512);
  rewind = minim.loadSample("data/sounds/rewindvinyl.aif",512);
  startProgramming = minim.loadSample("data/sounds/boip.aif", 512);
  failure = minim.loadSample("data/sounds/vidgame-bleep1.aif",512);
  start = minim.loadSample("data/sounds/computer6.aif", 512);
  achievement = minim.loadSample("data/sounds/puzzleComplete2.mp3",512);
  coin = minim.loadSample("data/sounds/vidgame-points.aif",512);
  computerBlip = minim.loadSample("data/sounds/computer_blip.aif", 512);


  
  
  //control = ControlIO.getInstance(this);

  key_pressed = key_up = key_down = key_left = key_right = false; 
  key_z = key_x = false; 
  key_r = key_spc = false;

  Stella = new Player(playerStartX, playerStartY) ;
  gWorld = new World(numZones);
  gWorld.loadZones();
 // ai.loadPrebuiltEnemies(numberOfEnemies);

  gameScreen = new Canvas(gridDimention, 1840, 800, Stella);
 
  runState = DIALOG;  
  //OS.loadEntities();
  //OS.loadLevel(); 
  start.trigger();
  loadLevel(currentLevel);
  frameRate(10);
}  


/*

  each time a level is loaded most of the non player information needs to be reset. 
  

*/
public void loadLevel(int level) {
  
  gWorld.loadLevel(level);
  gameScreen.loadLevel(gWorld.getcurrentZone());
  Stella.reset();  
  Stella.setAuthorizedLevel(  gWorld.getBackgroundTileData(1,1) );
  Stella.setMovementBoundaries(gWorld.getCurrentXDimention(), gWorld.getCurrentYDimention());
  currentLevel = level;
  //each level has a dialog intro
  levelDialog = true;
  Stella.updatePosition(playerStartX, playerStartY);
  
  //Stella flips the block she is on
  if (Stella.hasFlipAbility())
    gWorld.flipBackgroundTile(playerStartX, playerStartY);
  //println(gWorld.getCurrentXDimention());
  prepDialog();
  gameScreen.initializeRespawnAnimation();
  resetKeys();


  
}
/*

  prep functions transition the runState

*/

public void prepHumanMode() {
  Stella.setHUMAN();
  Stella.reset();
  runState = HUMAN;
}

public void resetKeys() {
  
  key_pressed = key_up = key_down = key_left = key_right = false; 
  key_z = key_x = false; 
  key_r = key_spc = false;
  
  
}

/*

  if the player gets hit by an enemy or chooses to reset the level by pressing c

*/

public void resetLevel() {
  gWorld.resetZone();
  loadLevel(currentLevel);
  Stella.updatePosition(1,1);
  if (runState != DIALOG)
    runState = HUMAN;
  //prepHumanMode();
  
}


public void prepSubroutineUI() {
  
  //Stella.setRobotPos(Stella.getX(), Stella.getY());
  runState = PROGRAMMING;
  Stella.resetDepth();
 
}


public void prepDialog() {
  

    
  gWorld.setDialog(Stella.getSkillLevel());
    

  doDialog();
  
}

/*

  functionality to add later

*/

public void collectItem(int x, int y) {
  
  
}

/*

  checks if player has collided with with either a living or world based entity 
  called after each player or enemy movement

*/


/*
  
  After the robot has hit the puzzle ("=") button this method is called 
  It checks all the blocks on the level to see if each one satisfys the condition. 

*/

public void checkBlocks() {
 
  int stellaX = Stella.getX();
  int stellaY = Stella.getY();
  if (Stella.hasSolveAbility()) {
    if (gWorld.isPuzzle(stellaX, stellaY)) {
    
      gWorld.doPuzzleOperation(stellaX, stellaY);
      puzzlePiece.trigger();
      
    } else if (gWorld.isTrigger(stellaX, stellaY)) {
      if (gWorld.checkPuzzle()) {
        int score = gWorld.solvePuzzle() * (int) pow((40 - Stella.getMovementLength()), 2); 
        Stella.increasebytes(score);
        puzzleSolved.trigger();
        gameScreen.initializeColourAnimation();
        gameScreen.solvedPuzzle();
      }
      else {
        failure.trigger(); 
      }
    
    }
  }
  
  
}

/*

  checks what the next robot move is 
  then checks if the move is possible 
  updates the ai moves in turn

*/

public void runRobot() {
  int move = Stella.calculateRobotMove();
  if (move > 0) {
    if (!gWorld.isObstruction(Stella.getX(), Stella.getY())) {
      Stella.doMove();
      checkBlocks();
      
      
    }
    //check collisions 
    //do stuff
  } else if (move < 0) {
    prepHumanMode();
  }

}

public void prepSubroutine() {
  
  //create a way for immediate nested loops
  Stella.setROBOT();
  int validate = Stella.validateSubroutine();
  
  if (validate == 1)
    
    runState = ROBOT;

  else {
    
    Stella.setHUMAN();
    Stella.reset();
   
  }
}

public void addSubroutineMove() {
  int robotMove = 0;
        
  if (key_up) {
    robotMove = -5;
    key_up = false;
  } else if (key_down) {
    robotMove = -6;
    key_down = false;
  } else if (key_right) {
    robotMove = -4;
    key_right = false;
  } else if (key_left) {
    robotMove = -3;
    key_left = false;
      
  } else if (key_z) {
    robotMove = -2;
    key_z = false;
  } else if (key_x) {
    robotMove = -1;
    key_x = false;
  } else if (key_spc) {
    robotMove = -7;
    prepSubroutine();
    key_spc = false;
  } 
  
  if (robotMove != 0) {   
    Stella.addRobotMove(robotMove);
    computerBlip.trigger();   
  }
  key_pressed = false;
  
}


public void playerAction() {

  if (runState == RESET_LEVEL) {
      
    gameState = RESET;
    
  } else if (runState == ROBOT && frameCount % 2 == 0) {
      
    runRobot();
      
  } else if (key_pressed) {
    switch(runState) {
      case PROGRAMMING:
        addSubroutineMove();
        break;
      case HUMAN:
        doHumanMovement();
        break;
      case DIALOG:
        break;          
      case CUTSCENE:
        break;
    }
    
  }
  
}

public void doDialog() {
  
  if (levelDialog) {

    boolean moreDialog = gWorld.setNextDialog(Stella);
    if (!moreDialog) {
      levelDialog = false; 
      prepHumanMode();
    }

    
  } 
    
  
  
 //check if done 
 //if done switch modes;
  
}

public void doHumanMovement() {
  //todo make this easier to read using methods inside player    
  
  int movePossible = 0; 
     
  if (key_up) {
    movePossible = Stella.tryUP();
  } else if (key_down) {
    movePossible = Stella.tryDOWN();
  } else if (key_left) {
    movePossible = Stella.tryLEFT();
       
  } else if (key_right) {
    movePossible = Stella.tryRIGHT();
      
  }
  
  //println("mp " + movePossible);
  if (movePossible == 1) {
    int potX = Stella.getPotX();
    int potY = Stella.getPotY();
    boolean authorized = gWorld.isAuthorized(potX, potY, Stella.getAuthorizedLevel());

    int level;
    switch(gWorld.getBackgroundTileType(potX, potY)) {
    
      case 0: 
        if (authorized) {
          Stella.doMove(); 
          if (Stella.hasFlipAbility)
            gWorld.flipBackgroundTile(potX, potY);
          
        }
        //play sound
        break;
      case 1:
        Stella.doMove(); 
        //set authorization level
        //play sound
        Stella.setAuthorizedLevel(gWorld.getBackgroundTileData(potX, potY));
        switcher.trigger();
        gameScreen.startScreenShake();
        break;
        
      case 2:
        //collect coin
        if (authorized) {
          Stella.doMove(); 
          gWorld.removeCoin(potX, potY);
          coin.trigger();
          Stella.addCoin();
          gameScreen.gotCoin();
          //collect coin
        }
        //play sound
        //Stella.setAuthorizedLevel(gWorld.getBackgroundTileData(potX, potY));      
        break;
      case 3:
        //obstructed permanently 
        //play sound
        break;
      case 4:
        //puzzle area 
        //play sound
        break;
      case 5:
          Stella.doMove(); 
          if (gWorld.getBackgroundTileData(potX, potY) == 2) {
            Stella.levelUp();
            achievement.trigger();
            gameScreen.initializeLevelUpAnimation();
            gWorld.normalizeTile(potX, potY);
            gameScreen.startScreenShake();
            gameScreen.backgroundFlash();
 
          }
            
        //free area
        //play sound
        break;
      case 6:
        if (authorized) {
          Stella.doMove(); 
        }
        //teleporter
        //play sound
        level = gWorld.getBackgroundTileData(potX, potY);
        gameScreen.startScreenShake();

        if (Stella.getSkillLevel() >= level) {
          runState = DIALOG;
          loadtheLevel.trigger();
          loadLevel(level);
        }
        break;


      
    }
    
  } 
  
}

public void draw() {
  

  
  if (gameState == GAMEPLAY) {
    
      playerAction();
      gameScreen.render(runState);
      

  } else if (gameState == RESET) {
    
    resetLevel();
    
    
  } else if (gameState == INTRO_SCREEN) {}

  
}

/*
  keypress handler code modified from COMP 1501 Tutorial code

*/

public void keyPressed() {
  // check if the key is coded
  // this is for keys like arrow keys, shift, etc
  
  
  if (key == CODED) {
    switch(keyCode) {
    case UP: 
      key_up = true;
      break;
    case DOWN:
      key_down = true;
      break;
    case LEFT: 
      key_left = true;
      break;
    case RIGHT:
      key_right = true;
      break;

    }
    key_pressed = true; 

  } else if (key == 'r') {
    key_r = true;
    key_pressed = true; 

  } else if (key == 'z') {
    key_z = true;
    key_pressed = true; 

  } else if (key == 'x') {
    key_x = true;    
    key_pressed = true; 

  } else if (key == ' ') {
   key_spc = true; 
   key_pressed = true; 

  }
  

}


public void keyReleased() {
  // if the player stops holding a key, stop moving in that direction
  if (key == CODED) {
    switch(keyCode) {
    case UP: 
      key_up = false;
      break;
    case DOWN:
      key_down = false;
      break;
    case LEFT: 
      key_left = false;
      break;
    case RIGHT:
      key_right = false;
      break;
    }
    key_pressed = false; 

  } else if (key == 'z') {
    //else if (runState == DIALOG) {
      //doDialog(); 
    //}
    key_z = false;
    key_pressed = false; 


    
  } else if (key == 'x') {
    
    key_x = false;
    key_pressed = false; 

  //  if (runState == DIALOG)
    if (runState == DIALOG)
      doDialog(); 
      
  } else if (key == ' ') {
    if (runState == HUMAN && Stella.hasStealthAbility()) {
      startProgramming.trigger();
      prepSubroutineUI();
    } 
    key_pressed = false; 

  } else if (key =='r') {
    rewind.trigger();
    resetLevel();
    key_pressed = false; 

  }

  
} 
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
  
  public boolean isAlive() {
    return isAlive;
    
  }
  
  public int moveBullet() {
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
  
  public int getX() {
   
    return PosX;
    
  }
  
  public int getY() {
    
    return PosY; 
    
  }
  
}
final int RESTRICTED_RED = 0xff6B1414;
final int ENEMY_RED = 0xffB50042;
final int VALID_GREEN = 0xff1D7B5E;
final int STD_GRAY = 100;
final int HUMAN_COLOUR = 0xff7BC8E1;
final int ROBOT_COLOUR = 0xff89DDE1;
//final color ROBOT_COLOUR = #7CC9E2;
final int STELLA1 = 0xffFAC864;
final int STELLA2 = 0xffE9E445;

final int BLOCKT1 = 0xffC8C8C8;
final int BLOCKT2 = 0xff353535;
final int BLOCKT3 = 0xff7BC8E1;

final int BLOCKT4 = 0xffDC91D2;
final int BACKGROUND = 0xff001E3A;
final int UI_BACKGROUND = 0xff002555;
final int SUBROUTINE_BACKGROUND = 0xff7BC8E1;
final int OBSTRUCTION = 0xff003366;
final int PEACEFUL_GREEN = 0xff00fa9a; 
final int FLASH = 0xfff0ffff;
final int COIN = 0xffffa500;
final int BRIGHT_YELLOW = 0xffffd700;

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
  
  public void renderPlayerData() {
    
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
  
  public void renderSubroutineUI() {
    
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
    float offset = gridDimention * 4.5f;
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
class Dialog {
  

  
  public Dialog () {
    
    
  }
  
  
}


class Movement {
  
  int [] moves;
  int numMoves;
  int currentMove;
  Stack <Integer> LoopPointer;
  Stack <Integer> LoopCounter;
  Stack <Integer> LoopTotalCount;
  int nStraightMoves;
  int dx, dy; 
  int posX, posY;
  int potX, potY;
  int maxX, maxY;
  int nextMove;
  int depth;
  BufferedReader reader;
  
  public Movement () {
    currentMove = -1;
    depth = 0; 
    LoopPointer = new Stack();
    LoopCounter = new Stack();
    LoopTotalCount = new Stack();
    
    nStraightMoves = 0;
    dx = dy = 0;
    numMoves = 0;
    potX = potY = 0;
    nextMove = -2;
    moves = new int [40];
    maxX = maxY = 0; 
  }
  
  public void setMovementBoundaries(int cX,int cY) {
    maxX = cX;
    maxY = cY;
  }
  
  public int [] getSubroutine() {
    return moves; 
    
  }
  
  
  public int getSubroutineLength() {
    return numMoves; 
  }
    
  public void loadMovement(String pre, int identifier, int beat, String filetype) {
    
    String levelName = "data/entities/" + pre + beat + "/" + identifier + filetype;
    reader = createReader(levelName);
    int i = 0; 
    try {
      String line = reader.readLine();
      while (line != null && i < 40) {
        switch(line) {
          case "S" :
            moves[i] = -2;
            numMoves++;
            break;
          case "E" :
            moves[i] = -1;
            numMoves++;
            break;
          case "L" :
            moves[i] = -3;
            numMoves++;
            break;
          case "R" :
            moves[i] = -4;
            numMoves++;
            break;        
          case "U" :
            moves[i] = -5;
            numMoves++;
            break;        
          case "D" :
            moves[i] = -6;
            numMoves++;
            break;
          case "Z" :
            moves[i] = -7;
            numMoves++;
            break;
          case "N":
            moves[i] = -9;
            numMoves++;
            break;
          case "B" :
            int digit = -31;
            line = reader.readLine();
            for (char c : line.toCharArray()) {
              switch(c) {
                case '0':
                  digit += 1;
                  break;
                case '1':
                  digit += 2;
                  break;
                case '2':
                  digit += 4;
                  break;
                case '3':
                  digit += 8;
                  break;
                  
              }
            } 
            //println(digit);
            moves[i] = digit;
            numMoves++;
            break;
          default :
            moves[i] = Integer.parseInt(line);
            numMoves++;
            break;
        }
        line = reader.readLine();
        i++;
      }
    
    
    
    } catch (IOException e) {}
  }
  
  
  public void setPos(int nx, int ny) {
    posX = nx;
    posY = ny;
  }
  
  public int getX() {
    return posX; 
  }
  
  public int getY() {
    
   
    return posY;
  }
  
  public void reset() {
   reload(); 
   clearMoves();
   numMoves = 0;
    
  }
  
  public void reload() {
   dy = dx = 0;
   nStraightMoves = 0;
   potX = potY = 0;
   currentMove = -1;
   nextMove = -2;
   depth = 0; 
   LoopPointer.clear();
   LoopCounter.clear();   
    
  }
  
  public int validateMovement() {
    int depth = 0;
    int balence = 0; 
    for(int i = 0; i < numMoves; i++) {
      if (moves[i] == -2) 
        depth++;
      else if (moves[i] == -1)
        depth--;  
      if (moves[i] <  -1 && moves[i] > -7)
        balence++;
      else if (moves[i] >= 0)
        balence--;
    }
    if (depth < 0 || balence != 0)
      return 0;
    return 1;
    
  }
  
  public int tryUP() {
    potY = posY -1;
    potX = posX;
    if (potY >= 0 && potY < maxY)
      return 1;
    return 0;
  }
  public int tryDOWN() {
    potY = posY + 1;
    potX = posX;
    if (potY >= 0 && potY < maxY)
      return 1;
    return 0;    
  }
  public int tryLEFT() {
    potX = posX - 1;
    potY = posY;
    if (potX >= 0 && potX < maxX)
      return 1;
    return 0;
  }
  public int tryRIGHT() {
    potX = posX + 1;
    potY = posY;
    if (potX >= 0 && potX < maxX)
      return 1;
    return 0;
  }  
  
  public void goUP() {
    posY--;
  }
  public void goDOWN() {
    posY++;
  }
  public void goLEFT() {
    posX--;
  }
  public void goRIGHT() {
    posX++;
  }
  
  public void clearMoves() {
    for (int i = 0; i < 40; i++) {
      moves[i] = 0;  
    }
    
    
  }
  
  public int getCurrentMove() {
    
    return currentMove;
    
  }
  
  public void resetDepth() {
    
    depth = 0;
    
  }

  
  public void addMove(int move) {
    
    if (numMoves > 39)
      return;
    else if (numMoves == 0) {
      if (move < -1 && move > -7) {
        moves[numMoves] = move;
        moves[numMoves + 1]++;
        numMoves = 2;
        if (move == -2)
          depth = 1;
        else 
          depth = 0;
      } else if (move != -1) {
        moves[numMoves] = move; 
        numMoves++;     
      }
    } else if (move < -2 && move > -7) {
      if (moves[numMoves - 2] == move) {
        moves[numMoves - 1]++;       
      } else {
        moves[numMoves] = move; 
        moves[numMoves + 1]++;
        numMoves = numMoves + 2;
      }
    } else if (move == -2) {
      if (moves[numMoves -2] == -2) {
        moves[numMoves -1]++; 
      }  else {
        moves[numMoves] = -2;
        moves[numMoves+1]++;
        numMoves = numMoves + 2;
        depth++;
      }
      
    } else if (move == -1) {
      if (depth < 1)
        return;
      depth--;  

      moves[numMoves] = move;
      numMoves++;

    } else{
      moves[numMoves] = move;
      numMoves++;
    }
    
  }
  public void setMove(int i, int e) {
    moves[i] = e;
    
  }
  
  
  
  public int calculateMove() {
    
    if (nStraightMoves > 0) {
      //directions are now represented in a binary number
      int dir = 2* dy + dx + 4;
      potentialMove(posX + dx, posY + dy);
      nStraightMoves--;
      if ((posX + dx) >= 0 || (posX + dx) < maxX || (posY + dy) >= 0 || (posY + dy) < maxY)
        return dir;
      return 0; 
    }
    
    currentMove++;
    if (numMoves == 0)
      return -1;
    currentMove = currentMove % numMoves;
    //println(moves[currentMove] + " " + getX() + " " + getY()); 
    
    switch(moves[currentMove]) {
      case -6:
        //println("D");
        dx = 0;
        dy = 1;
        calculateMove();
        break;
      case -5:
        //println("U");
        dx = 0;
        dy = -1;
        calculateMove();
        break;
      case -4:
        //println("R");
        dx = 1;
        dy = 0;
        calculateMove();
        break;
      case -3:
        //println("L");
        dx = -1;
        dy = 0;
        calculateMove();
        break;
      case -2:
        //println("push " + currentMove);
        if (nextMove != (currentMove - 1)) {
          LoopTotalCount.push(moves[currentMove + 1]);          
        }
        LoopPointer.push(currentMove);
        moves[currentMove + 1]--;  
        LoopCounter.push(moves[currentMove + 1]);
        currentMove++;
        calculateMove();
        break;
      case -1: //
        int count = LoopCounter.pop();
        nextMove = LoopPointer.pop() - 1;   
        if (count > 0) {
          
          currentMove = nextMove;
       //   calculateMove();
          //println("pop " + currentMove);
        } else {

          moves[nextMove + 2] = LoopTotalCount.pop();
        //  calculateMove();
        }
        break;
      case -7: 
        return -1;
      case -8:
        return -2;
      case -9:
        break;
      default:
        if (moves[currentMove] > 0) 
          nStraightMoves = moves[currentMove];
        else {
          int number = moves[currentMove] + 61;
          //println(number);
          return number;          
        }
        break;
    }
    return 0;
  }
  
  public void potentialMove(int x, int y) {
    potX = x;
    potY = y;
    
  }
  public int get_potX() {
    return potX;
    
  }
  public int get_potY() {
    return potY; 
    
  }
  
  
  public void doMove() {
    posX = potX;
    posY = potY;
    
  }
  
  
}
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
  
  
  public void increasebytes(int newbytes) {
    
    bytes += newbytes;
    
  }
  
  public void resetDepth() {
    
    movement.resetDepth();
    
  }
  
  public int getCoins() {
    
    return coins;
    
  }
  
  public void addCoin () {
    
    coins++;
    
  }
  
  public int getbytes() {
    
    return bytes;
    
  }
  
  public void increaseSkill(int toLevel) {
    
    if (toLevel > skillLevel)
      skillLevel = toLevel;
    
  }
  
  public void levelUp() {
    
    if (skillLevel > 20)
      return;
    skillLevel++;
    
  }
  
  public int getSkillLevel() {
    
    return skillLevel; 
    
  }
  
  public int getCurrentMove() {
   
    return movement.getCurrentMove();
    
  }
  
  public void addFlipAbility() {
   
    hasFlipAbility = true;
    
  }
  
  public void addRescueAbility() {
    
    hasRescueAbility = true;
    
  }
  
  public void addSolveAbility() {
    
    hasSolveAbility = true; 
    
  }
  
  public boolean hasFlipAbility() {
    
    return hasFlipAbility;
    
  }
  
  public void addStealthAbility() {
    
    hasStealthAbility = true; 
    
  }
  
  public boolean hasStealthAbility() {
    
    return hasStealthAbility;
    
  }
  
  public boolean hasRescueAbility() {
    
    return hasRescueAbility;
    
  }
  public boolean hasSolveAbility() {
    
    return hasSolveAbility;
    
  }

  
  
  public int dropBlock() {
    if (collectedBlocks.size() > 0)
      return collectedBlocks.pop();
    return -1;
  }
 
  public void updatePosition(int newX, int newY) {

    movement.setPos(newX, newY);
    
  }
  
  public void addBlock( int block ) {
    collectedBlocks.push(block); 
  }
  
  public int [] getMovement () {
    return movement.getSubroutine();
  }
  
  public void setMovementBoundaries(int x, int y) {
   movement.setMovementBoundaries(x, y); 
    
  }
  
  public int getMovementLength () {
    return movement.getSubroutineLength(); 
  }
  
  public int getType() {
    return playerType; 
  }
  public void setHUMAN() {
    playerType = HUMAN;
    movement.setPos(retX, retY);
  }
  public void setROBOT() {
    playerType = ROBOT;
    retX = movement.getX();
    retY = movement.getY();
  }
  
  public int tryUP() {
    return movement.tryUP(); 
  }
  public int tryDOWN() {
    return movement.tryDOWN(); 
  }
  public int tryLEFT() {
    return movement.tryLEFT(); 
  }
  public int tryRIGHT() {
    return movement.tryRIGHT();  
  }
  
  public void goUP() {
    movement.goUP(); 
  }
  public void goDOWN() {
    movement.goDOWN(); 
  }
  public void goLEFT() {
    movement.goLEFT(); 
  }
  public void goRIGHT() {
    movement.goRIGHT(); 
  }
  
  public int getAuthorizedLevel() {
    return authorizedLevel;
  }
  
  
  
  public void addRobotMove(int move) {
     movement.addMove(move);
  }
  
  public int calculateRobotMove() {
    return movement.calculateMove();
  }
  
  public int validateSubroutine() {
    return movement.validateMovement();
  }
  
  public void doMove() {
    movement.doMove();
  }


  public void reset() {
    movement.reset();
    retX = 1;
    retY = 1;
  }
  
  public void setAuthorizedLevel(int l) {
    
    authorizedLevel = l; 
    
  }
  
  public int getX() {
    
    return movement.getX();
    
  }
  public int getY() {
    
    return movement.getY();
    
  }
  
  public int getPotX() {
    return movement.get_potX();  
  }
  public int getPotY() {
    return movement.get_potY(); 
  }
  
}
class World {
  
  int numZones;
  Zone [] zones;
  int currZone;
 
  public World (int nZones) {
   
    numZones = nZones;
    zones = new Zone [numZones];
    currZone = 0;
   
  }
 
  public void loadZones() {
   
    for (int i = 0; i < numZones; i++) {
      zones[i] = new Zone(i);
      zones[i].loadZone(); 
      zones[i].loadZoneData();
    }
   
  }
  
  public void scrambleColours() {
    
    zones[currZone].scrambleColours();
    
  }
  
  public void setPeaceful(int x, int y) {
    
    zones[currZone].setPeaceful(x, y);
    
  }
  
  public void setDialog(int progress) {
    
    zones[currZone].setDialog(progress);
    
  }
  
  public boolean isPuzzle(int x, int y) {
    
    return zones[currZone].isPuzzle(x, y);
    
  }
  
  public void doPuzzleOperation(int x, int y) {
    
    zones[currZone].doPuzzleOperation(x, y);
    
  }
 
  public void loadLevel(int level) {
    
    zones[currZone].resetLiveGrid();
    currZone = level;
   
  }
  
  public boolean setNextDialog(Player Stella) {
    
    return zones[currZone].setNextDialog(Stella);
    
  }
  
  public boolean isObstruction(int x, int y) {
    
    return zones[currZone].isObstruction(x, y);
    
  }
  
  public boolean isTrigger(int x, int y) {
  
    return zones[currZone].isTrigger(x, y);

  
  }
  
  public boolean checkPuzzle() {
  
    return zones[currZone].checkPuzzle();
    
  }
  
  public int solvePuzzle() {
  
    return zones[currZone].solvePuzzle();
  
  }
 
  public int getCurrentXDimention() {
    return zones[currZone].getXDim(); 
  }
  public int getCurrentYDimention() {
    return zones[currZone].getYDim();
  }
 
  public Zone getcurrentZone() {
 
    return zones[currZone];
   
  }
 
  public void changeBlock( int x, int y, int e ) {
    zones[currZone].changeBlock(x, y, e); 
  }
 
 
  public int getBackgroundTileType(int x, int y) {
    
    return zones[currZone].getBackgroundTileType(x, y);
    
  }
  
  public int getBackgroundTileData(int x, int y) {
    
    return zones[currZone].getBackgroundTileData(x, y);
    
  }
  
  public int getBackgroundTileColour(int x, int y) {
    
    return zones[currZone].getBackgroundTileColour(x, y);
    
  }


 
  
  public void moveEnemyUP(int x, int y) {
    zones[currZone].moveEnemyUP(x,y);
    
  }
  
  public void moveEnemyDOWN(int x, int y) {
    zones[currZone].moveEnemyDOWN(x,y);
    
    
  }
  
  public void setEnemy(int x, int y) {
    zones[currZone].setEnemy(x, y);
    
  }
  
  public void moveEnemyLEFT(int x, int y) {
    zones[currZone].moveEnemyLEFT(x,y);
    
    
  }
  
  public int testBoundaries(int x, int y) {
    return zones[currZone].testBoundaries(x, y);
  }
  public void moveEnemyRIGHT(int x, int y) {
    zones[currZone].moveEnemyRIGHT(x,y);
    
    
  }
  public void removeBullet(int x, int y) {
    
    zones[currZone].removeBullet(x, y);
    
  }
  
  public void updateBullets(int x, int y, int nx, int ny) {
   
    zones[currZone].updateLiveGridBullets(x, y, nx, ny);
    
  }
  
  public void spawnBulletUP(int x, int y) {

    zones[currZone].spawnBulletUP(x,y);
    
  }
  public void spawnBulletDOWN(int x, int y) {

    zones[currZone].spawnBulletDOWN(x,y);
    
  }
  public void spawnBulletLEFT(int x, int y) {

    zones[currZone].spawnBulletLEFT(x,y);

  }
  public void spawnBulletRIGHT(int x, int y) {
    zones[currZone].spawnBulletRIGHT(x,y);

    
  }
  
  public void resetZone() {
    zones[currZone].loadZone();
    
  }
  
  public boolean hasEnemy(int x, int y) {
    
    return zones[currZone].hasEnemy(x, y);
    
  }
  
  public boolean hasBullet(int x, int y) {
    
    return zones[currZone].hasBullet(x, y);
    
  }
  
  public boolean hasItem(int x, int y) {
    
    return zones[currZone].hasItem(x, y);
    
  }

  public String getNextDialog() {
  
    return zones[currZone].getNextDialog();
    
  }
  
  public void flipBackgroundTile(int x, int y) {
    
    zones[currZone].flip( x,  y);
    
  }
  
  public void normalizeTile(int x, int y) {
    
    zones[currZone].changeBlock( x,  y, 0);
    zones[currZone].setLeveledUp();

  }
  
  public boolean isLeveled() {
    
    return zones[currZone].isLeveledUp();
    
  }
  
  public void removeCoin(int x, int y) {
    
    zones[currZone].removeCoin(x, y);
    
  }
  
  public boolean isAuthorized(int x, int y, int level) {
    
    return zones[currZone].isAuthorized(x, y, level);
    
  }
  
  public int getLiveElement(int x, int y) {
   
    return zones[currZone].getLiveElement(x, y);
   
  }
}
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
  
  
  public boolean isLeveledUp() {
    
    return leveledUp;
    
  }
  
  public void setLeveledUp() {
    
    leveledUp = true;
    
  }
  
  
  public void scrambleColours() {
    
   for (int i = 0; i < yDim; i++) {
     
     for (int j = 0; j < xDim; j++) {
       
       grid[i][j].setColor();
       
     }
     
     
   }
    
  }
  
  public boolean isPuzzle(int x, int y) {
    
    if (testBoundaries(x,y) == 0)
      return false;
    return grid[y][x].isPuzzle();
    
  }
  
  public boolean isObstruction(int x, int y) {
    
    if (testBoundaries(x,y) == 0)
      return true;

    return grid[y][x].isObstruction();
    
  }
  
  public boolean isTrigger(int x, int y) {

    if (testBoundaries(x,y) == 0)
      return false;

    return grid[y][x].isTrigger();

  
  }
  
  public boolean checkPuzzle() {
  
    boolean complete = true;
    for(int i = 0; i < yDim; i++) {
      
      for(int j = 0; j < xDim; j++) {
        
        if(grid[i][j].isPuzzle() && grid[i][j].getBackgroundTileData() != 0)
          complete = false;
        
      }
      
    }
    return complete;
    
  }
  
  public int solvePuzzle() {
  
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
  
  public void doPuzzleOperation(int x, int y) {
    
    if (testBoundaries(x,y) == 0)
      return;
    if (!hasSolveRestriction)
      grid[y][x].doPuzzleOperation();
    
  }
  
  public boolean isPeaceful(int x, int y) {

    if (testBoundaries(x,y) == 0)
      return false;

    return grid[y][x].isPeaceful();
    
  }
  
  
  
  public void loadZoneData() {
    
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
  
  
  public void setDialog(int progress) {
  
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
  
  public boolean setNextDialog(Player Stella) {
   
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
  
  public String getCurrDialog () {
    
    return currDialog; 
    
  }
  
  public String getNextDialog() {
    if (functionalDialog.size() != 0)
      return functionalDialog.poll();

     return null;
  }
  
  public void loadZone() {

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
  
  public int getXDim() {
    return xDim; 
  }
  
  public int getYDim() {
    return yDim; 
  }
  public gridElement [][] getGrid() {
    
    return grid; 
    
  }
  
  public String getLevelName() {
    
    return zoneName;
    
  }
  
  public void flip(int x, int y) {
    if (!hasFlipRestriction)
      grid[y][x].flip();
    
  }
  
  public boolean hasEnemy(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return false;
    return grid[y][x].hasEnemy();
    
  }
  
  public boolean hasBullet(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return false;
    return grid[y][x].hasBullet();
    
  }
  
  public boolean hasItem(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return false;    
    return grid[y][x].hasItem();
    
  }
  
  public void setPeaceful(int x, int y) {

    if (testBoundaries(x,y) == 0)
      return;    
    
    grid[y][x].setPeaceful();
    
    
  }
  
  public int getLiveElement(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return -1;

    return grid[y][x].getLiveEntityType();
    
  }
 
  public int get_xDim() {
    
    return xDim;
    
  }
  public int get_yDim() {
    
    return yDim;
    
  }
  
  
  public int getBackgroundTileType(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return -1;
    return grid[y][x].getBackgroundTileType();
  }
  
  public int getBackgroundTileData(int x, int y) {

    if (testBoundaries(x,y) == 0)
      return -1;
    return grid[y][x].getBackgroundTileData();    
    
  }
  
  public int getBackgroundTileColour(int x, int y) {
    
    if (testBoundaries(x,y) == 0)
      return -1;
    return grid[y][x].getBackgroundTileColour();    

    
  }
  
  public boolean isAuthorized(int x, int y, int level) {
    
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
  
  public void removeCoin(int x, int y) {
    
    grid[y][x].removeCoin(); 
    
  }
  
  public void updateLiveGridBullets(int x, int y, int nx, int ny) {
    
    grid[y][x].removeBullet();
    grid[ny][nx].addBullet();
    
  }
  
  public void resetLiveGrid() {
    
    for(int j = 0; j < xDim; j++) {
      for(int i = 0; i < yDim; i++) {
        grid[i][j].setLiveEntityType(0);  
      }
    }
  }
  
  public void removeBullet(int x, int y) {
    
    grid[y][x].removeBullet();
    
  }
  public void moveEnemyUP(int x, int y) {
    
    grid[y][x].removeEnemy();
    grid[y-1][x].addEnemy();
      
  }
  
  public void moveEnemyDOWN(int x, int y) {

    grid[y][x].removeEnemy();
    grid[y+1][x].addEnemy();

  }
  
  public int testBoundaries(int x, int y) {
    if (x < 0 || x >= xDim || y < 0 || y >= yDim) {
      return 0; 
    }
    return 1;
    
  }
  
  public void moveEnemyLEFT(int x, int y) {
    
    grid[y][x].removeEnemy();
    grid[y][x-1].addEnemy();
    
  }
  
  public void moveEnemyRIGHT(int x, int y) {

    grid[y][x].removeEnemy();
    grid[y][x+1].addEnemy();
      
    
  }
  
  public void spawnBulletUP(int x, int y) {
    
    if ((y-1) >= 0)
      grid[y-1][x].addBullet();
    
  }
  
  public void spawnBulletDOWN(int x, int y) {
    
    if ((y+1) < yDim)
      grid[y+1][x].addBullet();
    
  }
  public void spawnBulletLEFT(int x, int y) {

    if ((x-1) >= 0)
      grid[y][x-1].addBullet();

  }
  public void spawnBulletRIGHT(int x, int y) {
    if ((x+1) < xDim)
      grid[y][x+1].addBullet();
      
    
  }
  
  public void setEnemy(int x, int y) {
    if (testBoundaries(x,y) == 0)
      return;

    grid[y][x].addEnemy();
    
    
  }
  
  public void changeBlock(int x, int y, int e) {
    
    grid[y][x].setBackgroundTileData(e);
    
  }
  
}
final int Enemy = 1;
final int Bullet = 2;
final int Item = 4;
final int PEACEFUL = 8;

class gridElement {
  
  int liveEntityType;
  int backgroundTileType;
  int backgroundTileData;
  int backgroundTileColour;
  
  public gridElement(int eType, int bType, int bData) {
  
    liveEntityType = eType;
    backgroundTileType = bType;
    backgroundTileData = bData;
    if (bType < 3)
      setColor();
    else 
      backgroundTileColour = color(0); 
      
    
  }
  
  public void setColor() {

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
  
  public boolean isTrigger() {
    
    if (backgroundTileType == 5 && backgroundTileData == 1)
      return true;
      
    return false;
    
  }
  
  public boolean isObstruction() {
    
    if(backgroundTileType == 3)
      return true;
      
    return false;
    
  }
  
  
  
  public boolean isPuzzle() {
   
    if (backgroundTileType == 4)
      return true;
      
    return false;
    
  }

  public void doPuzzleOperation() {
    
    if (backgroundTileType == 4) {
      
      backgroundTileData--;
      
    }
    
  }
  
  public void removeCoin() {
    
   backgroundTileType = 0; 
    
  }
  
  
  public void flip() {
    
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
  
  public int getBackgroundTileColour() {
    
    return backgroundTileColour;
    
  }
  
  public void setBackgroundTileColour(int colour) {
   
    backgroundTileColour = colour;
    
  }
  
  public int getBackgroundTileData() {
    
    return backgroundTileData;
    
  }
  
  public void setBackgroundTileData(int data) {
   
    backgroundTileData = data;
    
  }  
  
  public void solvePuzzlePiece() {
    
    backgroundTileType = 5;
    
  }

  public int getBackgroundTileType() {
    
    return backgroundTileType;
    
  }
  
  public void setBackgroundTileType(int type) {
   
    backgroundTileType = type;
    
  }
  
  public void setPeaceful () {
   
    liveEntityType |= PEACEFUL;
    
  }
  
  public int getLiveEntityType() {
    
    return liveEntityType;
    
  }
  
  public boolean isPeaceful() {
 
   if ((liveEntityType & PEACEFUL) != 0)
     return true;
     
   return false;
    
    
  }
  
  public boolean hasEnemy() {
    
   if ((liveEntityType & Enemy) != 0)
     return true;
     
   return false;
    
  }
  
  public boolean hasBullet() {
    
   if ((liveEntityType & Bullet) != 0)
     return true;
     
   return false;
    
  }
  
  public boolean hasItem() {
    
   if ((liveEntityType & Item) != 0)
     return true;
     
   return false;
    
  }

  public void addEnemy () {
    
    liveEntityType |= Enemy;
    
  }

  public void addBullet () {
    
    liveEntityType |= Bullet;
    
  }

  public void addItem () {
    
    liveEntityType |= Item;
    
  }
  
  public void removeEnemy() {
   
    liveEntityType -= liveEntityType & Enemy;
    
  }
  
  public void removeBullet() {
   
    liveEntityType -= liveEntityType & Bullet;
    
  }

  public void removeItem() {
   
    liveEntityType -= liveEntityType & Item;
    
  }


  
  public void addLiveEntity(int entityToAdd) {
    
   liveEntityType |= entityToAdd;
    
  }
  
  public void removeEntity(int entityToRemove) {
    
    liveEntityType -= liveEntityType & entityToRemove;
    
  }
 
  public void setLiveEntityType(int type) {
   
    liveEntityType = type;
    
  }
  
  
  
}
  public void settings() {  size(1440, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "game" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
