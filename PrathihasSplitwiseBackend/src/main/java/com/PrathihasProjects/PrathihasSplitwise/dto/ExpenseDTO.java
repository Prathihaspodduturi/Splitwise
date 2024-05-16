package com.PrathihasProjects.PrathihasSplitwise.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ExpenseDTO {
    private String expenseName;
    private BigDecimal amount;
    private Map<String, BigDecimal> payers;
    private Map<String, Boolean> participants;

    private boolean isPayment;

    // Getters and Setters
    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Map<String, BigDecimal> getPayers() {
        return payers;
    }

    public void setPayers(Map<String, BigDecimal> payers) {
        this.payers = payers;
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public boolean getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(boolean isPayment) {
        this.isPayment = isPayment;
    }
}
