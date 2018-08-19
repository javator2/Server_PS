package com.sda.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        int portNumber = 5555; //zawsze powyżej 1024!!! - poniżej są systemowe
        System.out.println("Slucham na porcie: " + portNumber);

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);//gniazdo - domyślnie protokół TCP
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    if (clientSocket.isConnected()) {
                        System.out.println("Nowy klient: " + clientSocket.getRemoteSocketAddress().toString());
                    }
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(clientSocket.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
                        String request, response;
                        while ((request = in.readLine()) != null) {
                            response = processRequest(request);
                            out.println(response);
                            if ("Done".equals(request)) {
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String request) {
        System.out.println("Server receive messsage from> " + request);
        return request;
    }
}
