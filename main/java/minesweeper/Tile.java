package minesweeper;

public class Tile{
    protected boolean hidden = true;
    public boolean isMine;
    protected boolean isFlag = false;
    public boolean showImage = false;
    public boolean onTile = false;
    protected int x;
    protected int y;
    protected int nMines;
    public boolean animate;

    public Tile(int x, int y, boolean isMine){
        this.x = x;
        this.y = y;
        this.isMine = isMine;
        nMines = -1;
        animate = true;
    }

    public void setFlag(boolean isFlag){
        this.isFlag = isFlag;
    }

    public boolean getFlag(){
        return isFlag;
    }

    public boolean getHidden(){
        return hidden;
    }

    public void setHidden(boolean hidden){
        this.hidden = hidden;
    }

    public int getNMine(){
        return nMines;
    }

    public void setNMine(int nMine){
        this.nMines = nMine;
    }
}