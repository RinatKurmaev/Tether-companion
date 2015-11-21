package rino.org.tethercompanion;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    ToggleButton servButton;
    MyHttpServer ws;
    boolean state = false;
    TextView hint;
    Intent WssIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ws = new MyHttpServer(this);
        servButton = (ToggleButton) findViewById(R.id.toggleButtonServer);
        hint = (TextView) findViewById(R.id.textViewHint);
        WssIntent = new Intent(MainActivity.this, WebServerService.class);
        servButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!state ) {
                    state = true;

                        startService(WssIntent);

                    //ws.start();

                    hint.setText("Started on: " + getWifiApIpAddress() + ":8000");
                } else {
                    state = false;
                    stopService(WssIntent);
                    //ws.stop();
                    hint.setText("Disabled");
                }
            }
        });

    }

    public String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            Log.d("IP ADRESS", inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("ex", ex.toString());
        }
        return null;
    }

}


