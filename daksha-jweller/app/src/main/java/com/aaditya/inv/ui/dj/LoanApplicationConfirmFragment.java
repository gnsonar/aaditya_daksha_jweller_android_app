package com.aaditya.inv.ui.dj;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;
import com.aaditya.inv.custom.RecyclerViewAdapterLoanItems;
import com.aaditya.inv.databinding.DjLoanApplicationConfirmBinding;
import com.aaditya.inv.databinding.DjLoanApplicationItemsBinding;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.airbnb.lottie.LottieAnimationView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanApplicationConfirmFragment extends Fragment {
    private DjLoanApplicationConfirmBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        binding = DjLoanApplicationConfirmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LottieAnimationView loading = binding.loadingGears;
        final LinearLayout container1 = binding.container;

        binding.loanApplicationCustomerName.setText(InMemoryInfo.customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME));
        binding.loanApplicationBankName.setText(InMemoryInfo.customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK));

        final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getContext());
        SQLiteDatabase db = sqlLite.getReadableDatabase();

        try {
            int loanId = InMemoryInfo.customerDetails.getAsInteger(Constants.SQLiteDatabase.BANK_LOAN_APP_ID);
            Cursor cursor = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, null, "loan_id = ?", new String[]{String.valueOf(loanId)}, null, null, null, null);

            int totalItems = 0;
            BigDecimal totalNetWeight = new BigDecimal("0.0").setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal totalMarketValue = new BigDecimal("0.0").setScale(2, RoundingMode.HALF_EVEN);
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    totalItems += cursor.getInt(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS));
                    totalNetWeight = totalNetWeight.add(BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT))));
                    totalMarketValue = totalMarketValue.add(BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE))));
                }
                binding.loanApplicationNoOfItems.setText(String.valueOf(totalItems));
                binding.loanApplicationTotalWeight.setText(String.valueOf(totalNetWeight.floatValue()));
                binding.loanApplicationTotalItemsMarketValue.setText(String.valueOf(totalMarketValue.floatValue()));
            }
            binding.customerPhotoConfirm.setImageURI(Uri.parse(InMemoryInfo.customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO)));

            binding.newApplication.setOnClickListener(view -> Navigation.findNavController(view).popBackStack(R.id.loan_application_page_destination, false));
            binding.home.setOnClickListener(view -> Navigation.findNavController(view).popBackStack(R.id.nav_home, false));
        } catch (Exception e) {
            e.printStackTrace();
            Commons.showAlertBox(getContext(), "Error in application", "application error", false);
        }

        return root;
    }

}