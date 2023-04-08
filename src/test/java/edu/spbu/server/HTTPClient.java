package edu.spbu.server;

import java.io.*;
import java.net.Socket;

public class HTTPClient {

    public void request(String url, int port) throws IOException {

        String[] fullUrl = url.strip().split("/", 2);
        String getInfo = fullUrl[1];

        Socket clientSocket = new Socket(url, port);

        OutputStream output = clientSocket.getOutputStream();
        InputStream input = clientSocket.getInputStream();

        PrintStream print = new PrintStream(output);
        OutputStreamWriter writer = new OutputStreamWriter(output);

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
    }

    public static void main(String[] args) throws IOException {
        HTTPClient client = new HTTPClient();
        client.request("localhost/exercise1.html", 8000);
    }

}
