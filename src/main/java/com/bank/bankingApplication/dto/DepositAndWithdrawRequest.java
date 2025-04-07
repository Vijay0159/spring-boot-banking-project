package com.bank.bankingApplication.dto;

public class DepositAndWithdrawRequest {
    private Long id;
    private Double amount;

    public DepositAndWithdrawRequest() {}

    public DepositAndWithdrawRequest(Long id, Double amount) {
        this.id = id;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
