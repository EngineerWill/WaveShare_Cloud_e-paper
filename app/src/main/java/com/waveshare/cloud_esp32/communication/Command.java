package com.waveshare.cloud_esp32.communication;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.waveshare.cloud_esp32.AppStartActivity;
import com.waveshare.cloud_esp32.R;


public class Command {
    Log log;

    public byte DEVICE_ID = '0';
    public byte HOST = '1';
    public byte WIFI_SSID = '2';
    public byte WIFI_PAWD = '3';
    public byte USER_PAWD = 'P';
    public byte CHECK = 'C';
    public byte LOCK = 'L';
    public byte NULOCK = 'N';
    public byte REBOOT = 'R';
    public byte SHUTDOWN = 'S';
    public byte SET = 's';

    public boolean Lock_flag=false;

    public static BluetoothDevice btDevice;
    public SocketHandler CMD_Handler = null;

    public Command(SocketHandler handler) {
        CMD_Handler = handler;
    }

    public byte[] reed() {
        long cnt = 0;
        SystemClock.sleep(10);
        while (!CMD_Handler.Read_Sign) {
            SystemClock.sleep(1);
            cnt++;
            if (cnt > 3000) {return null;}
        }
        byte[] raw_date = CMD_Handler.Data_Buffer;

        int x,y;
        for(x = 0; x<raw_date.length;x++)
        {
            if(raw_date[x]=='$')break;
        }
        boolean END_FLAG=false;
        for(y = 0; y<raw_date.length;y++)
        {
            if((raw_date[y]=='#')&&(y>x+1))
            {
                END_FLAG=true;
                break;
            }
        }
        if(x<y&& END_FLAG){
            byte[] ret=new byte[y-x];
            for(int temp=0;temp<(y-x-1);temp++)
            {
                ret[temp]=raw_date[x+1+temp];
            }
            ret[y-x-1]='\0';
            CMD_Handler.Read_Sign=false;
            return ret;
        }

        return null;
    }

    private boolean Safe_send(byte[] SEND, byte check) {

        for (int cnt = 0; cnt < 5; cnt++) {

            if(!CMD_Handler.send(SEND, SEND.length))return false;
            byte[] get = reed();
            if (get == null) continue;
            else if (get[0] == check) return true;
            else {
                //log.e("SEND"+SEND+"check"+check,"get"+get[0] );
            }
        }
        return false;
    }
    public boolean Send_Comman(byte CMD, String DATA) {
        byte[] cmd_byte = DATA.getBytes();
        byte check = 0x00;
        for (byte b=0;b<cmd_byte.length;b++) {
            check ^= cmd_byte[b];
        }
        check ^= CMD;
        byte[] SEND=new byte[5+cmd_byte.length];
        SEND[0]=';';
        SEND[1]=CMD;
        SEND[cmd_byte.length+2]='/';
        SEND[cmd_byte.length+3]=check;
        SEND[cmd_byte.length+4]='\0';
        for(int temp = 0;temp<cmd_byte.length;temp++)
        {
            SEND[temp+2]=cmd_byte[temp];
        }
        return Safe_send(SEND, check);

    }
    public boolean Send_Comman(byte CMD, byte [] DATA) {
        byte[] cmd_byte = DATA;
        byte check = 0x00;
        for (byte b : cmd_byte) {
            check ^= b;
        }
        check ^= CMD;
        byte[] SEND=new byte[5+cmd_byte.length];
        SEND[0]=';';
        SEND[1]=CMD;
        SEND[cmd_byte.length+2]='/';
        SEND[cmd_byte.length+3]=check;
        SEND[cmd_byte.length+4]='\0';
        for(int temp = 0;temp<cmd_byte.length;temp++)
        {
            SEND[temp+2]=cmd_byte[temp];
        }
        return Safe_send(SEND, check);
    }


}

