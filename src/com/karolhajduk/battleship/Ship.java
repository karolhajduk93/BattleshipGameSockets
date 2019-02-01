package com.karolhajduk.battleship;

import java.awt.*;

public class Ship {
    static final int MAX_SHIP_SIZE = 4;

    private int size;
    private  int id;
    private static int shipNumber = 0;
    private boolean horizontal;
    private Coordinates coordinates;
    private boolean shipOnBoard;

    Ship(int size, int x, int y) {
            shipNumber++;
            this.horizontal = true;
            this. shipOnBoard = false;
            this.size = size;
            this.coordinates = new Coordinates(x, y);
            this.id = shipNumber;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
            return size;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public Coordinates getCoordinates() {
            return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
    }

    public Rectangle getBounds(int offset){
            if(horizontal)
                return new Rectangle(coordinates.getX() - offset, coordinates.getY() - offset, size*Captain.DEFAULT_GRID_SIZE + offset * 2, Captain.DEFAULT_GRID_SIZE + offset * 2);
            else
                return new Rectangle(coordinates.getX() - offset, coordinates.getY() - offset, Captain.DEFAULT_GRID_SIZE + offset * 2, size*Captain.DEFAULT_GRID_SIZE + offset * 2);
    }
    
    public boolean isShipOnBoard() {
        return shipOnBoard;
    }

    public void setShipOnBoard(boolean shipOnBoard) {
        this.shipOnBoard = shipOnBoard;
    }
}
