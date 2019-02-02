package com.karolhajduk.battleship;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Server extends Thread {
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    String sendMessage = "", receivedMessage = "";

    public Server(Captain player) throws IOException {

        player.setReady(GameState.WAITING);
        serverSocket = new ServerSocket(6666);
        socket = serverSocket.accept();


        player.setReady(GameState.PLAYING);

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());


        //send logic table at the beginning of the game
        for (int i1 = 0; i1 < player.getMyBoard().length; i1++) {
            for (int j = 0; j < player.getMyBoard()[i1].length; j++) {
                if (player.getMyBoard()[j][i1] == HitOrMiss.COVERED_HIT)
                    sendMessage += "1";
                else
                    sendMessage += "0";
            }
        }

        System.out.println("Server\n" + sendMessage);//////////////////////////////////////////////////

        dataOutputStream.writeUTF(sendMessage);
        dataOutputStream.flush();
        sendMessage = "";

        //receive String and convert it to enemy logic table
        receivedMessage = dataInputStream.readUTF();
        player.setEnemyBoard(receivedMessage);
        receivedMessage = "";

        player.setMyTurn(true);

        while (true) {
            if (!player.isMyTurn()) {
                receivedMessage = dataInputStream.readUTF();

                if (!receivedMessage.isEmpty()) {
                    BattleShipGame.coordinatesInput = receivedMessage;
                    receivedMessage = "";
                    player.setMyTurn(true);
                }
            } else {
                sendMessage = BattleShipGame.coordinatesOutput;

                if (!sendMessage.isEmpty()) {
                    dataOutputStream.writeUTF(sendMessage);

                    sendMessage = "";
                    BattleShipGame.coordinatesOutput = "";
                    player.setMyTurn(false);

                    if(player.getHitsGiven() == Captain.MAX_NUMBER_OF_HITS) {
                        BattleShipGame.gameResult = GameResult.WIN;
                        break;
                    }
                }
            }
            dataOutputStream.flush();
        }
        dataInputStream.close();
        socket.close();
        serverSocket.close();
    }
}
