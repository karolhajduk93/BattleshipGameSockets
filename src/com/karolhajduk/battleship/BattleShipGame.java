package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class BattleShipGame extends JFrame implements ActionListener {


    //Win condition & Disconnection of one player
    private Optional<Ship> newShip;
    private int logicPosX;
    private int logicPosY;
    private Coordinates start;
    static String coordinatesInput = "", coordinatesOutput = "";
    static boolean connected = false;
    static int gameResult = 0;
    JButton reset;
    JButton ready;
    Captain player = new Captain();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleShipGame());
    }

    private BattleShipGame()
    {
        this.setTitle("Battleship Game");
        this.setSize(1100, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        JPanel panel = new JPanel();
        player.initializeShips();

        Timer timer = new Timer(20, this);

        DrawBoardAndShips boardAndShips = new DrawBoardAndShips(player);

        timer.start();

        reset = new JButton("RESET");
        reset.addActionListener(e -> {
            //set to default position of ships
            player.resetPositions();
            player.getShips().forEach(ship -> ship.setShipOnBoard(false));
        });
        ready = new JButton("READY!");
        ready.addActionListener((ActionEvent e) -> { //set logic when button ready pressed
            //check if all ships are on board

            if (player.getShips().stream().filter(Ship::isShipOnBoard).count() == 10 && e.getSource() == ready) {

                //set logic
                for (Ship ship : player.getShips()) {
                    player.setMyBoard(ship);
                }

                //establish network connection
                boolean outcome = serverOrClientDecision(JOptionPane.YES_OPTION, player);

                if(outcome) {
                   reset.setVisible(false);
                   ready.setVisible(false);
                   reset.setEnabled(false);
                   ready.setEnabled(false);
                }
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
                customMouseDragged(player, e);
            }
        });

        boardAndShips.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                customMouseClicked(e, player);

            }

            @Override
            public void mousePressed(MouseEvent e) {
                customMousePressed(e, player);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                customMouseReleased(player);
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
    synchronized public void actionPerformed(ActionEvent e) {
        if(!coordinatesInput.isEmpty()){
            String[] parts = coordinatesInput.split("\\.");
            logicPosX = Integer.parseInt(parts[0]);
            logicPosY = Integer.parseInt(parts[1]);
            if (player.getMyBoard()[logicPosX][logicPosY] == 1) {
                player.getMyBoard()[logicPosX][logicPosY] = 2;
                player.setHitsTaken(player.getHitsTaken() + 1);
                isSunk(player.getMyBoard());
                if(player.getHitsTaken() == 20)
                    gameResult = 2; //M/N
            }
            else if(player.getMyBoard()[logicPosX][logicPosY] == 0)
                player.getMyBoard()[logicPosX][logicPosY] = -1;
        }

        if(gameResult != 0) { //MAGIC NUMBER - REMOVE LATER
            repaint();
            tryAgain(gameResult);
            gameResult = 0;
        }


        repaint();
    }



    public void customMouseDragged(Captain player, MouseEvent e) {
        if (player.getReady() == 0) {
            newShip.filter(Ship::isHorizontal)
                    .ifPresent(ship -> ship.setCoordinates(new Coordinates(e.getX() - ship.getSize() * 16, e.getY() - 16)));
            newShip.filter(ship -> !ship.isHorizontal())
                    .ifPresent(ship -> ship.setCoordinates(new Coordinates(e.getX() - 16, e.getY() - ship.getSize() * 16)));
        }
    }

    public void customMouseClicked(MouseEvent e, Captain player) {
        if (player.getReady() == 0) {
            //ship is in bounds of the BOARD after rotating
            player.getShips().stream()
                    .filter(ship -> ship.getBounds().contains(e.getPoint()))
                    .filter(ship -> (logicPosX < 11 && logicPosY + ship.getSize() < 11 && ship.isHorizontal())
                            || (logicPosX + ship.getSize() < 11 && logicPosY < 11 && !ship.isHorizontal()))
                    .forEach(ship -> ship.setHorizontal(!ship.isHorizontal()));

            //ship don't overlap space of other ship, if does revert rotation to previous state
            if(newShip.isPresent()) {
                player.getShips().stream()
                        .filter(ship -> ship.getId() != newShip.get().getId())
                        .filter(ship -> ship.getBigBounds().intersects(newShip.get().getBounds()))
                        .forEach(ship -> {
                            newShip.get().setHorizontal(!newShip.get().isHorizontal());
                        });
            }

        }
    }

    synchronized public void customMousePressed(MouseEvent e, Captain player) {
        if (player.getReady() == 0) {
            logicPosX = logicPosY = -1;

            //picking up ship
            newShip = player.getShips().stream().
                    filter(ship -> ship.getBounds().contains(e.getPoint()))
                    .findAny();

            newShip.ifPresent(newShip -> start = newShip.getCoordinates());
        } else if (player.getReady() == 2 && player.isMyTurn()) {
            //System.out.println(e.getPoint().toString());

            //-1 - uncovered (miss)
            // 0 - covered (miss)
            // 1 - covered (hit)
            // 2 - uncovered (hit)
            // 3 - uncovered (hit and sunk)

            // is shoot in bounds of the enemy BOARD
            if ((e.getX() > 600 && e.getX() < 920) && (e.getY() > 50 && e.getY() < 370)){
                logicPosX = (e.getX() - 600) / 32;
                logicPosY = (e.getY() - 50) / 32;

                if (player.getEnemyBoard()[logicPosX][logicPosY] == 1) { // if covered (hit)
                    player.getEnemyBoard()[logicPosX][logicPosY] = 2;// set as uncovered (hit)
                    //check if ship sunk
                    isSunk(player.getEnemyBoard());
                    player.setHitsGiven(player.getHitsGiven() + 1);
                    coordinatesOutput = Integer.toString(logicPosX) + "." + Integer.toString(logicPosY);
                    //
                    /*coordinatesOutput = "";
                    player.setMyTurn(false);*/
                }
                else if (player.getEnemyBoard()[logicPosX][logicPosY] == 0) { //if covered (miss)
                    player.getEnemyBoard()[logicPosX][logicPosY] = -1; // set as uncovered (miss)
                    coordinatesOutput = Integer.toString(logicPosX) + "." + Integer.toString(logicPosY);

                }
            }
        }
    }
    public void customMouseReleased(Captain player) {
        if (player.getReady() == 0) {
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
    }

    public boolean serverOrClientDecision(int decision, Captain player) {

        Object[] options = {"CREATE", "JOIN", "EXIT"};
        Object[] options2 = {"BACK", "TRY AGAIN", "EXIT"};
        if (decision == JOptionPane.YES_OPTION) {

            decision = JOptionPane.showOptionDialog(this, "Create game or join one?", "Battleship Game",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
        }

        //
        if(decision == JOptionPane.CANCEL_OPTION)
            return false;

        if (decision == JOptionPane.YES_OPTION) {
            //create server

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        Server server = new Server(player);
                    }
                    catch (IOException e){
                        tryAgain(3);
                    }
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
                    try {
                        Client client = new Client(serversIP, player);
                    }
                    catch (IOException e){
                        tryAgain(3);
                    }
                    return null;
                }
            };
            worker1.execute();

            while (!worker1.isDone()) {
                if (connected == true)
                    break;
            }

            if (!connected) {
                decision = JOptionPane.showOptionDialog(this, "Host not responding", "Join Game",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options2, null);
                serverOrClientDecision(decision, player);
            }
        }
        return true;
    }

    public void tryAgain(int gameResult){

        //1 - win
        //2 - lose

        int decision = -1;

        if(gameResult == 1) {
            decision = JOptionPane.showConfirmDialog(this, "Play again?", "WIN!!!",
                    JOptionPane.YES_NO_OPTION);
        }
        else if (gameResult == 2){
            decision = JOptionPane.showConfirmDialog(this, "Play again?", "LOOSE!!!",
                    JOptionPane.YES_NO_OPTION);
        }
        else if(gameResult == 3)
            decision = JOptionPane.showConfirmDialog(this, "Start new game?", "Opponent disconnected",
                    JOptionPane.YES_NO_OPTION);

        if(decision == JOptionPane.YES_OPTION){
            player.resetPositions();
            player.setReady(0);
            player.getShips().forEach(ship -> ship.setShipOnBoard(false));
            player.setHitsTaken(0);
            player.setHitsGiven(0);

            reset.setVisible(true);
            ready.setVisible(true);
            reset.setEnabled(true);
            ready.setEnabled(true);

            coordinatesInput = "";
        }
        else if (decision == JOptionPane.NO_OPTION){
            System.exit(0);
        }

    }

    synchronized private void isSunk(int[][] board) {

        //add to struckPosition
        int struck = 0;
        int notStruck = 0;
        ArrayList<Point> struckPositions = new ArrayList<>();

        //checking every direction for not struck poles in ship
        for (int n = 0; n < 4; n++) {
            if (logicPosY - n >= 0){
                if (board[logicPosX][logicPosY - n] == 2) {
                    struckPositions.add(new Point(logicPosX, logicPosY - n));
                    struck++;
                }
                else if(board[logicPosX][logicPosY - n] == 1)
                    notStruck++;
                else if(board[logicPosX][logicPosY - n] == 0)
                    break;
            }
            else
                break;
        }
        for (int s = 1; s < 4; s++) {
            if (logicPosY + s  < 10){
                if (board[logicPosX][logicPosY + s] == 2) {
                    struckPositions.add(new Point(logicPosX, logicPosY + s));
                    struck++;
                }
                else if(board[logicPosX][logicPosY + s] == 1)
                    notStruck++;
                else if(board[logicPosX][logicPosY + s] == 0)
                    break;
            }
            else
                break;
        }
        for (int e = 1; e < 4; e++) {
            if (logicPosX + e < 10){
                if (board[logicPosX + e][logicPosY] == 2) {
                    struckPositions.add(new Point(logicPosX + e, logicPosY));
                    struck++;
                }
                else if(board[logicPosX + e][logicPosY] == 1)
                    notStruck++;
                else if(board[logicPosX + e][logicPosY] == 0)
                    break;
            }
            else
                break;
        }
        for (int w = 1; w < 4; w++) {
            if (logicPosX - w >= 0){
                if (board[logicPosX - w][logicPosY] == 2) {
                    struckPositions.add(new Point(logicPosX - w, logicPosY));
                    struck++;
                }
                else if(board[logicPosX - w][logicPosY] == 1)
                    notStruck++;
                else if(board[logicPosX - w][logicPosY] == 0)
                    break;
            }
            else
                break;
        }

        // if sunk change it in logical table
        if (notStruck == 0){
            for (Point p : struckPositions){
                //System.out.println("x= " + p.x + " y= " + p.y);
                board[p.x][p.y] = 3;
                //return int = struck
            }
        }
        //else return -1; - in future to display sunk ships as small squares next to tables


    }
}



