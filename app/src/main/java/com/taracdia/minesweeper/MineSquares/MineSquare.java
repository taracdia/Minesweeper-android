package com.taracdia.minesweeper.MineSquares;

public class MineSquare {
    private boolean isClicked = false;
    private boolean isBomb = false;
    private boolean isFlagged = false;
    private int neighborMines = 0;

    public MineSquare() {
    }


    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setBomb(boolean bomb) {
        isBomb = bomb;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public int getNeighborMines() {
        return neighborMines;
    }

    public void setNeighborMines(int neighborMines) {
        this.neighborMines = neighborMines;
    }
}
