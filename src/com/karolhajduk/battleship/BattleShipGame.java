package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;


public class BattleShipGame extends JFrame implements ActionListener {

    private Optional<Ship> newShip;

    public static void main(String[] args) {
        new BattleShipGame();
    }

    private BattleShipGame(){
        this.setTitle("Battleship Game");
        this.setSize(1100, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);


        JPanel panel = new JPanel();
        Captain player = new Captain();
        player.initializeShips();

        Timer timer = new Timer(20, this);

        DrawBoardAndShips boardAndShips = new DrawBoardAndShips(player);

        timer.start();
        //create player
        //set your ships (one map & ships to drag from place to place)
        //when READY button clicked and all ships are on board ->
        //->search for player (Connection - sockets)
        //draw 2 maps (one with ships, one to shoot)
        //receive  coordinates (hit or miss - draw changes to 1 map) - win condition checker (send info to another player about shoot result)
        //send coordinates

        JButton reset = new JButton("reset");
        reset.addActionListener(e -> {
            //set to default position of ships
            player.resetPositions();
        });
        panel.add(reset);
        this.add(panel, BorderLayout.SOUTH);
        this.add(boardAndShips, BorderLayout.CENTER);


        boardAndShips.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                newShip.ifPresent(ship -> ship.setCoordinates(new Coordinates(e.getX() - ship.getSize()*16, e.getY() - 16)));

                System.out.println(newShip.isPresent());
            }
        });

        boardAndShips.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                newShip = player.getShips().stream().filter(ship -> ship.getBounds().contains(e.getPoint())).findAny();
                if(newShip.isPresent()){
                    System.out.println("size: " + newShip.get().getSize());
                }
                else System.out.println("X");
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        //Ship actualtShip = myShip.get();
        //mouse coordinates have to be sent as ship coordinates after adjusting
        //create mouse listener
            //if PRESSED
                //check if on ship
                    //which ship
            //if RELEASED
            //if coordinates in area on board adjust them accordingly
                //

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
