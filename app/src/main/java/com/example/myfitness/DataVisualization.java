package com.example.myfitness;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfitness.databinding.ActivityDataVisualizationBinding;

public class DataVisualization extends AppCompatActivity {
    private ActivityDataVisualizationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataVisualizationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.stepsValue.setText("11,000");
        binding.caloriesValue.setText("480 kcal");
        binding.heartRateValue.setText("72 bpm");
    }
}
