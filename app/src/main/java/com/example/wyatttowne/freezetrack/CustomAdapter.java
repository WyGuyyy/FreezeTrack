package com.example.wyatttowne.freezetrack;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.LeftoverHolder> {

    ArrayList<Leftover> leftovers;

    RVAdapter(ArrayList<Leftover> leftovers) {
        this.leftovers = leftovers;
    }

    @Override
    public LeftoverHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        LeftoverHolder lh = new LeftoverHolder(v);
        return lh;
    }

    @Override
    public void onBindViewHolder(LeftoverHolder holder, int position) {
        holder.leftoverName.setText(leftovers.get(position).name);
        holder.leftoverTime.setText(leftovers.get(position).time);
        holder.leftoverImage.setImageResource(leftovers.get(position).photoName); //figure this part out <<<<<
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

        }

    }

}