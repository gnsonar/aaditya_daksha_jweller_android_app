package com.aaditya.inv.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;
import com.aaditya.inv.custom.RecyclerViewAdapterGoldRatesView;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GoldRatesDialogFragment extends Dialog {
    final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getContext());
    SQLiteDatabase db = sqlLite.getReadableDatabase();

    public GoldRatesDialogFragment(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dj_manage_gold_rate_display, null);

        TextView dateTime =  dialogView.findViewById(R.id.gold_rate_date_time_last_update);
        LinearLayout noGoldRates =  dialogView.findViewById(R.id.no_gold_rate_message);
        RecyclerView goldRates =  dialogView.findViewById(R.id.gold_rate_display_recycler_view);
        AppCompatButton button = dialogView.findViewById(R.id.updatesGoldRates);
        Spinner rateBanks = dialogView.findViewById(R.id.gold_rate_display_bank);

        List<Float[]> rateList = new ArrayList<>();
        rateBanks.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, InMemoryInfo.loanBankList));

        RecyclerView.Adapter goldRatesItems = new RecyclerViewAdapterGoldRatesView(rateList);
        goldRates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        goldRates.setAdapter(goldRatesItems);

        String lastUpdates = Commons.fetchGoldRates(goldRatesItems, db, rateBanks, rateList, dialogView.findViewById(R.id.gold_rate_section), noGoldRates);

        rateBanks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rateList.clear();
                Commons.fetchGoldRates(goldRatesItems, db, rateBanks, rateList, dialogView.findViewById(R.id.gold_rate_section), noGoldRates);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button.setOnClickListener(view -> {
            InMemoryInfo.updateGoldRate = true;
            dismiss();
        });

        dateTime.setText(lastUpdates != null ? LocalDateTime.parse(lastUpdates).format(DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm a")) : "-");
        setContentView(dialogView);
    }


}
