package com.example.denis.argusnew;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by set-user on 23.04.2015.
 */
public class DataTreatment extends AsyncTask<Void, Integer, Void> {

    private MapsActivity myActivity = null;

    private InputStream inStream = null;
    private OutputStream outStream = null;
    private String currentLatString, currentLonString="";
    private Socket socket=null;


    private String outmessage;
    private long prevTime=0;

    CompareHelper myCompare;


    public DataTreatment(Context context){
        myActivity = (MapsActivity)context;
    }

    private class CompareHelper {

        public void progressUpdate(Integer... message) {
            for (Integer ad:message){
                myActivity.compare(ad);
            }
        }
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        myCompare = new CompareHelper();
        prevTime=0;
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            InetAddress serverAddr = InetAddress.getByName("82.137.161.2");
            socket = new Socket(serverAddr, 65323);

            for (int i = 0; i < myActivity.dataLoc.size(); i++) {
                LocationData LD = myActivity.dataLoc.get(i);
                String latDtoString = Double.toString(LD.getLatitude());
                String lonDtoString = Double.toString(LD.getLongitude());
                String inputMessage = null;
                inputMessage = "ff " + latDtoString + " " + lonDtoString;
                byte[] msgBuffer = new byte[1024];
                msgBuffer = inputMessage.getBytes();
                try {
                    outStream = socket.getOutputStream();
                    outStream.write(msgBuffer);
                    outStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SystemClock.sleep(1000);
            }

        } catch (Exception e) {
            int z = 0;
        }
        while(true) {
            if(socket==null || socket.isClosed()) return null;


                if (System.currentTimeMillis() - prevTime > 1000) {
                    try {
                        currentLatString = Double.toString(MyLocationListener.imHere.getLatitude());
                        currentLonString = Double.toString(MyLocationListener.imHere.getLongitude());

                        String inputMessage=null;
                        inputMessage = "aa "+currentLatString+" "+currentLonString;
                        byte[] msgBuffer= new byte[1024];
                         msgBuffer = inputMessage.getBytes();
                        try {
                            outStream = socket.getOutputStream();
                            outStream.write(msgBuffer);
                            outStream.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        prevTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        Log.e("Main send", e.getMessage());
                    }

                    int outmessageInt=0;
                    byte[] msgOutBuffer = new byte[1];
                    try {
                        inStream = socket.getInputStream();
                        int btread = inStream.read(msgOutBuffer); //останавливается здесь, все время пытается прочитать из буфера
                        if (btread > 0) {
                            outmessage = new String(msgOutBuffer);//Записываем полученную строку в outmessage. Сравниваем эту строку с 0 или 1. И в главной потоке меняем текст вью
                            outmessageInt = Integer.parseInt(outmessage);

                    }
                } catch (Exception e) {
                    int z=0;
                }

                publishProgress(outmessageInt);
                }
            }

    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        myCompare.progressUpdate(values);
    }

    public void disconnect() {
        if (socket==null) return;
        if (socket.isClosed()) return;
        try {
            socket.close();
        } catch (Exception e){
            Log.e("socket_close", e.getMessage());
        }

    }
}
