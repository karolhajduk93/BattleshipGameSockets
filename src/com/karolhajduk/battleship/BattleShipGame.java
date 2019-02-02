package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class BattleShipGame extends JFrame implements ActionListener {


    //Display ship count (graphic form) moves dont show (misses [at all] & sunk [correctly])
    static final int MY_BOARD_START_POSITION_X = 150;
    static final int MY_BOARD_END_POSITION_X = 470;
    static final int ENEMY_BOARD_START_POSITION_X = 600;
    static final int ENEMY_BOARD_END_POSITION_X = 920;
    static final int BOTH_BOARD_START_POSITION_Y = 50;
    static final int BOTH_BOARD_END_POSITION_Y = 370;
    static final int DEFAULT_LOGICAL_TABLE_SIZE = 10;

    private Optional<Ship> newShip;
    private int logicPosX;
    private int logicPosY;
    private Coordinates start;
    static String coordinatesInput = "", coordinatesOutput = "";
    static boolean connected = false;
    static GameResult gameResult = GameResult.PENDING;
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
            if (player.getShips().stream().filter(Ship::isShipOnBoard).count() == player.getShips().size() && e.getSource() == ready) {
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
            System.out.print("Receive move: " + coordinatesInput); /////////////////////////


            String[] parts = coordinatesInput.split("\\.");
            logicPosX = Integer.parseInt(parts[0]);
            logicPosY = Integer.parseInt(parts[1]);

            System.out.println(player.getMyBoard()[logicPosX][logicPosY]);

            if (player.getMyBoard()[logicPosX][logicPosY] == HitOrMiss.COVERED_HIT) {
                player.getMyBoard()[logicPosX][logicPosY] = HitOrMiss.UNCOVERED_HIT;
                player.setHitsTaken(player.getHitsTaken() + 1);
                /*int tmp = */isSunk(player.getMyBoard());
                //player.getSunkShip(tmp, false);

                System.out.println(player.getMyBoard()[logicPosX][logicPosY] + "uncovered hit RECIVE"); ////////////////////////

                if(player.getHitsTaken() == Captain.MAX_NUMBER_OF_HITS)
                    gameResult = GameResult.LOOSE;
            }
            else if(player.getMyBoard()[logicPosX][logicPosY] == HitOrMiss.COVERED_MISS) {
                player.getMyBoard()[logicPosX][logicPosY] = HitOrMiss.UNCOVERED_MISS;

                System.out.println(player.getMyBoard()[logicPosX][logicPosY] + "uncovered miss RECIVE"); /////////////////
            }
        }
        if(!gameResult.equals(GameResult.PENDING)) {
            repaint();
            tryAgain(gameResult);
            gameResult = GameResult.PENDING;
        }
        repaint();
    }



    public void customMouseDragged(Captain player, MouseEvent e) {
        if (player.getReady().equals(GameState.PREPARE)) {
            newShip.filter(Ship::isHorizontal)
                    .ifPresent(ship -> ship.setCoordinates(new Coordinates(e.getX() - ship.getSize() * (Captain.DEFAULT_GRID_SIZE/2), e.getY() - (Captain.DEFAULT_GRID_SIZE/2))));
            newShip.filter(ship -> !ship.isHorizontal())
                    .ifPresent(ship -> ship.setCoordinates(new Coordinates(e.getX() - (Captain.DEFAULT_GRID_SIZE/2), e.getY() - ship.getSize() * (Captain.DEFAULT_GRID_SIZE/2))));
        }
    }

    public void customMouseClicked(MouseEvent e, Captain player) {
        if (player.getReady().equals(GameState.PREPARE)) {
            //ship is in bounds of the BOARD after rotating
            player.getShips().stream()
                    .filter(ship -> ship.getBounds(0).contains(e.getPoint()))
                    .filter(ship -> (logicPosX < DEFAULT_LOGICAL_TABLE_SIZE && logicPosY + ship.getSize() - 1 < DEFAULT_LOGICAL_TABLE_SIZE && ship.isHorizontal())
                            || (logicPosX + ship.getSize() - 1 < DEFAULT_LOGICAL_TABLE_SIZE && logicPosY < DEFAULT_LOGICAL_TABLE_SIZE && !ship.isHorizontal()))
                    .forEach(ship -> ship.setHorizontal(!ship.isHorizontal()));

            //ship don't overlap space of other ship, if does revert rotation to previous state
            if(newShip.isPresent()) {
                player.getShips().stream()
                        .filter(ship -> ship.getId() != newShip.get().getId())
                        .filter(ship -> ship.getBounds(Captain.DEFAULT_GRID_SIZE).intersects(newShip.get().getBounds(0)))
                        .forEach(ship -> {
                            newShip.get().setHorizontal(!newShip.get().isHorizontal());
                        });
            }
        }
    }

    synchronized public void customMousePressed(MouseEvent e, Captain player) {
        if (player.getReady().equals(GameState.PREPARE)) {
            logicPosX = logicPosY = -1;

            //picking up ship
            newShip = player.getShips().stream().
                    filter(ship -> ship.getBounds(0).contains(e.getPoint()))
                    .findAny();

            newShip.ifPresent(newShip -> start = newShip.getCoordinates());
        } else if (player.getReady().equals(GameState.PLAYING) && player.isMyTurn()) { ////////////////////////////////////////////////////////////////////////////////////

            // is shoot in bounds of the enemy BOARD
            if ((e.getX() > ENEMY_BOARD_START_POSITION_X && e.getX() < ENEMY_BOARD_END_POSITION_X)
                    && (e.getY() > BOTH_BOARD_START_POSITION_Y && e.getY() < BOTH_BOARD_END_POSITION_Y)){
                logicPosX = (e.getX() - ENEMY_BOARD_START_POSITION_X) / Captain.DEFAULT_GRID_SIZE;
                logicPosY = (e.getY() - BOTH_BOARD_START_POSITION_Y) / Captain.DEFAULT_GRID_SIZE;

                System.out.println("Send move: " + logicPosX + "." + logicPosY); ////////////////////////////////////////

                if (player.getEnemyBoard()[logicPosX][logicPosY] == HitOrMiss.COVERED_HIT) { // if covered (hit)
                    player.getEnemyBoard()[logicPosX][logicPosY] = HitOrMiss.UNCOVERED_HIT;// set as uncovered (hit)

                    System.out.println("Uncovered hit SEND: " + logicPosX + "." + logicPosY);
                    //check if ship sunk
                    /*int tmp = */isSunk(player.getEnemyBoard());
                    //player.getSunkShip(tmp, true);
                    player.setHitsGiven(player.getHitsGiven() + 1);
                    coordinatesOutput = Integer.toString(logicPosX) + "." + Integer.toString(logicPosY);
                }
                else if (player.getEnemyBoard()[logicPosX][logicPosY] == HitOrMiss.COVERED_MISS) { //if covered (miss)
                    player.getEnemyBoard()[logicPosX][logicPosY] = HitOrMiss.UNCOVERED_MISS; // set as uncovered (miss)
                    coordinatesOutput = Integer.toString(logicPosX) + "." + Integer.toString(logicPosY);

                    System.out.println("Uncovered miss SEND: " + logicPosX + "." + logicPosY);

                }
            }
        }
    }
    public void customMouseReleased(Captain player) {
        if (player.getReady().equals(GameState.PREPARE)) {
            if (newShip.isPresent()) {
                logicPosX = (newShip.get().getCoordinates().getX() - MY_BOARD_START_POSITION_X) / Captain.DEFAULT_GRID_SIZE;
                logicPosY = (newShip.get().getCoordinates().getY() - BOTH_BOARD_START_POSITION_Y) / Captain.DEFAULT_GRID_SIZE;

                //ship placed on board
                if (((logicPosX + newShip.get().getSize() - 1  < DEFAULT_LOGICAL_TABLE_SIZE) && (logicPosY < 10) && newShip.get().isHorizontal() && logicPosX > -1 && logicPosY > -1)
                        || ((logicPosX < DEFAULT_LOGICAL_TABLE_SIZE) && (logicPosY + newShip.get().getSize() - 1 < DEFAULT_LOGICAL_TABLE_SIZE) && !newShip.get().isHorizontal() && logicPosX > -1 && logicPosY > -1)) {

                    //adjust to grid
                    newShip.get().getCoordinates().setX(MY_BOARD_START_POSITION_X + logicPosX * Captain.DEFAULT_GRID_SIZE);
                    newShip.get().getCoordinates().setY(BOTH_BOARD_START_POSITION_Y + logicPosY * Captain.DEFAULT_GRID_SIZE);

                    newShip.ifPresent(newShip -> newShip.setShipOnBoard(true));

                    //ship don't overlap space of other ship, if does - revert move
                    player.getShips().stream()
                            .filter(ship -> ship.getId() != newShip.get().getId())
                            .filter(ship -> ship.getBounds(Captain.DEFAULT_GRID_SIZE).intersects(newShip.get().getBounds(0)))
                            .forEach(ship -> {
                                newShip.get().setCoordinates(start);
                                newShip.get().setShipOnBoard(false);
                            });
                } else {
                    newShip.get().setHorizontal(newShip.get().isHorizontal());
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
                        tryAgain(GameResult.DISCONNECTED);
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
                        if(connected)
                            tryAgain(GameResult.DISCONNECTED);
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

    public void tryAgain(GameResult gameResult){

        int decision = -1;

        if(gameResult.equals(GameResult.WIN)) {
            decision = JOptionPane.showConfirmDialog(this, "Play again?", "WIN!!!",
                    JOptionPane.YES_NO_OPTION);
        }
        else if (gameResult.equals(GameResult.LOOSE)){
            decision = JOptionPane.showConfirmDialog(this, "Play again?", "LOOSE!!!",
                    JOptionPane.YES_NO_OPTION);
        }
        else if(gameResult.equals(GameResult.DISCONNECTED))
            decision = JOptionPane.showConfirmDialog(this, "Start new game?", "Opponent disconnected",
                    JOptionPane.YES_NO_OPTION);

        if(decision == JOptionPane.YES_OPTION){
            player.resetPositions();
            player.setReady(GameState.PREPARE);
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

    synchronized private void isSunk(HitOrMiss[][] board) {

        //add to struckPosition
        int struck = 0;
        int notStruck = 0;
        ArrayList<Point> struckPositions = new ArrayList<>();

        //checking every direction for not struck poles in ship
        for (int n = 0; n < Ship.MAX_SHIP_SIZE; n++) {
            if (logicPosY - n >= 0){
                if (board[logicPosX][logicPosY - n] == HitOrMiss.UNCOVERED_HIT) {
                    struckPositions.add(new Point(logicPosX, logicPosY - n));
                    struck++;
                }
                else if(board[logicPosX][logicPosY - n] == HitOrMiss.COVERED_HIT)
                    notStruck++;
                else if(board[logicPosX][logicPosY - n] == HitOrMiss.COVERED_MISS || board[logicPosX][logicPosY - n] == HitOrMiss.UNCOVERED_MISS)
                    break;
            }
            else
                break;
        }
        for (int s = 1; s < Ship.MAX_SHIP_SIZE; s++) {
            if (logicPosY + s  < DEFAULT_LOGICAL_TABLE_SIZE){
                if (board[logicPosX][logicPosY + s] == HitOrMiss.UNCOVERED_HIT) {
                    struckPositions.add(new Point(logicPosX, logicPosY + s));
                    struck++;
                }
                else if(board[logicPosX][logicPosY + s] == HitOrMiss.COVERED_HIT)
                    notStruck++;
                else if(board[logicPosX][logicPosY + s] == HitOrMiss.COVERED_MISS || board[logicPosX][logicPosY + s] == HitOrMiss.UNCOVERED_MISS)
                    break;
            }
            else
                break;
        }
        for (int e = 1; e < Ship.MAX_SHIP_SIZE; e++) {
            if (logicPosX + e < DEFAULT_LOGICAL_TABLE_SIZE){
                if (board[logicPosX + e][logicPosY] == HitOrMiss.UNCOVERED_HIT) {
                    struckPositions.add(new Point(logicPosX + e, logicPosY));
                    struck++;
                }
                else if(board[logicPosX + e][logicPosY] == HitOrMiss.COVERED_HIT)
                    notStruck++;
                else if(board[logicPosX + e][logicPosY] == HitOrMiss.COVERED_MISS || board[logicPosX + e][logicPosY] == HitOrMiss.UNCOVERED_MISS)
                    break;
            }
            else
                break;
        }
        for (int w = 1; w < Ship.MAX_SHIP_SIZE; w++) {
            if (logicPosX - w >= 0){
                if (board[logicPosX - w][logicPosY] == HitOrMiss.UNCOVERED_HIT) {
                    struckPositions.add(new Point(logicPosX - w, logicPosY));
                    struck++;
                }
                else if(board[logicPosX - w][logicPosY] == HitOrMiss.COVERED_HIT)
                    notStruck++;
                else if(board[logicPosX - w][logicPosY] == HitOrMiss.COVERED_MISS || board[logicPosX - w][logicPosY] == HitOrMiss.UNCOVERED_MISS )
                    break;
            }
            else
                break;
        }

        // if sunk change it in logical table
        if (notStruck == 0){
            for (Point p : struckPositions){
                board[p.x][p.y] = HitOrMiss.SUNK;
                //return struck;
            }
        }
        //return 0;
    }
}



