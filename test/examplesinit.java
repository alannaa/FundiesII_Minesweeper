import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import tester.*;

/////////////////EXAMPLES CLASS//////////////
class ExamplesMinesweeper {
  //mine cell example, empty neighbors to begin
  Cell mineExample; 
  Cell mineExample2;
  //non-mine cell example empty neighbors to begin
  Cell cellExample;
  Cell cellExample2;
  Cell cellExample3;
  Cell cellExample4;
  
  // Example of a Game constructor with a random seed so that mine locations
  // can be tested, and which does not open bigbang so that graphics can be tested
  Game testerGame;
  
  // Test a random-generated game, one commented out at a time
 //Game randomGame = new Game(20, 20, 50);
 WelcomePage wp = new WelcomePage();
  
  //Initial Conditions
  void initConditions() {
    this.mineExample = new Cell(true);
    this.mineExample2 = new Cell(true);
    this.cellExample = new Cell(false);
    this.cellExample2 = new Cell(false);
    this.cellExample3 = new Cell(false);
    this.cellExample4 = new Cell(false);
    
    this.testerGame = new Game(3, 3, 3, false, 900, 600);
  }
  
  //Test for addNeighbor in Cell class
  void testAddNeighbor(Tester t) {
    initConditions();
    //test init conditions
    t.checkExpect(cellExample.neighbors, new ArrayList<Cell>());
    //modify 
    cellExample.addNeighbor(cellExample2);
    //test that both cells have each other as a neighbor
    t.checkExpect(cellExample.neighbors, new ArrayList<Cell>(
        Arrays.asList(this.cellExample2)));
    t.checkExpect(cellExample2.neighbors, new ArrayList<Cell>(
        Arrays.asList(this.cellExample)));
  }
  
  //Test for numNeighborMines in Cell class
  void testNumNeighborMines(Tester t) {
    initConditions();
    cellExample.addNeighbor(mineExample);
    t.checkExpect(this.cellExample2.numNeighborMines(), 0);
    t.checkExpect(this.cellExample.numNeighborMines(), 1);
    cellExample.addNeighbor(mineExample2);
    t.checkExpect(this.cellExample.numNeighborMines(), 2);
  }
  
  //Test for floodFill in Cell class
  void testFloodFill(Tester t) {
    initConditions();
    //Add only mineless cells, no mines, to the neighbors of cellExample
    cellExample.addNeighbor(cellExample2);
    cellExample.addNeighbor(cellExample3);
    cellExample.addNeighbor(cellExample4);
    cellExample.flipOrFloodFill();
    //Check that all cells gave been flipped (because there were no mines to stop the floodfill)
    t.checkExpect(this.cellExample.isFlipped, true);
    t.checkExpect(this.cellExample2.isFlipped, true);
    t.checkExpect(this.cellExample3.isFlipped, true);
    t.checkExpect(this.cellExample4.isFlipped, true);
    
    //Create an example of three cells in a row:
    // leftCell | midCell | rightCell(mine)
    Cell leftC = new Cell(false);
    Cell midC = new Cell(false);
    Cell rightC = new Cell(true);
    leftC.addNeighbor(midC);
    midC.addNeighbor(rightC);
    //If you call floodFill on leftC, leftC and midC will flip, but rightC will not.
    leftC.flipOrFloodFill();
    t.checkExpect(leftC.isFlipped, true);
    t.checkExpect(midC.isFlipped, true);
    t.checkExpect(rightC.isFlipped, false);
  }
  
  //Tests revealMines in Cell class 
  void testRevealMines(Tester t) {
    this.initConditions();
    t.checkExpect(this.mineExample.isFlipped, false);
    this.mineExample.revealMines();
    t.checkExpect(this.mineExample.isFlipped, true);
  }
  
  //Test the game:
  //tests the placement of mines, populateNeighbors, and numNeighborMines
  void testGame(Tester t) {
    this.initConditions();
    t.checkExpect(testerGame.gameboard.get(0).get(0).isMine, true);
    t.checkExpect(testerGame.gameboard.get(0).get(1).isMine, false);
    t.checkExpect(testerGame.gameboard.get(0).get(2).isMine, true);
    t.checkExpect(testerGame.gameboard.get(1).get(0).isMine, false);
    t.checkExpect(testerGame.gameboard.get(1).get(1).isMine, false);
    t.checkExpect(testerGame.gameboard.get(1).get(2).isMine, false);
    t.checkExpect(testerGame.gameboard.get(2).get(0).isMine, true);
    t.checkExpect(testerGame.gameboard.get(2).get(1).isMine, false);
    t.checkExpect(testerGame.gameboard.get(2).get(2).isMine, false);
    t.checkExpect(testerGame.gameboard.get(0).get(0).numNeighborMines(), 0);
    t.checkExpect(testerGame.gameboard.get(1).get(0).numNeighborMines(), 2);
    t.checkExpect(testerGame.gameboard.get(1).get(1).numNeighborMines(), 3);
  }
  
  //Tests for getMineLocations
  void testGetMineLocations(Tester t) {
    this.initConditions();
    // This was predicted by looking at the above tested gameboard properties, and drawing
    // out visual representations of the game grid. getMineLocations() creates a flattened 
    // ArrayList version of this visual, which is drawn lined up as a grid here for better 
    // understanding.
    // key: "02T" indicates Cell at gameboard.get(0).get(2) which is True for isMine
    //[00T, 01F, 02T ---> [1, 2, 3
    //-10F, 11F, 12F ---> -4, 5, 6
    //-20T, 21F, 22F]---> -7, 8, 9]
    // Align the True for isMine Cells with their corresponding integer on the 
    // grid to the right:
    ArrayList<Integer> answer = new ArrayList<Integer>(Arrays.asList(7, 1, 3));
    t.checkExpect(testerGame.mineLocations, answer);
  }
  
  //Tests for constructor Exception in Game class
  void testLessThan1Mine(Tester t) {
    this.initConditions();
    t.checkConstructorException(new IllegalArgumentException("There must be at least one mine!"),
        "Game", 3, 3, 0);
    t.checkConstructorException(new IllegalArgumentException("There must be at least one mine!"),
        "Game", 3, 3, 0, false, 400, 200);
  }
  
  //Tests for drawing
  void testDrawing(Tester t) {
    this.initConditions();
    //Cell class draw method:
    t.checkExpect(this.cellExample.draw(10, 10), new RectangleImage(10, 10, OutlineMode.SOLID,
        Color.GRAY));
    t.checkExpect(this.mineExample.draw(10, 10), new RectangleImage(10, 10, OutlineMode.SOLID,
        Color.GRAY));
    //Test the drawing of cells once they've been flipped
    this.cellExample.isFlipped = true;
    this.mineExample.isFlipped = true;
    t.checkExpect(this.cellExample.draw(10, 10), new RectangleImage(10, 10, OutlineMode.SOLID,
        Color.darkGray));
    t.checkExpect(this.mineExample.draw(10, 10), new OverlayImage(new CircleImage(10 / 2, 
        OutlineMode.SOLID, Color.RED), new RectangleImage(10, 10, OutlineMode.SOLID, 
            Color.GRAY)));
    this.cellExample2.isFlagged = true;
    t.checkExpect(this.cellExample2.draw(10, 10), new OverlayImage(
        new EquilateralTriangleImage(10 / 2, OutlineMode.SOLID, Color.GREEN),
        new RectangleImage(10, 10, OutlineMode.SOLID, Color.GRAY)));
    
   //Draw method in Game class:
    WorldScene w = new WorldScene(900, 600);
    //Because we always want the playing grid to be a 600x600 pixel square and the cells within
    //the grid to scale according to this outter bounds, the cell Width is derrived from subtracting
    //the whitespace from the canvas width, divided by the number of columns there will be (whitespace
    //is left for writing information that keeps track of the score):
    int cellWidth = (900 - 300) / 3;
    int cellHeight = 600 / 3;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        Cell curCell = testerGame.gameboard.get(i).get(j);
        int scaleXLoc = (i * cellWidth) + (cellWidth / 2);
        int scaleYLoc = (j * cellHeight) + (cellHeight / 2);
        w.placeImageXY(new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID, Color.BLACK),
            scaleXLoc, scaleYLoc);
        w.placeImageXY(curCell.draw(cellWidth - 2, cellHeight - 2),
            scaleXLoc, scaleYLoc);
      }
    }
    WorldImage stats = this.testerGame.writeGameStats();
    w.placeImageXY(stats, 750, 300);
    t.checkExpect(testerGame.makeScene(), w);
  }
  
  //Tests for creating game stats:
  //countRemainingFlags in Game class
  //countNumCellsRemaining in Game class
  void testGameStats(Tester t) {
    this.initConditions();
    t.checkExpect(this.testerGame.countRemainingFlags(), 3);
    this.testerGame.gameboard.get(2).get(0).isFlagged = true;
    t.checkExpect(this.testerGame.countRemainingFlags(), 2);
    
    t.checkExpect(this.testerGame.countNumCellsRemaining(), 6);
    this.testerGame.gameboard.get(0).get(1).isFlipped = true;
    t.checkExpect(this.testerGame.countNumCellsRemaining(), 5);
  }
  
  //Tests for triggerGameOver in Game class
  void testTriggerGameOver(Tester t) {
    this.initConditions();
    //check that the mine is has not been flipped:
    t.checkExpect(this.testerGame.gameboard.get(0).get(0).isFlipped, false);
    t.checkExpect(this.testerGame.gameOver, false);
    this.testerGame.triggerGameOver();
    t.checkExpect(this.testerGame.gameboard.get(0).get(0).isFlipped, true);
    t.checkExpect(this.testerGame.gameOver, true);
  }
}
  



