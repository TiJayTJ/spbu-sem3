package edu.spbu.server;

import java.io.*;
import java.net.Socket;

public class HTTPClient {

    public void request(String url, int port) {

        var fullUrl = url.strip().split("/", 2);
        String getInfo;
        if (fullUrl.length == 2) {
            getInfo = fullUrl[1];
        }
        else if (fullUrl.length == 1) {
            getInfo = "";
        } else{
            System.out.println("Incorrect input");
            return;
        }
        
        try(Socket clientSocket = new Socket(fullUrl[0], port)){
            OutputStream output = clientSocket.getOutputStream();
            InputStream input = clientSocket.getInputStream();
    
            PrintStream print = new PrintStream(output);
    
            print.printf("GET /%s HTTP/1.1%n", getInfo);
            print.printf("Host: %s:%s%n%n", url, port);
            print.println("");
            print.flush();
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    
            String outStr;
            while ((outStr = reader.readLine()) != null) {
                System.out.println(outStr);
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
    
        client.request("localhost/index.html", hostPort);
        client.request("localhost/index.html", hostPort);
        client.request("localhost", hostPort);
        client.request("openai.com/blog/chatgpt", ePort);
        client.request("openai.com/hfsd", ePort);
    }

}
