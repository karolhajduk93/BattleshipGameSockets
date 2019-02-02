package com.karolhajduk.battleship;

import java.io.*;
import java.net.Socket;

public class Client {

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    BufferedReader bufferedReader;

    String sendMessage = "", receivedMessage = "";

    public Client(String host, Captain player) throws IOException {

        socket = new Socket(host, 6666);

        BattleShipGame.connected = socket.isConnected();

        player.setReady(GameState.PLAYING);

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        //send logic table at the beginning of the game
        for (int i1 = 0; i1 < player.getMyBoard().length; i1++) {
            for (int j = 0; j < player.getMyBoard()[i1].length; j++) {
                if (player.getMyBoard()[j][i1] == HitOrMiss.COVERED_HIT)
                    sendMessage += "1";
                else
                    sendMessage += "0";
            }
        }

        System.out.println("Client\n" + sendMessage); /////////////////////////////////////////////

        dataOutputStream.writeUTF(sendMessage);
        receivedMessage = dataInputStream.readUTF();
        sendMessage = "";


        //receive String and convert it to enemy logic table
        player.setEnemyBoard(receivedMessage);
        receivedMessage = "";

        player.setMyTurn(false);

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
    }
}