package com.example.wyatttowne.freezetrack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        if (!(leftovers.get(position).endDate.equals("None"))) {

            try {
                cal1.setTime(new Date());
                cal2.setTime(formatter.parse(leftovers.get(position).endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(!(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) ||
                    (cal1.get(android.icu.util.Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR))){

                holder.leftoverStatus.setText("EXPIRED");
                holder.leftoverStatus.setTextColor(Color.RED);
                holder.leftoverStatus.setTypeface(null, Typeface.BOLD);

            }else{
                holder.leftoverStatus.setText("GOOD");
                holder.leftoverStatus.setTextColor(Color.GREEN);
                holder.leftoverStatus.setTypeface(null, Typeface.BOLD);
            }

        }else {
            holder.leftoverStatus.setText("No expiration date.");
        }

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
        TextView leftoverStatus;
        ImageView leftoverImage;

        LeftoverHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardWrapper);
            leftoverName = (TextView) itemView.findViewById(R.id.leftover_name);
            leftoverTime = (TextView) itemView.findViewById(R.id.leftover_time);
            leftoverStatus = (TextView) itemView.findViewById(R.id.txtStatus);
            leftoverImage = (ImageView) itemView.findViewById(R.id.leftover_image);

            /*itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });*/
        }

    }

}