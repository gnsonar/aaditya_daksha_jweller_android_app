package com.aaditya.inv.custom;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;
import com.aaditya.inv.db.SQLConstants;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterLoanIViewsItem extends RecyclerView.Adapter<RecyclerViewAdapterLoanIViewsItem.ViewHolder> {
    private Context context;
    private FragmentActivity fragmentActivity;
    private List<Map<String, String>> loanApplications;
    private SQLiteDatabase db = null;
    public RecyclerViewAdapterLoanIViewsItem(List<Map<String, String>> loanApplications, Context ctx, FragmentActivity fragmentActivity, SQLiteDatabase db) {
        this.loanApplications = loanApplications;
        this.context = ctx;
        this.fragmentActivity = fragmentActivity;
        this.db = db;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterLoanIViewsItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerViewAdapterLoanIViewsItem.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dj_loan_application_views_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterLoanIViewsItem.ViewHolder viewHolder, int i) {
        viewHolder.customerName.setText(loanApplications.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME));
        viewHolder.loanBank.setText(loanApplications.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK));
        viewHolder.packageNo.setText(loanApplications.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ID));
        viewHolder.totalItems.setText(loanApplications.get(i).get(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_ITEM));
        viewHolder.totalAmount.setText(Commons.convertNumberToINFormat(loanApplications.get(i).get(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_AMOUNT)));
        viewHolder.totalNetWt.setText(loanApplications.get(i).get(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_NET_WT));
        viewHolder.totalGrossWt.setText(loanApplications.get(i).get(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_GROSS_WT));

        if(loanApplications.get(i).get(Constants.SQLiteDatabase.LOAN_APPLICATION_SHOW_GEN_FORM).contentEquals("true")) {
            viewHolder.pdfGenerate.setVisibility(View.VISIBLE);
            viewHolder.pdfGenerate.setOnClickListener(view -> {
                try {
                    Cursor cursorLoan = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, null, "id = ?", new String[]{ loanApplications.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ID) }, null, null, null, null);
                    Cursor cursorItems = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, null, "loan_id = ?", new String[]{ loanApplications.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ID) }, null, null, null, null);
                    Commons.createAndDownloadForm(context, cursorLoan, cursorItems);
                } catch (Exception e) {
                    e.printStackTrace();
                    Commons.showAlertBox(context, "Error in generating form", "Application Error", false);
                }
            });
        }

        viewHolder.loan_applications_edit_application.setOnClickListener(view -> {
            try {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.LOAN_OBJECT, loanApplications.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ID));
                Navigation.findNavController(view).navigate(R.id.to_loan_application_page, bundle);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), Constants.ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.deleteApplication.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Delete confirmation")
                    .setMessage("Do you want to delete this application?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (v1, v2) -> {
                        db.delete(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, "id = ?", new String[]{loanApplications.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ID)});
                        db.delete(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, "loan_id = ?", new String[]{loanApplications.get(i).get(Constants.SQLiteDatabase.BANK_LOAN_APP_ID)});
                        Toast.makeText(context, "Loan application deleted", Toast.LENGTH_SHORT).show();
                        loanApplications.remove(i);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", (v1, v2) -> v1.dismiss())
                    .show();

        });
    }

    @Override
    public int getItemCount() {
        return loanApplications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView customerName;
        public TextView loanBank;
        public TextView packageNo;
        public TextView totalItems;
        public TextView totalAmount;
        public TextView totalNetWt;
        public TextView totalGrossWt;
        public ImageView pdfGenerate;
        public ImageView deleteApplication;
        public ImageView loan_applications_edit_application;
        public ViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.loan_application_views_customer_name);
            loanBank = view.findViewById(R.id.loan_application_views_bank_name);
            totalItems = view.findViewById(R.id.loan_application_views_total_items);
            totalAmount = view.findViewById(R.id.loan_application_views_total_amount);
            totalNetWt = view.findViewById(R.id.loan_application_views_total_net_wt);
            totalGrossWt = view.findViewById(R.id.loan_application_views_total_gross_wt);
            packageNo = view.findViewById(R.id.loan_application_views_package_no);
            pdfGenerate = view.findViewById(R.id.loan_applications_form_download);
            deleteApplication = view.findViewById(R.id.loan_applications_delete_application);
            loan_applications_edit_application = view.findViewById(R.id.loan_applications_edit_application);
        }
    }
}
