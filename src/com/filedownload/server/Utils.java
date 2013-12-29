package com.filedownload.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by renpika on 12/28/13.
 */
public class Utils {
    public static void send404(HttpExchange exchange) throws IOException {
        //SEND 404
        String response = "404 (Not Found)\n";
        exchange.sendResponseHeaders(404, response.length());
        OutputStream os = exchange.getResponseBody();
        try {
            os.write(response.getBytes());
        } finally {
            os.close();
        }
    }

    public static Date parseHttpDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        //send malformed request
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            //rethrow MailFormedRequest
            e.printStackTrace();
        }
        return d;
    }
}
