package edu.skku.map.changer;

import ai.fritz.core.FritzManagedModel;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.PredictorStatusListener;
import ai.fritz.vision.base.FritzVisionPredictorOptions;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictorOptions;
import ai.fritz.vision.styletransfer.FritzVisionStyleResult;
import ai.fritz.vision.styletransfer.PaintingManagedModels;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.styletransfer.PaintingStyleModels;
import edu.skku.map.changer.entities.Filter;
import edu.skku.map.changer.entities.FilterAdapter;

public class ChangeStyleActivity extends AppCompatActivity {
    private Uri uri;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private List<Filter> filters = new ArrayList<>();
    private Bitmap original;
    private ImageView back_button;
    private TextView save_button;
    private TextView share_button;
    private FritzVisionStylePredictor predictor;
    private GestureDetector gestureDetector;
    private PaintingStyleModels paintingStyleModels;
    private int width;
    private int height;
    private int exifDegree;
   // private ProgressBar progressBar;


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
       // progressBar = findViewById(R.id.progressBar_c);


        // 사진 받아와서 띄우기
        Intent intent = getIntent();
        uri = Uri.parse(String.valueOf(intent.getParcelableExtra("uri")));
        imageView.setImageURI(uri);


        try {
            ExifInterface exif = new ExifInterface(uri.getPath());
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } catch (IOException e) {
            Log.v("error", "갸아아악");
            e.printStackTrace();
        }


        // uri를 bitmap으로 변환
        try {
            original = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            width = original.getWidth();
            height = original.getHeight();
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
        FritzVisionImage fritzVisionImage = FritzVisionImage.fromBitmap(original);
        paintingStyleModels = FritzVisionModels.getPaintingStyleModels();



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
                //showDialog();

                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    int currentPosition = rv.getChildAdapterPosition(childView);
                    Filter currentFilter = filters.get(currentPosition);

                    if (currentFilter.getName().equals("원본")) { imageView.setImageBitmap(original); }
                    else {
                        if (currentFilter.getName().equals("반고흐")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getStarryNight());
                        } else if (currentFilter.getName().equals("뭉크")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getTheScream());
                        } else if (currentFilter.getName().equals("모네")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getPoppyField());
                        } else if (currentFilter.getName().equals("리히텐슈타인")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getBicentennialPrint());
                        } else if (currentFilter.getName().equals("피카소")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getFemmes());
                        } else if (currentFilter.getName().equals("쿠터")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getHeadOfClown());
                        } else if (currentFilter.getName().equals("키리코")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getHorsesOnSeashore());
                        } else if (currentFilter.getName().equals("시드니놀란")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getTheTrial());
                        } else if (currentFilter.getName().equals("세베리니")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getRitmoPlastico());
                        } else if (currentFilter.getName().equals("망원경")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getKaleidoscope());
                        } else if (currentFilter.getName().equals("마름모")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getPinkBlueRhombus());
                        }


                        FritzVisionImage visionImage = FritzVisionImage.fromBitmap(original);
                        FritzVisionStyleResult styleResult = predictor.predict(visionImage);
                        //Bitmap styledBitmap = styleResult.toBitmap();

                        //Size targetSize = new Size(2048, 2048);
                        //Bitmap bitmap = styleResult.toBitmap(targetSize);
                        Size target = new Size(width, height);
                        Bitmap styledBitmap = styleResult.toBitmap(target);
                        imageView.setImageBitmap(rotate(styledBitmap, exifDegree));
                        //hideDialog();
                        return true;
                    }
                }
                //hideDialog();
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
        });
    }


    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /*
    private void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDialog(){
        if(progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

     */

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; }
        return 0;
    }
}
