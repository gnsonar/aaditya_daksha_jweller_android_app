package com.aaditya.inv.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;

import java.util.List;

public class RecyclerViewAdapterGoldRatesView extends RecyclerView.Adapter<RecyclerViewAdapterGoldRatesView.ViewHolder> {

    private List<Float[]> rateList;

    public RecyclerViewAdapterGoldRatesView(List<Float[]> rateList) {
        this.rateList = rateList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterGoldRatesView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewAdapterGoldRatesView.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dj_gold_view_rate_recycler_element, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterGoldRatesView.ViewHolder viewHolder, int i) {
        viewHolder.karat.setText(rateList.get(i)[0].toString());
        viewHolder.rate.setText(rateList.get(i)[1].toString());
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView karat;
        public TextView rate;

        public ViewHolder(View view) {
            super(view);
            karat = view.findViewById(R.id.god_rate_view_karat);
            rate = view.findViewById(R.id.gold_rate_view_rate);
        }
    }
}
