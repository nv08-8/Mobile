package vn.hcmute.uploadfile_socket;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BlueControl extends AppCompatActivity {
    ImageButton btnTb1, btnTb2, btnDisc;
    TextView txt1, txtMAC;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    String address = null;
    OutputStream outStream = null;

    // SPP UUID
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // flags and helpers
    private int flaglamp1 = 0;
    private int flaglamp2 = 0;
    private ProgressDialog progress;
    private Set<BluetoothDevice> pairedDevices1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        Intent newint = getIntent();
        address = newint.getStringExtra(SocketActivity.EXTRA_ADDRESS);

        btnTb1 = findViewById(R.id.btnOnLeft);
        btnTb2 = findViewById(R.id.btnOnRight);
        btnDisc = findViewById(R.id.btnCenter);
        txt1 = findViewById(R.id.textViewSmallTitle);
        txtMAC = findViewById(R.id.textViewMac);

        if (address != null) {
            txtMAC.setText(address);
        }

        btnTb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thietTb1();
            }
        });

        btnTb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thietTb2();
            }
        });

        btnDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });

        // Start connection in background using AsyncTask (as in slides)
        new ConnectBT().execute();
    }

    private void connectToDevice() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted
            runOnUiThread(() -> Toast.makeText(BlueControl.this, "No BLUETOOTH_CONNECT permission", Toast.LENGTH_SHORT).show());
            return;
        }

        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
        try {
            btSocket = dispositivo.createRfcommSocketToServiceRecord(myUUID);
            myBluetooth.cancelDiscovery();
            btSocket.connect();
            outStream = btSocket.getOutputStream();
            isBtConnected = true;
            runOnUiThread(() -> Toast.makeText(BlueControl.this, "Đã kết nối", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(BlueControl.this, "Không thể kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show());
            try {
                if (btSocket != null) btSocket.close();
            } catch (IOException ignored) {}
        }
    }

    private void sendCommand(String s) {
        if (!isBtConnected || outStream == null) {
            Toast.makeText(this, "Chưa kết nối Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            outStream.write(s.getBytes());
            Toast.makeText(this, "Gửi: " + s, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi gửi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void Disconnect() {
        try {
            if (outStream != null) outStream.close();
            if (btSocket != null) btSocket.close();
            isBtConnected = false;
            runOnUiThread(() -> Toast.makeText(BlueControl.this, "Đã ngắt kết nối", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Disconnect();
        super.onDestroy();
    }

    // --- Methods from slides: toggle device 1/2, AsyncTask ConnectBT, pairedDevicesList1(), msg() ---

    private void thietTb1() {
        if (btSocket != null) {
            try {
                if (this.flaglamp1 == 0) {
                    this.flaglamp1 = 1;
                    this.btnTb1.setBackgroundResource(R.drawable.tb1on);
                    if (outStream == null) outStream = btSocket.getOutputStream();
                    outStream.write("1".getBytes());
                    txt1.setText("Thiết bị số 1 đang bật");
                    return;
                } else {
                    this.flaglamp1 = 0;
                    this.btnTb1.setBackgroundResource(R.drawable.tb1off);
                    if (outStream == null) outStream = btSocket.getOutputStream();
                    outStream.write("A".getBytes());
                    txt1.setText("Thiết bị số 1 đang tắt");
                    return;
                }
            } catch (IOException e) {
                msg("Lỗi");
            }
        } else {
            msg("Chưa kết nối Bluetooth");
        }
    }

    private void thietTb2() {
        if (btSocket != null) {
            try {
                if (this.flaglamp2 == 0) {
                    this.flaglamp2 = 1;
                    this.btnTb2.setBackgroundResource(R.drawable.tb7on);
                    if (outStream == null) outStream = btSocket.getOutputStream();
                    outStream.write("2".getBytes());
                    txt1.setText("Thiết bị số 2 đang bật");
                    return;
                } else {
                    this.flaglamp2 = 0;
                    this.btnTb2.setBackgroundResource(R.drawable.tb7off);
                    if (outStream == null) outStream = btSocket.getOutputStream();
                    outStream.write("B".getBytes());
                    txt1.setText("Thiết bị số 2 đang tắt");
                    return;
                }
            } catch (IOException e) {
                msg("Lỗi");
            }
        } else {
            msg("Chưa kết nối Bluetooth");
        }
    }

    // AsyncTask to connect in background (follow slides)
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true; // if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BlueControl.this, "Đang kết nối...", "Xin vui lòng đợi!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(BlueControl.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ConnectSuccess = false;
                        return null;
                    }

                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    myBluetooth.cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false; // if try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();

            if (!ConnectSuccess) {
                msg("Kết nối thất bại ! Kiểm tra thiết bị.");
                finish();
            } else {
                msg("Kết nối thành công.");
                isBtConnected = true;
                pairedDevicesList1();
            }
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void pairedDevicesList1() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            msg("No Bluetooth permission");
            return;
        }
        pairedDevices1 = myBluetooth.getBondedDevices();

        if (pairedDevices1 != null && !pairedDevices1.isEmpty()) {
            for (BluetoothDevice bt : pairedDevices1) {
                txtMAC.setText(bt.getName() + " - " + bt.getAddress()); // show device name and address
            }
        } else {
            Toast.makeText(getApplicationContext(), "Không tìm thấy thiết bị kết nối.", Toast.LENGTH_LONG).show();
        }
    }
}
