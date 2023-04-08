package edu.spbu.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class HTTPServer {
    private static void response(OutputStream output, int statusCode, String statusText, byte[] file){
        PrintStream printer = new PrintStream(output);
        printer.print("HTTP/1.1 200 OK%n");
        printer.printf("Content-Type: %s%n", "text/html");
        printer.printf("Content-Length: %s%n%n", file.length);
        try {
            output.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int count = 0;
        ServerSocket serverSocket = new ServerSocket(8000);

        while(true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client accepted " + (count++));

            OutputStream output = clientSocket.getOutputStream();
            InputStream input = clientSocket.getInputStream();

            Scanner scanner = new Scanner(input).useDelimiter("\r\n");

            String str = scanner.next();
            System.out.println(str);
            List<String> request = List.of(str.split(" "));

            Path filePath = Path.of("src/test/java/edu/spbu/server/webSites/mainPageFiles", request.get(1));
            System.out.println(filePath);

            response(output, 200, "OK", Files.readAllBytes(filePath));

            System.out.println("socket closed\n");
            clientSocket.close();
        }
//        serverSocket.close();
    }
}