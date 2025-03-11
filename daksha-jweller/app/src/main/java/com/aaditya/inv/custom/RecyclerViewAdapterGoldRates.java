package com.aaditya.inv.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;

import java.util.List;

public class RecyclerViewAdapterGoldRates extends RecyclerView.Adapter<RecyclerViewAdapterGoldRates.ViewHolder> {

    private final List<Float[]> rateList;

    public RecyclerViewAdapterGoldRates(List<Float[]> rateList) {
        this.rateList = rateList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterGoldRates.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewAdapterGoldRates.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dj_gold_rate_recycler_element, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterGoldRates.ViewHolder viewHolder, int i) {
        viewHolder.karat.setText(rateList.get(i)[0].toString());
        viewHolder.rate.setText(rateList.get(i)[1].toString());
        viewHolder.delete.setOnClickListener(view -> {
            this.rateList.remove(i);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public EditText karat;
        public EditText rate;
        public ImageView delete;

        public ViewHolder(View view) {
            super(view);
            karat = view.findViewById(R.id.gold_karet);
            rate = view.findViewById(R.id.gold_rate);
            delete = view.findViewById(R.id.delete_rate);
        }

        public EditText getKarat() {
            return karat;
        }

        public EditText getRate() {
            return rate;
        }
    }
}
