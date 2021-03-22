package com.example.eyepedia;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.Inflater;


public class Readurl {
    static ArrayList<String> urls=new ArrayList<String>(); //to read each line
    TextView t;

    public static void main(String[] args) {
        try {
            URL url = new URL("http://www.google.com:80/");
            // read text returned by server
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60000); // timing out in a minute

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            //t=(TextView)findViewById(R.id.TextView1); // ideally do this in onCreate()
            String line;
            while ((line = in.readLine()) != null) {
                urls.add(line);
            }
            in.close();
            System.out.println(urls.get(0));
            Log.i("url:" , urls.get(0));
        }
        catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }
}