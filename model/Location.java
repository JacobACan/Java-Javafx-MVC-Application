package model;

public class Location {
    private int row;
    private int col;


    public Location(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Location){
            if(this.getRow() == ((Location)obj).getRow() && this.getCol() == ((Location)obj).getCol()){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return row*1001 + col*513;
    }
    @Override
    public String toString() {
        return String.format("%s, %s", row, col);
    }
}