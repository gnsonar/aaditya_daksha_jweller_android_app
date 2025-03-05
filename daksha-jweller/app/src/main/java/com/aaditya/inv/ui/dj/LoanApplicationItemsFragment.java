package com.aaditya.inv.ui.dj;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;
import com.aaditya.inv.custom.RecyclerViewAdapterGoldRates;
import com.aaditya.inv.custom.RecyclerViewAdapterLoanItems;
import com.aaditya.inv.databinding.DjLoanApplicationItemsBinding;
import com.aaditya.inv.databinding.DjLoanApplicationItemsEntryBinding;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.dialogs.GoldRatesDialogFragment;
import com.aaditya.inv.dialogs.LoanApplicationItemsPhotoDialogFragment;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.airbnb.lottie.LottieAnimationView;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanApplicationItemsFragment extends Fragment {
    private DjLoanApplicationItemsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DjLoanApplicationItemsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getContext());
        SQLiteDatabase db = sqlLite.getReadableDatabase();
        List<Map<String, String>> itemList = new ArrayList<>();
        int loanId = InMemoryInfo.customerDetails.getAsInteger(Constants.SQLiteDatabase.BANK_LOAN_APP_ID);
        try {
            Cursor cursor = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, null, "loan_id = ?", new String[]{String.valueOf(loanId)}, null, null, null, null);

            int totalItems = 0;
            BigDecimal totalNetWeight = Commons.weightBigDecimal("0.0");
            BigDecimal totalMarketValue = Commons.amountBigDecimal("0.0");
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Map<String, String> item = new HashMap<>();
                    item.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ID, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ID)));
                    item.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC)));
                    item.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY))+"K");
                    item.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS)));
                    item.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT,
                            cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT)));
                    item.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE,
                            cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE)));
                    itemList.add(item);
                    totalItems += cursor.getInt(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS));
                    totalNetWeight = totalNetWeight.add(Commons.weightBigDecimal(item.get(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT)));
                    totalMarketValue = totalMarketValue.add(Commons.amountBigDecimal(item.get(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE)));
                }

                binding.loanApplicationInfoHeader.setVisibility(View.VISIBLE);
                binding.loanApplicationInfoDetails.setVisibility(View.VISIBLE);

                binding.loanApplicationItemsTotalItems.setText(String.valueOf(totalItems));
                binding.loanApplicationItemsTotalNetwt.setText(String.valueOf(totalNetWeight));
                binding.loanApplicationItemsTotalMktval.setText(Commons.convertNumberToINFormat(totalMarketValue.toString()));


                RecyclerView.Adapter items = new RecyclerViewAdapterLoanItems(itemList, getContext(), getActivity());
                binding.loanApplicationItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                binding.loanApplicationItems.setAdapter(items);
            }

            cursor = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, null, "id = ?", new String[]{String.valueOf(loanId)}, null, null, null, null);
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                String loanAmount = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT));
                if(loanAmount != null && !loanAmount.isEmpty())
                    binding.customerLoanAmount.setText(loanAmount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Commons.showAlertBox(getContext(), "Error in application", "application error", false);
        }

        binding.loanApplicationCustomerName.setText(InMemoryInfo.customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME));
        binding.loanApplicationBankName.setText(InMemoryInfo.customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK));

        binding.addNewItem.setOnClickListener((view -> {
            Navigation.findNavController(view).navigate(R.id.loan_application_items_entry_page);
        }));

        binding.viewGoldRates.setOnClickListener((view -> {
            int width = (int) (getResources().getDisplayMetrics().widthPixels*0.90);
            int height = (int) (getResources().getDisplayMetrics().heightPixels*0.90);
            GoldRatesDialogFragment goldRatesDialog = new GoldRatesDialogFragment(getContext());
            goldRatesDialog.getWindow().setLayout(width, height);
            goldRatesDialog.show();
            goldRatesDialog.setOnDismissListener(view1 -> {
                if(InMemoryInfo.updateGoldRate) {
                    InMemoryInfo.updateGoldRate = false;
                    Navigation.findNavController(view).navigate(R.id.loan_application_gold_rate_updates);
                }
            });
        }));

        binding.customerLoanAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ContentValues values = new ContentValues();
                values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT, binding.customerLoanAmount.getText().toString().trim());
                db.update(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, values, "id = ?", new String[]{String.valueOf(loanId)});
            }
        });
        binding.generateForm.setOnClickListener((view -> {
            if(itemList.isEmpty()) {
                Toast.makeText(getContext(), "No Items added", Toast.LENGTH_SHORT).show();
                return;
            }
            if(binding.customerLoanAmount.getText() == null || binding.customerLoanAmount.getText().toString().trim().contentEquals("")) {
                binding.customerLoanAmount.setError("Update loan amount");
                return;
            }
            try {
                Cursor cursorLoan = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, null, "id = ?", new String[]{String.valueOf(loanId)}, null, null, null, null);
                Cursor cursorItems = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, null, "loan_id = ?", new String[]{String.valueOf(loanId)}, null, null, null, null);
                Commons.createAndDownloadForm(getContext(), cursorLoan, cursorItems);
            } catch (Exception e) {
                e.printStackTrace();
                Commons.showAlertBox(getContext(), "Error in generating form", "Application Error", false);
            }
        }));

        binding.viewUploadItemsPhoto.setOnClickListener(view -> {
            LoanApplicationItemsPhotoDialogFragment dialogFragment = new LoanApplicationItemsPhotoDialogFragment(getContext(), getFragmentManager());
            dialogFragment.show();
        });

        requireActivity().
                getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Navigation.findNavController(getView()).popBackStack(R.id.nav_home, false);
                    }
                }
        );
        return root;
    }
}