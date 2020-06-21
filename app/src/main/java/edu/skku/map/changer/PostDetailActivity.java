package edu.skku.map.changer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import edu.skku.map.changer.entities.Post;
import edu.skku.map.changer.entities.PostAdapter;
import edu.skku.map.changer.entities.Preference;
import edu.skku.map.changer.fragment.FragmentCommunity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostDetailActivity extends AppCompatActivity {
    private String name;
    private String heart;
    private Bitmap profile;
    private Bitmap image;
    private String id;
    private String date;

    private TextView nameTV;
    private TextView heartTV;
    private ImageView profileIV;
    private ImageView imageIV;
    private TextView dateTV;

    private ImageView backButton;
    private ImageView heartButton;
    private TextView saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        nameTV = findViewById(R.id.name_post_detail);
        heartTV = findViewById(R.id.heart_post_detail);
        profileIV = findViewById(R.id.profile_post_detail);
        imageIV = findViewById(R.id.image_post_detail);
        dateTV = findViewById(R.id.date_post_detail);
        imageIV.setScaleType(ImageView.ScaleType.FIT_CENTER);

        backButton = findViewById(R.id.back_button_post_detail);
        heartButton = findViewById(R.id.heart_button_post_detail);
        saveButton = findViewById(R.id.save_button_post_detail);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        heart = intent.getStringExtra("heart");
        //profile = intent.getEx("profile");
        id = intent.getStringExtra("id");
        date = intent.getStringExtra("date");


        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(id);
        final long ONE_MEGABYTE = 1024 * 1024;
        mStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageIV.setImageBitmap(image);
               // hideDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //hideDialog();
            }});

        nameTV.setText(name);
        heartTV.setText(heart);
        dateTV.setText(date);
        Bitmap person = BitmapFactory.decodeResource(this.getResources(), R.drawable.person);
        profileIV.setImageBitmap(person);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Log.v("error2", saveToInternalStorage(image));
                Toast.makeText(PostDetailActivity.this, "사진을 저장했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                Intent intent1 = new Intent(PostDetailActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int heart_num = Integer.parseInt(heart);
                heart_num += 1;
                heart = String.valueOf(heart_num);
                heartTV.setText(heart);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                db.collection("Post").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Post post = new Post(documentSnapshot.getData());
                        post.updateHeart(1);
                        db.collection("Post").document(id).update(post.toMap());
                    }
                });
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

    public String getDate_and_time()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String datestr = sdf.format(cal.getTime());

        return datestr;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent1 = new Intent(PostDetailActivity.this, MainActivity.class);
        startActivity(intent1);
        finish();
    }
}
