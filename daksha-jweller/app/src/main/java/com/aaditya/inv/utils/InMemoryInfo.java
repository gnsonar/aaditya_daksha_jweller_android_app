package com.aaditya.inv.utils;

import android.content.ContentValues;

import com.aaditya.inv.models.LoanApplicationSearch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryInfo {
    public static LocalDateTime expiryDateTime = LocalDateTime.parse("2025-03-10T00:00:00");
    public static String androidID = "60c8f051b2eef5c7";
    public static String branchId;
    public static List<String> loanBankList = List.of("Bank of Baroda", "State Bank of India");
    public static List<String> bankList = List.of("Bank of Baroda - Main", "Bank of Baroda - Dena Bank", "Bank of Baroda - Dahival", "State Bank of India - Main", "State Bank of India - Karvand Naka");
    public static List<String> loanTypes = List.of("Personal Loan", "Agri Loan", "Other Loan");
    public static ContentValues customerDetails;
    public static String loanPhotoUploadedFrom = "";
    public static boolean updateGoldRate;
    public static LoanApplicationSearch loanAppSearchObject = null;
}
