package com.meorient.pojo;

public class Company {

    private String tranId;
    private String transactionLineId;
    private String transactionOrder;
    private String companyId;
    private String fullName;
    private String transactionId;
    private String amount;
    private String memo;

    public Company() {
    }

    public Company(String tranId, String transactionLineId, String transactionOrder,
                   String companyId, String fullName, String transactionId,
                   String amount, String memo) {
        this.tranId = tranId;
        this.transactionLineId = transactionLineId;
        this.transactionOrder = transactionOrder;
        this.companyId = companyId;
        this.fullName = fullName;
        this.transactionId = transactionId;
        this.amount = amount;
        this.memo = memo;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getTransactionLineId() {
        return transactionLineId;
    }

    public void setTransactionLineId(String transactionLineId) {
        this.transactionLineId = transactionLineId;
    }

    public String getTransactionOrder() {
        return transactionOrder;
    }

    public void setTransactionOrder(String transactionOrder) {
        this.transactionOrder = transactionOrder;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "Company{" +
                "tranId='" + tranId + '\'' +
                ", transactionLineId='" + transactionLineId + '\'' +
                ", transactionOrder='" + transactionOrder + '\'' +
                ", companyId='" + companyId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", amount='" + amount + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
