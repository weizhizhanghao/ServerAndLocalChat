package com.example.think.innerconnectionapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainActivity extends Activity {

    TextView contentShowText;
    EditText contentEdit;
    Button sendButton;


    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                contentShowText.append("server:" + msg.obj + "\n");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentShowText = (TextView) findViewById(R.id.results);
        contentEdit = (EditText) findViewById(R.id.content_edit);
        sendButton = (Button) findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputContent = contentEdit.getText().toString();
                contentShowText.append("serverContent:" + inputContent + "\n");
                new MyThread(inputContent).start();
            }
        });






    }

    public class MyThread extends Thread{
        public String contentResult;
        public MyThread(String str){
            contentResult = str;
        }

        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;

            try {
                Socket socket = new Socket();
                //ip,port,timeout
                socket.connect(new InetSocketAddress("10.1.2.183", 8600), 3000);

                OutputStream out = socket.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //向服务器发送消息
                out.write(contentResult.getBytes("utf-8"));
                out.flush();

                //读取服务器发来的消息
                String result = "";
                String buffer = "";
                while ((buffer = bufferedReader.readLine()) != null){
                    result = result + buffer;
                }

                message.obj = result.toString();
                mHandler.sendMessage(message);

                bufferedReader.close();
                out.close();
                socket.close();

            }catch (SocketTimeoutException aa){
                message.obj = "服务器连接失败";
                mHandler.sendMessage(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
