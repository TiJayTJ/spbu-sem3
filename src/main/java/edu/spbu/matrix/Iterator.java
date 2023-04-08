package edu.spbu.matrix;

public class Iterator {
    int currentRow = -1;
    Integer maxRows;
    public Iterator(int row) {
        this.maxRows = row;
    }
    synchronized public Integer next() {
        this.currentRow++;
        return ((this.currentRow < this.maxRows) ? this.currentRow : null);
    }
}