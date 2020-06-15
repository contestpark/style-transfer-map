package edu.skku.map.changer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.styletransfer.PaintingManagedModels;
import ai.fritz.vision.styletransfer.PaintingStyleModels;
import edu.skku.map.changer.entities.Filter;
import edu.skku.map.changer.entities.FilterAdapter;

public class ChangeStyleActivity extends AppCompatActivity {
    private Uri uri;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private List<Filter> filters = new ArrayList<>();
    private Bitmap bitmapImage;
    private ImageView back_button;
    private TextView save_button;
    private TextView share_button;
    private FritzVisionStylePredictor predictor;
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_style);


        imageView = findViewById(R.id.image_change_style);
        share_button = findViewById(R.id.share_button_change_style);
        save_button = findViewById(R.id.save_button_change_style);
        back_button = findViewById(R.id.back_button_change_style);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }});
        recyclerView = findViewById(R.id.rv_filter);

        // 사진 받아와서 띄우기
        Intent intent = getIntent();
        uri = Uri.parse(String.valueOf(intent.getParcelableExtra("uri")));
        imageView.setImageURI(uri);

        // uri를 bitmap으로 변환
        try {
            bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            finish();
        }

        // filter 받아오기
        if (filters.isEmpty())
        {
            filters.add(new Filter(R.color.colorLightLight,R.color.colorAccent,"원본"));
            filters.add(new Filter(R.drawable.filter1_starry_night,R.color.color1,"반고흐"));
            filters.add(new Filter(R.drawable.filter2_scream,R.color.color2,"뭉크"));
            filters.add(new Filter(R.drawable.filter3_puppy,R.color.color3,"모네"));
            filters.add(new Filter(R.drawable.filter4_century,R.color.color4,"리히텐슈타인"));
            filters.add(new Filter(R.drawable.filter5_femmes,R.color.color5,"피카소"));
            filters.add(new Filter(R.drawable.filter6_clown,R.color.color6,"쿠터"));
            filters.add(new Filter(R.drawable.filter7_horses,R.color.color7,"키리코"));
            filters.add(new Filter(R.drawable.filter8_trial,R.color.color8,"시드니놀란"));
            filters.add(new Filter(R.drawable.filter9_plasrico,R.color.color9,"세베리니"));
            filters.add(new Filter(R.drawable.filter10_kaleidoscope,R.color.color10,"망원경"));
            filters.add(new Filter(R.drawable.filter11_rhombuses,R.color.color11,"마름모"));
        }
        FritzVisionImage fritzVisionImage = FritzVisionImage.fromBitmap(bitmapImage);



        gestureDetector = new GestureDetector(getApplicationContext(),new GestureDetector.SimpleOnGestureListener() {
            //누르고 뗄 때 한번만 인식하도록 하기위해서
            @Override
            public boolean onSingleTapUp(MotionEvent e) { return true; }
        });



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new FilterAdapter(filters));
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    int currentPosition = rv.getChildAdapterPosition(childView);
                    Filter currentFilter = filters.get(currentPosition);

                    if (currentFilter.getName().equals("반고흐")){ predictor = FritzVision.StyleTransfer.getPredictor();
                    }
                    else if (currentFilter.getName().equals("뭉크")){}
                    else if (currentFilter.getName().equals("모네")){}
                    else if (currentFilter.getName().equals("리히텐슈타인")){}
                    else if (currentFilter.getName().equals("피카소")){}
                    else if (currentFilter.getName().equals("쿠터")){}
                    else if (currentFilter.getName().equals("키리코")){}
                    else if (currentFilter.getName().equals("시드니놀란")){}
                    else if (currentFilter.getName().equals("세베리니")){}
                    else if (currentFilter.getName().equals("망원경")){}
                    else if (currentFilter.getName().equals("마름모")){}
                    else {

                    }


                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
        });
    }
}
