package edu.skku.map.changer;

import ai.fritz.core.FritzManagedModel;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.PredictorStatusListener;
import ai.fritz.vision.base.FritzVisionPredictorOptions;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictorOptions;
import ai.fritz.vision.styletransfer.FritzVisionStyleResult;
import ai.fritz.vision.styletransfer.PaintingManagedModels;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Bitmap changed;
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
    protected void onCreate(final Bundle savedInstanceState) {
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
        String path = String.valueOf(intent.getParcelableExtra("uri"));
        uri = Uri.parse(path);
        imageView.setImageURI(uri);


        try {
            ExifInterface exif = new ExifInterface(getRealPathFromURI(uri));
            Log.v("error", "next");
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } catch (IOException e) {
            Log.v("error", e.toString());
            Log.v("error", e.getMessage());
            e.printStackTrace();
        }


        // uri를 bitmap으로 변환
        try {
            original = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            width = original.getWidth();
            height = original.getHeight();
            changed = original;
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

                    if (currentFilter.getName().equals("원본")) { changed = original; }
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
                        changed = styleResult.toBitmap(target);
                    }

                    imageView.setImageBitmap(rotate(changed, exifDegree));
                    return true;
                }
                //hideDialog();
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Log.v("error2", saveToInternalStorage(changed));
                Toast.makeText(ChangeStyleActivity.this, "사진을 저장했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Log.v("error?", changed.toString());

                String imagePath = saveToInternalStorage(changed);
                Uri shareUri = Uri.parse(imagePath);

                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String saveToInternalStorage(Bitmap bitmapImage){
        /*
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("Changer", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,"profile.jpg");

         */
        File root =new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Changer");
        if (!root.exists()) {root.mkdir();}
        File mypath = new File(root, getDate_and_time() + ".jpg");

        FileOutputStream fos = null;
        Log.v("error4", mypath.toString());
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("error1", e.toString());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.v("error1", e.getMessage());
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
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

    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
       // Uri uri = Uri.fromFile(new File(path));

        cursor.close();
        return path;
    }

    public String getDate_and_time()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String datestr = sdf.format(cal.getTime());

        return datestr;
    }
}
