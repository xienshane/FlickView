package com.android.flickview.activities

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class ActivityCenterActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_center)

        barChart = findViewById(R.id.barChart)
        val buttonBack: ImageView = findViewById(R.id.button_back)
        buttonBack.setOnClickListener {
            finish()
        }

        if (!hasUsageAccessPermission()) {
            Toast.makeText(this, "Please enable Usage Access in Settings", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        } else {
            setupBarChart()
        }
    }

    private fun hasUsageAccessPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    private fun setupBarChart() {
        val screenTimeData = getScreenTimeDataLast7Days()
        if (screenTimeData.isEmpty()) {
            Toast.makeText(this, "No screen time data available", Toast.LENGTH_SHORT).show()
            return
        }

        val entries = ArrayList<BarEntry>()
        val dateLabels = ArrayList<String>()

        for ((index, pair) in screenTimeData.withIndex()) {
            entries.add(BarEntry(index.toFloat(), pair.second.toFloat()))
            dateLabels.add(pair.first)
        }

        val dataSet = BarDataSet(entries, "Screen Time (mins)")
        dataSet.color = Color.parseColor("#FFBB86FC")
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f

        val data = BarData(dataSet)
        data.barWidth = 0.5f

        barChart.data = data
        barChart.setFitBars(true)
        barChart.setTouchEnabled(false)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.animateY(1000)
        barChart.description = Description().apply { text = "" }
        barChart.legend.isEnabled = false

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 10f
        xAxis.granularity = 1f
        xAxis.labelCount = dateLabels.size
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return dateLabels.getOrElse(value.toInt()) { "" }
            }
        }

        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.invalidate()
    }

    private fun getScreenTimeDataLast7Days(): List<Pair<String, Int>> {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val screenTimeData = mutableListOf<Pair<String, Int>>()

        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val start = calendar.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.timeInMillis
            val end = calendar.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.timeInMillis

            val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, start, end
            )

            val totalTime = stats.sumOf { it.totalTimeInForeground.toLong() } / 60000 // Convert to minutes
            val label = sdf.format(Date(start))
            screenTimeData.add(Pair(label, totalTime.toInt()))
        }

        return screenTimeData
    }
}
