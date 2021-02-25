import java.util.ArrayList;
import javalib.worldimages.*;
import java.util.Random;
import java.awt.Color;
import javalib.impworld.*;

/////////////////GAME CONTROL//////////////////
//Represets a game of Minesweeper
class Game extends World {
  int rows;
  int columns;
  int mines;
  boolean gameOver;
  Random rand;
  ArrayList<Integer> mineLocations;
  ArrayList<ArrayList<Cell>> gameboard;
  int bbWidth = 900;
  int bbHeight = 600;
  //The area to the right of the canvas with game stats:
  int whiteSpace = bbWidth - bbHeight;
  
  //This constructor is for actual random generated game-play.
  Game(int rows, int columns, int mines) {
    this.rows = rows;
    this.columns = columns; 
    if (mines <= 0) {
      throw new IllegalArgumentException("There must be at least one mine!");
    }
    else {
      this.mines = mines;
    }
    this.gameOver = false;
    this.rand = new Random();
    this.mineLocations = getMineLocations();
    this.gameboard = makeGrid();
    populateNeighbors();
    bigBang(bbWidth, bbHeight);
  }

  // This constructor over-writes bbWidth and bbHeight in order to test very
  // specific aspects of your game. For example, if you want to test how the Cell
  // sizes scale, you can easily do this with this Constructor. Also provided is
  // a seed for random here to test the game grid properties.
  Game(int rows, int columns, int mines, boolean gO, int bbWidth, int bbHeight) {
    this.rows = rows;
    this.columns = columns;
    if (mines <= 0) {
      throw new IllegalArgumentException("There must be at least one mine!");
    }
    else {
      this.mines = mines;
    }
    this.gameOver = gO;
    //Random set to one to help testing
    this.rand = new Random(1); 
    //Creates an ArrayList containing the locations of mines in the grid
    this.mineLocations = getMineLocations();
    // This triggers the method makeGrid, which returns the Minesweeper grid in the format
    // of an ArrayList of ArrayLists containing individual Cells.
    this.gameboard = makeGrid();
    // Once the grid layout is complete, populateNeighbors modifies each individual
    // Cell in the grid and adds all neighbors to each Cell's neighbor list
    populateNeighbors();
    this.bbWidth = bbWidth;
    this.bbHeight = bbHeight;
  }
  
  // Generates an ArrayList of Integers representing random positions of mines 
  // from a linear representation of the grid.
  // First, maps a two-dimensional ArrayList into a one-dimensional ArrayList (linearList)
  // Then, initiates the mineCount, which will count from zero to the number of mines specified
  // in the Game constructor.
  // Then, nxt grabs integers at random from 1 to the number of Cells not yet processed
  // Add those random integers to the mineLocs list and advance the mineCount while 
  // subtracting one from the Cells not yet processed
  // Terminates when the number of mines specified in the constructor has been reached
  ArrayList<Integer> getMineLocations() {
    ArrayList<Integer> linearList = new ArrayList<Integer>();
    int numCells = this.rows * this.columns;
    for (int x = 1; x <= numCells; x++) {
      linearList.add(x);
    }
    int mineCount = 1;
    ArrayList<Integer> mineLocs = new ArrayList<Integer>();
    while (mineCount <= this.mines) {
      int nxt = this.rand.nextInt(numCells);
      mineLocs.add(linearList.remove(nxt));
      mineCount = mineCount + 1;
      numCells = numCells - 1;
    }
    return mineLocs;
  }

  // Returns a representation of a Minesweeper game grid
  // For example, a 3x3 grid would resemble this, where an ArrayList is
  // represented inside each [ ]:
  // allRows: [ row 1 [Cell, Cell, Cell]
  // -----------row 2 [Cell, Cell, Cell]
  // -----------row 3 [Cell, Cell, Cell]]
  // If you want to access the third cell of the second row:
  // allRows.get(1).get(3);
  ArrayList<ArrayList<Cell>> makeGrid() {
    ArrayList<ArrayList<Cell>> allRows = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < this.rows; i++) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < this.columns; j++) {
        if (this.mineLocations.contains(i * columns + j + 1)) {
          // note: the "i * columns + j + i" returns an integer indicating the exact
          // location of the Cell on the grid if each Cell was given an integer from 
          // 1 to the total number of cells, starting from the left incrementing one
          // with each cell to the right, from top down, eg:
          // ----j0-j1-j2
          // i0 [ 1, 2, 3
          // i1 - 4, 5, 6
          // i2 - 7, 8, 9] <- eg: 2*3+2+1 == 9
          row.add(new Cell(true));
        }
        else {
          row.add(new Cell(false));
        }
      }
      allRows.add(row);
    }
    return allRows;
  }

  // Loops through every single Cell in the game grid and determines what the
  // boundaries of neighboring Cells are and adds all the surrounding Cells to the 
  // current Cell's list of neighbors
  void populateNeighbors() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) { // loops through every single cell
        Cell curCell = this.gameboard.get(i).get(j); // grabs the current cell to work on
        // Handles the "side" cases:
        int xMin = i - 1; // the leftmost index that a neighbor can be in
        if (xMin < 0) { // if the xMin is less than 0, that means it is a side case...
          xMin = 0; // therefore, cap the xMin at 0
        }
        int xMax = i + 1; // the rightmost index that a neighbor can be in
        if (xMax > rows - 1) {
          xMax = rows - 1;
        }
        int yMin = j - 1; // the uppermost index that a neighbor can be in
        if (yMin < 0) {
          yMin = 0;
        }
        int yMax = j + 1; // the lowermost index that a neighbor can be in
        if (yMax > columns - 1) {
          yMax = columns - 1;
        }
        for (int x = xMin; x <= xMax; x++) {
          for (int y = yMin; y <= yMax; y++) { // loops through every single neighbor of the curCell
            Cell neighbor = this.gameboard.get(x).get(y); // grabs the neighbor
            if (!(x == i && y == j)) { //check that the neighbor isn't curCell, 
              curCell.addNeighbor(neighbor); // add the neighbor to the cell
            }
          }
        }
      }
    }
  }

  ////////////////////DRAW/////////////////////////
  // Draws the game grid
  // The canvas is designed so that a square grid takes up the leftmost area of the canvas,
  // while the rightmost area is left blank to leave space for us to fill with text to 
  // keep track of game stats. This explains the "whiteSpace" variable.
  public WorldScene makeScene() {
    int cellWidth = (bbWidth - whiteSpace) / rows;
    int cellHeight = bbHeight / columns;
    WorldScene w = new WorldScene(bbWidth, bbHeight);
    //handles the drawing of the grid:
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        Cell curCell = this.gameboard.get(i).get(j);
        int xLoc = (i * cellWidth) + (cellWidth / 2);
        int yLoc = (j * cellHeight) + (cellHeight / 2);
        //Creates a black outline around cells:
        w.placeImageXY(new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID, Color.BLACK),
            xLoc, yLoc);
        w.placeImageXY(curCell.draw(cellWidth - 2, cellHeight - 2), xLoc, yLoc);
      }
    }
    // Delegates to another method that returns a WorldImage that is placed at the righthand
    // side of the canvas that presents the user with real-time game stats
    WorldImage gameStats = this.writeGameStats();
    w.placeImageXY(gameStats, bbWidth - (whiteSpace / 2), bbHeight / 2);
    return w;
  }
  
  // Constructs an image to be placed to the right side of the grid to convey to the 
  // user the current statistics of their game including:
  // -instructions for the game
  // -the number of flags they have left to use (# of mines - # of flags they've used)
  // -the number of cells they have left to uncover to win the game 
  // ((# of total cells - # of mines) - # of unflipped cells)
  public WorldImage writeGameStats() {
    WorldImage bckgr = new RectangleImage(whiteSpace, bbHeight, OutlineMode.OUTLINE, 
        Color.BLACK);
    //Creates the Title & Instructions:
    WorldImage aMine = new CircleImage(12, OutlineMode.SOLID, Color.RED);
    WorldImage title = new TextImage("MINESWEEPER", 20, FontStyle.ITALIC, Color.BLACK);
    WorldImage titleImage = new AboveImage(
        new BesideImage(aMine, title, aMine),
        new TextImage("To Win: Open all the cells of", 14, FontStyle.ITALIC, Color.BLACK),
        new TextImage("the board which do not contain a mine", 14, FontStyle.ITALIC, Color.BLACK));
    //Creates the Statistics:
    WorldImage aFlag = new EquilateralTriangleImage(12, OutlineMode.SOLID, Color.GREEN);
    int remainingFlags = countRemainingFlags();
    WorldImage flagInfo = new BesideImage(aFlag, 
        new TextImage(Integer.toString(remainingFlags), 16, FontStyle.REGULAR, Color.BLACK));
    int numCellsRemaining = countNumCellsRemaining();
    //Aligns all the info:
    WorldImage info = new AboveImage(
        titleImage,
        new RectangleImage(1, 80, OutlineMode.OUTLINE, Color.white),
        new TextImage("remaining flags: ", 16, FontStyle.REGULAR, Color.BLACK),
        flagInfo,
        new RectangleImage(1, 20, OutlineMode.OUTLINE, Color.white),
        new TextImage("remaining cells to uncover:", 16, FontStyle.REGULAR, Color.BLACK),
        new TextImage(Integer.toString(numCellsRemaining) + " / " + 
            Integer.toString(rows * columns), 16, FontStyle.REGULAR, Color.BLACK));
    WorldImage gameStats = new OverlayImage(info.movePinholeTo(new Posn(0, 120)), bckgr);
    //Handles when a game has been lost or won:
    WorldImage gameOverMsg = new TextImage("GAMEOVER", 20, FontStyle.BOLD, Color.RED);
    WorldImage winnerMsg = new TextImage(":D!!!YOU'RE A WINNER!!!:D", 20, FontStyle.BOLD, 
        Color.GREEN);
    WorldImage sadRestartMsg = new TextImage("Close this window to try again...", 15, 
        FontStyle.REGULAR, Color.BLACK);
    WorldImage happyRestartMsg = new TextImage("Close this window for a new challenge!", 
        15, FontStyle.REGULAR, Color.BLUE);
    if (this.gameOver) {
      return new OverlayImage(
          new AboveImage(gameOverMsg, sadRestartMsg).movePinhole(0, -30), gameStats);
    }
    if (!this.gameOver && this.countNumCellsRemaining() == 0) {
      return new OverlayImage(
          new AboveImage(winnerMsg, happyRestartMsg).movePinhole(0, -30), gameStats);
    }
    return gameStats;
  }
  ///////////////////END DRAW METHODS////////////////////////
  
  /////////////////////CREATING GAME STATS///////////////////////////
  // Counts the number of flags the user has available to them by calculating
  // the total number of mines on the board minus the number of flagged cells on the board.
  // If the total number of flags has been used up (eg the answer is < zero) then 
  // this continues to return zero, and not a negative number.
  public int countRemainingFlags() {
    int numFlags = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        Cell curCell = this.gameboard.get(i).get(j);
        if (curCell.isFlagged()) {
          numFlags = numFlags + 1;
        }
      }
    }
    int answer = this.mines - numFlags;
    if (answer < 0) {
      return 0;
    }
    else { 
      return answer;
    }
  }
  
  // Counts the total number of Cells that need to be uncovered in order 
  // to win the game by calculating the total number of cells that 
  // need to be uncovered in order to win the game minus the number that
  // have been uncovered so far in real-time
  public int countNumCellsRemaining() {
    int numUncovered = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        Cell curCell = this.gameboard.get(i).get(j);
        if (!curCell.isMine() && curCell.isFlipped()) {
          numUncovered = numUncovered + 1;
        }
      }
    }
    int cellsNeededToBeUncovered = rows * columns - this.mines;
    if (cellsNeededToBeUncovered - numUncovered < 0) {
      return 0;
    }
    else {
      return cellsNeededToBeUncovered - numUncovered;
    }
  }
  /////////////////////END GAME STATS///////////////////////////
  
  ///////////////////// MOUSE CLICKS///////////////////////////
  // Handles mouse clicks by calculating the position of each cell and triggering
  // the flipOrFloodFill method which handles how the Cell reacts to a click
  public void onMouseClicked(Posn pos, String buttonName) {
    // The game-playing grid should always be a kept a square, though the canvas is
    // a rectangle, so the whiteSpace difference is needed in order to keep the
    // cell click positions to scale
    if (!this.gameOver) {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
          //Aligns clicks within the game grid, ignores all other clicks:
          if (pos.x > (bbWidth - whiteSpace) / rows * i && 
              pos.x < (bbWidth - whiteSpace) / rows * (i + 1) && 
              pos.y > (bbHeight / columns * j) && 
              pos.y < (bbHeight / columns * (j + 1))) {
            Cell curCell = this.gameboard.get(i).get(j);
            // ****For testing purposes **** take out before submitting****
            System.out.println("CLICK: the i value is" + Integer.toString(i));
            System.out.println("------ the j value is" + Integer.toString(j));
            if (buttonName.equals("LeftButton")) {
              // If the current cell is not a mine, only if it has not been flagged
              // can it be part of a flip or flood fill:
              if (!curCell.isMine() && !curCell.isFlagged()) {
                curCell.flipOrFloodFill();
              }
              // If the current cell IS a mine, the only way it can be clicked is if
              // the safety-net flag was not placed on it:
              else {
                if (!curCell.isFlagged()) {
                  this.triggerGameOver();
                }
              }
            }
            // If you try to flag a Cell that is already flagged, or is already flipped,
            // or if you have no flags left - you can't flag it. Otherwise, go ahead:
            if (buttonName.equals("RightButton")) {
              if (!curCell.isFlagged() && !curCell.isFlipped() && this.countRemainingFlags() > 0) {
                curCell.flag();
              }
              else {
                curCell.unFlag();
              }
            }
          }
        }
      }
    }
  }

  // Adjusts the game logic when a game is lost:
  // Switches the gameOver boolean to true, reveals all of the mines on the board
  public void triggerGameOver() {
    this.gameOver = true;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        Cell curCell = this.gameboard.get(i).get(j);
        curCell.revealMines();
      }
    }
  }
  
  public boolean lostGame() {
    return this.gameOver;
  }

}

