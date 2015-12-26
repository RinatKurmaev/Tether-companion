package rino.org.tethercompanion;

/*
 * This is the source code of Thether companion for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Rinat Kurmaev, 2015-2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

import fi.iki.elonen.NanoHTTPD;



public class MyHttpServer extends NanoHTTPD {
    private Context context;
    int batteryLevel;
    private Connectivity connManger;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };

    MyHttpServer(Context context)
    {
        super(8000);
        this.context=context;
    }


    @Override
    public Response serve(IHTTPSession session) {
        String answer = "<html><body>\n";
        context.getApplicationContext().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        connManger = new Connectivity();

        Map<String, String> parms = session.getParms();

        Log.d("params", String.valueOf(parms));
        answer += "<H1>Welcome to Tether companion</H2>";
        answer += "<H2>Battery level :" + batteryLevel + "% </H2>";
        answer += "<H2>Network type :" +String.valueOf(connManger.SubType(context)) + "</H2>";
        answer += "<H2>IP: " + getWifiApIpAddress() +"</H2>";
        if (connManger.isConnected(context))
        {
            answer +="<H2> Network connected </H2>";
        }
        else
        {
            answer +="<H2> Network disconnected </H2>";
        }

        return newFixedLengthResponse( answer + "</body></html>\n" );
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