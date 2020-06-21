package edu.skku.map.changer.entities;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
                intent.putExtra("name", list.get(position).name);
                intent.putExtra("heart", list.get(position).heart);
                //intent.putExtra("profile", list.get(position).profile);
                intent.putExtra("id", list.get(position).id);
                intent.putExtra("date", list.get(position).date);

                v.getContext().startActivity(intent);
                ((Activity)v.getContext()).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}//Adapter



class PostViewHolder extends RecyclerView.ViewHolder{
    ImageView image;
    ImageView profile;
    TextView name;
    TextView heart;
    LinearLayout layout;

    public PostViewHolder(@NonNull View postView) {
        super(postView);

        image = postView.findViewById(R.id.image_post);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profile = postView.findViewById(R.id.profile_post);
        name = postView.findViewById(R.id.name_post);
        heart = postView.findViewById(R.id.heart_post);
        layout = postView.findViewById(R.id.layout_post);
    }
}//Holder