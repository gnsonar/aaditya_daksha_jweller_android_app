package com.aaditya.inv.models;

public class LoanApplicationSearch {

    private String loanId;
    private String customerName;
    private String loanBank;

    public LoanApplicationSearch(String loanId, String customerName, String loanBank) {
        this.loanId = loanId;
        this.customerName = customerName;
        this.loanBank = loanBank;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLoanBank() {
        return loanBank;
    }

    public void setLoanBank(String loanBank) {
        this.loanBank = loanBank;
    }
}
