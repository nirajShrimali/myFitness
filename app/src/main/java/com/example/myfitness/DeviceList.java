package com.example.myfitness;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DeviceList extends AppCompatActivity {
    private RecyclerView deviceRecyclerView;
    private DeviceAdapter deviceAdapter;
    private List<String> deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        deviceRecyclerView = findViewById(R.id.deviceRecyclerView);
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        deviceList = new ArrayList<>();
        deviceList.add("Smartwatch");
        deviceList.add("Fitness Band");
        deviceList.add("Heart Monitor");
        deviceList.add("Smart Scale");

        deviceAdapter = new DeviceAdapter(deviceList);
        deviceRecyclerView.setAdapter(deviceAdapter);
    }
}
