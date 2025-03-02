package com.aaditya.inv.custom;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterLoanItems extends RecyclerView.Adapter<RecyclerViewAdapterLoanItems.ViewHolder> {
    private Context context;
    private FragmentActivity fragmentActivity;
    private List<Map<String, String>> itemList;

    public RecyclerViewAdapterLoanItems(List<Map<String, String>> itemList, Context ctx, FragmentActivity fragmentActivity) {
        this.itemList = itemList;
        this.context = ctx;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterLoanItems.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewAdapterLoanItems.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dj_loan_application_items_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterLoanItems.ViewHolder viewHolder, int i) {
        viewHolder.description.setText(itemList.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC));
        viewHolder.purity.setText(itemList.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY));
        viewHolder.totalItems.setText(itemList.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS));
        viewHolder.totalWeight.setText(itemList.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT));
        viewHolder.totalAmount.setText(Commons.convertNumberToINFormat(Commons.getTwoPrecisionString(Commons.getTwoPrecisionFloat(itemList.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE)))));
        viewHolder.loan_application_edit_item.setOnClickListener(view -> {
            try {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.LOAN_ITEM_OBJECT, itemList.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ID));
                Navigation.findNavController(view).navigate(R.id.loan_application_items_entry_page, bundle);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), Constants.ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView purity;
        public TextView totalItems;
        public TextView totalWeight;
        public TextView totalAmount;
        public ImageView loan_application_edit_item;

        public ViewHolder(View view) {
            super(view);
            description = view.findViewById(R.id.loan_application_item_desc);
            purity = view.findViewById(R.id.loan_application_item_purity);
            totalItems = view.findViewById(R.id.loan_application_item_total_items);
            totalWeight = view.findViewById(R.id.loan_application_item_total_weight);
            totalAmount = view.findViewById(R.id.loan_application_item_total_amount);
            loan_application_edit_item = view.findViewById(R.id.loan_application_edit_item);
        }
    }
}
