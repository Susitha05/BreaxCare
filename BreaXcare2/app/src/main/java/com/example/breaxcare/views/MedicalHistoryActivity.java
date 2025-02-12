package com.example.breaxcare.views;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;
import com.example.breaxcare.model.MedicalHistoryAdapter;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.MedicalHistory;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMedicalHistory;
    private MedicalHistoryAdapter adapter;
    private DBhelper dbHelper;
    private int userId = 1; // Replace with actual user ID
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medical_history);

        recyclerViewMedicalHistory = findViewById(R.id.recyclerViewMedicalHistory);
        barChart = findViewById(R.id.barChart);  // Initialize the BarChart
        recyclerViewMedicalHistory.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBhelper(this);

        // Fetch and set data
        List<MedicalHistory> historyList = dbHelper.getMedicalHistoryForUser(userId);
        if (historyList.isEmpty()) {
            Toast.makeText(this, "No medical history available.", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new MedicalHistoryAdapter(this, historyList);
            recyclerViewMedicalHistory.setAdapter(adapter);

            // Set BarChart data when activity is opened
            updateBarChart(historyList); // Pass the entire list to display in the chart
        }

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void updateBarChart(List<MedicalHistory> historyList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();

        // Loop through all the medical history entries and add them to the chart
        for (int i = 0; i < historyList.size(); i++) {
            MedicalHistory history = historyList.get(i);
            entries.add(new BarEntry(i, history.getWhiteDotsCount()));  // Add white dots count as bar entry
            xLabels.add(history.getDate());  // Use date or any identifier for the X-Axis label
        }

        // Create a BarDataSet from the entries
        BarDataSet dataSet = new BarDataSet(entries, "White Dots Count");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        // Create a BarData object
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate(); // Refresh chart

        // Use IndexAxisValueFormatter to set the X-Axis labels
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(xLabels.toArray(new String[0]));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(formatter); // Set the formatter for X-Axis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(1f);
        xAxis.setTextColor(Color.BLACK);

        // Customize Y-Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.BLACK);
        barChart.getAxisRight().setEnabled(false); // Hide right axis

        // Other Customizations
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDrawGridBackground(false);
    }
}
