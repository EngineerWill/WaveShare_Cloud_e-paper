package com.waveshare.cloud_esp32.communication;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

//---------------------------------------------------------
//  Socket Handler
//---------------------------------------------------------
public class SocketHandler extends Handler {
    public byte[] Data_Buffer;
    public boolean Read_Sign = false;
    public int Socket_error = 0;
    Log log;

    public SocketHandler() {
        super();
    }

    // Sends command cmd
    //-----------------------------------------------------
    public boolean send(byte[] senf_buff, int senf_length) {
        if(!BluetoothHelper.isconnected())return false;
        if (!BluetoothHelper.btThread.write(senf_buff, senf_length))
            return false; // Command sending is failed
        return true;      // Command is sent successful
    }
    public boolean get_flag(){return  Read_Sign;}

    public byte[] Read_Data_Buffer() {
        return Data_Buffer;      // Command is sent successful
    }

    //-------------------------------------------
    //  Handles socket message
    //-------------------------------------------
//    public void handleMessage(android.os.Message msg) {
//
//        if (msg.what == BluetoothHelper.BT_FATAL_ERROR) {
//            Socket_error = 1;//致命错误
//        } else if (msg.what == BluetoothHelper.BT_RECEIVE_DATA) {
//            // "Data is received" event
//            //-------------------------------------------------
//            // Convert data to string
//            //---------------------------------------------
//            String line = new String((byte[]) msg.obj, 0, msg.arg1);
//
//            Data_Buffer += line;
//            Read_Sign = true;
//            Socket_error = 0;
//            //Log.e("recv123:",msg.obj+"  len:"+((byte[]) msg.obj).length);
//            send(line.getBytes(),line.getBytes().length);
//            return;
//
//        }
//    }
    public void handleMessage(android.os.Message msg) {

        if (msg.what == BluetoothHelper.BT_FATAL_ERROR) {
            Socket_error = 1;//致命错误
        } else if (msg.what == BluetoothHelper.BT_RECEIVE_DATA) {
            // "Data is received" event
            //-------------------------------------------------
            // Convert data to string
            //---------------------------------------------
            //String line = new String((byte[]) msg.obj, 0, msg.arg1);
            Bundle data = msg.getData();
            //String line = data.getString("BT_RECEIVE_DATA");
            byte[] get=data.getByteArray("BT_RECEIVE_DATA");

            if(get != null){
                Data_Buffer = get;
                Read_Sign = true;
                Socket_error = 0;
                //Log.e("recv:",new String(get)+" ");
            }

            return;

        }
    }

}
