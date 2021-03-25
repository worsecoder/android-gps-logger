package com.example.myapplication;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {
    private static final int BUFFER_SIZE = 1024;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        Runnable networkTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(9000);
                    Log.d("GPS", "Waiting for connection:" + serverSocket.getInetAddress());
                    Socket socket = serverSocket.accept();
                    Log.d("GPS", "Accepted");
                    //while (socket.isConnected()){
                    DataInputStream din = new DataInputStream(socket.getInputStream());
                    DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                    //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //char[] data = new char[BUFFER_SIZE];
                    //int len = br.read(data);
                    //Log.d("GPS", "len = " + len);
                    //String str = String.valueOf(data, 0, len);
                    String str = (String)din.readUTF();
                    if (str != null) {
                        Log.d("GPS", "data arrived: " + str);
                    } else {
                        Log.d("GPS", "no data!");
                    }
                    dout.writeUTF("server response");
                    dout.flush();
                    //socket.close();
                    //serverSocket.close();
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
                Log.d("GPS", "Start to log GPS!!");
                new Thread(networkTask).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}