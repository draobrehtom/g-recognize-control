package com.example.draobrehtom.gesturerecognizecontrol;
import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, Void> {

    //define callback interface
    interface MyCallbackInterface {
        void tcpHandler(String result);
    }

    final MyCallbackInterface callback;

    String dstAddress;
    int dstPort;
    String response = "";
    String dstQuery = "";

    Client(String addr, int port, String query, MyCallbackInterface callback){
        dstAddress = addr;
        dstPort = port;
        dstQuery = query;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            // send()
            socket.getOutputStream().write(dstQuery.getBytes());
            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

/*
 * inputStream.read() will block if no data return
 */
            while ((bytesRead = inputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        callback.tcpHandler(response);
        super.onPostExecute(result);
    }

}
