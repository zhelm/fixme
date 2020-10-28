package org.fixme;

import java.net.Socket;

public class ClientModel {
    public Socket socket;
    public int id;

    public ClientModel(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

}
