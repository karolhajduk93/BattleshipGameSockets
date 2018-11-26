package com.karolhajduk.battleship;

import javax.swing.*;

public class BattleShipGame extends JFrame{
    public static void main(String[] args) {
        new BattleShipGame();
    }

    public BattleShipGame(){
        this.setTitle("Battleship Game");
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);







        this.setVisible(true);
    }
}
