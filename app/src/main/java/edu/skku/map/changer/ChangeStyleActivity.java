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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.styletransfer.PaintingStyleModels;
import edu.skku.map.changer.entities.Filter;
import edu.skku.map.changer.entities.FilterAdapter;
import edu.skku.map.changer.entities.Post;
import edu.skku.map.changer.entities.Preference;
import edu.skku.map.changer.fragment.FragmentCommunity;

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
    //private ProgressBar mProgressbar;
    private int width;
    private int height;
    private int exifDegree;
    private String filter_flag = "";
    private Preference preference;


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
            public void onClick(View v) {
                Intent intent1 = new Intent(ChangeStyleActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }});
        recyclerView = findViewById(R.id.rv_filter);
       // mProgressbar = findViewById(R.id.progressBar_c);


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
            //finish();
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

                    if (currentFilter.getName().equals("원본")) {
                        changed = original;
                        filter_flag = "";
                    }
                    else {
                        if (currentFilter.getName().equals("반고흐")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getStarryNight());
                            filter_flag =  "Gogh";
                        } else if (currentFilter.getName().equals("뭉크")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getTheScream());
                            filter_flag =  "Munch";
                        } else if (currentFilter.getName().equals("모네")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getPoppyField());
                            filter_flag =  "Monet";
                        } else if (currentFilter.getName().equals("리히텐슈타인")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getBicentennialPrint());
                            filter_flag =  "Lichtenstein";
                        } else if (currentFilter.getName().equals("피카소")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getFemmes());
                            filter_flag =  "Picasso";
                        } else if (currentFilter.getName().equals("쿠터")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getHeadOfClown());
                            filter_flag =  "Kutter";
                        } else if (currentFilter.getName().equals("키리코")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getHorsesOnSeashore());
                            filter_flag =  "Chirico";
                        } else if (currentFilter.getName().equals("시드니놀란")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getTheTrial());
                            filter_flag =  "Nolan";
                        } else if (currentFilter.getName().equals("세베리니")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getRitmoPlastico());
                            filter_flag =  "Severini";
                        } else if (currentFilter.getName().equals("망원경")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getKaleidoscope());
                            filter_flag =  "Kaleidoscope";
                        } else if (currentFilter.getName().equals("마름모")) {
                            predictor = FritzVision.StyleTransfer.getPredictor(paintingStyleModels.getPinkBlueRhombus());
                            filter_flag =  "Rhombuses";
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
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (filter_flag == null || filter_flag.length() != 0) {
                    db.collection("User").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            preference = new Preference(documentSnapshot.getData());
                            preference.updateData(filter_flag);
                            db.collection("User").document(firebaseAuth.getCurrentUser().getEmail()).update(preference.toPreference());
                        }
                    });
                    db.collection("Filter").document("Filter").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            preference = new Preference(documentSnapshot.getData());
                            preference.updateData(filter_flag);
                            db.collection("Filter").document("Filter").update(preference.toPreference());
                        }
                    });
                }
                Log.v("error", filter_flag);

                Toast.makeText(ChangeStyleActivity.this, "사진을 저장했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final CharSequence[] shareWhich = {"해당 앱 사용자에게 공유", "기타 공유"};
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (filter_flag == null || filter_flag.length() != 0) {
                    db.collection("User").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            preference = new Preference(documentSnapshot.getData());
                            preference.updateData(filter_flag);
                            db.collection("User").document(firebaseAuth.getCurrentUser().getEmail()).update(preference.toPreference());
                        }
                    });
                    db.collection("Filter").document("Filter").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            preference = new Preference(documentSnapshot.getData());
                            preference.updateData(filter_flag);
                            db.collection("Filter").document("Filter").update(preference.toPreference());
                        }
                    });
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("공유 범위를 선택하세요")
                        .setItems(shareWhich, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int index){
                                if (index == 0) {
                                    // 파베에 올림
                                    // preference firestore로
                                    //showDialog();

                                    Post post = new Post(firebaseAuth.getCurrentUser().getDisplayName(),
                                            getDate_and_time_Korean(), 0);

                                    // toast 출력
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> postValue = null;
                                    postValue = post.toMap();
                                    final String name = db.collection("Post").document().getId();
                                    db.collection("Post").document(name).set(postValue).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(name);
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            changed.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] data = baos.toByteArray();
                                            UploadTask uploadTask = mStorageRef.putBytes(data);
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    //hideDialog();
                                                    Toast.makeText(ChangeStyleActivity.this, "사진을 공유했습니다", Toast.LENGTH_SHORT).show();
                                                    //hideDialog();
                                                }
                                            });

                                        }
                                    });

                                }
                                else {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    Log.v("error?", changed.toString());

                                    String imagePath = saveToInternalStorage(changed);
                                    Uri shareUri = Uri.parse(imagePath);

                                    sharingIntent.setType("image/*");
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                                    startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                                }
                            }
                        }).setCancelable(true)
                        .show();

                AlertDialog dialog = builder.create();
                dialog.show();
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

    public String getDate_and_time_Korean()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        String datestr = sdf.format(cal.getTime());

        return datestr;
    }

    /*

    private void showDialog(){
        mProgressbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDialog(){
        if(mProgressbar.getVisibility() == View.VISIBLE){
            mProgressbar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent1 = new Intent(ChangeStyleActivity.this, MainActivity.class);
        startActivity(intent1);
        finish();
    }
}
