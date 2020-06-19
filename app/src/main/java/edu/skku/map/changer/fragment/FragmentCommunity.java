package edu.skku.map.changer.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import edu.skku.map.changer.R;
import edu.skku.map.changer.entities.Post;
import edu.skku.map.changer.entities.PostAdapter;

public class FragmentCommunity extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private List<Post> posts = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_community, container, false);
        recyclerView = rootView.findViewById(R.id.rv_post);
        mProgressBar = rootView.findViewById(R.id.progressBar_com);

        if (posts != null) {
            Bitmap person = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.person);

            Bitmap example1_r = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example1_r);
            posts.add(new Post("박경연", person, example1_r, 0));
            Bitmap example1_f = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example1_f);
            posts.add(new Post("박경연", person, example1_f, 1));
            Bitmap example2_r = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example2_r);
            posts.add(new Post("박경연" , person, example2_r, 2));
            Bitmap example2_f = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example2_f);
            posts.add(new Post("박경연", person, example2_f, 3));
        }

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot snapshots = task.getResult();
                if (snapshots != null) {
                    for (DocumentSnapshot documentSnapshot : snapshots.getDocuments()) {
                        final Post post = new Post(documentSnapshot.getData());


                        firestore.collection(documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                QuerySnapshot snapshots1 = task.getResult();
                                List<Post> posts = new ArrayList<>();
                                for (DocumentSnapshot documentSnapshot1 : snapshots1.getDocuments()) {
                                    Post post = new Post(documentSnapshot1.getData());
                                    Log.v("error", post.toString());
                                    posts.add(post);
                                }
                                //post.set(vendors);
                                posts.add(post);

                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                recyclerView.setAdapter(new PostAdapter(posts));
                                hideDialog();
                            }
                        });
                    }
                }
                else hideDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideDialog();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new PostAdapter(posts));
        return rootView;
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
