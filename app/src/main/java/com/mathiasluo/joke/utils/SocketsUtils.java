package com.mathiasluo.joke.utils;

import com.mathiasluo.joke.model.Joke;
import com.mathiasluo.joke.model.JokeEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mathiasluo on 16-5-3.
 */
public class SocketsUtils {

    public static final int SEND_SETUPS = 1;
    public static final int SEND_JOKE = 2;
    public static final int RECEIVE_JOKE = 3;
    public static final String HOST = "10.32.95.80";
    public static final int PORT = 54321;


    public static final boolean postJoke(String setup, String punchline) throws IOException {

        Socket socket = new Socket(HOST, PORT);
        socket.setSoTimeout(1000 * 5);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        writer.println(RECEIVE_JOKE);
        writer.println(setup);
        writer.println(punchline);
        writer.flush();
        String content = reader.readLine();

        reader.close();
        writer.close();
        socket.close();


        return content.equals("");
    }


    public static final JokeEntry getJokes() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        socket.setSoTimeout(1000 * 5);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        writer.println(SEND_SETUPS);
        writer.flush();

        String line;
        int size = Integer.parseInt(reader.readLine());
        List<String> setups = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            setups.add(line);
        }

        reader.close();
        writer.close();
        socket.close();
        return new JokeEntry(size, setups);
    }


    public static final Joke getJokeByIndex(int index) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        socket.setSoTimeout(1000 * 5);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        writer.println(SEND_JOKE);
        writer.println(index);
        writer.flush();

        String setup = reader.readLine();
        String punchline = reader.readLine();

        reader.close();
        writer.close();
        socket.close();
        return new Joke(setup, punchline);
    }


}
