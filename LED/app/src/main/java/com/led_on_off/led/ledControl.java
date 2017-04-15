package com.led_on_off.led;

import android.content.ActivityNotFoundException;
import android.os.Build;
import android.provider.AlarmClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class ledControl extends ActionBarActivity implements View.OnClickListener {

   // Button btnOn, btnOff, btnDis;
    ImageButton Discnt;
    Button appliance1_off, appliance1_on, appliance2_off, appliance2_on, appliance3_off, appliance3_on, speak;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_led_control);

        //call the widgets
        appliance1_off = (Button)findViewById(R.id.appliance1_off);
        appliance2_off = (Button)findViewById(R.id.appliance2_off);
        appliance3_off = (Button)findViewById(R.id.appliance3_off);
        appliance1_on = (Button)findViewById(R.id.appliance1_on);
        appliance2_on = (Button)findViewById(R.id.appliance2_on);
        appliance3_on = (Button)findViewById(R.id.appliance3_on);
        Discnt = (ImageButton)findViewById(R.id.discnt);
        speak = (Button)findViewById(R.id.speak);

        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
/*        appliance1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //stateChange(1);
            }
        });

        appliance2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //stateChange(2);
            }
        });

        appliance3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //stateChange(3);
            }
        });

        Discnt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        });*/

        appliance1_off.setOnClickListener(this);
        appliance1_on.setOnClickListener(this);
        appliance2_off.setOnClickListener(this);
        appliance2_on.setOnClickListener(this);
        appliance3_off.setOnClickListener(this);
        appliance3_on.setOnClickListener(this);
        Discnt.setOnClickListener(this);
        speak.setOnClickListener(this);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Hello");

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });


    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say proper commands");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(ledControl.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String inSpeech = res.get(0);
                recognition(inSpeech);
            }
        }
    }

    private void recognition(String text){
        Log.e("Speech","" + text);
        String[] speech = text.split(" ");
        if(text.contains("minute") || text.contains("hour")){
            /*speak(speech[speech.length-1]);
            String[] time = speech[speech.length-1].split(":");
            String hour = time[0];
            String minutes = time[1];*/
            /*Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(hour));
            i.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(minutes));
            startActivity(i);*/
            speak("Timer has been set");
        }
        else if(text.contains("turn on") && text.contains("all")) {
            stateChange(4,1);
            speak("Turning on all the lights");

        } else if(text.contains("turn off") && text.contains("all")) {
            stateChange(4,0);
            speak("Turning off all the lights");

        } else if(text.contains("turn off") && text.contains("projector")) {
            stateChange(2,0);
            speak("Turning off the light near projector");

        } else if(text.contains("turn on") && text.contains("projector")) {
            stateChange(2,1);
            speak("Turning on the light near projector");

        } else if(text.contains("turn off") && text.contains("curtains")) {
            stateChange(1,0);
            speak("Turning off the lights near curtains");

        } else if(text.contains("turn on") && text.contains("curtains")) {
            stateChange(1,1);
            speak("Turning on the lights near curtains");

        } else if(text.contains("turn off") && text.contains("printer")) {
            stateChange(3,0);
            speak("Turning off the lights near the 3-D printer");

        } else if(text.contains("turn on") && text.contains("printer")) {
            stateChange(3,1);
            speak("Turning on the lights near the 3-D printer");

        }

        /*if(text.contains("what time is it")){
            SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");//dd/MM/yyyy
            Date now = new Date();
            String[] strDate = sdfDate.format(now).split(":");
            if(strDate[1].contains("00"))
                strDate[1] = "o'clock";
            speak("The time is " + sdfDate.format(now));

        }*/


    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private void stateChange(int appliance, int code)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write((appliance+""+code+"").getBytes());
                Log.e("STATE", appliance+""+code+"");
                for (int i =0; i < (appliance+""+code+"").getBytes().length; i++){
                    Log.e("VALUES", (appliance+""+code+"").getBytes()[i]+"...");
                }
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

   /* private void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("1".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }*/

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.appliance1_off:
                stateChange(1,0);

                break;
            case R.id.appliance1_on:
                stateChange(1,1);

                break;
            case R.id.appliance2_off:
                stateChange(2,0);

                break;
            case R.id.appliance2_on:
                stateChange(2,1);

                break;
            case R.id.appliance3_off:
                stateChange(3,0);

                break;
            case R.id.appliance3_on:
                stateChange(3,1);

                break;
            case R.id.discnt:
                Disconnect();

                break;
            case R.id.speak:
                listen();

                break;
        }
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
