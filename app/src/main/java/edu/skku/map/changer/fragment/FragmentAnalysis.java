package edu.skku.map.changer.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.skku.map.changer.LoginActivity;
import edu.skku.map.changer.R;
import edu.skku.map.changer.entities.Post;
import edu.skku.map.changer.entities.PostAdapter;
import edu.skku.map.changer.entities.Preference;

public class FragmentAnalysis extends Fragment {
    private RadarChart chart;
    private Button button;
    int me = 0xFF147EFB;
    int others = 0xFFFC3158;
    private ProgressBar mProgressBar;
    private Preference myPreference = new Preference();
    private Preference allPreference = new Preference();
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_analysis, container, false);
        button = rootView.findViewById(R.id.logout_analysis);
        mProgressBar = rootView.findViewById(R.id.progressBar_a);
        textView = rootView.findViewById(R.id.text_analysis);
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


        // get preference and analysis
        myChart(rootView);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        showDialog();
        db.collection("User").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                myPreference = new Preference(documentSnapshot.getData());
                String preference = maxPref();
                if (preference.length() != 0) {
                    textView.setText(firebaseAuth.getCurrentUser().getDisplayName() + "님은 " + preference + " 필터를 가장 많이 사용하셨습니다.");
                }

                db.collection("Filter").document("Filter").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                        hideDialog();
                        DocumentSnapshot documentSnapshot1 = task1.getResult();
                        allPreference = new Preference(documentSnapshot1.getData());
                        //myChart(rootView);
                        setData();

                    }
                });
            }
        });

        return rootView;
    }

    private void myChart(ViewGroup rootView)
    {
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
        yAxis.setLabelCount(5, true);
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
    }


    private void setData() {

        float mul = 1;
        float min = 0;
        int cnt = 11;

        int scale = 150;

        ArrayList<RadarEntry> entries1 = new ArrayList<>();
        ArrayList<RadarEntry> entries2 = new ArrayList<>();

        int mySum = myPreference.sumPreference();
        int allSum = allPreference.sumPreference();


        Log.v("error2", String.valueOf(mySum));
        Log.v("error2", String.valueOf((float) scale * myPreference.getGogh()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getGogh()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getMunch()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getMonet()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getLichtenstein()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getPicasso()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getKutter()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getChirico()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getNolan()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getSeverini()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getKaleidoscope()/mySum));
        entries1.add(new RadarEntry((float) scale * myPreference.getRhombuses()/mySum));

        entries2.add(new RadarEntry((float) scale * allPreference.getGogh()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getMunch()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getMonet()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getLichtenstein()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getPicasso()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getKutter()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getChirico()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getNolan()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getSeverini()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getKaleidoscope()/allSum));
        entries2.add(new RadarEntry((float) scale * allPreference.getRhombuses()/allSum));


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

    private String maxPref()
    {
        String maximumPref = "";
        int max = 0;
        String[] mActivities = new String[]{"반고흐의 Starry Night", "뭉크의 The Scream",
                "모네의 The Poppy Field", "리히텐슈타인의 The Third Century",
                "피카소의 Les Femmes d'Alger", "쿠터의 Head of a Clown",
                "키리코의 Horses on the Seashore", "시드니놀란의 The Trial",
                "세베리니의 Ritmo Plastico", "망원경", "마름모"};

        if (myPreference.getGogh() > max) {max = myPreference.getGogh(); maximumPref = mActivities[0];}
        else if (myPreference.getMunch() > max) {max = myPreference.getGogh(); maximumPref = mActivities[1];}
        else if (myPreference.getMonet() > max) {max = myPreference.getGogh(); maximumPref = mActivities[2];}
        else if (myPreference.getLichtenstein() > max) {max = myPreference.getGogh(); maximumPref = mActivities[3];}
        else if (myPreference.getPicasso() > max) {max = myPreference.getGogh(); maximumPref = mActivities[4];}
        else if (myPreference.getKutter() > max) {max = myPreference.getGogh(); maximumPref = mActivities[5];}
        else if (myPreference.getChirico() > max) {max = myPreference.getGogh(); maximumPref = mActivities[6];}
        else if (myPreference.getNolan() > max) {max = myPreference.getGogh(); maximumPref = mActivities[7];}
        else if (myPreference.getSeverini() > max) {max = myPreference.getGogh(); maximumPref = mActivities[8];}
        else if (myPreference.getKaleidoscope() > max) {max = myPreference.getGogh(); maximumPref = mActivities[9];}
        else if (myPreference.getRhombuses() > max) {max = myPreference.getGogh(); maximumPref = mActivities[10];}

        return maximumPref;
    }


    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
