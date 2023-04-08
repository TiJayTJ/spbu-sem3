package edu.spbu.server;

//import java.io.*;
import javax.swing.table.JTableHeader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class HTTPServer {
    
    private int port;
    private String directory;
    public HTTPServer(int port, String directory){
        this.port = port;
        this.directory = directory;
    }
    void start(){
        try (var server = new ServerSocket(this.port)){
            while (true){
                var socket = server.accept();
                var thread = new Handler(socket, this.directory);
                thread.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        var port = Integer.parseInt(args[0]);
        var directory = args[1];
        new HTTPServer(port, directory).start();
    }
}