package edu.skku.map.changer.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.skku.map.changer.LoginActivity;
import edu.skku.map.changer.R;

public class FragmentAnalysis extends Fragment {
    private RadarChart chart;
    private Button button;
    int me = 0xFF147EFB;
    int others = 0xFFFC3158;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_analysis, container, false);
        button = rootView.findViewById(R.id.logout_analysis);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("로그아웃하시겠습니까?");
                builder.setCancelable(true);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        getActivity().finish();
                        Intent intent = new Intent(getContext(), LoginActivity.class);

                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        /* ABOUT CHART */
        // 전체화면
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setTitle("RadarChartActivity");
        chart = rootView.findViewById(R.id.chart_analysis);
        //chart.setBackgroundColor(Color.rgb(60, 65, 82));
        chart.getDescription().setEnabled(false);
        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.LTGRAY);
        chart.setWebLineWidthInner(1f);
        chart.setWebColorInner(Color.LTGRAY);
        chart.setWebAlpha(80);
        chart.setTouchEnabled(false);

        setData();

        // 애니메이션 효과
        chart.animateXY(1000, 1000);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(13f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new MyValueFormatter());
        xAxis.setTextColor(Color.BLACK);
        YAxis yAxis = chart.getYAxis();
        yAxis.setLabelCount(4, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setDrawLabels(false);

        // 차트 어떤건지
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYOffset(-5);
        l.setYEntrySpace(0);
        l.setTextSize(13);
        l.setTextColor(Color.BLACK);

        return rootView;
    }

    private void setData() {

        float mul = 1;
        float min = 0;
        int cnt = 11;

        ArrayList<RadarEntry> entries1 = new ArrayList<>();
        ArrayList<RadarEntry> entries2 = new ArrayList<>();

        // 값 설정!!
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        /*
        for (int i = 0; i < cnt; i++) {
            float val1 = (float) (Math.random() * mul) + min;
            entries1.add(new RadarEntry(val1));

            float val2 = (float) (Math.random() * mul) + min;
            entries2.add(new RadarEntry(val2));
        }
         */

        /*
        entries1.add(new RadarEntry(hardware.getWeight()));
        entries1.add(new RadarEntry(hardware.getDesign()));
        entries1.add(new RadarEntry(hardware.getScreen()));
        entries1.add(new RadarEntry(hardware.getPerformance()));
        entries1.add(new RadarEntry(hardware.getGraphic()));
        entries1.add(new RadarEntry(hardware.getBattery()));

        entries2.add(new RadarEntry(review.getWeight()));
        entries2.add(new RadarEntry(review.getDesign()));
        entries2.add(new RadarEntry(review.getScreen()));
        entries2.add(new RadarEntry(review.getPerformance()));
        entries2.add(new RadarEntry(review.getGraphic()));
        entries2.add(new RadarEntry(review.getBattery()));

         */

        entries1.add(new RadarEntry(100));
        entries1.add(new RadarEntry(80));
        entries1.add(new RadarEntry(0));
        entries1.add(new RadarEntry(0));
        entries1.add(new RadarEntry(80));
        entries1.add(new RadarEntry(0));
        entries1.add(new RadarEntry(0));
        entries1.add(new RadarEntry(0));
        entries1.add(new RadarEntry(0));
        entries1.add(new RadarEntry(0));
        entries1.add(new RadarEntry(0));

        entries2.add(new RadarEntry(30));
        entries2.add(new RadarEntry(60));
        entries2.add(new RadarEntry(50));
        entries2.add(new RadarEntry(20));
        entries2.add(new RadarEntry(80));
        entries2.add(new RadarEntry(90));
        entries2.add(new RadarEntry(10));
        entries2.add(new RadarEntry(30));
        entries2.add(new RadarEntry(40));
        entries2.add(new RadarEntry(70));
        entries2.add(new RadarEntry(80));


        RadarDataSet set1 = new RadarDataSet(entries1, "나");
        set1.setColor(me);
        set1.setFillColor(me);
        set1.setDrawFilled(true);
        set1.setFillAlpha(70);
        set1.setLineWidth(1f);
        //set1.setDrawHighlightCircleEnabled(true);
        //set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "다른 사용자");
        set2.setColor(others);
        set2.setFillColor(others);
        set2.setDrawFilled(true);
        set2.setFillAlpha(70);
        set2.setLineWidth(1f);
        //set2.setDrawHighlightCircleEnabled(true);
        //set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setDrawValues(false);
        // 데이터 값 표시
        /*
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.BLACK);
         */

        chart.setData(data);
        chart.invalidate();
    }

    public class MyValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;
        private final String[] mActivities = new String[]{"반고흐", "뭉크", "모네", "리히텐슈타인",
                "피카소", "쿠터", "키리코", "시드니놀란", "세베리니", "망원경", "마름모"};

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mActivities[(int) value % mActivities.length];
        }
    }
}
