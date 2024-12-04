package minesweeper;

//I have used the template used in Tutorial 5 for building this project

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int NUM_ROWS = (HEIGHT-TOPBAR)/CELLSIZE;
    public static final int NUM_COLS = BOARD_WIDTH;

    public static final int FPS = 30;
    public static Tile[][] board = new Tile[NUM_ROWS][NUM_COLS];
    public String configPath;
    public Map<String, PImage> images;
    public Helper helper;
    public boolean canGame = true;
    public boolean win = false;
    public boolean lose = false;
    public List<PImage> mineImages;
    public int start = 0;
    public boolean startTime = false;
    public int bound = 100;
    public int currFrame = 0;
    public int crow = 0;
    public int icount = 0;
    public int c = 0;
    public int time = 0;
    List<List<Integer>> mineSet = new ArrayList<>();

    public static Random random = new Random();
	
	public static int[][] mineCountColour = new int[][] {
            {0,0,0}, // 0 is not shown
            {0,0,255},
            {0,133,0},
            {255,0,0},
            {0,0,132},
            {132,0,0},
            {0,132,132},
            {132,0,132},
            {32,32,32}
    };
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
        images = new HashMap<>();
        mineImages = new ArrayList<>();
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
    //first function that is run
	@Override
    public void setup() {
        frameRate(FPS);
		//See PApplet javadoc:
		//loadJSONObject(configPath)
        helper = new Helper(board, NUM_ROWS, NUM_COLS);
        textSize(CELLSIZE/2);
        textAlign(CENTER, CENTER);
        {
            images.put("mine0", loadImage(this.getClass().getResource("mine0.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
            images.put("tile1", loadImage(this.getClass().getResource("tile1.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
            images.put("flag", loadImage(this.getClass().getResource("flag.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
            images.put("tile", loadImage(this.getClass().getResource("tile.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
            images.put("wall0", loadImage(this.getClass().getResource("wall0.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
            images.put("tile2", loadImage(this.getClass().getResource("tile2.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
            for(int i=0; i<10; i++){
                mineImages.add(loadImage(this.getClass().getResource("mine"+i+".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
            }
        }
        //create attributes for data storage, eg board
        if(args!=null){
            try{
                bound = Integer.parseInt(args[0]);
                if(bound > 486) bound = 100;
            }
            catch(Exception e){
                bound = 100;
            }
        }
        for(int row =0; row<NUM_ROWS; row++){
            for(int col=0; col<NUM_COLS; col++){
                boolean isMine = false;
                board[row][col] = new Tile(col*CELLSIZE, row*CELLSIZE+TOPBAR, isMine);
            }
        }

        Set<List<Integer>> mined = helper.setMines(bound);
        for(List<Integer> pos: mined){
            int row = pos.get(0);
            int col = pos.get(1);
            board[row][col].isMine = true;
            mineSet.add(Arrays.asList(row, col));
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if(key == 'r'){
            background(196);
            lose = false;
            startTime = false;
            time = 0;
            c = 0;
            for(int row =0; row<NUM_ROWS; row++){
                for(int col=0; col<NUM_COLS; col++){
                    boolean isMine = false;
                    board[row][col] = new Tile(col*CELLSIZE, row*CELLSIZE+TOPBAR, isMine);
                }
            }
    
            mineSet = new ArrayList<>();
            Set<List<Integer>> mined = helper.setMines(bound);
            for(List<Integer> pos: mined){
                int row = pos.get(0);
                int col = pos.get(1);
                board[row][col].isMine = true;
                mineSet.add(Arrays.asList(row, col));
            }
            canGame = true;
            win = false;
            crow = 0;
            icount = 0;
            currFrame = 0;
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
    //ignore keyPressed and released
	@Override
    public void keyReleased(){
    }

    //implement these methods
    @Override
    public void mousePressed(MouseEvent e) {
        if(canGame){
            int x = e.getX();
            int y = e.getY();

            int row = (y-TOPBAR)/CELLSIZE;
            int col = x/CELLSIZE;
            Tile tile = board[row][col];

            int modifiers = e.getModifiers();
            if(modifiers == 4){
                if(!tile.getFlag() && tile.getHidden())
                    tile.setFlag(true);
                else if(tile.getFlag() && tile.getHidden())   
                    tile.setFlag(false);
            }
            if(modifiers == 0){
                tile.showImage = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(canGame){
            int x = e.getX();
            int y = e.getY();

            int row = (y-TOPBAR)/CELLSIZE;
            int col = x/CELLSIZE;

            int modifiers = e.getModifiers();
            Tile tle = board[row][col];
            tle.showImage = false;
            startTime = true;
            if(c < 1){
                start = millis();
                c++;
            }
            
            if(modifiers == 0 && !board[row][col].getFlag()){
                if(board[row][col].isMine == false){
                    if(board[row][col].getNMine() == 0){
                        Set<List<Integer>> sweeps = helper.sweeper(row, col);
                        for(List<Integer> sweep: sweeps){
                            board[sweep.get(0)][sweep.get(1)].setHidden(false);
                        }
                    }
                    else {
                        board[row][col].setHidden(false);
                    }
                }
                else{
                    for(int i=0; i<NUM_ROWS; i++){
                        for(int j=0; j<NUM_COLS; j++){
                            Tile tile = board[i][j];
                            if(tile.isMine){
                                tile.setHidden(false);
                            }
                        }
                    }
                    canGame = false;
                    lose = true;
                    startTime = false;
                }
            }
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        if(startTime){
            background(196);
            int currentTime = millis()-start;
            int seconds = (currentTime / 1000);
            time = seconds%60;
        }
        fill(0);
        text("Time: "+time, HEIGHT+TOPBAR, CELLSIZE);
        
        //draw game board
        if(!lose){
            for(Tile[] row: board){
                for(Tile tile: row){            
                    if(tile.isMine){
                        image(images.get("mine0"),tile.x, tile.y);
                    }
                    else{
                        image(images.get("tile"), tile.x, tile.y);
                    }

                    //count neighboring mines
                    if(!tile.isMine){
                        int nMine = helper.calcNeighborMines((tile.y-TOPBAR)/CELLSIZE, tile.x/CELLSIZE);
                        tile.setNMine(nMine);
                        for(int i=1; i<9; i++){
                            if(tile.getNMine() == i){
                                fill(mineCountColour[i][0], mineCountColour[i][1], mineCountColour[i][2]);
                            }
                        }
                        if(tile.getNMine()>0)
                            text(tile.getNMine(), tile.x+CELLSIZE/2, tile.y+CELLSIZE/2);
                    }

                    //show cell
                    if(tile.getHidden()){
                        if(!tile.onTile)
                            image(images.get("tile1"), tile.x, tile.y);
                        else
                            image(images.get("tile2"), tile.x, tile.y);
                        tile.onTile = false;
                    }

                    if(tile.showImage && tile.getHidden() && !tile.getFlag() && !(tile.isMine && !tile.getHidden())){
                        image(images.get("wall0"), tile.x, tile.y);
                    }

                    //set a flag on a cell
                    if(tile.getFlag()){
                        image(images.get("flag"), tile.x, tile.y);
                    }
                }
            }
        
            int r = (mouseY-TOPBAR)/CELLSIZE;
            int c = (mouseX)/CELLSIZE;
            if(r>-1 && r<NUM_ROWS && c>-1 && c<NUM_COLS){
                board[r][c].onTile = true;
            }
        }

        boolean w = true;
        for(Tile[] r: board){
            for(Tile t: r){
                if(!t.isMine && t.getHidden()){
                    w = false;
                    break;
                }
            }
        }
        if(w == true){
            win = true;
            canGame = false;
            startTime = false;
        }
        if(lose && !canGame){
            for(List<Integer> pos: mineSet){
                Tile tile = board[pos.get(0)][pos.get(1)];
                if(tile.isMine && tile.animate){
                    image(images.get("mine0"), tile.x, tile.y);
                }
            }

            for(List<Integer> pos: mineSet){
                Tile tile = board[pos.get(0)][pos.get(1)];
                if(tile.isMine && !tile.getHidden() && tile.animate){
                    tile.animate = false;
                }
            }

            if(crow < mineSet.size()){
                int row = mineSet.get(crow).get(0);
                int col = mineSet.get(crow).get(1);
                Tile t = board[row][col];
                image(mineImages.get(icount), t.x, t.y);
            }

            icount = (icount+1)%mineImages.size();
            if(icount == 0){
                crow++;
            }

            fill(0);
            text("You Lost!", TOPBAR+WIDTH/4, CELLSIZE);
        }
        if(win){
            fill(0);
            text("You Win!", TOPBAR+WIDTH/4, CELLSIZE);
        }
    }

    //dont do anything on this
    public static void main(String[] args) {
        PApplet.main("minesweeper.App", args);
    }

}
