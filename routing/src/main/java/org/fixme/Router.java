package org.fixme;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class Router {
    public static ArrayList<ClientModel> connections = new ArrayList<ClientModel>();
    public static void main(String[] args) throws IOException {
        new ServerSocketThread(5000).start();
        new ServerSocketThread(5001).start();
    }

}
