package com.aaditya.inv.ui.dj;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aaditya.inv.R;
import com.aaditya.inv.databinding.DjLoanApplicationItemsEntryBinding;
import com.aaditya.inv.databinding.DjLoanApplicationStartBinding;
import com.aaditya.inv.db.SQLConstants;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.airbnb.lottie.LottieAnimationView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LoanApplicationItemsEntryFragment extends Fragment {
    private DjLoanApplicationItemsEntryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String itemId = bundle != null ? bundle.getString(Constants.LOAN_ITEM_OBJECT) : null;

        binding = DjLoanApplicationItemsEntryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getContext());


        SQLiteDatabase db = sqlLite.getReadableDatabase();
        SQLiteDatabase db1 = sqlLite.getWritableDatabase();
        Cursor cursor = db.query(Constants.SQLiteDatabase.TABLE_GOLD_RATES, null, "bank = ?", new String[]{InMemoryInfo.customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK)}, null, null, null, null);

        List<String> karatList = new ArrayList<>();
        List<Map<String, String>> rateKaratList = new ArrayList<>();

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Map<String, String> rate = new HashMap<>();
                rate.put(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN)));
                rate.put(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN)));
                rate.put(Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN)));
                rateKaratList.add(rate);
            }
            if(!rateKaratList.isEmpty()) {
                karatList.clear();
                binding.loanApplicationPurity.setVisibility(View.VISIBLE);
                binding.noGoldRates.setVisibility(View.GONE);
                karatList.addAll(rateKaratList.stream().map(map -> map.get(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN) + "K").collect(Collectors.toList()));
                binding.loanApplicationPurity.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, karatList));
            }
        } else {
            binding.loanApplicationPurity.setVisibility(View.GONE);
            binding.noGoldRates.setVisibility(View.VISIBLE);
        }
        binding.saveItem.setText(" Save & Add New ");
        if(itemId != null) {
            binding.saveItem.setText(" Update ");
            cursor = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, null, "id = ?", new String[]{itemId}, null, null, null, null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                binding.itemDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC)));
                binding.itemNumbers.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS)));
                binding.itemGrossWeight.setText(Commons.weightBigDecimal(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT))).toString());
                binding.itemNetWeight.setText(Commons.weightBigDecimal(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT))).toString());
                binding.itemApproxWeight.setText(Commons.weightBigDecimal(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_APPROX_WT))).toString());
                binding.loanApplicationPurity.setSelection(karatList.indexOf(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY))+"K"));
            }
        }
        binding.loanApplicationPurity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedKarat = binding.loanApplicationPurity.getSelectedItem().toString().replace("K", "");
                rateKaratList.stream().filter(map -> map.get(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN).equalsIgnoreCase(selectedKarat))
                        .findFirst()
                        .ifPresent((map) -> {
                            binding.lonApplicationEntryRate.setText(map.get(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN));
                            BigDecimal selectedKaratRate = Commons.amountBigDecimal(map.get(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN));
                            if(binding.itemNetWeight.getText() != null && !binding.itemNetWeight.getText().toString().trim().isEmpty()){
                                BigDecimal marketValue = selectedKaratRate.multiply(Commons.weightBigDecimal(binding.itemNetWeight.getText().toString().trim()));
                                binding.lonApplicationEntryMarketValue.setText(marketValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.itemNetWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable != null && binding.loanApplicationPurity.getVisibility() == View.VISIBLE && !editable.toString().trim().isEmpty()) {
                    String selectedKarat = binding.loanApplicationPurity.getSelectedItem().toString().replace("K", "");
                    rateKaratList.stream().filter(map -> map.get(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN).equalsIgnoreCase(selectedKarat))
                            .findFirst()
                            .ifPresent((map) -> {
                                binding.lonApplicationEntryRate.setText(map.get(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN));
                                BigDecimal selectedKaratRate = Commons.amountBigDecimal(map.get(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN));
                                if(binding.itemNetWeight.getText() != null && !binding.itemNetWeight.getText().toString().trim().isEmpty()){
                                    BigDecimal marketValue = selectedKaratRate.multiply(Commons.weightBigDecimal(binding.itemNetWeight.getText().toString().trim()));
                                    binding.lonApplicationEntryMarketValue.setText(marketValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                                }
                            });
                }
            }
        });

        binding.saveItem.setOnClickListener(view -> {
            try {
                if(validateForms()) {
                    ContentValues values = new ContentValues();
                    int id = 1;
                    if(itemId == null) {
                        Cursor cursor1 = db.rawQuery(SQLConstants.SELECT_MAX_ITEM_ID, null);
                        if (cursor1.getCount() > 0) {
                            cursor1.moveToFirst();
                            id = cursor1.getInt(0) + 1;
                        }
                        values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ID, id);
                    }
                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_LOAN_ID, InMemoryInfo.customerDetails.getAsInteger(Constants.SQLiteDatabase.BANK_LOAN_APP_ID));
                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC, binding.itemDescription.getText().toString().trim());
                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS, Integer.valueOf(binding.itemNumbers.getText().toString().trim()));
                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT, Commons.weightBigDecimal((binding.itemGrossWeight.getText().toString().trim())).toString());
                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_APPROX_WT, Commons.weightBigDecimal(binding.itemApproxWeight.getText().toString().trim()).toString());
                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT, Commons.weightBigDecimal(binding.itemNetWeight.getText().toString().trim()).toString());


                    String selectedKarat = binding.loanApplicationPurity.getSelectedItem().toString().replace("K", "");
                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY, Commons.getTwoPrecisionFloat(selectedKarat));
                    rateKaratList.stream().filter(map -> map.get(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN).equalsIgnoreCase(selectedKarat))
                            .findFirst()
                            .ifPresent((map) -> {
                                values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE, Commons.getTwoPrecisionFloat(map.get(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN)));
                                values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE_DATETIME, map.get(Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN));
                            });

                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE,
                            Commons.weightBigDecimal(binding.lonApplicationEntryMarketValue.getText().toString()).toString());
                    values.put(Constants.SQLiteDatabase.CREATED_AT, InMemoryInfo.branchId);
                    values.put(Constants.SQLiteDatabase.CREATED_BY, LocalDateTime.now().toString());

                    if(itemId != null)
                        db1.update(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, values, "id = ?", new String[]{itemId});
                    else
                        db1.insert(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, null, values);

                    Toast.makeText(getContext(), "loan item " + (itemId == null ? "saved" : "updated"), Toast.LENGTH_SHORT).show();
                    if(itemId == null)
                        clearForm();
                    else
                        Navigation.findNavController(view).popBackStack();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Commons.showAlertBox(getContext(), "Error in saving item", "application error", false);
            }

        });

        binding.clearForm.setOnClickListener(view -> clearForm());
        binding.backForm.setOnClickListener(view -> Navigation.findNavController(view).popBackStack());
        return root;
    }

    private void clearForm() {
        binding.itemDescription.getText().clear();
        binding.itemNumbers.getText().clear();
        binding.itemGrossWeight.getText().clear();
        binding.itemApproxWeight.getText().clear();
        binding.itemNetWeight.getText().clear();
        binding.loanApplicationPurity.setSelection(0);
        binding.lonApplicationEntryMarketValue.setText("-");
    }
    private boolean validateForms() {
        if(binding.itemDescription.getText().toString().trim().contentEquals(""))
            binding.itemDescription.setError("enter item description");

        if(binding.itemNumbers.getText().toString().trim().contentEquals("")) {
            binding.itemNumbers.setError("enter number of items");
        }
        if(binding.itemGrossWeight.getText().toString().trim().contentEquals("")) {
            binding.itemGrossWeight.setError("enter item gross weight");
        }
        if(binding.itemApproxWeight.getText().toString().trim().contentEquals("")) {
            binding.itemApproxWeight.setError("enter item approx weight");
        }
        if(binding.itemNetWeight.getText().toString().trim().contentEquals("")) {
            binding.itemNetWeight.setError("enter item net weight");
        }
        if(binding.loanApplicationPurity.getVisibility() == View.GONE) {
            Toast.makeText(getContext(), "update and select purity", Toast.LENGTH_SHORT).show();
        }

        return binding.itemDescription.getError() == null && binding.itemNumbers.getError() == null &&
                binding.itemGrossWeight.getError() == null && binding.itemApproxWeight.getError() == null &&
                binding.itemNetWeight.getError() == null && binding.loanApplicationPurity.getVisibility() != View.GONE;
    }
}