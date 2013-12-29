package com.filedownload.server;

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


public class Server {
    public static void main(String[] args) throws IOException {
        Config c = Config.getDefault();

        String directoryName = c.filesDirectory;
        InetAddress addr = InetAddress.getByName(c.address);
        int port = c.port;

        File directory = new File(directoryName);
        HttpServer server = HttpServer.create(new InetSocketAddress(addr, port), 0);

        server.createContext("/api/files", new FilesListHandler(directory));
        server.createContext("/api/files/", new FileHandler(directory));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port 8000");
    }
}