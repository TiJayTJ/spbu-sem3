package edu.spbu.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class HTTPServer {
    
    private static void sendResponse(OutputStream output, int statusCode, String statusText, long length){
        PrintStream printer = new PrintStream(output);
        printer.printf("HTTP/1.1 %s %s", statusCode, statusText);
        printer.printf("Content-Type: %s%n", "text/html");
        printer.printf("Content-Length: %s%n%n", length);
    }
    
    public static void main(String[] args){
        int count = 0;
        var port = Integer.parseInt(args[0]);
        var directory = args[1];
        
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while(true) {
                var socket = serverSocket.accept();
                System.out.println("Client accepted " + (count++));
        
                // streams creating
                OutputStream output = socket.getOutputStream();
                InputStream input = socket.getInputStream();
        
                // getting request
                Scanner scanner = new Scanner(input).useDelimiter("\r\n");
                String str = scanner.next();
                System.out.println(str);
                List<String> request = List.of(str.split(" "));
    
                // opening error files
                Path error400 = Path.of(directory, "error400.html");
                var error400Bytes = Files.readAllBytes(error400);
                Path error404 = Path.of(directory, "error404.html");
                var error404Bytes = Files.readAllBytes(error404);
                
                // checking for a get request
                if(!Objects.equals(request.get(0), "GET")){
                    sendResponse(output, 400, "Bad Request", error400Bytes.length);
                    output.write(error400Bytes);
                    return;
                }
                
                // checking for the existence of a file
                // and
                // sending response
                Path filePath = Path.of(directory, request.get(1));
                if (Files.exists(filePath) && !Files.isDirectory(filePath)){
                    var fileByte = Files.readAllBytes(filePath);
                    sendResponse(output, 200, "OK", fileByte.length);
                    output.write(fileByte);
                }
                else {
                    sendResponse(output, 404, "Not found", error404Bytes.length);
                    output.write(error404Bytes);
                }
                System.out.println(filePath);
        
                System.out.println("socket closed\n");
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}