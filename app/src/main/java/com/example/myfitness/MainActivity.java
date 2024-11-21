package com.example.myfitness;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myfitness.databinding.ActivityMainBinding;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        binding.connectDeviceButton.setOnClickListener(v -> startBluetoothScanning());

        binding.btnViewData.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DataVisualization.class);
            startActivity(intent);
        });
    }

    private void startBluetoothScanning() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
            return;
        }

        // Check Bluetooth permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermissions();
            return;
        }

        // Enable Bluetooth if not enabled
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_PERMISSION);
            return;
        }

        // List paired devices
        listPairedDevices();

        // Start discovery
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
        Toast.makeText(this, "Starting Bluetooth discovery...", Toast.LENGTH_SHORT).show();

        // Register BroadcastReceiver for found devices
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(bluetoothReceiver, filter);
            isReceiverRegistered = true;
        }
    }

    private void listPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermissions();
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                System.out.println("Paired Device: " + deviceName + " [" + deviceHardwareAddress + "]");
            }
        } else {
            System.out.println("No paired devices found.");
        }
    }


    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
        }, REQUEST_BLUETOOTH_PERMISSION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBluetoothScanning();
            } else {
                Toast.makeText(this, "Bluetooth permissions are required to scan for devices.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device != null) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address

                        System.out.println("Discovered Device: " + (deviceName != null ? deviceName : "Unknown") +
                                " [" + deviceHardwareAddress + "]");
                    } else {
                        requestBluetoothPermissions();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister receiver
        if (isReceiverRegistered) {
            unregisterReceiver(bluetoothReceiver);
            isReceiverRegistered = false;
        }
    }
}
