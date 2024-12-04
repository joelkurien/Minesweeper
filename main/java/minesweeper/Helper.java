package minesweeper;

import java.util.*;

public class Helper{
    protected Tile[][] board;
    private int NUM_COLS;
    private int NUM_ROWS;

    private static final int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

    public Helper(Tile[][] board, int NUM_ROWS, int NUM_COLS){
        this.board = board;
        this.NUM_COLS = NUM_COLS;
        this.NUM_ROWS = NUM_ROWS;
    }

    public int calcNeighborMines(int row, int col){
        int nMines = 0;

        for(int k=0; k<8; k++){
            int x = row+dx[k];
            int y = col+dy[k];
            if(x>=0 && x<NUM_ROWS && y>=0 && y<NUM_COLS && board[x][y].isMine){
                nMines++;
            }
        }
        return nMines;
    }

    public Set<List<Integer>> sweeper(int row, int col){
        Set<List<Integer>> visited = new HashSet<>();
        searchBoard(row, col, visited);
        return visited;
    }

    private void searchBoard(int row, int col, Set<List<Integer>> visited){
        if(row<0 || row>=NUM_ROWS || col<0 || col>=NUM_COLS || 
        visited.contains(Arrays.asList(row, col)) || board[row][col].getFlag() 
        || board[row][col].isMine){
            return;
        }
        
        if(board[row][col].getNMine()>0){
            visited.add(Arrays.asList(row, col));
            return;
        }

        visited.add(Arrays.asList(row, col));

        for(int k=0; k<8; k++){
            int x = row+dx[k];
            int y = col+dy[k];
            searchBoard(x, y, visited);
        }
    }

    public Set<List<Integer>> setMines(int bound){
        Random rand = new Random();
        Set<List<Integer>> mined = new HashSet<>();
        while(mined.size() < bound){
            int row = rand.nextInt(NUM_ROWS);
            int col = rand.nextInt(NUM_COLS);
            List<Integer> pos = Arrays.asList(row, col);
            if(!mined.contains(pos)){
                mined.add(pos);
            }
        }
        return mined;
    }
}
