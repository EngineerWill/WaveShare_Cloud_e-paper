package com.waveshare.cloud_esp32;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.waveshare.cloud_esp32.communication.BluetoothHelper;
import com.waveshare.cloud_esp32.image_processing.EPaperDisplay;

import java.io.IOException;

/**
 * <h1>Upload activity</h1>
 * The activity shows the progress of image uploading into display
 * of the selected bluetooth device.
 *
 * @author  Waveshare team
 * @version 1.0
 * @since   8/20/2018
 */
//
//public class UploadActivity extends AppCompatActivity
//{
//    private TextView textView;
//    private SocketHandler handler;
//
//    Log log;
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        super.onCreate(savedInstanceState);
//         /*setContentView(R.layout.upload_activity);
//        getSupportActionBar().setTitle(R.string.dlg_send);
//
//
//         */
//        // View
//        //--------------------------------------
//        textView = findViewById(R.id.upload_text);
//        textView.setText("Uploading: 0%");
//
//        // Bluetooth helper and its handler
//        //--------------------------------------
//
//        BluetoothHelper.initialize(AppStartActivity.btDevice, handler = new SocketHandler());
//
//        BluetoothHelper.connect();
//        byte[] send_buff = {(byte)'1',(byte)'2',(byte)'3',(byte)'4',(byte)'5'};
//
//        handler.send(send_buff,5);
//        //handler.Monitor_data_reception();
//    }
//
//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//
//        // Bluetooth socket connection
//        //--------------------------------------
//        if (!BluetoothHelper.connect())
//        {
//            setResult(RESULT_CANCELED);//操作取消
//            finish();
//        }
//    }
//
//    @Override
//    protected void onPause()
//    {
//        BluetoothHelper.close();
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy()
//    {
//        BluetoothHelper.close();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onBackPressed()
//    {
//        BluetoothHelper.close();
//        setResult(RESULT_OK);
//        finish();
//    }
//
//    public void onCancel(View view)
//    {
//        onBackPressed();
//    }
//}
//class Bluetooth_device
//{
//    SocketHandler handler;
//    public Bluetooth_device() throws IOException {
//        BluetoothHelper.initialize(AppStartActivity.btDevice, handler = new SocketHandler());
//        BluetoothHelper.connect();//设备连接
//
//        byte[] send_buff = {(byte)'1',(byte)'2',(byte)'3',(byte)'4',(byte)'5'};//发送数据
//        handler.send(send_buff,5);
//    }
//
//    public void Device_quit(){
//        BluetoothHelper.close();
//    }
//
//    public void Device_Send(byte[] buff, int length){
//        handler.send(buff, length);
//    }
//
//    public void Device_Send(String buff){
//        handler.send(buff.getBytes(), buff.length());
//    }
//    public boolean Get_Data_Flag(){
//        return  handler.Read_Sign;
//    }
//    public String Get_Data_Buff(){
//        return  handler.Read_Data_Buffer();
//    }
//}
//
//
//
//
//
////---------------------------------------------------------
////  Socket Handler
////---------------------------------------------------------
//class SocketHandler extends Handler {
//    public String Data_Buffer;
//    public boolean Read_Sign = false;
//    public int Socket_error = 0;
//    Log log;
//
//    public SocketHandler() {
//        super();
//    }
//
//    // Sends command cmd
//    //-----------------------------------------------------
//    public boolean send(byte[] senf_buff, int senf_length) {
//        if (!BluetoothHelper.btThread.write(senf_buff, senf_length))
//            return false; // Command sending is failed
//        return true;      // Command is sent successful
//    }
//
//
//    public String Read_Data_Buffer() {
//        return Data_Buffer;      // Command is sent successful
//    }
//
//    //-------------------------------------------
//    //  Handles socket message
//    //-------------------------------------------
//    public void handleMessage(android.os.Message msg) {
//
//        // log.e("xx handleMessage"," ");
//        // "Fatal error" event
//        //-------------------------------------------------
//        if (msg.what == BluetoothHelper.BT_FATAL_ERROR) {
//            Socket_error = 1;//致命错误
//        } else if (msg.what == BluetoothHelper.BT_RECEIVE_DATA) {
//            // "Data is received" event
//            //-------------------------------------------------
//            // Convert data to string
//            //---------------------------------------------
//            String line = new String((byte[]) msg.obj, 0, msg.arg1);
//
//            log.e(" Receive bytes", "--> " + line);
//            // If esp32 is ready for new command
//            //---------------------------------------------
//            //if (line.contains("Ok!")) {
//                Data_Buffer = line;
//                Read_Sign = true;
//                Socket_error = 0;
//                // Try to handle received data.
//                // If it's failed, restart the uploading
//                //-----------------------------------------
//                return;
//            //}
//            // Exit is the message is unknown
//            //---------------------------------------------
//           /*
//            else if (!line.contains("Error!")) {
//                Socket_error = 2;//退出消息未知
//                return;
//            }
//            */
//            // Otherwise restart the uploading
//            //-----------------------------------------
//            /*
//            BluetoothHelper.close();
//            BluetoothHelper.connect();
//             */
//        }
//    }
//}
//
