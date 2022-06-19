package rmr.kairos.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rmr.kairos.R;
import rmr.kairos.database.KairosDB;
import rmr.kairos.model.Estadistica;

/**
 * Actividad que muestra las estadísticas de uso de la app mediante la API de AnyChart
 * @author Rafa M.
 * @version 1.0
 * @since 1.0
 */
public class StatActivity extends AppCompatActivity {
    private ImageView imBack;
    private KairosDB db;
    private Date creationDate;
    private SharedPreferences kp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        imBack = findViewById(R.id.imBack);
        db = new KairosDB(StatActivity.this);
        creationDate = null;
        kp = PreferenceManager.getDefaultSharedPreferences(this);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentToMain);
                finish();
            }
        });
        if (creationDate != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               long nowDate = Date.from(Instant.now()).getTime() - creationDate.getTime();
               if (nowDate>1) db.updateAllStats();
            }
        }
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progressBar));

        Cartesian cartesian = AnyChart.column();
        ArrayList<Estadistica> statList = new ArrayList<>();
        statList = db.selectStats();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("L", statList.get(0).getWorkTime()));
        data.add(new ValueDataEntry("M", statList.get(1).getWorkTime()));
        data.add(new ValueDataEntry("X", statList.get(2).getWorkTime()));
        data.add(new ValueDataEntry("J", statList.get(3).getWorkTime()));
        data.add(new ValueDataEntry("V", statList.get(4).getWorkTime()));
        data.add(new ValueDataEntry("S", statList.get(5).getWorkTime()));
        data.add(new ValueDataEntry("D", statList.get(6).getWorkTime()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            creationDate = Date.from(Instant.now());
        }

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Tiempo empleado esta semana (en minutos)");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Día");
        //cartesian.yAxis(0).title("Tiempo");
        if(kp.getBoolean("dark_mode_key",true)) {
            cartesian.background().fill("#000000");
        }else cartesian.background().fill("#FFFFFF");


        anyChartView.setChart(cartesian);
    }
}