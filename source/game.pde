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

import ddf.minim.*;
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

void setup () {

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


  size(1440, 800);
  
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
void loadLevel(int level) {
  
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

void prepHumanMode() {
  Stella.setHUMAN();
  Stella.reset();
  runState = HUMAN;
}

void resetKeys() {
  
  key_pressed = key_up = key_down = key_left = key_right = false; 
  key_z = key_x = false; 
  key_r = key_spc = false;
  
  
}

/*

  if the player gets hit by an enemy or chooses to reset the level by pressing c

*/

void resetLevel() {
  gWorld.resetZone();
  loadLevel(currentLevel);
  Stella.updatePosition(1,1);
  if (runState != DIALOG)
    runState = HUMAN;
  //prepHumanMode();
  
}


void prepSubroutineUI() {
  
  //Stella.setRobotPos(Stella.getX(), Stella.getY());
  runState = PROGRAMMING;
  Stella.resetDepth();
 
}


void prepDialog() {
  

    
  gWorld.setDialog(Stella.getSkillLevel());
    

  doDialog();
  
}

/*

  functionality to add later

*/

void collectItem(int x, int y) {
  
  
}

/*

  checks if player has collided with with either a living or world based entity 
  called after each player or enemy movement

*/


/*
  
  After the robot has hit the puzzle ("=") button this method is called 
  It checks all the blocks on the level to see if each one satisfys the condition. 

*/

void checkBlocks() {
 
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

void runRobot() {
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

void prepSubroutine() {
  
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

void addSubroutineMove() {
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


void playerAction() {

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

void doDialog() {
  
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

void doHumanMovement() {
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

void draw() {
  

  
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

void keyPressed() {
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


void keyReleased() {
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