package com.aaditya.inv.ui.home;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;
import com.aaditya.inv.custom.RecyclerViewAdapterHomeScreen;
import com.aaditya.inv.databinding.FragmentHomeBinding;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.models.HomeMenuModel;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.airbnb.lottie.L;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final String[] REQUIRED_PERMISSIONS = new String[]{ "android.permission.CAMERA"};
    private final String[] REQUIRED_PERMISSIONS_IMPORT = new String[]{ "android.permission.READ_EXTERNAL_STORAGE"};
    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    private static final int STORAGE_REQUEST = 1999;
    SQLiteDatabase db = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(!allPermissionsGranted(REQUIRED_PERMISSIONS))
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_IMAGE_CAPTURE);

        if(!allPermissionsGranted(REQUIRED_PERMISSIONS_IMPORT))
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS_IMPORT, REQUEST_IMAGE_CAPTURE);

        final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getContext());
        db = sqlLite.getReadableDatabase();

        String androidId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        if(LocalDateTime.now().isAfter(InMemoryInfo.expiryDateTime)) {
            Commons.showAlertBox(getContext(), "Expired you can't use this app anymore", "Application Error", true);
        }
        if(androidId == null || !InMemoryInfo.androidIDUs.contains(androidId))
            Commons.showAlertBox(getContext(), "This mobile not registered", "Application Error", true);

        List<HomeMenuModel> mainMenu = new ArrayList<>();
        mainMenu.add(new HomeMenuModel("Gold rates",  "Add or update gold rates", ContextCompat.getDrawable(getContext(), R.drawable.purchase)));
        mainMenu.add(new HomeMenuModel("Loan Applications",  "Start new loan applications", ContextCompat.getDrawable(getContext(), R.drawable.payment)));
        mainMenu.add(new HomeMenuModel("View Loan Applications",  "view all loan applications", ContextCompat.getDrawable(getContext(), R.drawable.paymententries)));

        InMemoryInfo.loanAppSearchObject = null;
        RecyclerView.Adapter mainMenuItems = new RecyclerViewAdapterHomeScreen(mainMenu, getContext(), getActivity());
        RecyclerView recyclerView = binding.homeMenus;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mainMenuItems);

        final Gson gson = new Gson();
        binding.exportData.setOnClickListener(view -> {
            Cursor cursor = db.query(Constants.SQLiteDatabase.TABLE_GOLD_RATES,null, null, null, null, null, null, null);
            final Map<String, Object> exportData = new HashMap<>();
            readCursorRecords(cursor, exportData, Constants.RATE_DATA, Constants.RATE_DATA_COLUMNS);

            cursor = db.query(Constants.SQLiteDatabase.TABLE_GOLD_RATES_AUDIT,null, null, null, null, null, null, null);
            readCursorRecords(cursor, exportData, Constants.RATE_AUDIT_DATA, Constants.RATE_DATA_COLUMNS);

            cursor = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION,null, null, null, null, null, null, null);
            readCursorRecords(cursor, exportData, Constants.LOAN_APPS_DATA, Constants.LOAN_APPS_COLUMNS);

            for(Map<String, Object> loanApplication : (List<Map<String, Object>>) exportData.get("loanApplications")) {
                if(loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO) != null) {
                    loanApplication.put(Constants.BANK_LOAN_APP_PHOTO_CONTENT, Commons.getByteFileContent(loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO).toString()));
                }
                if(loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO) != null) {
                    loanApplication.put(Constants.BANK_LOAN_APP_PACKET_PHOTO_CONTENT, Commons.getByteFileContent(loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO).toString()));
                }
            }
            cursor = db.query(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS,null, null, null, null, null, null, null);
            readCursorRecords(cursor, exportData, Constants.LOAN_APPS_ITEMS_DATA, Constants.LOAN_APPS_ITEMS_COLUMNS);

            Commons.saveFileToDownloadsAndroid10Plus("app_export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMYYYYHHmmss")),
                    gson.toJson(exportData),
                    getContext());
        });

        binding.importData.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), STORAGE_REQUEST);
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private boolean allPermissionsGranted(String[] permissions){
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && !allPermissionsGranted(REQUIRED_PERMISSIONS)) {
            Toast.makeText(this.getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == STORAGE_REQUEST && !allPermissionsGranted(REQUIRED_PERMISSIONS_IMPORT)) {
            Toast.makeText(this.getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == STORAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

                Map<String, Object> importedData = new Gson().fromJson(reader, Map.class);

                importDataToDB(importedData);
                inputStream.close();
            } catch (Exception e) {
               e.printStackTrace();
               Commons.showAlertBox(getContext(), "Error in importing data", "Application error", false);
            }
        }
    }

    private void importDataToDB(Map<String, Object> importedData) {
        if(importedData != null && !importedData.isEmpty()) {
            boolean success = true;
            if(importedData.containsKey(Constants.RATE_DATA))
                success = importRecords(Constants.SQLiteDatabase.TABLE_GOLD_RATES, (List<Map<String, Object>>) importedData.get(Constants.RATE_DATA), List.of());
            if(success && importedData.containsKey(Constants.RATE_AUDIT_DATA))
                success = importRecords(Constants.SQLiteDatabase.TABLE_GOLD_RATES_AUDIT, (List<Map<String, Object>>) importedData.get(Constants.RATE_AUDIT_DATA), List.of());
            if(success && importedData.containsKey(Constants.LOAN_APPS_DATA))
                success = importRecords(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION, (List<Map<String, Object>>) importedData.get(Constants.LOAN_APPS_DATA), List.of(Constants.BANK_LOAN_APP_PHOTO_CONTENT, Constants.BANK_LOAN_APP_PACKET_PHOTO_CONTENT));
            if(success && importedData.containsKey(Constants.LOAN_APPS_ITEMS_DATA))
                success = importRecords(Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS, (List<Map<String, Object>>) importedData.get(Constants.LOAN_APPS_ITEMS_DATA), List.of());

            // recreate the images
            if(success && importedData.containsKey(Constants.LOAN_APPS_DATA)) {
                List<Map<String, Object>> loanApps = (List<Map<String, Object>>) importedData.get(Constants.LOAN_APPS_DATA);
                if(loanApps != null && !loanApps.isEmpty()) {
                    loanApps.stream().filter(map -> map.containsKey(Constants.BANK_LOAN_APP_PHOTO_CONTENT) && map.containsKey(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO) &&
                            map.get(Constants.BANK_LOAN_APP_PHOTO_CONTENT) != null && map.get(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO) != null)
                            .forEach(map -> Commons.saveImageBytes(getContext(), Commons.getFileName(map.get(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO).toString()), Base64.getDecoder().decode((String) map.get(Constants.BANK_LOAN_APP_PHOTO_CONTENT))));

                    loanApps.stream().filter(map -> map.containsKey(Constants.BANK_LOAN_APP_PACKET_PHOTO_CONTENT) && map.containsKey(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO) &&
                                    map.get(Constants.BANK_LOAN_APP_PACKET_PHOTO_CONTENT) != null && map.get(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO) != null)
                            .forEach(map -> Commons.saveImageBytes(getContext(), Commons.getFileName(map.get(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO).toString()), Base64.getDecoder().decode((String) map.get(Constants.BANK_LOAN_APP_PACKET_PHOTO_CONTENT))));
                }
            }
            if(success)
                Toast.makeText(getContext(), "Data imported successfully", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "Error importing data or data already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private void readCursorRecords(Cursor cursor, Map<String, Object> exportData, String dataSet, List<String> fields) {
        if(cursor.getCount() > 0) {
            List<Map<String, Object>> rateList = new ArrayList<>();
            while (cursor.moveToNext()) {
                final Map<String, Object> rateData = new HashMap<>();
                fields.forEach(field ->  rateData.put(field,  cursor.getString(cursor.getColumnIndexOrThrow(field))));
                rateList.add(rateData);
            }
            exportData.put(dataSet, rateList);
        }
    }

    private boolean importRecords(String table, List<Map<String, Object>> importDataList, List<String> skipFields) {
        Cursor c = db.query(table, null, null, null, null, null, null, null);

        if(c.getCount() > 0)
            return false;

        c.close();
        try {
            importDataList.stream()
                    .map(map -> {
                        ContentValues values = new ContentValues();
                        map.entrySet().stream()
                                .filter(entry -> !skipFields.contains(entry.getKey()))
                                .forEach(entry -> values.put(entry.getKey(), entry.getValue().toString()));
                        return values;
                    }).forEach(values -> db.insert(table, null, values));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}