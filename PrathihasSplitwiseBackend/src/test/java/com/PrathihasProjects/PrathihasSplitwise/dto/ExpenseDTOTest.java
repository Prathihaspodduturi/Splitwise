package com.PrathihasProjects.PrathihasSplitwise.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpenseDTOTest {

    @Test
    void testExpenseDTO() {

        String expenseName = "Dinner";
        BigDecimal amount = new BigDecimal("29.99");
        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("John", new BigDecimal("15.00"));
        Map<String, Boolean> participants = new HashMap<>();
        participants.put("John", true);
        participants.put("Jane", false);
        boolean isPayment = true;

        ExpenseDTO dto = new ExpenseDTO();
        dto.setExpenseName(expenseName);
        dto.setAmount(amount);
        dto.setPayers(payers);
        dto.setParticipants(participants);
        dto.setIsPayment(isPayment);

        assertEquals(expenseName, dto.getExpenseName(), "Expense names should match");
        assertEquals(amount, dto.getAmount(), "Amounts should match");
        assertEquals(payers, dto.getPayers(), "Payers maps should match");
        assertEquals(participants, dto.getParticipants(), "Participants maps should match");
        assertEquals(isPayment, dto.getIsPayment(), "Payment status should match");
    }

}
