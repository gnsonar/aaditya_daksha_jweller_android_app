package com.aaditya.inv.ui.dj;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.custom.RecyclerViewAdapterLoanIViewsItem;
import com.aaditya.inv.databinding.DjLoanApplicationViewsBinding;
import com.aaditya.inv.db.SQLConstants;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.models.LoanApplicationSearch;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoanApplicationViewsFragment extends Fragment {
    private DjLoanApplicationViewsBinding binding;
    SQLiteDatabaseHelper sqlLite = null;
    SQLiteDatabase db = null;
    List<Map<String, String>> loanApplications = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DjLoanApplicationViewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sqlLite = new SQLiteDatabaseHelper(getContext());
        db = sqlLite.getReadableDatabase();

        List<String> loanBankList = new ArrayList<>();
        loanBankList.add("All");
        loanBankList.addAll(InMemoryInfo.loanBankList);
        binding.loanBanks.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, loanBankList));

        if(InMemoryInfo.loanAppSearchObject != null) {
            if(InMemoryInfo.loanAppSearchObject.getLoanId() != null && !InMemoryInfo.loanAppSearchObject.getLoanId().isEmpty())
                binding.packageNo.setText(InMemoryInfo.loanAppSearchObject.getLoanId());

            if(InMemoryInfo.loanAppSearchObject.getCustomerName() != null && !InMemoryInfo.loanAppSearchObject.getCustomerName().isEmpty())
                binding.customerName.setText(InMemoryInfo.loanAppSearchObject.getCustomerName());

            if(InMemoryInfo.loanAppSearchObject.getLoanBank() != null && !InMemoryInfo.loanAppSearchObject.getLoanBank().isEmpty())
                binding.loanBanks.setSelection(loanBankList.indexOf(InMemoryInfo.loanAppSearchObject.getLoanBank()));

            searchLoanApplications();
        }
        binding.findApplications.setOnClickListener(view -> {
            Commons.minimizeKeyboard(getContext(),  binding.findApplications);
            String packageNo = binding.packageNo.getText().toString();
            String customerName = binding.customerName.getText().toString();
            String bankName = binding.loanBanks.getSelectedItem().toString();

            if(InMemoryInfo.loanAppSearchObject == null) {
                InMemoryInfo.loanAppSearchObject = new LoanApplicationSearch(packageNo, customerName, bankName);
            }
            else {
                InMemoryInfo.loanAppSearchObject.setLoanId(packageNo);
                InMemoryInfo.loanAppSearchObject.setCustomerName(customerName);
                InMemoryInfo.loanAppSearchObject.setLoanBank(bankName);
            }
            searchLoanApplications();
        });

        binding.getConsolidatedReport.setOnClickListener(view -> {
            if(loanApplications == null || loanApplications.isEmpty()) {
                Toast.makeText(getContext(), "Please search to generate report", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Commons.downloadConsolidatedReport(getContext(), loanApplications);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error in generating report", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    private void searchLoanApplications() {
        loanApplications.clear();
        List<String> whereClauses = new ArrayList<>();
        if(InMemoryInfo.loanAppSearchObject.getLoanId() != null && !InMemoryInfo.loanAppSearchObject.getLoanId().isEmpty() && Commons.isInt(InMemoryInfo.loanAppSearchObject.getLoanId())) {
            whereClauses.add(Constants.SQLiteDatabase.BANK_LOAN_APP_ID + SQLConstants.EQUAL_MARK + Integer.parseInt(InMemoryInfo.loanAppSearchObject.getLoanId()));
        }
        if(InMemoryInfo.loanAppSearchObject.getCustomerName() != null && !InMemoryInfo.loanAppSearchObject.getCustomerName().isEmpty()) {
            whereClauses.add("LOWER(" + Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME + ")" + SQLConstants.LIKE + "\"%" + InMemoryInfo.loanAppSearchObject.getCustomerName().toLowerCase() + "%\"");
        }
        if(InMemoryInfo.loanAppSearchObject.getLoanBank() != null && !InMemoryInfo.loanAppSearchObject.getLoanBank().isEmpty() && !InMemoryInfo.loanAppSearchObject.getLoanBank().contentEquals("All")) {
            whereClauses.add(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK + SQLConstants.EQUAL_MARK + "\"" + InMemoryInfo.loanAppSearchObject.getLoanBank() + "\"");
        }
        StringBuilder query = new StringBuilder(SQLConstants.SEARCH_LOAN_APPLICATION);
        if(!whereClauses.isEmpty()) {
            query.append(SQLConstants.WHERE).append(String.join(SQLConstants.AND, whereClauses));
        }
        query.append(" ORDER BY " + Constants.SQLiteDatabase.CREATED_AT + " desc");
        Log.i("loan_views", "query: " + query);
        Cursor cursor = db.rawQuery(query.toString(), null);
        if(cursor.getCount() > 0) {
            binding.noLoanAppViews.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                Map<String, String> loanApp = new HashMap<>();
                loanApp.put(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME)));
                loanApp.put(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK)));
                loanApp.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ID, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ID)));
                loanApp.put(Constants.SQLiteDatabase.CREATED_AT,
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.CREATED_AT))).format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
                loanApp.put(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE)));
                loanApp.put(Constants.SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO)));
                loanApp.put(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT)));

                Cursor cursor1 = db.rawQuery(SQLConstants.SEARCH_LOAN_APPLICATION_ITEMS, new String[]{ cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ID)) });
                if(cursor1.getCount() > 0) {
                    loanApp.put(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_ITEM, String.valueOf(cursor1.getCount()));
                    loanApp.put(Constants.SQLiteDatabase.LOAN_APPLICATION_SHOW_GEN_FORM, "true");

                    BigDecimal totalAmount = new BigDecimal("0.0").setScale(2, RoundingMode.HALF_EVEN);
                    BigDecimal totalNetWeight = Commons.weightBigDecimal("0.0");
                    BigDecimal totalGrossWeight = Commons.weightBigDecimal("0.0");
                    while (cursor1.moveToNext()) {
                        totalAmount = totalAmount.add(new BigDecimal(cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE))).setScale(2, RoundingMode.HALF_EVEN));
                        totalNetWeight = totalNetWeight.add(Commons.weightBigDecimal(cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT))));
                        totalGrossWeight = totalGrossWeight.add(Commons.weightBigDecimal(cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT))));
                    }
                    loanApp.put(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_AMOUNT, Commons.getTwoPrecisionString(totalAmount.floatValue()));
                    loanApp.put(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_NET_WT, totalNetWeight.toString());
                    loanApp.put(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_GROSS_WT, totalGrossWeight.toString());
                } else {
                    loanApp.put(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_ITEM, "0");
                    loanApp.put(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_AMOUNT, "0.0");
                    loanApp.put(Constants.SQLiteDatabase.LOAN_APPLICATION_SHOW_GEN_FORM, "false");
                }
                cursor1.close();
                loanApplications.add(loanApp);
            }
        } else {
            binding.noLoanAppViews.setVisibility(View.VISIBLE);
        }
        cursor.close();
        RecyclerView.Adapter<RecyclerViewAdapterLoanIViewsItem.ViewHolder> items = new RecyclerViewAdapterLoanIViewsItem(loanApplications, getContext(), getActivity(), db);
        binding.loanApplications.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.loanApplications.setAdapter(items);
    }
}