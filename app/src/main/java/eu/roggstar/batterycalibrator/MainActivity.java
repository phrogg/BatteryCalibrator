package eu.roggstar.batterycalibrator;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView txt_percentage, txt_isCharging,txt_3t,txt_2t,txt_1t;
    private Button but_calibrate;
    private boolean isReady = false;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            txt_percentage.setText(String.valueOf(level) + "%");
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            /*
            if(level < 90){
                txt_percentage.setTextColor(Color.CYAN);
                txt_2t.setTextColor(Color.GREEN);txt_2t.setText("✔");
                txt_3t.setTextColor(Color.RED);txt_3t.setText("✘");
                but_calibrate.setBackgroundColor(Color.CYAN);
            } else */
            if(isCharging){
                txt_isCharging.setText(getResources().getString(R.string.text_indicator1));
                txt_1t.setTextColor(Color.GREEN);txt_1t.setText("✔");
            } else {
                txt_isCharging.setText(getResources().getString(R.string.text_indicator0));
                txt_1t.setTextColor(Color.RED);txt_1t.setText("✘");
            }

            if(level == 100){
                txt_percentage.setTextColor(Color.GREEN);
                txt_1t.setTextColor(Color.GREEN);txt_1t.setText("✔");
                txt_2t.setTextColor(Color.GREEN);txt_2t.setText("✔");
                isReady = true;
                but_calibrate.setTextColor(Color.GREEN);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect
        but_calibrate = findViewById(R.id.but_calibrate);
        txt_percentage = findViewById(R.id.txt_percentage);
        txt_isCharging = findViewById(R.id.txt_isCharging);
        txt_3t = findViewById(R.id.txt_3t);
        txt_2t = findViewById(R.id.txt_2t);
        txt_1t = findViewById(R.id.txt_1t);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //setTitle
        setTitle("NoAd Battery Calibrator");

        //Button Onclicklistener
        but_calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReady) {
                    execu("mv /data/system/batterystats.bin /data/system/batterystats.bin.old");
                    execu("rm /data/system/batterystats*.bin");
                    txt_3t.setTextColor(Color.GREEN);txt_3t.setText("✔");
                } else {
                    dialog(false);
                }
            }
        });

        SharedPreferences mPref = getSharedPreferences("init",0);
        if(mPref.getBoolean("init",true)){
            startActivity(new Intent(MainActivity.this,WizardActivity.class));
            mPref.edit().putBoolean("init",false).commit();
        }

        //Startup
        txt_2t.setTextColor(Color.RED);txt_2t.setText("✘");
        txt_1t.setTextColor(Color.RED);txt_1t.setText("✘");
        txt_3t.setTextColor(Color.RED);txt_3t.setText("✘");
    }

    void dialog(boolean oneButton) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        execu("mv /data/system/batterystats.bin /data/system/batterystats.bin.old");
                        execu("rm /data/system/batterystats*.bin");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(MainActivity.this, "Good Decision :)", Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEUTRAL:
                          finish();
                    break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        if(!oneButton){
            builder.setMessage("The battery is below 100% you will not get the best results!")
                    .setPositiveButton("I know what im doing!", dialogClickListener)
                    .setNegativeButton("I think about that again...", dialogClickListener).show();
        } else {
            builder.setMessage("Let the battery deplete to 0% for the best results!")
                    .setNeutralButton("Sure thing!", dialogClickListener).show();
        }
    }

    void execu(String com){
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes( com+"\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            //su.waitFor();
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        dialog(true);
        Toast.makeText(MainActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
    }


    //My Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.developer:
                startActivity(new Intent(MainActivity.this, VersionActivity.class));
                return true;
            case R.id.aboutme:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.phrogg.de"));
                startActivity(browserIntent);
                return true;
            case R.id.revert:
                execu("mv /data/system/batterystats.bin.old /data/system/batterystats.bin");
                return true;
        }
        return false;
    }
}
