package com.waveshare.cloud_esp32;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.waveshare.cloud_esp32.communication.BluetoothHelper;
import com.waveshare.cloud_esp32.communication.Command;
import com.waveshare.cloud_esp32.communication.MySP;
import com.waveshare.cloud_esp32.communication.PermissionHelper;
import com.waveshare.cloud_esp32.communication.SocketHandler;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AppStartActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static final int REQ_BLUETOOTH_CONNECTION = 2;
    public static final int REQ_OPEN_FILE = 3;
    public static final int REQ_DISPLAY_SELECTION = 4;
    public static final int REQ_PALETTE_SELECTION = 5;
    public static final int REQ_UPLOADING = 6;
    public static final int REQ_SETTING = 7;

    public static String fileName;
    public static String filePath;

    public String Sendmgs;
    public String getmgs;


    public TextView textBlue;
    public TextView textSend;
    public TextView textAddr;

    public EditText editText1;

    public EditText ssid_editText;
    public EditText pswd_editText;
    public EditText host_editText;
    public EditText id_editText;
    public EditText dpswd_editText;
    public EditText warnvol_editText;
    public EditText dip_editText;
    public EditText gwip_editText;
    public EditText netmask_editText;


    public CheckBox wpcb;
    public CheckBox dpcb;

    public Switch d_lock;
    public Switch dhcp_sw;

    public ImageView pictFile;
    public ImageView pictFilt;


    public static Bitmap originalImage;
    public static Bitmap indTableImage;


    public static BluetoothDevice btDevice;
    public SocketHandler handler;
    public SharedPreferences mySP;
    public MySP SP;

    static boolean connect_init = false;
    boolean bluetooth_reless = false;

    private AlertDialog alertDialog2;

    public byte DEVICE_ID = '0';
    public byte HOST = '1';
    public byte WIFI_SSID = '2';
    public byte WIFI_PAWD = '3';
    public byte STATIC_IP = '4';
    public byte USER_PAWD = 'P';
    public byte CHECK = 'C';
    public byte LOCK = 'L';
    public byte NULOCK = 'N';
    public byte REBOOT = 'R';
    public byte SHUTDOWN = 'S';
    public byte SET = 's';
    public byte LOPW = 'V';

    public String[] KEY = {"ID", "SSID", "PSWD", "HOST", "DPSWD", "LOCK", "DHCP", "IP", "GW", "NETMASK"};

    public String id;
    public String wifi_ssid;
    public String wifi_pasword;
    public String host_ip;
    public String devicepswd;
    public String devicelock;
    public String clean_flag;
    public String warn_voltage;
    public boolean device_lock;


    public boolean dhck_onf;
    public String dhckonf_s;
    public String device_ip;
    public String gw_ip;
    public String netmask;

    public int Choose_setting;
    public String[] Config_list;

    public String putin_password;
    public boolean Lock_flag;
    public boolean BREAK_flag;

    public void checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }

            if (!isGranted) {
                this.requestPermissions(
                        new String[]{Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                        },
                        102);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_activity);
        fileName = null;
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        textBlue = findViewById(R.id.text_blue);
        originalImage = null;
        indTableImage = null;
        mySP = super.getSharedPreferences("test", MODE_PRIVATE);
        ssid_editText = (EditText) findViewById(R.id.wifi_ssid);
        pswd_editText = (EditText) findViewById(R.id.wifipswd);
        host_editText = (EditText) findViewById(R.id.host_ip);
        id_editText = (EditText) findViewById(R.id.device_id);
        dpswd_editText = (EditText) findViewById(R.id.devicepswd);
        d_lock = (Switch) findViewById(R.id.devicelock);
        warnvol_editText = (EditText) findViewById(R.id.low_power);
        dhcp_sw = (Switch) findViewById(R.id.dhcp);
        dip_editText = (EditText) findViewById(R.id.device_ip);
        gwip_editText = (EditText) findViewById(R.id.gateway_ip);
        netmask_editText = (EditText) findViewById(R.id.netmask);
        warnvol_editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        id = mySP.getString("ID", "");
        wifi_ssid = mySP.getString("SSID", "");
        wifi_pasword = mySP.getString("PSWD", "");
        host_ip = mySP.getString("HOST", "");
        devicepswd = mySP.getString("DPSWD", "");
        device_lock = mySP.getBoolean("LOCK", false);
        warn_voltage = mySP.getString("LOPW", "");
        device_ip = mySP.getString("DIP", "");
        gw_ip = mySP.getString("GWIP", "");
        netmask = mySP.getString("NETMASK", "");
        dhck_onf = mySP.getBoolean("DHCP", true);
        id_editText.setText(id);
        ssid_editText.setText(wifi_ssid);
        pswd_editText.setText(wifi_pasword);
        host_editText.setText(host_ip);
        dpswd_editText.setText(devicepswd);
        d_lock.setChecked(device_lock);
        warnvol_editText.setText(warn_voltage);
        dip_editText.setText(device_ip);
        gwip_editText.setText(gw_ip);
        netmask_editText.setText(netmask);
        dhcp_sw.setChecked(dhck_onf);
        String str1 = AppStartActivity.this.getString(R.string.config1);
        String str2 = AppStartActivity.this.getString(R.string.config2);
        String str3 = AppStartActivity.this.getString(R.string.config3);
        String str4 = AppStartActivity.this.getString(R.string.config4);
        Config_list = new String[]{str1, str2, str3, str4};
        wpcb = (CheckBox) findViewById(R.id.WPcheckBox);
        dpcb = (CheckBox) findViewById(R.id.DPcheckBox);
        wpcb.setOnCheckedChangeListener(this);
        dpcb.setOnCheckedChangeListener(this);
        checkPermission();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        EditText Change_EditText = null;
        if (compoundButton.getId() == R.id.WPcheckBox) {
            Change_EditText = pswd_editText;
        } else if (compoundButton.getId() == R.id.DPcheckBox) {
            Change_EditText = dpswd_editText;
        }
        if ((Change_EditText != null)) {
            if (b) {
                Change_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                Change_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

        Change_EditText.setSelection(Change_EditText.getText().length());
    }

    public void onScan(View view) {
        startActivityForResult(
                new Intent(this, ScanningActivity.class),
                REQ_BLUETOOTH_CONNECTION);
    }

    public void onClean(View view) {
        if (btDevice == null) PermissionHelper.note(this, R.string.no_blue);
        else if (bluetooth_reless) PermissionHelper.note(this, R.string.Transmitting_data);
        else {
            bluetooth_reless = true;
            Lock_flag = true;
            BREAK_flag = false;
            putin_password = "";

            Cheack_lock();

            Upload_Setting(true);
        }
    }

    public void onUpload(View view) {
        if (btDevice == null) PermissionHelper.note(this, R.string.no_blue);
        else if (bluetooth_reless) PermissionHelper.note(this, R.string.Transmitting_data);
        else {
            bluetooth_reless = true;
            Lock_flag = true;
            BREAK_flag = false;
            putin_password = "";

            id = id_editText.getText().toString();
            wifi_ssid = ssid_editText.getText().toString();
            wifi_pasword = pswd_editText.getText().toString();
            host_ip = host_editText.getText().toString();
            devicepswd = dpswd_editText.getText().toString();
            devicelock = d_lock.isChecked() ? "1" : "0";
            device_lock = d_lock.isChecked();
            warn_voltage = warnvol_editText.getText().toString();
            dhck_onf = dhcp_sw.isChecked();
            dhckonf_s = dhcp_sw.isChecked() ? "1" : "0";
            device_ip = dip_editText.getText().toString();
            gw_ip = gwip_editText.getText().toString();
            netmask = netmask_editText.getText().toString();
            Cheack_lock();
            Upload_Setting(false);
        }


    }

    public void onLoad(View view) {
        final Thread thread1 = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AppStartActivity.this);
                alertBuilder.setTitle(R.string.config_list);
                final int[] choose = {0};
                alertBuilder.setSingleChoiceItems(Config_list, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choose[0] = i;
                    }

                });
                alertBuilder.setNegativeButton(R.string.btn_cncl, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choose[0] = 0xff;
                        alertDialog2.dismiss();
                    }
                });
                alertBuilder.setPositiveButton(R.string.btn_okey, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        id = mySP.getString(Config_list[choose[0]] + "ID", "");
                        wifi_ssid = mySP.getString(Config_list[choose[0]] + "SSID", "");
                        wifi_pasword = mySP.getString(Config_list[choose[0]] + "PSWD", "");
                        host_ip = mySP.getString(Config_list[choose[0]] + "HOST", "");
                        devicepswd = mySP.getString(Config_list[choose[0]] + "DPSWD", "");
                        device_lock = mySP.getBoolean(Config_list[choose[0]] + "LOCK", false);
                        warn_voltage = mySP.getString(Config_list[choose[0]] + "LOPW", "");

                        dhck_onf = mySP.getBoolean(Config_list[choose[0]] + "DHCP", true);
                        device_ip = mySP.getString(Config_list[choose[0]] + "DIP", "");
                        gw_ip = mySP.getString(Config_list[choose[0]] + "GWIP", "");
                        netmask = mySP.getString(Config_list[choose[0]] + "NETMASK", "");

                        id_editText.setText(id);
                        ssid_editText.setText(wifi_ssid);
                        pswd_editText.setText(wifi_pasword);
                        host_editText.setText(host_ip);
                        dpswd_editText.setText(devicepswd);
                        d_lock.setChecked(device_lock);
                        warnvol_editText.setText(warn_voltage);

                        dhcp_sw.setChecked(dhck_onf);
                        dip_editText.setText(device_ip);
                        gwip_editText.setText(gw_ip);
                        netmask_editText.setText(netmask);
                        alertDialog2.dismiss();
                    }
                });

                alertDialog2 = alertBuilder.create();
                alertDialog2.show();

                Looper.loop();
            }
        });
        thread1.start();
    }

    public void onSave(View view) {

        final Thread thread1 = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                String str1 = AppStartActivity.this.getString(R.string.config1);
                String str2 = AppStartActivity.this.getString(R.string.config2);
                String str3 = AppStartActivity.this.getString(R.string.config3);
                String str4 = AppStartActivity.this.getString(R.string.config4);
                final String[] Config_list = {str1, str2, str3, str4};

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AppStartActivity.this);
                alertBuilder.setTitle(R.string.config_list);
                alertBuilder.setSingleChoiceItems(Config_list, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Choose_setting = i;
                    }

                });
                alertBuilder.setNegativeButton(R.string.btn_cncl, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog2.dismiss();
                        Choose_setting = 0xff;
                    }
                });
                alertBuilder.setPositiveButton(R.string.btn_okey, new DialogInterface.OnClickListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog2.dismiss();
                        Save_Setting();
                    }
                });
                alertDialog2 = alertBuilder.create();
                alertDialog2.show();
                Looper.loop();
            }
        });
        thread1.start();
    }

    public void onDisplay(View view) {
        startActivityForResult(
                new Intent(this, DisplaysActivity.class),
                REQ_DISPLAY_SELECTION);
    }

    public void onFilter(View view) {
        if (originalImage == null) PermissionHelper.note(this, R.string.no_pict);
        else startActivityForResult(
                new Intent(this, FilteringActivity.class),
                REQ_PALETTE_SELECTION);
    }

    public void onSetting(View view) {
        if (btDevice == null) PermissionHelper.note(this, R.string.no_blue);
        else startActivityForResult(
                new Intent(this, SettingActivity.class),
                REQ_SETTING);
    }


    public void onGetWifiName(View view) {
        checkPermission();
        WifiManager WIFIMANAGER = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert WIFIMANAGER != null;
        WifiInfo WIFIINFO = WIFIMANAGER.getConnectionInfo();
        wifi_ssid = WIFIINFO.getSSID();
        int networkID = WIFIINFO.getNetworkId();
        List<WifiConfiguration> configuredNetworks = WIFIMANAGER.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.networkId == networkID) {
                wifi_ssid = wifiConfiguration.SSID;
                break;
            }
        }
        int start = wifi_ssid.indexOf('\"');
        int end = start >= 0 ? wifi_ssid.indexOf('\"', start + 1) : start;
        if (end > 0) {
            wifi_ssid = wifi_ssid.substring(start + 1, end);
        } else {
            wifi_ssid = null;
            Toast toast = Toast.makeText(AppStartActivity.this, "null", Toast.LENGTH_LONG);
            toast.setText(R.string.cannot_get_ssid);
            toast.show();
        }
        if (wifi_ssid != null) ssid_editText.setText(wifi_ssid);
    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_BLUETOOTH_CONNECTION) {

            if (resultCode == RESULT_OK) {
                if (btDevice != null) BluetoothHelper.close();

                btDevice = data.getParcelableExtra("DEVICE");

                textBlue.setText(btDevice.getName() + " (" + btDevice.getAddress() + ")");

                Toast toast = Toast.makeText(AppStartActivity.this, "null", Toast.LENGTH_LONG);
                toast.setText(R.string.Connect_to_device);
                toast.show();
                BluetoothHelper.initialize(btDevice, handler = new SocketHandler());
                Thread thread1 = new Thread(new Runnable() {
                    public void run() {
                        Looper.prepare();
                        bluetooth_reless = true;
                        BluetoothHelper.connect();
                        bluetooth_reless = false;
                        Looper.loop();
                    }
                });
                thread1.start();
            }
        }
        else if (requestCode == REQ_OPEN_FILE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream is = new FileInputStream(filePath + "/" + fileName);
                    originalImage = (new BitmapDrawable(is)).getBitmap();
                    pictFile.setImageBitmap(originalImage);
                } catch (Exception e) {
                }
            }
        }
        else if (requestCode == REQ_DISPLAY_SELECTION) {
            if (resultCode == RESULT_OK) ;
        }
        else if (requestCode == REQ_PALETTE_SELECTION) {
            if (resultCode == RESULT_OK) {
                try {
                    pictFilt.setImageBitmap(indTableImage);
                } catch (Exception e) {

                }
            }
        }
    }

    public void clean_connect(String str) {
        BluetoothHelper.close();
        bluetooth_reless = false;
        btDevice = null;
        textBlue.setText(null);
        Toast toast = Toast.makeText(AppStartActivity.this, "null", Toast.LENGTH_LONG);
        toast.setText(str);
        toast.show();
    }

    public void Cheack_lock() {
        final Thread thread1 = new Thread(new Runnable() {
            @SuppressLint("WrongConstant")
            public void run() {
                Looper.prepare();

                if (btDevice == null)
                    PermissionHelper.note(AppStartActivity.this, R.string.no_blue);
                else {
                    final Command COMMAND = new Command(handler);
                    if (!COMMAND.Send_Comman(CHECK, "1")) {
                        String STR = AppStartActivity.this.getString(R.string.lose_connect);
                        clean_connect(STR);
                        Looper.loop();
                        return;
                    }
                    Lock_flag = COMMAND.reed()[0] == '1';
                    if (Lock_flag) {
                        Toast toast = Toast.makeText(AppStartActivity.this, "null", Toast.LENGTH_LONG);
                        toast.setText(R.string.device_is_locked);
                        toast.setDuration(2000);
                        toast.show();

                        final EditText inputServer = new EditText(AppStartActivity.this);
                        inputServer.setText(mySP.getString("EDPSWD", ""));
                        final AlertDialog.Builder builder = new AlertDialog.Builder(AppStartActivity.this);
                        builder.setTitle(R.string.Enter_device_password).setView(inputServer)
                                .setNegativeButton(R.string.btn_cncl, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BREAK_flag = true;
                                        dialog.dismiss();
                                    }
                                });

                        builder.setPositiveButton(R.string.btn_okey, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                putin_password = inputServer.getText().toString();
                                COMMAND.Send_Comman(NULOCK, putin_password);

                                byte[] get = COMMAND.reed();
                                if (get == null) {
                                    Looper.loop();
                                }
                                assert get != null;
                                Lock_flag = get[0] != '1';
                                if (Lock_flag) {
                                    BREAK_flag = true;
                                    PermissionHelper.note(AppStartActivity.this, R.string.Incorrect_password);
                                } else {
                                    SharedPreferences.Editor editor = mySP.edit();
                                    editor.putString("ID", id);
                                    editor.putString("SSID", wifi_ssid);
                                    editor.putString("PSWD", wifi_pasword);
                                    editor.putString("HOST", host_ip);
                                    editor.putString("DPSWD", devicepswd);
                                    editor.putBoolean("LOCK", device_lock);
                                    editor.putString("EDPSWD", putin_password);
                                    editor.putString("LOPW", warn_voltage);

                                    editor.putBoolean("DHCP", dhck_onf);
                                    editor.putString("DIP", device_ip);
                                    editor.putString("GWIP", gw_ip);
                                    editor.putString("NETMASK", netmask);
                                    editor.apply();
                                }

                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }

                Looper.loop();
            }
        });
        thread1.start();
    }

    byte[] IP_Change(String IP) {
        String[] ip_split = IP.split("\\.");
        byte[] ip_byte = new byte[4];
        for (int cnt = 0; cnt < 4; cnt++) {
            ip_byte[cnt] = (byte) Integer.parseInt(ip_split[cnt]);
        }
        return ip_byte;
    }

    byte[] Static_ipset() {
        if (dhck_onf) {
            return new byte[15];
        } else {
            byte[] ip_b = IP_Change(device_ip);
            byte[] gw_b = IP_Change(gw_ip);
            byte[] netmask_b = IP_Change(netmask);
            byte[] dhcp_b = new byte[1];
            dhcp_b[0] = (byte) ((dhck_onf) ? 0 : 1);
            byte[] result = Arrays.copyOf(dhcp_b, dhcp_b.length + ip_b.length + gw_b.length + netmask_b.length);

            System.arraycopy(dhcp_b, 0, result, 0, dhcp_b.length);
            System.arraycopy(ip_b, 0, result, (dhcp_b.length), ip_b.length);
            System.arraycopy(gw_b, 0, result, (dhcp_b.length + ip_b.length), gw_b.length);
            System.arraycopy(netmask_b, 0, result, (dhcp_b.length + ip_b.length + gw_b.length), netmask_b.length);
            return result;
        }

    }

    public void Upload_Setting(final boolean clean) {
        final Thread thread2 = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                boolean Err_flag = false;
                if (clean) {
                    id = "";
                    host_ip = "";
                    wifi_ssid = "";
                    wifi_pasword = "";
                    devicepswd = "";
                    devicelock = "";
                    warn_voltage = "";
                    dhckonf_s = "";
                    dhck_onf = true;
                } else {

                    Err_flag = id.equals("") || Err_flag;
                    Err_flag = host_ip.equals("") || Err_flag;
                    Err_flag = wifi_ssid.equals("") || Err_flag;
                    Err_flag = wifi_pasword.equals("") || Err_flag;
                    Err_flag = devicepswd.equals("") || Err_flag;
                    Err_flag = devicepswd.equals("") || Err_flag;
                    Err_flag = devicelock.equals("") || Err_flag;
                    Err_flag = warn_voltage.equals("") || Err_flag;
                    Err_flag = dhckonf_s.equals("") || Err_flag;
                    Err_flag = devicelock.equals("") || Err_flag;
                    if (!dhck_onf) {
                        Err_flag = device_ip.equals("") || Err_flag;
                        Err_flag = gw_ip.equals("") || Err_flag;
                        Err_flag = netmask.equals("") || Err_flag;
                    }

                    if (Err_flag) {
                        PermissionHelper.note(AppStartActivity.this, R.string.Miss_setting);
                        bluetooth_reless = false;
                        Looper.loop();
                    } else {
                        int WV = Integer.parseInt(warn_voltage);
                        if (WV > 4150) {
                            warn_voltage = "3000";
                            Toast toast_w = Toast.makeText(AppStartActivity.this, "null", Toast.LENGTH_LONG);
                            toast_w.setText(R.string.warm_voltage_hight);
                            warnvol_editText.setText(warn_voltage);
                            toast_w.show();
                        }
                    }

                }
                if(!Err_flag){
                    clean_flag = (clean) ? "0" : "1";
                    while (Lock_flag && (!BREAK_flag)) SystemClock.sleep(2);
                    SystemClock.sleep(10);


                    Command COMMAND = new Command(handler);
                    if (!BREAK_flag) {
                        int cnt = 0;
                        for (cnt = 0; cnt < 5; cnt++) {
                            if (!COMMAND.Send_Comman(DEVICE_ID, id)) continue;
                            if (!COMMAND.Send_Comman(HOST, host_ip)) continue;
                            if (!COMMAND.Send_Comman(WIFI_SSID, wifi_ssid)) continue;
                            if (!COMMAND.Send_Comman(WIFI_PAWD, wifi_pasword)) continue;
                            if (!COMMAND.Send_Comman(USER_PAWD, devicepswd)) continue;
                            if (!COMMAND.Send_Comman(LOCK, devicelock)) continue;
                            if (!COMMAND.Send_Comman(SET, clean_flag)) continue;
                            if (!COMMAND.Send_Comman(LOPW, warn_voltage)) continue;
                            if (!COMMAND.Send_Comman(STATIC_IP, Static_ipset())) continue;
                            if (!COMMAND.Send_Comman(REBOOT, "1")) continue;


                            break;
                        }
                        Toast toast2 = Toast.makeText(AppStartActivity.this, "null", Toast.LENGTH_LONG);
                        if (cnt < 5) {
                            if (clean) {
                                toast2.setText(R.string.Formatting_completed);
                            } else {
                                toast2.setText(R.string.upload_config_completed);
                            }

                            SharedPreferences.Editor editor = mySP.edit();
                            editor.putString("ID", id);
                            editor.putString("SSID", wifi_ssid);
                            editor.putString("PSWD", wifi_pasword);
                            editor.putString("HOST", host_ip);
                            editor.putString("DPSWD", devicepswd);
                            editor.putBoolean("LOCK", device_lock);
                            editor.putString("EDPSWD", putin_password);
                            editor.putString("LOPW", warn_voltage);


                            editor.putBoolean("DHCP", dhck_onf);
                            editor.putString("DIP", device_ip);
                            editor.putString("GWIP", gw_ip);
                            editor.putString("NETMASK", netmask);
                            editor.apply();

                        } else {
                            if (clean) {
                                toast2.setText(R.string.Formatting_failed);
                            } else {
                                toast2.setText(R.string.upload_config_failed);
                            }
                        }
                        toast2.show();
                    }
                    SystemClock.sleep(500);
                    BluetoothHelper.close();
                    bluetooth_reless = false;
                    btDevice = null;
                    textBlue.setText(null);
                }

                Looper.loop();
            }
        });
        thread2.start();
    }

    public void Save_Setting() {
        SharedPreferences.Editor editor = mySP.edit();
        String id = id_editText.getText().toString();
        String wifi_ssid = ssid_editText.getText().toString();
        String wifi_pasword = pswd_editText.getText().toString();
        String host_ip = host_editText.getText().toString();
        String devicepswd = dpswd_editText.getText().toString();
        device_lock = d_lock.isChecked();
        warn_voltage = warnvol_editText.getText().toString();

        dhck_onf = dhcp_sw.isChecked();
        dhckonf_s = dhcp_sw.isChecked() ? "1" : "0";
        device_ip = dip_editText.getText().toString();
        gw_ip = gwip_editText.getText().toString();
        netmask = netmask_editText.getText().toString();

        int WV = Integer.parseInt(warn_voltage);
        if (WV > 4150) {
            warn_voltage = "3000";
            Toast toast = Toast.makeText(AppStartActivity.this, "null", Toast.LENGTH_LONG);
            toast.setText(R.string.warm_voltage_hight);
            warnvol_editText.setText(warn_voltage);
            toast.show();
        }
        editor.putString(Config_list[Choose_setting] + "ID", id);
        editor.putString(Config_list[Choose_setting] + "SSID", wifi_ssid);
        editor.putString(Config_list[Choose_setting] + "PSWD", wifi_pasword);
        editor.putString(Config_list[Choose_setting] + "HOST", host_ip);
        editor.putString(Config_list[Choose_setting] + "DPSWD", devicepswd);
        editor.putBoolean(Config_list[Choose_setting] + "LOCK", device_lock);
        editor.putString(Config_list[Choose_setting] + "LOPW", warn_voltage);
        editor.putBoolean(Config_list[Choose_setting] + "DHCP", dhck_onf);
        editor.putString(Config_list[Choose_setting] + "DIP", device_ip);
        editor.putString(Config_list[Choose_setting] + "GWIP", gw_ip);
        editor.putString(Config_list[Choose_setting] + "NETMASK", netmask);

        editor.apply();
    }
};

