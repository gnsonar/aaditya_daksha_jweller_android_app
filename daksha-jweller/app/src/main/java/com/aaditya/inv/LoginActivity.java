package com.aaditya.inv;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aaditya.inv.apis.ApiConnection;
import com.aaditya.inv.databinding.ActivityMainBinding;
import com.aaditya.inv.db.SQLConstants;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.google.android.material.textfield.TextInputEditText;


import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int PHONE_STATE = 1881;
    private final String[] REQUIRED_PERMISSIONS = new String[]{ "android.permission.READ_PRIVILEGED_PHONE_STATE"};
    private final String[] REQUIRED_PERMISSIONS_WS = new String[]{ "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private final String[] REQUIRED_PERMISSIONS_RS = new String[]{ "android.permission.READ_EXTERNAL_STORAGE"};
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE_WS = 101;
    private static final int PERMISSION_REQUEST_CODE_RS = 102;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.login_activity);

        Button login_btn = findViewById(R.id.login_btn);
        TextInputEditText inputUsername = findViewById(R.id.inputUsername);
        TextInputEditText inputPassword = findViewById(R.id.inputPassword);

        ProgressBar progress_bar = findViewById(R.id.login_progress_bar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getApplicationContext());


        if(!allPermissionsGranted(REQUIRED_PERMISSIONS_WS))
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS_WS, PERMISSION_REQUEST_CODE_WS);

        SQLiteDatabase db = sqlLite.getReadableDatabase();
        SQLiteDatabase db1 = sqlLite.getWritableDatabase();
        Cursor cursor = db.query(Constants.SQLiteDatabase.TABLE_NAME, null, null, null, null, null, null, null);

        try {
            new Thread(() -> {
                try {
                    Cursor cursor1 = db.query(Constants.SQLiteDatabase.TABLE_GOLD_RATES, null, null, null, null, null, null, null);
                    if(cursor1.getCount() > 0) {
                        Map<String, Object> body = new HashMap<>();

                        List<Map<String, Object>> currentRateList = new ArrayList<>();
                        while (cursor1.moveToNext()) {
                            Map<String, Object> rate = new HashMap<>();
                            rate.put("carat", cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN)));
                            rate.put("rate", cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN)));
                            rate.put("date_time", cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN)));
                            currentRateList.add(rate);
                        }
                        cursor1 = db.query(Constants.SQLiteDatabase.TABLE_GOLD_RATES_AUDIT, null, null, null, null, null, null, null);
                        List<Map<String, Object>> auditRateList = new ArrayList<>();
                        while (cursor1.moveToNext()) {
                            Map<String, Object> rate = new HashMap<>();
                            rate.put("carat", cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN)));
                            rate.put("rate", cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN)));
                            rate.put("date_time", cursor1.getString(cursor1.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN)));
                            auditRateList.add(rate);
                        }
                        body.put("current_rate_list", currentRateList);
                        body.put("audit_rate_list", auditRateList);
                        //ApiConnection.getInstance().callPostAPI(Constants.APIS_CONSTANTS.VALIDATE_TOKEN_API, null, body);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) { }


        Map<String, String> userDetails = new HashMap<>();
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();


            userDetails.put(Constants.SQLiteDatabase.USERNAME_COLUMN, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.USERNAME_COLUMN)));
            userDetails.put(Constants.SQLiteDatabase.PASSWORD_COLUMN, cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.PASSWORD_COLUMN)));

            InMemoryInfo.branchId = userDetails.get(Constants.SQLiteDatabase.USERNAME_COLUMN);

            // changes to post gold rates

            if(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.LOGGED_IN_COLUMN)).contentEquals("1")) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else {
            ContentValues values = new ContentValues();
            values.put(Constants.SQLiteDatabase.USERNAME_COLUMN, Constants.USER_DETAILS.USERNAME);
            values.put(Constants.SQLiteDatabase.PASSWORD_COLUMN, Constants.USER_DETAILS.PASSWORD);
            values.put(Constants.SQLiteDatabase.LOGGED_IN_COLUMN, false);
            db1.insert(Constants.SQLiteDatabase.TABLE_NAME, null, values);

            // setting details first time
            userDetails.put(Constants.SQLiteDatabase.USERNAME_COLUMN, Constants.USER_DETAILS.USERNAME);
            userDetails.put(Constants.SQLiteDatabase.PASSWORD_COLUMN, Constants.USER_DETAILS.PASSWORD);
        }

        // checking bank details
        cursor = db.query(Constants.SQLiteDatabase.TABLE_BANK_DETAILS, null, null, null, null, null, null, null);
        if (cursor.getCount() <= 0) {
            InMemoryInfo.bankList.stream().forEach(item -> {
                ContentValues values = new ContentValues();
                values.put(Constants.SQLiteDatabase.BANK_DETAILS_NAME, item);
                db1.insert(Constants.SQLiteDatabase.TABLE_BANK_DETAILS, null, values);
            });

        }

        login_btn.setOnClickListener(e -> {
        /*if(!haveNetworkConnection())
            Commons.showAlertBox(this, "Please connect to internet", "Error", true);*/

            String userName = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();

            if(userName == null || userName.isEmpty())
                Toast.makeText(getApplicationContext(), Constants.ENTER_USERNAME, Toast.LENGTH_SHORT).show();

            if(userName != null && !userName.isEmpty() &&
                    (password == null || password.isEmpty()))
                Toast.makeText(getApplicationContext(), Constants.ENTER_PASSWORD, Toast.LENGTH_SHORT).show();

            if(password != null && userName != null && !userName.isEmpty() && !password.isEmpty()) {
                login_btn.setVisibility(View.INVISIBLE);
                progress_bar.setVisibility(View.VISIBLE);

                new Thread(() -> {
                    try {
                        Commons.minimizeKeyboard(getApplicationContext(), login_btn);
                        if(userDetails.get(Constants.SQLiteDatabase.USERNAME_COLUMN).contentEquals(userName) &&
                                userDetails.get(Constants.SQLiteDatabase.PASSWORD_COLUMN).contentEquals(password)) {

                            InMemoryInfo.branchId = userName;
                            db1.execSQL(MessageFormat.format(SQLConstants.SQL_UPDATE_LOGGED_IN_STATUS, true, userName));
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            runOnUiThread(() -> {
                                login_btn.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.INVISIBLE);
                                Commons.showAlertBox(this, "Login Error", "Incorrect username / password", false);
                            });
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        runOnUiThread(() -> {
                            login_btn.setVisibility(View.VISIBLE);
                            progress_bar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        });



    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private boolean allPermissionsGranted(String[] REQUIRED_PERMISSIONS){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getIMEI();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE_WS) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String imei = telephonyManager.getImei();
            Toast.makeText(this, "IMEI: " + imei, Toast.LENGTH_LONG).show();
        } else {
            String imei = telephonyManager.getDeviceId();
            Toast.makeText(this, "IMEI: " + imei, Toast.LENGTH_LONG).show();
        }
    }

}
