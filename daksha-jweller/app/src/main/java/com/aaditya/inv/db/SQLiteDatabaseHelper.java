package com.aaditya.inv.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.aaditya.inv.utils.Constants;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "internal_database.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Constants.SQLiteDatabase.TABLE_NAME + " (" +
                    Constants.SQLiteDatabase.USERNAME_COLUMN + " TEXT PRIMARY KEY," +
                    Constants.SQLiteDatabase.PASSWORD_COLUMN + " TEXT," +
                    Constants.SQLiteDatabase.LOGGED_IN_COLUMN + " BOOLEAN)";

    private static final String SQL_BANK_DETAILS = "CREATE TABLE " + Constants.SQLiteDatabase.TABLE_BANK_DETAILS + " (" +
            Constants.SQLiteDatabase.BANK_DETAILS_NAME + " TEXT)";

    private static final String SQL_CREATE_GOLD_RATES = "CREATE TABLE " + Constants.SQLiteDatabase.TABLE_GOLD_RATES + " (" +
            Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN + " FLOAT," +
            Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN + " FLOAT," +
            Constants.SQLiteDatabase.GOLD_RATES_BANK_COLUMN + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE + " TEXT," +
            Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN + " TIMESTAMP)";

    private static final String SQL_CREATE_GOLD_RATES_AUDIT = "CREATE TABLE " + Constants.SQLiteDatabase.TABLE_GOLD_RATES_AUDIT + " (" +
            Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN + " FLOAT," +
            Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN + " FLOAT," +
            Constants.SQLiteDatabase.GOLD_RATES_BANK_COLUMN + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE + " TEXT," +
            Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN + " TIMESTAMP)";

    private static final String SQL_CREATE_LOAN_APPLICATIONS = "CREATE TABLE " + Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION + " (" +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ID + " INT PRIMARY KEY," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_ADDRESS + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO + " INT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ACC_NO + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_BANK + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_BANK + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_JOINT_CUSTODY + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT + " FLOAT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_BAG_PACKET_NO + " INT," +
            Constants.SQLiteDatabase.CREATED_BY + " TEXT," +
            Constants.SQLiteDatabase.CREATED_AT + " TIMESTAMP)";

    private static final String SQL_CREATE_LOAN_APPLICATION_ITEMS = "CREATE TABLE " + Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS + " (" +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ID + " INT PRIMARY KEY," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_LOAN_ID + " INT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS + " INT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_APPROX_WT + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE + " TEXT," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE_DATETIME + " TIMESTAMP," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE + " TEXT," +
            Constants.SQLiteDatabase.CREATED_BY + " TEXT," +
            Constants.SQLiteDatabase.CREATED_AT + " TIMESTAMP)";


    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_GOLD_RATES);
        db.execSQL(SQL_CREATE_GOLD_RATES_AUDIT);
        db.execSQL(SQL_BANK_DETAILS);
        db.execSQL(SQL_CREATE_LOAN_APPLICATIONS);
        db.execSQL(SQL_CREATE_LOAN_APPLICATION_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table " +  Constants.SQLiteDatabase.TABLE_NAME);
        db.execSQL("drop table " +  Constants.SQLiteDatabase.TABLE_BANK_DETAILS);
        db.execSQL("drop table " +  Constants.SQLiteDatabase.TABLE_GOLD_RATES);
        db.execSQL("drop table " +  Constants.SQLiteDatabase.TABLE_GOLD_RATES_AUDIT);
        db.execSQL("drop table " +  Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION);
        db.execSQL("drop table " +  Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS);
        onCreate(db);
    }
}
