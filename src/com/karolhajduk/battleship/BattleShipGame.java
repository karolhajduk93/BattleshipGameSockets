package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattleShipGame extends JFrame implements ActionListener {
    public static void main(String[] args) {
        new BattleShipGame();
    }

    private BattleShipGame(){
        this.setTitle("Battleship Game");
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);


        Captain you = new Captain();
        you.initializeShips();

        Timer timer = new Timer(1000, this);

        DrawBoardAndShips boardAndShips = new DrawBoardAndShips();
        this.add(boardAndShips);
        timer.start();
        //create player
        //set your ships (one map & ships to drag from place to place)
        //draw 2 maps (one with ships, one to shoot)
        //receive  coordinates (hit or miss - draw changes to 1 map) - win condition checker (if loose - send info to winner)
        //send coordinates



        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
