package com.filedownload.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renpika on 12/29/13.
 */
public class FilesListHandler implements HttpHandler {
    private File directory;

    public FilesListHandler(File directory) {
        this.directory = directory;
    }

    public void handle(HttpExchange exchange) throws IOException {
        //get all the files from a directory
        List<String> filesList = new ArrayList<String>();
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                filesList.add(file.getName());
            }
        }
        Gson gson = new Gson();
        String response = gson.toJson(filesList);
        Headers h = exchange.getResponseHeaders();
        h.add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
