package com.example.wyatttowne.freezetrack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.LeftoverHolder> {

    Context context;
    ArrayList<Leftover> leftovers;

    CustomAdapter(Context context, ArrayList<Leftover> leftovers) {
        this.context = context;
        this.leftovers = leftovers;
    }

    @Override
    public LeftoverHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        LeftoverHolder lh = new LeftoverHolder(v);
        return lh;
    }

    @Override
    public void onBindViewHolder(LeftoverHolder holder, final int position) {
        holder.leftoverName.setText(leftovers.get(position).name);
        holder.leftoverTime.setText("Days active: " + String.valueOf(leftovers.get(position).days));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LeftOverActivity.class);
                intent.putExtra("Name", leftovers.get(position).name);
                context.startActivity(intent);
            }
        });

        File dir = new File(Environment.getExternalStorageDirectory()+ "/FreezePics");
        File imgFile = new File(dir, leftovers.get(position).photoName.replace(" ", "_"));

        if(imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.leftoverImage.setImageBitmap(bitmap);
        }else{
            holder.leftoverImage.setImageResource(R.drawable.food_image);
        }
    }

    @Override
    public int getItemCount() {
        return leftovers.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class LeftoverHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView leftoverName;
        TextView leftoverTime;
        ImageView leftoverImage;

        LeftoverHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardWrapper);
            leftoverName = (TextView) itemView.findViewById(R.id.leftover_name);
            leftoverTime = (TextView) itemView.findViewById(R.id.leftover_time);
            leftoverImage = (ImageView) itemView.findViewById(R.id.leftover_image);

            /*itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });*/
        }

    }

}