package com.karolhajduk.battleship;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Server extends Thread {
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    String start = "", message1 = "";

    public Server(Captain player) throws IOException {

        System.out.println("SERVER");

        player.setReady(1);
        serverSocket = new ServerSocket(6666);


        socket = serverSocket.accept();
        player.setReady(2);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        //send my logic table at the beginning of the game //////////////////////////////////////////////
        for (boolean[] row: player.getMyBoard()){
            for(boolean value: row){
                if(value)
                    start += "1";
                else
                    start += "0";
            }
        }
        dataOutputStream.writeUTF(start);
        System.out.println(start);
        while(!message1.equals("WIN") || !message1.equals("LOOSE")){

            //System.out.println("Server");
            /*//send 1 messages
                //other: shoot
            BattleShipGame.coordinatesInput = dataInputStream.readUTF();

            System.out.println(BattleShipGame.coordinatesInput);
            if(BattleShipGame.moveDone) {

                dataOutputStream.writeUTF(BattleShipGame.coordinatesOutput);
                BattleShipGame.coordinatesOutput = "";
            }*/
            dataOutputStream.flush();
        }

        dataInputStream.close();
        socket.close();
        serverSocket.close();
    }


}
