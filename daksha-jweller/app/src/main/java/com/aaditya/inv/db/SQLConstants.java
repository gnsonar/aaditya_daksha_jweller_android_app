package com.aaditya.inv.db;

import com.aaditya.inv.utils.Constants;

public class SQLConstants {
    public static final String EQUAL_MARK                       = " = ";
    public static final String LIKE                             = " like ";
    public static final String WHERE                            = " WHERE ";
    public static final String AND                              = " AND ";
    public static final String SQL_UPDATE_LOGGED_IN_STATUS      = "UPDATE " + Constants.SQLiteDatabase.TABLE_NAME + " SET " + Constants.SQLiteDatabase.LOGGED_IN_COLUMN +
            " = {0} WHERE " + Constants.SQLiteDatabase.USERNAME_COLUMN + " = \"{1}\"";
    public static final String AUDIT_RATES_TABLE                = "INSERT INTO " + Constants.SQLiteDatabase.TABLE_GOLD_RATES_AUDIT + " SELECT * FROM " + Constants.SQLiteDatabase.TABLE_GOLD_RATES + " WHERE " + Constants.SQLiteDatabase.GOLD_RATES_BANK_COLUMN + "= \"{0}\" AND " + Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE + "= \"{1}\"";
    public static final String TRUNCATE_RATE_TABLE              = "DELETE FROM " + Constants.SQLiteDatabase.TABLE_GOLD_RATES + " WHERE " + Constants.SQLiteDatabase.GOLD_RATES_BANK_COLUMN + "= \"{0}\" AND " + Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE + "= \"{1}\"";
    public static final String INSERT_RATE                      = "INSERT INTO " + Constants.SQLiteDatabase.TABLE_GOLD_RATES + "(" +
            Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN + "," +
            Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN + "," +
            Constants.SQLiteDatabase.GOLD_RATES_BANK_COLUMN + "," +
            Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE + "," +
            Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN +") VALUES ({0}, {1}, \"{2}\", \"{3}\", \"{4}\")";
    public static final String SELECT_MAX_LOAN_ID               = "SELECT MAX(id) from " + Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION;
    public static final String SELECT_MAX_ITEM_ID               = "SELECT MAX(id) from " + Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS;
    public static final String SEARCH_LOAN_APPLICATION          = "SELECT * from " + Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION;
    public static final String SEARCH_LOAN_APPLICATION_ITEMS    = "SELECT * from " + Constants.SQLiteDatabase.TABLE_LOAN_APPLICATION_ITEMS + " WHERE loan_id = ?";
}
