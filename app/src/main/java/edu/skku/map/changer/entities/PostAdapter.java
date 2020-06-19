package edu.skku.map.changer.entities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.skku.map.changer.PostDetailActivity;
import edu.skku.map.changer.R;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder>{
    List<Post> list;

    public PostAdapter(List<Post> list){
        this.list = list;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, final int position) {
        holder.image.setImageBitmap(list.get(position).image);
        holder.profile.setImageBitmap(list.get(position).profile);
        holder.name.setText(list.get(position).name);
        holder.heart.setText(list.get(position).heart);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}//Adapter



class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView image;
    ImageView profile;
    TextView name;
    TextView heart;

    public PostViewHolder(@NonNull View postView) {
        super(postView);

        image = postView.findViewById(R.id.image_post);
        profile = postView.findViewById(R.id.profile_post);
        name = postView.findViewById(R.id.name_post);
        heart = postView.findViewById(R.id.heart_post);


        postView.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
        v.getContext().startActivity(intent);
    }
}//Holder