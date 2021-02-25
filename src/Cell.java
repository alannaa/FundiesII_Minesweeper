import java.util.ArrayList;
import javalib.worldimages.*;
import java.awt.Color;


//Represents a cell in a game of Minesweeper:
//Contains:
//a boolean that is true when the cell is a mine and false when it is not
//a List of Cell that represent the adjacent neighbors of this cell (including diagonals)
//a boolean that determines if the Cell has been flipped (or clicked)
//a boolean that determines if the Cell has been flagged
class Cell {
  boolean isMine;
  ArrayList<Cell> neighbors;
  boolean isFlipped;
  boolean isFlagged;

  Cell(boolean isMine) {
    this.isMine = isMine;
    // This constructor only needs to determine whether or not the cell is a mine
    // Everything else is determined by the initiation of and changes in the game
    this.neighbors = new ArrayList<Cell>();
    this.isFlipped = false;
    this.isFlagged = false;
  }

  // EFFECT: Modifies this Cell by adding the given Cell to its list of neighbors 
  // and modifies the given Cell to add this as its neighbor
  // only if they are not already neighbors 
  void addNeighbor(Cell c) {
    if (!this.neighbors.contains(c)) {
      this.neighbors.add(c);
      c.neighbors.add(this);
    }
  }

  // Returns an integer that is the number of neighbors of this cell that are mines
  int numNeighborMines() {
    int count = 0;
    for (Cell c : this.neighbors) {
      if (c.isMine) {
        count++;
      }
    }
    return count;
  }

  // Draws an individual Cell as a WorldImage, depending on its properties.
  // Cell width and height are passed in as parameters in order to keep the
  // squares to scale with the size of the canvas determined in the Game class
  WorldImage draw(int cellWidth, int cellHeight) {
    WorldImage aFlag = new EquilateralTriangleImage(cellWidth / 2, OutlineMode.SOLID,
        Color.GREEN);
    WorldImage aMine = new CircleImage(cellWidth / 2, OutlineMode.SOLID, Color.RED);
    WorldImage unFlippedCell = new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID, 
        Color.GRAY);
    WorldImage flippedCell = new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID, 
        Color.darkGray);
  //CELLS THAT HAVE NOT BEEN FLIPPED / CLICKED:
    if (!this.isFlipped && this.isFlagged) {
      return new OverlayImage(aFlag, unFlippedCell);
    }
    if (!this.isFlipped) {
      return unFlippedCell;
    }
    //CELLS THAT HAVE BEEN FLIPPED / CLICKED:
    else { 
      //HANDLES MINES:
      if (this.isMine) {
        if (this.isFlagged) {
          //(Used if a game has been lost & user wants to see if they correctly flagged any mines)
          //the unFlippedCell is used as the background despite this being what handles flipped Cells
          //because it was easier to visually contrast mines and flags on the light-grey background
          WorldImage mineAndFlag = new OverlayImage(aFlag, aMine);
          return new OverlayImage(mineAndFlag, unFlippedCell);
        }
        else {
          return new OverlayImage(aMine, unFlippedCell);
        }
      }
      //PLAIN CELLS THAT ARE NOT MINES
      else {
        String val = Integer.toString(this.numNeighborMines());
        if (this.numNeighborMines() == 0) {
          return flippedCell;
        } // has neighbors that are mines, the only difference being the color of the text:
        if (this.numNeighborMines() == 1) {
          return new OverlayImage(new TextImage(val, cellWidth / 2, FontStyle.BOLD, Color.CYAN),
              flippedCell);
        }
        if (this.numNeighborMines() == 2) {
          return new OverlayImage(new TextImage(val, cellWidth / 2, FontStyle.BOLD, Color.GREEN),
              flippedCell);
        }
        if (this.numNeighborMines() == 3) {
          return new OverlayImage(new TextImage(val, cellWidth / 2, FontStyle.BOLD, Color.RED),
              flippedCell);
        }
        else {
          return new OverlayImage(new TextImage(val, cellWidth / 2, FontStyle.BOLD, Color.BLACK),
              flippedCell);
        }
      }
    }
  }
  
  /////////RESPONSES TO CLICKING/////////////
  // There are two options when the mouse has been left clicked:
  // 1. Either flipOrFloodFill is triggered or
  // 2. the clicked cell is a Mine and it's game-over
  // The onMouseClicked method in the Game class delegates to one of these methods:
  
  // If this Cell has one or more neighboring mines, the Cell is flipped,
  // but the floodfill effect does not take place.
  // If this Cell has no neighboring mines, this method iterates through 
  // this Cell's ArrayList of neighboring cells and determines if each element/Cell possesses
  // the proper properties to trigger floodfill;
  // If yes: keep flipping cells and triggering floodfill
  // If no: end the floodfill there
  void flipOrFloodFill() {
    if (this.numNeighborMines() > 0) {
      this.isFlipped = true;
    }
    else { // Triggers the floodfill effect:
      this.isFlipped = true;
      for (int idx = 0; idx < this.neighbors.size(); idx++) {
        Cell curNeighb = this.neighbors.get(idx);
        if (!curNeighb.isFlipped // prevents stack overflow
            && !curNeighb.isMine // prevents flipping a mine
            && !curNeighb.isFlagged) { // prevents trying to flip a Cell that's been flagged
          curNeighb.flipOrFloodFill();
        }
      }
    }
  }

  // Causes a mine to flip. If one mine is clicked, this method is called on all
  // cells in the grid so that the player can see where all the mines were located.
  void revealMines() {
    if (this.isMine) {
      this.isFlipped = true;
    }
  }
  /////////END RESPONSES TO CLICKING/////////////
  
  // The following four methods are referred to in the Game class and provide information
  // that is otherwise not accessible by field-access:
  // isFlipped returns whether or not the Cell has been flipped
  boolean isFlipped() {
    return this.isFlipped;
  }
  
  //isFlagged returns whether or not the Cell has been flagged by the user 
  boolean isFlagged() {
    return this.isFlagged;
  }

  //Flags a cell
  public void flag() {
    this.isFlagged = true;
  }
  
  //Unflags a cell
  public void unFlag() {
    this.isFlagged = false;
  }

  //isMine returns whether or not the Cell is a mine 
  boolean isMine() {
    return this.isMine;
  }
}
/////////////////////////////////////////////////////////////

