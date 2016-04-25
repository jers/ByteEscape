import java.util.*;

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
  
  void setMovementBoundaries(int cX,int cY) {
    maxX = cX;
    maxY = cY;
  }
  
  int [] getSubroutine() {
    return moves; 
    
  }
  
  
  int getSubroutineLength() {
    return numMoves; 
  }
    
  void loadMovement(String pre, int identifier, int beat, String filetype) {
    
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
  
  
  void setPos(int nx, int ny) {
    posX = nx;
    posY = ny;
  }
  
  int getX() {
    return posX; 
  }
  
  int getY() {
    
   
    return posY;
  }
  
  void reset() {
   reload(); 
   clearMoves();
   numMoves = 0;
    
  }
  
  void reload() {
   dy = dx = 0;
   nStraightMoves = 0;
   potX = potY = 0;
   currentMove = -1;
   nextMove = -2;
   depth = 0; 
   LoopPointer.clear();
   LoopCounter.clear();   
    
  }
  
  int validateMovement() {
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
  
  int tryUP() {
    potY = posY -1;
    potX = posX;
    if (potY >= 0 && potY < maxY)
      return 1;
    return 0;
  }
  int tryDOWN() {
    potY = posY + 1;
    potX = posX;
    if (potY >= 0 && potY < maxY)
      return 1;
    return 0;    
  }
  int tryLEFT() {
    potX = posX - 1;
    potY = posY;
    if (potX >= 0 && potX < maxX)
      return 1;
    return 0;
  }
  int tryRIGHT() {
    potX = posX + 1;
    potY = posY;
    if (potX >= 0 && potX < maxX)
      return 1;
    return 0;
  }  
  
  void goUP() {
    posY--;
  }
  void goDOWN() {
    posY++;
  }
  void goLEFT() {
    posX--;
  }
  void goRIGHT() {
    posX++;
  }
  
  void clearMoves() {
    for (int i = 0; i < 40; i++) {
      moves[i] = 0;  
    }
    
    
  }
  
  int getCurrentMove() {
    
    return currentMove;
    
  }
  
  void resetDepth() {
    
    depth = 0;
    
  }

  
  void addMove(int move) {
    
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
  void setMove(int i, int e) {
    moves[i] = e;
    
  }
  
  
  
  int calculateMove() {
    
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
  
  void potentialMove(int x, int y) {
    potX = x;
    potY = y;
    
  }
  int get_potX() {
    return potX;
    
  }
  int get_potY() {
    return potY; 
    
  }
  
  
  void doMove() {
    posX = potX;
    posY = potY;
    
  }
  
  
}