package edu.skku.map.changer.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.skku.map.changer.R;
import edu.skku.map.changer.entities.Post;
import edu.skku.map.changer.entities.PostAdapter;

public class FragmentCommunity extends Fragment {
    private RecyclerView recyclerView;
    private List<Post> posts = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_community, container, false);
        recyclerView = rootView.findViewById(R.id.rv_post);

        if (posts != null) {
            Bitmap person = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.person);

            Bitmap example1_r = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example1_r);
            posts.add(new Post("박경연", person, example1_r, "0"));
            Bitmap example1_f = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example1_f);
            posts.add(new Post("박경연", person, example1_f, "1"));
            Bitmap example2_r = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example2_r);
            posts.add(new Post("박경연" , person, example2_r, "2"));
            Bitmap example2_f = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example2_f);
            posts.add(new Post("박경연", person, example2_f, "3"));
        }

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new PostAdapter(posts));
        return rootView;
    }
}
