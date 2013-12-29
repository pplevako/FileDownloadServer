package com.filedownload.server;

/**
 * Created by renpika on 12/29/13.
 */
public class Config {
    public int port;
    public String address;
    public String filesDirectory;

    public Config(String address, int port, String fileDirectory) {
        this.address = address;
        this.port = port;
        this.filesDirectory = fileDirectory;
    }

    public static Config getDefault() {
        return new Config("127.0.0.1", 8000, "sharedFiles");
    }
}
