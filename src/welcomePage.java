import javalib.worldimages.*;
import java.awt.Color;
import javalib.impworld.*;

//Handles the initial canvas opening and game options 
class WelcomePage extends World {
  int numGamesPlayed;
  int bbWidth = 900;
  int bbHeight = 600;
  
  WelcomePage() {
    int numGamesPlayed = 0;
    bigBang(bbWidth, bbHeight, .1);
  }
  
  // Draws the welcome page of the game
  public WorldScene makeScene() {
    WorldScene w = new WorldScene(bbWidth, bbHeight);
    // Creates the Title & Instructions:
    WorldImage aMine = new CircleImage(12, OutlineMode.SOLID, Color.RED);
    WorldImage title = new TextImage("MINESWEEPER", 20, FontStyle.ITALIC, Color.BLACK);
    WorldImage titleImage = new BesideImage(aMine, title, aMine);
    String numGames = Integer.toString(numGamesPlayed);
    WorldImage numGamesPlayed = new TextImage("games played so far: " + numGames, 15, 
        FontStyle.REGULAR, Color.BLUE);
    // Creates the options of game play, either:
    WorldImage easy = new OverlayImage(new TextImage("EASY: 10x10 grid, 10 mines", 15,
        FontStyle.BOLD, Color.BLACK), new RectangleImage(250, 30, OutlineMode.SOLID, Color.GREEN));
    WorldImage med = new OverlayImage(new TextImage("MEDIUM: 20x20 grid, 40 mines", 15,
        FontStyle.BOLD, Color.BLACK), new RectangleImage(250, 30, OutlineMode.SOLID, Color.YELLOW));
    WorldImage hard = new OverlayImage(new TextImage("HARD: 30x30 grid, 100 mines", 15,
        FontStyle.BOLD, Color.BLACK), new RectangleImage(250, 30, OutlineMode.SOLID, Color.RED));
    WorldImage playerChoices = new AboveImage(new TextImage("Chose One:", 16, FontStyle.BOLD, 
        Color.BLACK), easy, med, hard);
    w.placeImageXY(titleImage, bbWidth / 2, (bbHeight / 2) - 150);
    w.placeImageXY(numGamesPlayed, bbWidth / 2, bbHeight / 2 - 100);
    w.placeImageXY(playerChoices, bbWidth / 2, bbHeight / 2);
    return w;
  }
  
  // Launches the game depending on which option the user clicks
  public void onMouseClicked(Posn pos) {
    System.out.println("CLICK: the x value is" + Integer.toString(pos.x));
    System.out.println("CLICK: the y value is" + Integer.toString(pos.y));
    if (pos.x > 320 && pos.x < 575) {
      if (pos.y > 250 && pos.y < 290) {
        new Game(10, 10, 10);
        this.numGamesPlayed++;
      }
      if (pos.y > 290 && pos.y < 315) {
        new Game(20, 20, 40);
        this.numGamesPlayed++;
      }
      if (pos.y > 315 && pos.y < 345) {
        new Game(30, 30, 100);
        this.numGamesPlayed++;
      }
    }
  }
}






