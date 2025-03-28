package com.aaditya.inv.ui.dj;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aaditya.inv.R;
import com.aaditya.inv.dialogs.BottomSheetPhotoUploadDialog;
import com.aaditya.inv.databinding.DjLoanApplicationStartBinding;
import com.aaditya.inv.db.SQLConstants;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoanApplicationStartFragment extends Fragment {
    private DjLoanApplicationStartBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        binding = DjLoanApplicationStartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        InMemoryInfo.loanPhotoUploadedFrom = "";
        BottomSheetPhotoUploadDialog bottomSheet = new BottomSheetPhotoUploadDialog(binding.customerPhoto, getContext(), getActivity());
        final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getContext());
        binding.loanApplicationBanks.setEnabled(true);

        String loanId = bundle != null ? bundle.getString(Constants.LOAN_OBJECT) : null;

        Map<String, String> existingData = new HashMap<>();

        if(loanId == null)
            clearForm();

        SQLiteDatabase db = sqlLite.getReadableDatabase();
        SQLiteDatabase db1 = sqlLite.getWritableDatabase();
        Cursor cursor = db.query(Constants.SQLiteDatabase.TABLE_BANK_DETAILS, null, null, null, null, null, null, null);
        binding.loanApplicationBanks.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, InMemoryInfo.loanBankList));
        binding.loanApplicationType.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, InMemoryInfo.loanTypes));

        List<String> bankList = new ArrayList<>();
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                bankList.add(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_DETAILS_NAME)));
            }
            bankList.add("Other");
            binding.customerBankAccName.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, bankList));
        }
        cursor.close();
        if(loanId != null) {
            binding.loanApplicationPageNo.setText(loanId);
            cursor = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, null, "id = ?", new String[]{loanId}, null, null, null, null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                binding.customerName.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME)));
                binding.customerAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_ADDRESS)));
                binding.customerMobile.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO)));
                binding.customerBankAcc.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ACC_NO)));
                binding.jointCustody.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_JOINT_CUSTODY)));
                binding.loanApplicationBagNo.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_BAG_PACKET_NO)));

                var customerPhoto = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO));
                if(!customerPhoto.isEmpty()) {
                    binding.customerPhoto.setVisibility(View.VISIBLE);
                    binding.customerPhoto.setImageURI(Uri.parse(customerPhoto));
                }
                binding.loanApplicationBanks.setSelection(InMemoryInfo.loanBankList.indexOf(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK))));
                binding.loanApplicationType.setSelection(InMemoryInfo.loanTypes.indexOf(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE))));

                existingData.put(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK, binding.loanApplicationBanks.getSelectedItem().toString());
                existingData.put(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE, binding.loanApplicationType.getSelectedItem().toString());

                binding.loanApplicationPageNoBtnEdit.setVisibility(View.GONE);
                String customerBank = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_BANK));
                if(bankList.contains(customerBank)) {
                    binding.customerBankAccName.setSelection(bankList.indexOf(customerBank));
                } else {
                    binding.customerBankAccName.setSelection(bankList.size() - 1);
                    binding.customerBankName.setVisibility(View.VISIBLE);
                    binding.customerBankName.setText(customerBank);
                }
                bottomSheet.setmCurrentPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO)));
            }
        } else {
            cursor = db.rawQuery(SQLConstants.SELECT_MAX_LOAN_ID, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                binding.loanApplicationPageNo.setText(String.valueOf(cursor.getInt(0) + 1));
            }
            cursor.close();
        }

        binding.loanApplicationPageNoBtnEdit.setOnClickListener(view -> {
            binding.loanApplicationPageNo.setVisibility(View.GONE);
            binding.loanApplicationPageNoBtnEdit.setVisibility(View.GONE);
            binding.loanApplicationPageNoEdit.setVisibility(View.VISIBLE);
            binding.loanApplicationPageNoEdit.setText(binding.loanApplicationPageNo.getText());
        });

        binding.customerBankAccName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i).toString().equalsIgnoreCase("Other")) {
                    binding.otherBankName.setVisibility(View.VISIBLE);
                } else {
                    binding.otherBankName.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.photoCapture.setOnClickListener(view -> {
            bottomSheet.show(getFragmentManager(), "ModalBottomSheet");
        });

        binding.saveAndNext.setOnClickListener((view -> {
            if(validateForms()) {
                try {
                    ContentValues customerDetails = new ContentValues();
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME, binding.customerName.getText().toString());
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_ADDRESS, binding.customerAddress.getText().toString());
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO, binding.customerMobile.getText().toString());
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ACC_NO, binding.customerBankAcc.getText().toString());
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_BANK, binding.customerBankAccName.getSelectedItem().toString().equalsIgnoreCase("Other") ?
                            binding.customerBankName.getText().toString() : binding.customerBankAccName.getSelectedItem().toString());
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK, binding.loanApplicationBanks.getSelectedItem().toString());
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_JOINT_CUSTODY, binding.jointCustody.getText().toString());
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_BAG_PACKET_NO, binding.loanApplicationBagNo.getText().toString());
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE, binding.loanApplicationType.getSelectedItem().toString());

                    customerDetails.put(Constants.SQLiteDatabase.CREATED_AT, LocalDateTime.now().toString());
                    customerDetails.put(Constants.SQLiteDatabase.CREATED_BY, InMemoryInfo.branchId);


                    if(loanId == null) {
                        Cursor cursor1 = db.rawQuery(SQLConstants.SELECT_MAX_LOAN_ID, null);
                        if (cursor1.getCount() > 0) {
                            cursor1.moveToFirst();
                            customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ID, cursor1.getInt(0) + 1);
                        }
                    }

                    if(binding.loanApplicationPageNoEdit.getVisibility() == View.VISIBLE) {
                        String id = binding.loanApplicationPageNoEdit.getText().toString();
                        Cursor c = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, null, "id = ?", new String[]{id}, null, null, null, null);
                        if(c.getCount() > 0) {
                            Toast.makeText(getContext(), "Page No / Book No is already used", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ID, id);
                        }
                        c.close();
                    }

                    if(InMemoryInfo.loanPhotoUploadedFrom.equalsIgnoreCase("gallary")) {
                        bottomSheet.setmCurrentPhotoPath("file:" + Commons.saveImageView(getContext(), binding.customerPhoto));
                    }
                    customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO, bottomSheet.getmCurrentPhotoPath());
                    if(loanId != null) {
                        // get items list
                        Cursor c = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, null, "loan_id = ?", new String[]{loanId}, null, null, null, null);
                        if (c.getCount() > 0 &&
                                (!existingData.get(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK).contentEquals(customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK)) ||
                                        !existingData.get(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE).contentEquals(customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE)))) {


                            final Map<String, ContentValues> itemsUpdates = new HashMap<>();
                            Set<String> missingRates = new HashSet<>();
                            while(c.moveToNext()) {
                                Cursor c1 = db.query(Constants.SQLiteDatabase.TABLE_GOLD_RATES, null,
                                        Constants.SQLiteDatabase.GOLD_RATES_BANK_COLUMN + " = ? AND " + Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE + " = ? AND " + Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN + " = ?",
                                        new String[] {
                                                customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK),
                                                customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE),
                                                c.getString(c.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY))
                                        }, null, null, null, null);

                                if(c1.getCount() > 0) {
                                    c1.moveToLast();
                                    ContentValues values = new ContentValues();
                                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE, c1.getFloat(c1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN)));
                                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE_DATETIME, c1.getString(c1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN)));
                                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE,
                                            Commons.amountBigDecimal(c1.getString(c1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN)))
                                                    .multiply(Commons.amountBigDecimal(c.getString(c.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT)))).toString());

                                    itemsUpdates.put(c.getString(c.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ID)), values);

                                } else {
                                    missingRates.add(c.getString(c.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY)) + "K");
                                }
                                c1.close();
                            }
                            if(c.getCount() == itemsUpdates.size()) {
                                itemsUpdates.forEach((key, value) -> db.update(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, value, "id = ?", new String[]{key}));
                            } else {
                                Commons.showAlertBox(getContext(), new StringBuilder("Gold rates not added for purity(s) ")
                                        .append(String.join(", ", missingRates))
                                        .append(" for ")
                                        .append( customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE))
                                        .append(" of ")
                                        .append(customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK)).toString() , "Gold rate's issue", false);
                                return;
                            }
                        }
                        c.close();
                        db1.update(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, customerDetails, "id = ?", new String[]{loanId});
                        customerDetails.put(Constants.SQLiteDatabase.BANK_LOAN_APP_ID, Integer.valueOf(loanId));
                    } else {
                        db1.insert(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, null, customerDetails);
                    }
                    InMemoryInfo.customerDetails = customerDetails;
                    Toast.makeText(getContext(), "Application with Page No / Book No: " + customerDetails.getAsString(Constants.SQLiteDatabase.BANK_LOAN_APP_ID) + (loanId != null ? " Updated" : " Created"), Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.loan_application_items_page);
                } catch (Exception e) {
                    e.printStackTrace();
                    Commons.showAlertBox(getContext(), "Error in application", "application error", false);
                }
            }
        }));

        binding.clearForm.setOnClickListener(view -> clearForm());
        return root;
    }

    private boolean validateForms() {
        if(binding.customerName.getText().toString().trim().contentEquals(""))
            binding.customerName.setError("Enter customer name");

        if(binding.customerAddress.getText().toString().trim().contentEquals("")) {
            binding.customerAddress.setError("Enter customer address");
        }
        if(binding.customerMobile.getText().toString().trim().contentEquals("")) {
            binding.customerMobile.setError("enter customer mobile no");
        }
        if(binding.customerMobile.getError() == null && binding.customerMobile.getText().toString().trim().length() < 10) {
            binding.customerMobile.setError("enter valid mobile no");
        }
        if(binding.customerBankAcc.getText().toString().trim().contentEquals("")) {
            binding.customerBankAcc.setError("enter customer account number");
        }
        if(binding.otherBankName.getVisibility() == View.VISIBLE &&
                binding.customerBankName.getText().toString().trim().contentEquals("")) {
            binding.customerBankName.setError("enter or select customer bank");
        }
        if(binding.loanApplicationBagNo.getText().toString().trim().contentEquals("")) {
            binding.loanApplicationBagNo.setError("enter bag / packet number");
        }
        if(binding.loanApplicationPageNoEdit.getVisibility() == View.VISIBLE && binding.loanApplicationPageNoEdit.getText().toString().trim().contentEquals("")) {
            binding.loanApplicationPageNoEdit.setError("Enter Page No / Book No");
        }
        return binding.customerName.getError() == null && binding.customerAddress.getError() == null &&
                binding.customerMobile.getError() == null && binding.customerBankAcc.getError() == null &&
                binding.customerBankName.getError() == null && binding.jointCustody.getError() == null &&
                binding.loanApplicationPageNoEdit.getError() == null;
    }

    private void clearForm() {
        binding.customerName.getText().clear();
        binding.customerAddress.getText().clear();
        binding.customerMobile.getText().clear();
        binding.customerBankName.getText().clear();
        binding.jointCustody.getText().clear();
        binding.loanApplicationBanks.setSelection(0);
        binding.customerBankAcc.getText().clear();
        binding.customerBankAccName.setSelection(0);
        binding.customerPhoto.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}