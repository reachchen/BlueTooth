package com.tanjiaming.www.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.junkchen.blelib.Constants.ACTION_GATT_CONNECTED;
import static com.junkchen.blelib.Constants.ACTION_GATT_DISCONNECTED;
public class MainActivity extends AppCompatActivity {

    private Button device_one;
    private Button device_two;
    private Button device_three;
    private Button device_four;
    private TextView title;
    private BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "当前设备不支持ble蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }
        //获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
        device_one = (Button) findViewById(R.id.bt_deviceOne);
        device_two = (Button) findViewById(R.id.bt_deviceTwo);
        device_three = (Button) findViewById(R.id.bt_deviceThree);
        device_four = (Button) findViewById(R.id.bt_deviceFour);
        title = (TextView)findViewById(R.id.tv_activity_title);
        title.setText("综合检测设备客户端");

        device_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_one = new Intent(MainActivity.this,DeviceOneActivity.class);
                startActivity(intent_one);
            }
        });

        device_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_two = new Intent(MainActivity.this,DeviceTwoActivity.class);
                startActivity(intent_two);
            }
        });

        device_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_three = new Intent(MainActivity.this,DeviceThreeActivity.class);
                startActivity(intent_three);
            }
        });

        device_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_four = new Intent(MainActivity.this,DeviceFourActivity.class);
                startActivity(intent_four);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "蓝牙未启用", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
