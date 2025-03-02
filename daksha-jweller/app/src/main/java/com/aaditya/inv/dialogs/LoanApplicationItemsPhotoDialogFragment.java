package com.aaditya.inv.dialogs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.aaditya.inv.R;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.CustomListener;
import com.aaditya.inv.utils.InMemoryInfo;

public class LoanApplicationItemsPhotoDialogFragment extends Dialog {
    final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getContext());
    SQLiteDatabase db = sqlLite.getReadableDatabase();
    SQLiteDatabase db1 = sqlLite.getWritableDatabase();
    FragmentManager fragmentManager = null;

    public LoanApplicationItemsPhotoDialogFragment(@NonNull Context context, FragmentManager fragmentManager) {
        super(context);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dj_loan_application_items_photo_dialog, null);

        ImageView loanApplicationItemsImgView =  dialogView.findViewById(R.id.loan_application_items_imageview);
        LinearLayout loanApplicationItemsImgViewLayout =  dialogView.findViewById(R.id.loan_application_items_imageview_layout);
        LinearLayout loanApplicationItemsNoPhotoLayout =  dialogView.findViewById(R.id.loan_application_items_no_photo);
        AppCompatButton button = dialogView.findViewById(R.id.updatesLoanApplicationItemsPhoto);
        int loanId = InMemoryInfo.customerDetails.getAsInteger(Constants.SQLiteDatabase.BANK_LOAN_APP_ID);
        Cursor cursor = null;
        try {
            cursor = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, null, "id = ?", new String[]{String.valueOf(loanId)}, null, null, null, null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                loanApplicationItemsImgViewLayout.setVisibility(View.VISIBLE);
                loanApplicationItemsNoPhotoLayout.setVisibility(View.GONE);

                String itemsPhoto = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO));
                if(itemsPhoto != null && !itemsPhoto.isEmpty()) {
                    loanApplicationItemsImgView.setImageURI(Uri.parse(itemsPhoto));
                } else {
                    loanApplicationItemsNoPhotoLayout.setVisibility(View.VISIBLE);
                    loanApplicationItemsImgViewLayout.setVisibility(View.GONE);
                }
            } else {
                loanApplicationItemsNoPhotoLayout.setVisibility(View.VISIBLE);
                loanApplicationItemsImgViewLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) cursor.close();
        }

        CustomListener customListener = (value) -> {
            if(value != null) {
                try {
                    loanApplicationItemsImgViewLayout.setVisibility(View.VISIBLE);
                    loanApplicationItemsNoPhotoLayout.setVisibility(View.GONE);

                    if(!InMemoryInfo.loanPhotoUploadedFrom.isEmpty() && InMemoryInfo.loanPhotoUploadedFrom.equalsIgnoreCase("gallary")) {
                        value = "file:" + Commons.saveImageView(getContext(), loanApplicationItemsImgView);
                    }
                    loanApplicationItemsImgView.setImageURI(Uri.parse(value));

                    ContentValues values = new ContentValues();
                    values.put(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO, value);
                    db.update(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, values, "id = ?", new String[]{ String.valueOf(loanId) });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error in saving photo", Toast.LENGTH_SHORT).show();
                }
            }
        };
        button.setOnClickListener(view -> {
            BottomSheetPhotoUploadDialog bottomSheet = new BottomSheetPhotoUploadDialog(loanApplicationItemsImgView, getContext(), getOwnerActivity(), customListener);
            bottomSheet.show(this.fragmentManager, "UploadItemsPhoto");

        });
        setContentView(dialogView);
    }

}
