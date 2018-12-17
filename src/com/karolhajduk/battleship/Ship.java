package com.karolhajduk.battleship;

import java.awt.*;

public class Ship {
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

    public Rectangle getBounds(){
            if(horizontal)
                return new Rectangle(coordinates.getX(), coordinates.getY(), size*32, 32);
            else
                return new Rectangle(coordinates.getX(), coordinates.getY(), 32, size*32);
    }
    public Rectangle getBigBounds(){
        if(horizontal)
            return new Rectangle(coordinates.getX()- 32, coordinates.getY() - 32, size*32 + 64, 32 + 64);
        else
            return new Rectangle(coordinates.getX() - 32, coordinates.getY() - 32, 32 + 64, size*32 + 64);
    }

    public boolean isShipOnBoard() {
        return shipOnBoard;
    }

    public void setShipOnBoard(boolean shipOnBoard) {
        this.shipOnBoard = shipOnBoard;
    }
}
