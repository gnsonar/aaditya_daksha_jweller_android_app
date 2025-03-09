package com.aaditya.inv.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface Constants {

    interface APIS_CONSTANTS {
        String API_HOST = "http://13.201.4.8/invapp";
        String EVENT_API = "/loan_app/event_details";
    }

    List<String> RATE_DATA_COLUMNS = Arrays.asList(SQLiteDatabase.GOLD_RATES_RATE_COLUMN,
            SQLiteDatabase.GOLD_RATES_KARAT_COLUMN,
            SQLiteDatabase.GOLD_RATES_BANK_COLUMN,
            SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE,
            SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN);

    List<String> LOAN_APPS_COLUMNS = Arrays.asList(SQLiteDatabase.BANK_LOAN_APP_ID,
            SQLiteDatabase.BANK_LOAN_APP_CUST_NAME,
            SQLiteDatabase.BANK_LOAN_APP_CUST_ADDRESS,
            SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO,
            SQLiteDatabase.BANK_LOAN_APP_ACC_NO,
            SQLiteDatabase.BANK_LOAN_APP_BANK,
            SQLiteDatabase.BANK_LOAN_APP_CUST_BANK,
            SQLiteDatabase.BANK_LOAN_APP_JOINT_CUSTODY,
            SQLiteDatabase.BANK_LOAN_APP_PHOTO,
            SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO,
            SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE,
            SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT,
            SQLiteDatabase.BANK_LOAN_APP_BAG_PACKET_NO,
            SQLiteDatabase.CREATED_BY,
            SQLiteDatabase.CREATED_AT);

    List<String> LOAN_APPS_ITEMS_COLUMNS = Arrays.asList(SQLiteDatabase.BANK_LOAN_APP_ITEMS_ID,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_LOAN_ID,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_APPROX_WT,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE_DATETIME,
            SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE,
            SQLiteDatabase.CREATED_BY,
            SQLiteDatabase.CREATED_AT);

    String UTF_8_ENCODING                   = "UTF-8";
    String WORK_IN_PROGRESS_MSG             = "Work In-Progress";
    String ERROR_MESSAGE                    = "Something went wrong please try again";
    String ENTER_USERNAME                   = "Enter username";
    String ENTER_PASSWORD                   = "Enter password";
    String LOAN_OBJECT                      = "loanObject";
    String LOAN_ITEM_OBJECT                 = "loanItemObject";
    String BANK_LOAN_APP_PHOTO_CONTENT       = "cust_photo_content";
    String BANK_LOAN_APP_PACKET_PHOTO_CONTENT = "packet_photo_content";
    String RATE_DATA                          = "rateData";
    String RATE_AUDIT_DATA                    = "rateAuditData";
    String LOAN_APPS_DATA                     = "loanApplications";
    String LOAN_APPS_ITEMS_DATA               = "loanApplicationItems";

    interface SQLiteDatabase {
        String TABLE_NAME                       = "user_details";
        String USERNAME_COLUMN                  = "username";
        String PASSWORD_COLUMN                  = "password";
        String LOGGED_IN_COLUMN                 = "logged_in";

        String TABLE_GOLD_RATES                 = "gold_rates";
        String TABLE_GOLD_RATES_AUDIT           = "gold_rates_audit";
        String GOLD_RATES_KARAT_COLUMN          = "karat";
        String GOLD_RATES_RATE_COLUMN           = "rate";
        String GOLD_RATES_BANK_COLUMN           = "bank";
        String GOLD_RATES_TIMESTAMP_COLUMN      = "timestamp";

        String TABLE_BANK_DETAILS               = "bank_details";
        String BANK_DETAILS_NAME                = "bank_name";

        String TABLE_LOAN_APPLICATION           = "loan_applications";
        String BANK_LOAN_APP_ID                 = "id";
        String BANK_LOAN_APP_CUST_NAME          = "name";
        String BANK_LOAN_APP_CUST_ADDRESS       = "address";
        String BANK_LOAN_APP_MOBILE_NO          = "mobile_no";
        String BANK_LOAN_APP_ACC_NO             = "account_no";
        String BANK_LOAN_APP_BANK               = "loan_bank";
        String BANK_LOAN_APP_CUST_BANK          = "cust_bank";
        String BANK_LOAN_APP_JOINT_CUSTODY      = "joint_custody";
        String BANK_LOAN_APP_PHOTO              = "cust_photo";
        String BANK_LOAN_APP_PACKET_PHOTO       = "packet_photo";
        String BANK_LOAN_APP_LOAN_TYPE          = "loan_type";
        String BANK_LOAN_APP_LOAN_AMOUNT        = "loan_amount";
        String BANK_LOAN_APP_BAG_PACKET_NO      = "bag_packet_no";
        String CREATED_AT    = "created_at";
        String CREATED_BY    = "created_by";
        String TABLE_LOAN_APPLICATION_ITEMS     = "loan_application_items";
        String BANK_LOAN_APP_ITEMS_ID            = "id";
        String BANK_LOAN_APP_ITEMS_LOAN_ID       = "loan_id";
        String BANK_LOAN_APP_ITEMS_ITEM_DESC     = "item_description";
        String BANK_LOAN_APP_ITEMS_NO_ITEMS      = "item_numbers";
        String BANK_LOAN_APP_ITEMS_GROSS_WT      = "gross_weight";
        String BANK_LOAN_APP_ITEMS_APPROX_WT     = "approx_weight";
        String BANK_LOAN_APP_ITEMS_NET_WT        = "net_weight";
        String BANK_LOAN_APP_ITEMS_PURITY        = "purity";
        String BANK_LOAN_APP_ITEMS_RATE          = "rate";
        String BANK_LOAN_APP_ITEMS_RATE_DATETIME = "rate_datetime";
        String BANK_LOAN_APP_ITEMS_MARKET_VALUE  = "market_value";

        String LOAN_APPLICATION_TOTAL_ITEM       = "loan_application_total_items";
        String LOAN_APPLICATION_TOTAL_AMOUNT     = "loan_application_total_amount";
        String LOAN_APPLICATION_TOTAL_NET_WT     = "loan_application_net_weight";
        String LOAN_APPLICATION_TOTAL_GROSS_WT   = "loan_application_gross_weight";
        String LOAN_APPLICATION_SHOW_GEN_FORM    = "loan_application_generate_pdf";
    }

    interface USER_DETAILS {
        String USERNAME                       = "agent";
        String PASSWORD                       = "agent@123";
    }
}
