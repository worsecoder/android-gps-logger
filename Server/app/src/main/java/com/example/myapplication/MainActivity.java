package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    private double last_seen_lat = -1.0, last_seen_logi = -1.0;

    private final class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            last_seen_lat = location.getLatitude();
            last_seen_logi = location.getLongitude();
            Toast.makeText(MainActivity.this, "location:("+last_seen_lat+","+last_seen_logi, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String privder){
            Toast.makeText(MainActivity.this, "provider disabled", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){
            Toast.makeText(MainActivity.this, "status changed to " + status, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria ct = new Criteria();
        ct.setSpeedRequired(true);
        String provider = locationManager.getBestProvider(ct, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("GPS", "GPS not enabled");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0, new MyLocationListener());
        Toast.makeText(MainActivity.this, "LocationListener registered!", Toast.LENGTH_LONG).show();
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
                    while (true){
                        Log.d("GPS", "1");
                        String str = (String)din.readUTF();
                        if (str != null) {
                            Log.d("GPS", "request arrived: " + str);
                        } else {
                            Log.d("GPS", "no data!");
                        }
                        dout.writeUTF("lat:" + last_seen_lat + ",log:" + last_seen_logi);
                        dout.flush();
                    }
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