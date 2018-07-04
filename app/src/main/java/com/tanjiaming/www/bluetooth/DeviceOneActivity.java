package com.tanjiaming.www.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.junkchen.blelib.BleService;

import java.util.List;

public class DeviceOneActivity extends AppCompatActivity implements View.OnClickListener{
    private BluetoothAdapter mBluetoothAdapter;
    private Handler scanHandler;
    private Boolean scanning = false;
    private Button btnScanBl;
    private Button btnDeviceBack;
    private TextView tvNameAddress;
    private ImageView ivBlFlag;
    public BluetoothLeService mBluetoothLeService;
    public String  TAG;
    public String  mDeviceAddress = "";
    private EditText et_device_N;
    private Boolean binding = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    public String  standDeviceAddress = "18:93:D7:73:CB:41";
    public String  standDeviceService = "0000ff10-0000-1000-8000-00805f9b34fb";
    public String  standDeviceCharacteristic = "0000ff12-0000-1000-8000-00805f9b34fb";

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            if(!"".equals(mDeviceAddress)){
                mBluetoothLeService.connect(mDeviceAddress);
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_one);
        TAG = DeviceOneActivity.class.getSimpleName();
        btnScanBl = (Button)findViewById(R.id.btn_device_one);
        btnDeviceBack = (Button) findViewById(R.id.btn_device_one_back);
        ivBlFlag = (ImageView) findViewById(R.id.iv_device_one_connect_flag);
        et_device_N = (EditText) findViewById(R.id.et_device_one);
        tvNameAddress = (TextView) findViewById(R.id.tv_device_one_name_address);
        //获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        scanHandler = new Handler();
        initListener();

    }
    private void initListener(){
        btnScanBl.setOnClickListener(this);
        btnDeviceBack.setOnClickListener(this);
    }
    public void startScanLeDevice(){
        scanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        },20000);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //在这里可以把搜索到的设备保存起来
                    Log.i(TAG,"蓝牙设备名称"+device.getName());
                    Log.i(TAG,"蓝牙设备的mac地址："+device.getAddress());
                    if(standDeviceAddress.equals(device.getAddress())) {
                        mDeviceAddress = standDeviceAddress;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        ivBlFlag.setImageResource(R.mipmap.on);
                        btnScanBl.setText("连接成功");
                        tvNameAddress.setText(device.getName());
                        Intent intent = new Intent(DeviceOneActivity.this, BluetoothLeService.class);
                        intent.putExtra("type","type01");
                        bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
                        binding  =true;
                    }
                }
            });
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //找到服务，特征，进行读取。
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    }
                },2000);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(DeviceOneActivity.this,"数据读取失败,请重试！",Toast.LENGTH_LONG).show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //展示接受到蓝牙模块发送的数据
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        for (BluetoothGattService gattService : gattServices) {
            Log.e(TAG,"--service--"+gattService.getUuid().toString());
            if(gattService.getUuid().toString().equals(standDeviceService)){
                for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                    Log.e(TAG,"+characteristic+"+gattCharacteristic.getUuid().toString());
                    if(gattCharacteristic.getUuid().toString().equals(standDeviceCharacteristic)){
                        final int charaProp = gattCharacteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, true);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(gattCharacteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = gattCharacteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    gattCharacteristic, true);
                        }
                    }
                }
            }
        }
    }



    private void displayData(String data) {
        if (data != null) {
            Log.e(TAG,"接受到的16进制数据"+data+"长度"+data.length());
            if(data.length()==18){
                String dataSub = data.substring(9, 11) + data.substring(12, 14);
                Log.e(TAG,"长度"+dataSub.length());
                double data_16 = Util.OxStringtoInt(dataSub)*0.1;
                et_device_N.setText(data_16+"");
            }else{
                et_device_N.setText(0.0+"");
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_device_one:
                if(!"连接成功".equals(btnScanBl.getText())){
                    ivBlFlag.setImageResource(R.mipmap.off);
                    tvNameAddress.setText("");
                    startScanLeDevice();
                }
                break;
            case R.id.btn_device_one_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        startScanLeDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(binding){
            unbindService(mServiceConnection);
            mBluetoothLeService.disconnect();
        }
        unregisterReceiver(mGattUpdateReceiver);


    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
