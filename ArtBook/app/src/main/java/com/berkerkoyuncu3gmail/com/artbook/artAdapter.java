package com.berkerkoyuncu3gmail.com.artbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berkerkoyuncu3gmail.com.artbook.databinding.ArtRcycBinding;

import java.util.ArrayList;

public class artAdapter extends RecyclerView.Adapter<artAdapter.artHolder> {

    public ArrayList<Art> artList;
    public artAdapter(ArrayList<Art> artList) {
        this.artList = artList;
    }

    @NonNull
    @Override
    public artHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArtRcycBinding artRcycBinding =ArtRcycBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new artHolder(artRcycBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull artHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.recyclerTextview.setText(artList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(),detailsArtbook.class);
                intent.putExtra("info","old");
                intent.putExtra("artId",artList.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artList.size();
    }


    public class artHolder extends RecyclerView.ViewHolder{

        private ArtRcycBinding binding;
        public artHolder(ArtRcycBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
