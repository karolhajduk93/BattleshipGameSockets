package com.karolhajduk.battleship;

import java.awt.*;

public class Ship {
    private int size;
    private  int id;
    private static int shipNumber = 0;
    private boolean horizontal = true;
    private Coordinates coordinates;

    Ship(int size, int x, int y) {
            shipNumber++;
            this.size = size;
            this.coordinates = new Coordinates(x, y);
            this.id = shipNumber;
    }

    public void setSize(int size) {
            this.size = size;
    }

    public int getSize() {
            return size;
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
}
