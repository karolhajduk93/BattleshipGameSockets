package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Optional;


public class BattleShipGame extends JFrame implements ActionListener {

    private Optional<Ship> newShip;
    private int logicPosX;
    private int logicPosY;
    private Coordinates start;
    static String coordinatesInput = "INPUT", coordinatesOutput = "OUTPUT";
    static boolean moveDone = false;
    private static int i = 0;
    static boolean connected = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleShipGame());
    }

    private BattleShipGame() {
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

        JButton reset = new JButton("RESET");
        reset.addActionListener(e -> {
            //set to default position of ships
            player.resetPositions();
            player.getShips().forEach(ship -> ship.setShipOnBoard(false));
        });
        JButton ready = new JButton("READY!");
        ready.addActionListener((ActionEvent e) -> { //set logic when button ready pressed
            //check if all ships are on board
            //set ready = true

            //set logic
            for (Ship ship : player.getShips()) {
                player.setMyBoard(ship);
            }
            if (player.getShips().stream().filter(Ship::isShipOnBoard).count() == 10 && e.getSource() == ready) {

                serverOrClientDecision(JOptionPane.YES_OPTION, player);

                panel.remove(reset);
                panel.remove(ready);

                revalidate();
                repaint();
            }
        });
        panel.add(reset);
        panel.add(ready);
        this.add(panel, BorderLayout.SOUTH);
        this.add(boardAndShips, BorderLayout.CENTER);


        boardAndShips.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                newShip.filter(Ship::isHorizontal)
                        .ifPresent(ship -> ship.setCoordinates(new Coordinates(e.getX() - ship.getSize() * 16, e.getY() - 16)));
                newShip.filter(ship -> !ship.isHorizontal())
                        .ifPresent(ship -> ship.setCoordinates(new Coordinates(e.getX() - 16, e.getY() - ship.getSize() * 16)));
            }
        });

        boardAndShips.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //if clicked and playing (not starting)
                //is in bound of other board

                //ship is in bounds of BOARD after rotating
                player.getShips().stream()
                        .filter(ship -> ship.getBounds().contains(e.getPoint()))
                        .filter(ship -> (logicPosX < 11 && logicPosY + ship.getSize() < 11 && ship.isHorizontal())
                                || (logicPosX + ship.getSize() < 11 && logicPosY < 11 && !ship.isHorizontal()))
                        .forEach(ship -> ship.setHorizontal(!ship.isHorizontal()));

                //ship don't overlap space of other ship, if does revert rotation to previous state
                player.getShips().stream()
                        .filter(ship -> ship.getBounds().contains(e.getPoint()))
                        .filter(ship -> ship.getId() != newShip.get().getId())
                        .filter(ship -> ship.getBigBounds().intersects(newShip.get().getBounds()))
                        .forEach(ship -> newShip.get().setHorizontal(!newShip.get().isHorizontal()));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                logicPosX = logicPosY = -1;

                //picking up ship
                newShip = player.getShips().stream().
                        filter(ship -> ship.getBounds().contains(e.getPoint()))
                        .findAny();

                newShip.ifPresent(newShip -> start = newShip.getCoordinates());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (newShip.isPresent()) {
                    logicPosX = (newShip.get().getCoordinates().getX() - 150) / 32;
                    logicPosY = (newShip.get().getCoordinates().getY() - 50) / 32;

                    //ship placed on board
                    if (((logicPosX + newShip.get().getSize() < 11) && (logicPosY < 11) && newShip.get().isHorizontal() && logicPosX > -1 && logicPosY > -1)
                            || ((logicPosX < 11) && (logicPosY + newShip.get().getSize() < 11) && !newShip.get().isHorizontal() && logicPosX > -1 && logicPosY > -1)) {
                        //adjust to grid
                        newShip.get().getCoordinates().setX(150 + logicPosX * 32);
                        newShip.get().getCoordinates().setY(50 + logicPosY * 32);

                        newShip.ifPresent(newShip -> newShip.setShipOnBoard(true));

                        //ship don't overlap space of other ship, if does revert move
                        player.getShips().stream()
                                .filter(ship -> ship.getId() != newShip.get().getId())
                                .filter(ship -> ship.getBigBounds().intersects(newShip.get().getBounds()))
                                .forEach(ship -> {
                                    newShip.get().setCoordinates(start);
                                    newShip.get().setShipOnBoard(false);
                                });
                    } else {
                        newShip.get().setHorizontal(true);
                        newShip.get().setCoordinates(start);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        //check for change
    }

    public void serverOrClientDecision(int decision, Captain player) {

        Object[] options = {"CREATE", "JOIN", "EXIT"};
        Object[] options2 = {"BACK", "TRY AGAIN", "EXIT"};
        if (decision == JOptionPane.YES_OPTION) {
            decision = JOptionPane.showOptionDialog(this, "Create game or join one?", "Battleship Game",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
        }


        if (decision == JOptionPane.YES_OPTION) {
            //create server

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Server server = new Server(player);
                    return null;
                }
            };
            worker.execute();

        } else if (decision == JOptionPane.NO_OPTION) {
            //getting IP
            String serversIP = JOptionPane.showInputDialog("Type server's IP");

            //creating client
            SwingWorker<Void, Boolean> worker1 = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws IOException {
                    Client client = new Client(serversIP, player);
                    return null;
                }
            };
            worker1.execute();

            while (!worker1.isDone()) {
                if(connected == true)
                    break;
            }

            if (!connected) {
                decision = JOptionPane.showOptionDialog(this, "Host not responding", "Join Game",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options2, null);
                serverOrClientDecision(decision, player);
            }
        }
    }
}



