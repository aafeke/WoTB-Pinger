package com.kotdev.wotbping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    static TextView latency_eu;
    static TextView latency_na;
    static TextView latency_as;
    static TextView latency_ru;
    static TextView latency_ar;
    static TextView latency_la;

    final String addr_eu = "92.223.21.91";
    final String addr_ru = "92.223.34.34";
    final String addr_na = "92.223.56.112";
    final String addr_as = "92.223.29.27";
    final String addr_ar = "92.223.21.56";
    final String addr_la = "92.223.56.111";

    String response_eu = "-";
    String response_ru = "-";
    String response_na = "-";
    String response_as = "-";
    String response_ar = "-";
    String response_la = "-";

    Boolean isActivityDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latency_eu = (TextView) findViewById(R.id.latency_eu);
        latency_na = (TextView) findViewById(R.id.latency_na);
        latency_as = (TextView) findViewById(R.id.latency_as);
        latency_ru = (TextView) findViewById(R.id.latency_ru);
        latency_ar = (TextView) findViewById(R.id.latency_ar);
        latency_la = (TextView) findViewById(R.id.latency_la);

        Toast.makeText(getApplicationContext(),"Pinging To Servers..", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!isActivityDestroyed) {
                    response_eu = pinger(addr_eu);
                    response_ru = pinger(addr_ru);
                    response_na = pinger(addr_na);
                    response_as = pinger(addr_as);
                    response_ar = pinger(addr_ar);
                    response_la = pinger(addr_la);

                    System.out.println(response_eu);
                    System.out.println("<----------- DEBUG ----------->");

                    if (response_eu == "-" && response_ru == "-" && response_na == "-" &&
                            response_as == "-" && response_ar == "-" && response_la == "-") {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Check Your Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                        SystemClock.sleep(7000);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            latency_eu.setText(response_eu);
                            latency_ru.setText(response_ru);
                            latency_na.setText(response_na);
                            latency_as.setText(response_as);
                            latency_ar.setText(response_ar);
                            latency_la.setText(response_la);

                        }
                    });

                    System.out.println("End of Thread");
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        isActivityDestroyed = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActivityDestroyed = true;
    }

    public static String pinger(String address) {

            String line;
            String output = null;

            try {
                Process p = Runtime.getRuntime().exec
                        ("ping -c 1 "+ address);
                BufferedReader input =
                        new BufferedReader
                                (new InputStreamReader(p.getInputStream()));

                while ((line = input.readLine()) != null) {
                    output = output + line + "\n";
                }

                input.close();
            }
            catch (Exception err) {
                err.printStackTrace();
                return "-";
            }

            //System.out.println(output);
            String sub;
            try {
                int start = output.lastIndexOf("/mdev = ");
                int end = output.indexOf("ms", start);
                sub = output.substring(start, end); }

            catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
                System.out.println("-----Exception------");
                
                return "-";
            }

            catch (NullPointerException e) {
                return "-";
            }

            Scanner sc = new Scanner(sub);
            sc.useDelimiter("/");
            sc.next();
            String latency = sc.next();
            sc.close();

            //Round to Nearest Integer
            float f = Float.valueOf(latency);

            latency = Integer.toString( ((int) Math.round(f) ));
            latency = latency.concat("ms");

            return latency;
    }
}