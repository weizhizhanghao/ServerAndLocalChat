package com.example.think.innerconnectionapplication;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerActivity extends Activity {

    private static TextView ipText;
    private static TextView serverContentText;

    private String serverIp = "";

    public static Handler serHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            if (msg.what == 1){
                serverContentText.append("serverContent:" + msg.obj + "\n");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        ipText = (TextView) findViewById(R.id.ip_address);
        serverContentText = (TextView) findViewById(R.id.server_content);

        serverIp = getIpAddress();
        ipText.setText(serverIp);

        new Thread(){
            @Override
            public void run() {
                OutputStream output;
                String serverContent = "hello haha";
                try {
                    ServerSocket serverSocket = new ServerSocket(8600);

                    while (true){
                        Message message = new Message();
                        message.what = 1;
                        try{

                            Socket socket = serverSocket.accept();

                            output = socket.getOutputStream();
                            output.write(serverContent.getBytes("utf-8"));
                            output.flush();
                            socket.shutdownOutput();

                            BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            String result = "";
                            String buffer;

                            while ((buffer = bff.readLine())!= null){
                                result = result + buffer;
                            }
                            message.obj = result.toString();
                            serHandler.sendMessage(message);
                            bff.close();
                            output.close();
                            socket.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.start();//8600
    }

    private String getIpAddress(){
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        if (ipAddress == 0) return null;
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }
}
