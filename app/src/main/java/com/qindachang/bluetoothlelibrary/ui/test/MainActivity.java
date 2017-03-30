package com.qindachang.bluetoothlelibrary.ui.test;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
import com.qindachang.bluetoothle.OnLeIndicationListener;
import com.qindachang.bluetoothle.OnLeNotificationListener;
import com.qindachang.bluetoothle.OnLeReadCharacteristicListener;
import com.qindachang.bluetoothle.OnLeReadRssiListener;
import com.qindachang.bluetoothle.OnLeScanListener;
import com.qindachang.bluetoothle.OnLeWriteCharacteristicListener;
import com.qindachang.bluetoothle.exception.BleException;
import com.qindachang.bluetoothle.exception.ConnBleException;
import com.qindachang.bluetoothle.exception.ReadBleException;
import com.qindachang.bluetoothle.exception.ScanBleException;
import com.qindachang.bluetoothle.exception.WriteBleException;
import com.qindachang.bluetoothle.scanner.ScanRecord;
import com.qindachang.bluetoothle.scanner.ScanResult;
import com.qindachang.bluetoothlelibrary.ApiLevelHelper;
import com.qindachang.bluetoothlelibrary.BaseActivity;
import com.qindachang.bluetoothlelibrary.BluetoothUUID;
import com.qindachang.bluetoothlelibrary.LocationUtils;
import com.qindachang.bluetoothlelibrary.R;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.tv_text)
    TextView mTvText;

    private BluetoothLe mBluetoothLe;
    private BluetoothDevice mBluetoothDevice;

    private StringBuilder mStringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mStringBuilder = new StringBuilder();

        mBluetoothLe = BluetoothLe.getDefault();

        if (!mBluetoothLe.isSupportBluetooth()) {
            //设备不支持蓝牙
        } else {
            if (!mBluetoothLe.isBluetoothOpen()) {
                //没有打开蓝牙，请求打开手机蓝牙
                mBluetoothLe.enableBluetooth(this, 666);
            }
        }

        //监听蓝牙回调
        //监听扫描
        mBluetoothLe.setOnScanListener(TAG, new OnLeScanListener() {
            @Override
            public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord) {
                mBluetoothDevice = bluetoothDevice;
                Log.i(TAG, "扫描到设备：" + mBluetoothDevice.getName());
                showText("扫描到设备：" + mBluetoothDevice.getName());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                Log.i(TAG, "扫描到设备：" + results.toString());
                showText("扫描到设备：" + results.toString());
            }

            @Override
            public void onScanCompleted() {
                Log.i(TAG, "停止扫描");
                showText("停止扫描");
            }

            @Override
            public void onScanFailed(ScanBleException e) {
                Log.e(TAG, "扫描错误：" + e.toString());
                showText("扫描错误：" + e.toString());
            }
        });
        //监听连接
        mBluetoothLe.setOnConnectListener(TAG, new OnLeConnectListener() {
            @Override
            public void onDeviceConnecting() {
                Log.i(TAG, "正在连接--->：" + mBluetoothDevice.getAddress());
                showText("正在连接--->：" + mBluetoothDevice.getAddress());
            }

            @Override
            public void onDeviceConnected() {
                Log.i(TAG, "成功连接！");
                showText("成功连接！");
            }

            @Override
            public void onDeviceDisconnected() {
                Log.i(TAG, "连接断开！");
                showText("连接断开！");
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {
                Log.i(TAG, "发现服务");
                showText("发现服务");
            }

            @Override
            public void onDeviceConnectFail(ConnBleException e) {
                Log.e(TAG, "连接异常：" + e.toString());
                showText("连接异常：" + e.toString());
            }
        });
        //监听通知，类型notification
        mBluetoothLe.setOnNotificationListener(TAG, new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.i(TAG, "收到notification : " + Arrays.toString(characteristic.getValue()));
                showText("收到notification : " + Arrays.toString(characteristic.getValue()));
            }

            @Override
            public void onFailed(BleException e) {
                Log.e(TAG, "notification通知错误：" + e.toString());
                showText("notification通知错误：" + e.toString());
            }
        });
        //监听通知，类型indicate
        mBluetoothLe.setOnIndicationListener(TAG, new OnLeIndicationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.i(TAG, "收到indication: " + Arrays.toString(characteristic.getValue()));
                showText("收到indication: " + Arrays.toString(characteristic.getValue()));
            }

            @Override
            public void onFailed(BleException e) {
                Log.e(TAG, "indication通知错误：" + e.toString());
                showText("indication通知错误：" + e.toString());
            }
        });
        //监听写
        mBluetoothLe.setOnWriteCharacteristicListener(TAG, new OnLeWriteCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.i(TAG, "写数据到特征：" + Arrays.toString(characteristic.getValue()));
                showText("写数据到特征：" + Arrays.toString(characteristic.getValue()));
            }

            @Override
            public void onFailed(WriteBleException e) {
                Log.e(TAG, "写错误：" + e.toString());
                showText("写错误：" + e.toString());
            }
        });
        //监听读
        mBluetoothLe.setOnReadCharacteristicListener(TAG, new OnLeReadCharacteristicListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.i(TAG, "读取特征数据：" + Arrays.toString(characteristic.getValue()));
                showText("读取特征数据：" + Arrays.toString(characteristic.getValue()));
            }

            @Override
            public void onFailure(ReadBleException e) {
                Log.e(TAG, "读错误：" + e.toString());
                showText("读错误：" + e.toString());
            }
        });
        //监听信号强度
        mBluetoothLe.setReadRssiInterval(10000)
                .setOnReadRssiListener(TAG, new OnLeReadRssiListener() {
                    @Override
                    public void onSuccess(int rssi, int cm) {
                        Log.i(TAG, "信号强度：" + rssi + "   距离：" + cm + "cm");
                        showText("信号强度：" + rssi + "   距离：" + cm + "cm");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == RESULT_OK) {
            scan();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据TAG注销监听，避免内存泄露
        mBluetoothLe.destroy(TAG);
        //关闭GATT
        mBluetoothLe.close();
    }

    @OnClick({R.id.btn_stop_scan, R.id.btn_disconnect, R.id.btn_clear_text, R.id.btn_write_hr,
            R.id.btn_write_step, R.id.btn_read, R.id.btn_connect, R.id.btn_scan, R.id.btn_open_notification,
            R.id.btn_clear_queue, R.id.btn_close_notification, R.id.btn_clear_cache})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_stop_scan:
                mBluetoothLe.stopScan();
                break;
            case R.id.btn_disconnect:
                mBluetoothLe.disconnect();
                break;
            case R.id.btn_clear_text:
                mStringBuilder.delete(0, mStringBuilder.length());
                mTvText.setText(mStringBuilder.toString());
                break;
            case R.id.btn_write_hr:
                for (int i = 0; i < 50; i++) {
                    //每一次发送数据的时间间隔，将根据你在Application中设置的队列间隔
                    mBluetoothLe.writeDataToCharacteristic(new byte[]{0x01, 0x01},
                            BluetoothUUID.SERVICE, BluetoothUUID.WRITE);
                }
                break;
            case R.id.btn_write_step:
                mBluetoothLe.writeDataToCharacteristic(new byte[]{0x06, 0x01},
                        BluetoothUUID.SERVICE, BluetoothUUID.WRITE);
                break;
            case R.id.btn_read:
                mBluetoothLe.readCharacteristic(BluetoothUUID.SERVICE, BluetoothUUID.READ);
                break;
            case R.id.btn_connect:
                mBluetoothLe.startConnect(true, mBluetoothDevice);
                break;
            case R.id.btn_scan:
                scan();
                break;
            case R.id.btn_open_notification:
                mBluetoothLe.enableNotification(true, BluetoothUUID.SERVICE,
                        new UUID[]{BluetoothUUID.HR_NOTIFICATION, BluetoothUUID.STEP_NOTIFICATION});
                break;
            case R.id.btn_clear_queue:
                mBluetoothLe.clearQueue();
                break;
            case R.id.btn_close_notification:
                mBluetoothLe.enableNotification(false, BluetoothUUID.SERVICE,
                        new UUID[]{BluetoothUUID.HR_NOTIFICATION, BluetoothUUID.STEP_NOTIFICATION});
                break;
            case R.id.btn_clear_cache:
                mBluetoothLe.clearDeviceCache();
                break;
        }
    }

    private void scan() {
        //对于Android 6.0以上的版本，申请地理位置动态权限
        if (!checkLocationPermission()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("权限需求")
                    .setMessage("Android 6.0 以上的系统版本，扫描蓝牙需要地理位置权限。请允许。")
                    .setNeutralButton("取消", null)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLocationPermission();
                        }
                    })
                    .show();
            return;
        }
        //如果系统版本是7.0以上，则请求打开位置信息
        if (!LocationUtils.isOpenLocService(this) && ApiLevelHelper.isAtLeast(Build.VERSION_CODES.N)) {
            Toast.makeText(this,"您的Android版本在7.0以上，扫描需要打开位置信息。", Toast.LENGTH_LONG).show();
            LocationUtils.gotoLocServiceSettings(this);
            return;
        }
        mBluetoothLe.setScanPeriod(20000)
             //   .setScanWithServiceUUID(BluetoothUUID.SERVICE)
                .setReportDelay(0)
                .startScan(this);
    }

    private void showText(String text) {
        mStringBuilder.append(text).append("\n");
        mTvText.setText(mStringBuilder.toString());
    }
}
