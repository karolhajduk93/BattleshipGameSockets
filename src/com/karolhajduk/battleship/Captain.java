package com.karolhajduk.battleship;

import java.util.ArrayList;
import java.util.List;

public class Captain {

    private List<Ship> ships = new ArrayList<>();

    private static int[] startCoordinatesX = {600,
            600, 600 + 3 * 32 + 10,
            600, 600 + 2 * 32 + 10, 600 + (2 * 32 + 10) * 2,
            600, 600 + (32 + 10), 600 + 2 * (32 + 10), 600 + 3 * (32 + 10)};
    private static int[] startCoordinatesY = {100,
            150, 150,
            200, 200, 200,
            250, 250, 250, 250};

    /*boolean[][] myBoard = new boolean[10][10];
    boolean[][] enemyBoard = new boolean[10][10];
    private boolean winLoseState = false;*/


    public void initializeShips(){
        //create Ships inside ships list

        //Starting coordinates
        int k = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = i; j < 5; j++) {
                ships.add(new Ship(i, startCoordinatesX[k], startCoordinatesY[k]));
                k++;
            }
        }

        /*for(Ship ship: ships){
            System.out.println(ship.getCoordinates().getA() + " " + ship.getCoordinates().getB());
        }*/
    }

    public List<Ship> getShips() {
        return ships;
    }

    public void resetPositions(){
        int k = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = i; j < 5; j++) {
                ships.get(k).getCoordinates().setX(startCoordinatesX[k]);
                ships.get(k).getCoordinates().setY(startCoordinatesY[k]);
                k++;
            }
        }
    }
}
