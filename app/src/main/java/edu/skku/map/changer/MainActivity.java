package edu.skku.map.changer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.io.File;
import java.io.IOException;

import ai.fritz.core.Fritz;
import edu.skku.map.changer.fragment.FragmentAnalysis;
import edu.skku.map.changer.fragment.FragmentCommunity;

public class MainActivity extends AppCompatActivity {
    SpaceNavigationView spaceNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FragmentCommunity fragmentCommunity;
    private FragmentAnalysis fragmentAnalysis;
    private static final int REQUEST_CODE = 0;
    private File newImageFile;
    private Uri uri;
    private static final String API_KEY = "5168cce7d9794bc299881a6d6966e1b1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fritz.configure(this, API_KEY);

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.navibar);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.showIconOnly();
        spaceNavigationView.addSpaceItem(new SpaceItem("COMMUNITY", R.drawable.ic_home_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("ANALYSIS", R.drawable.ic_statistical_analysis));


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent gallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallary, REQUEST_CODE);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemName)
                {
                    case "COMMUNITY":
                        setFrag(0);
                        break;
                    case "ANALYSIS":
                        setFrag(1);
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) { }
        });

        fragmentCommunity = new FragmentCommunity();
        fragmentAnalysis = new FragmentAnalysis();
        setFrag(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    private void setFrag(int i)
    {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (i)
        {
            case 0:
                ft.replace(R.id.frameLayout_main,fragmentCommunity);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.frameLayout_main,fragmentAnalysis);
                ft.commit();
                break;
        }
    }


    // GET IMAGE FROM USER STORAGE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                Cursor cursor = null;

                try {
                    String[] proj = {MediaStore.Images.Media.DATA};

                    assert photoUri != null;
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();
                    newImageFile = new File(cursor.getString(column_index));

                    uri = photoUri;
                    Intent intent = new Intent(this, ChangeStyleActivity.class);
                    intent.putExtra("uri", uri);
                    startActivity(intent);

                } catch (Exception e) { }
                finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택을 취소하였습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
