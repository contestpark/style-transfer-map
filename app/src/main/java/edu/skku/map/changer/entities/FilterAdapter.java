package edu.skku.map.changer.entities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import edu.skku.map.changer.R;


public class FilterAdapter extends RecyclerView.Adapter<FilterViewHolder>{
    List<Filter> list;

    public FilterAdapter(List<Filter> list){
        this.list = list;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, final int position) {
        holder.textView1.setText(list.get(position).name);
        holder.textView1.setBackgroundResource(list.get(position).color);
        holder.imageView.setBackgroundResource(list.get(position).image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}//Adapter



class FilterViewHolder extends RecyclerView.ViewHolder{
    TextView textView1;
    ImageView imageView;

    public FilterViewHolder(@NonNull View filterView) {
        super(filterView);

        textView1 = filterView.findViewById(R.id.name_filter);
        imageView = filterView.findViewById(R.id.image_filter);
;
    }

}//Holder