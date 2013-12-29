package com.filedownload.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by renpika on 12/29/13.
 */
public class FileHandler implements HttpHandler {
    private File directory;

    public FileHandler(File directory) {
        this.directory = directory;
    }

    private void sendFile(HttpExchange exchange, File file) throws IOException {
        exchange.sendResponseHeaders(200, file.length());
        writeBody(exchange, file, 0);
    }

    private void writeBody(HttpExchange exchange, File file, int offset) throws IOException {
        FileInputStream fs = new FileInputStream(file);
        BufferedInputStream input = new BufferedInputStream(fs);
        OutputStream os = exchange.getResponseBody();
        BufferedOutputStream output = new BufferedOutputStream(os, 1024);
        if (offset > 0) {
            fs.skip(offset);
        }
        try {
            final byte[] buffer = new byte[1024];
            int count;
            while ((count = input.read(buffer)) >= 0) {
                output.write(buffer, 0, count);
            }
        } finally {
            output.close();
            input.close();
        }
    }

    private void sendFilePartial(HttpExchange exchange, File file) throws IOException {
        Headers requestHeaders = exchange.getRequestHeaders();
        String rangeHeader = requestHeaders.get("Range").get(0);
        Pattern p = Pattern.compile("bytes=(\\d+)-(\\d*)");
        Matcher m = p.matcher(rangeHeader);
        if (m.find()) {
            try {
                int offset = Integer.parseInt(m.group(1));
                //int range = Integer.parseInt(m.group(2));
                exchange.sendResponseHeaders(206, file.length());
                writeBody(exchange, file, offset);
            } catch (NumberFormatException e) {
                //rethrow MailFormedRequest
            }
        }
    }

    public void handle(HttpExchange exchange) throws IOException {
        //parse file name
        URI u = exchange.getRequestURI();
        String path = u.getPath();
        String fileName = path.substring(path.lastIndexOf('/') + 1);

        File file = new File(directory, fileName).getCanonicalFile();

        if (file.exists() && file.isFile()) {

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Accept-Ranges", "bytes");

            Headers requestHeaders = exchange.getRequestHeaders();
            if (requestHeaders.containsKey("Range")) {
                if (requestHeaders.containsKey("If-Range")) {
                    String date = requestHeaders.get("If-Range").get(0);
                    Date cachedFileDate = Utils.parseHttpDate(date);
                    Date fileDate = new Date(file.lastModified());
                    if (cachedFileDate.before(fileDate)) {
                        // file on server has changed
                        // send 200
                        sendFile(exchange, file);
                    } else {
                        //send 206
                        sendFilePartial(exchange, file);
                    }
                } else {
                    //If-Range headers are missing
                    //send 206
                    sendFilePartial(exchange, file);
                }
            } else {
                sendFile(exchange, file);
            }
        } else {
            Utils.send404(exchange);
        }
    }
}
