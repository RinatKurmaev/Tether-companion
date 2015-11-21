package rino.org.tethercompanion;

/**
 * Created by rino on 06/11/15.
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
   /*long prevBattLevelChange, currentBattLevelChange;по хорошему надо считать за сколько секунд сжирается один процент зарядки,
    но оно странно себя ведет, поэтому я отключил этот функционал, пока
*/
    private Connectivity connManger;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            Log.d("battery level", String.valueOf(batteryLevel));
            //prevBattLevelChange = currentBattLevelChange;
            //currentBattLevelChange = System.currentTimeMillis();
        }
    };

    MyHttpServer(Context context)
    {
        super(8000);
        this.context=context;
        //prevBattLevelChange = System.currentTimeMillis();
    }

    /*public void start() {
        /*context.getApplicationContext().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        connManger = new Connectivity();
    }*/


    @Override
    public Response serve(IHTTPSession session) {
        String answer = "<html><body>\n";
        context.getApplicationContext().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        connManger = new Connectivity();
        //Log.d("Request", "another one");

        Map<String, String> parms = session.getParms();

        Log.d("params", String.valueOf(parms));
        answer += "<H1>Welcome to Tether companion</H2>";
        answer += "<H2>Battery level :" + batteryLevel + "% </H2>";
        //answer += "<H2>delta time between 1% change " +String.valueOf ((currentBattLevelChange - prevBattLevelChange) / 1000) + "</H2>";
        //Log.d ("WTF", connManger.SubType(context));
        answer += "<H2>Network type :" +String.valueOf(connManger.SubType(context)) + "</H2>";
        answer += "<H2>IP: " + getWifiApIpAddress() +"</H2>";
        /*answer += "<p><input type='button' name='prevTrack' value='prev'> "
                + "<input type='button' name='pause' value='Pause'> " +
                "<input type='button' name='nextTrack' value='next'></p>\n";*/

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
                            //Log.d("IP ADRESS", inetAddress.getHostAddress());
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