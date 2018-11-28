package com.karolhajduk.battleship;

import java.util.ArrayList;
import java.util.List;

public class Captain {

    private List<Ship> ships = new ArrayList<>();

    boolean[][] myBoard = new boolean[10][10];
    boolean[][] enemyBoard = new boolean[10][10];
    private boolean winLoseState = false;


    public void initializeShips(){
        //create Ships inside ships list
        for (int i = 4; i > 0; i--) {
            for (int j = i; j < 5; j++) {
                ships.add(new Ship(i));
            }
        }
    }
}
