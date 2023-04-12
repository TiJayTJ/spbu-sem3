package edu.spbu.server;

import java.io.*;
import java.net.Socket;

public class HTTPClient {

    public void request(String url, int port) {

        // getting url of file
        var fullUrl = url.strip().split("/", 2);
        String getFileUrl;
        if (fullUrl.length == 2) {
            getFileUrl = fullUrl[1];
        }
        else if (fullUrl.length == 1) {
            getFileUrl = "";
        } else{
            System.out.println("Incorrect input");
            return;
        }
        
        try(Socket clientSocket = new Socket(fullUrl[0], port)){
            var output = clientSocket.getOutputStream();
            var input = clientSocket.getInputStream();
    
            var print = new PrintStream(output);
    
            print.printf("GET /%s HTTP/1.1%n", getFileUrl);
            print.printf("Host: %s:%s%n", fullUrl[0], port);
            print.println();
            print.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    
            String outStr;
            
            System.out.println(reader.readLine());
            while ((outStr = reader.readLine()) != null) {
                System.out.println(outStr);
                if (outStr.contains("</html>")) {
                    break;
                }
            }
    
            print.close();
            reader.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        int hostPort = Integer.parseInt(args[0]);
        int ePort = 80;
        
        HTTPClient client = new HTTPClient();
    
        client.request("localhost/inde.html", hostPort);
        System.out.println("--------------------------------------------------------------------------------------------");
        client.request("localhost/index.html", hostPort);
        System.out.println("--------------------------------------------------------------------------------------------");
        client.request("localhost", hostPort);
        System.out.println("--------------------------------------------------------------------------------------------");
        client.request("www.vulnweb.com", ePort);

    }
}
