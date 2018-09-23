package com.example.aydan.yorkuhacks;

import android.os.Vibrator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.content.Intent;
import android.util.Log;

import android.Manifest;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/*
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate.Status;
import com.google.android.gms.nearby.connection.Strategy;
*/

public class MainActivity extends Activity{
    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    public boolean startConfirm = false;
    public Boolean sensorToggled = false;

    TextView updateDefenseText;

    public static int START_WINDOW = 1000;
    public static int TIMING_WINDOW = 3000;
    public static final Random rand = new Random();

    public Timer timer = new Timer();

    public TimerTask timerTaskLoad;
    public TimerTask timerTaskEnd;

    public int playState = -1;

    public Intent sensorIntent;

    Vibrator v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("result"));

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //initializes vibration

        sensorToggled = true;
        sensorIntent = new Intent(MainActivity.this, SensorActivity.class);
        startService(sensorIntent);

//        createGesture();
    }

    //triggered by singleplayer button
    public void startSingleplayer(View view){
        startConfirm = true; //allows game to begin
        createGesture();     //begins loop of gesture generation

        //changing views
        setContentView(R.layout.game_screen);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        if(!sensorToggled && startConfirm){
            sensorToggled = true;
        }
        return true;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int result = intent.getIntExtra("result", 1);

            //Log.d("receiver", "Got message: " + Integer.toString(result));
            if(sensorToggled && startConfirm) {
                switch (result) {
                    case 1:
                        Log.d("MainActivity", "LEFT");
                        break;
                    case 2:
                        Log.d("MainActivity", "UP");
                        break;
                    case 3:
                        Log.d("MainActivity", "RIGHT");
                        break;
                    case 4:
                        Log.d("MainActivity", "DOWN");
                        break;
                    case 0:
                        Log.d("MainActivity", "RETURN");
                        break;
                }

                //displays results to player
                if(playState == result){
                    Log.d("MainActivity", "HIT!");
                    Toast.makeText(getApplicationContext(), ("YOU PARRIED!"), Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("MainActivity", "WRONG MOTION!");
                    Toast.makeText(getApplicationContext(), ("YOU WERE HIT!"), Toast.LENGTH_SHORT).show();

                }
                sensorToggled = false;

                createGesture();
            }
        }
    };

    public void createGesture(){
        playState = -1;
        timer.cancel();
        timer = new Timer();

        timerTaskLoad = new TimerTask(){
            @Override
            public void run() {
                v.vibrate(100); //vibrate each new "turn"
                playState = rand.nextInt(4)+1;

                runOnUiThread(new Runnable() { //required to update the ui
                    @Override
                    public void run() {
                        //stuff that updates ui
                        //changing text in game screen
                        updateDefenseText = findViewById(R.id.defenseDirection);
                        if (playState == 3){
                            updateDefenseText.setText("→");
                        }
                        else if (playState == 2){
                            updateDefenseText.setText("↑");
                        }
                        else if (playState == 1){
                            updateDefenseText.setText("←");
                        }
                        else if (playState == 4){
                            updateDefenseText.setText("↓");
                        }
                    }
                });

                switch(playState) {
                    case 1:
                        Log.d("MainActivity", "SWIPE LEFT");
                        break;
                    case 2:
                        Log.d("MainActivity", "SWIPE UP");
                        break;
                    case 3:
                        Log.d("MainActivity", "SWIPE RIGHT");
                        break;
                    case 4:
                        Log.d("MainActivity", "SWIPE DOWN");
                        break;
                    default:
                        Log.d("MainActivity", "HOW DOES THIS EVEN HAPPEN");
                        break;
                }

            }

        };

        timerTaskEnd = new TimerTask(){
            @Override
            public void run(){
                playState = -1;
                Log.d("MainActivity", "MISS!");
                sensorToggled = false;
                createGesture();
            }

        };

        timer.schedule(timerTaskLoad, START_WINDOW);
        timer.schedule(timerTaskEnd, TIMING_WINDOW);

    }

}