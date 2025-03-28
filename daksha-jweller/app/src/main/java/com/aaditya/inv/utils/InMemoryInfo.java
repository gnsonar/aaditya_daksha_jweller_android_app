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
    public static LocalDateTime expiryDateTime = LocalDateTime.parse("2026-03-31T23:59:59");
    public static List<String> androidIDUs = List.of(
            "60c8f051b2eef5c7", // gautam
            "d94bd92f17303549", // kunal
            "c085c547cb96ee9e"); // customer
    public static String branchId;
    public static List<String> loanBankList = List.of("Bank of Baroda", "State Bank of India");
    public static List<String> bankList = List.of("Bank of Baroda - Main", "Bank of Baroda - Dena Bank", "Bank of Baroda - Dahival", "State Bank of India - Main", "State Bank of India - Karvand Naka");
    public static List<String> loanTypes = List.of("Personal Loan", "Agri Loan", "Other Loan");
    public static ContentValues customerDetails;
    public static String loanPhotoUploadedFrom = "";
    public static boolean updateGoldRate;
    public static LoanApplicationSearch loanAppSearchObject = null;
}
