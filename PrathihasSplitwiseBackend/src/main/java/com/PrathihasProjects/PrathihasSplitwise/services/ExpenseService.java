package com.PrathihasProjects.PrathihasSplitwise.services;

import com.PrathihasProjects.PrathihasSplitwise.dao.*;
import com.PrathihasProjects.PrathihasSplitwise.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final GroupsDAOImpl theGroupsDAOImpl;
    private final ExpensesDAOImpl expensesDAO;
    private final ExpenseParticipantsDAOImpl expenseParticipantsDAO;
    private final GroupMembersDAOImpl groupMembersDAO;

    @Autowired
    public ExpenseService(GroupsDAOImpl theGroupsDAOImpl,
                          ExpensesDAOImpl expensesDAO,
                          ExpenseParticipantsDAOImpl expenseParticipantsDAO,
                          GroupMembersDAOImpl groupMembersDAO) {
        this.theGroupsDAOImpl = theGroupsDAOImpl;
        this.expensesDAO = expensesDAO;
        this.expenseParticipantsDAO = expenseParticipantsDAO;
        this.groupMembersDAO = groupMembersDAO;
    }

    // Method to retrieve and format expense details
    public Map<String, Object> getExpenseDetails(int expenseId, int groupId, String username) {
        Groups group = theGroupsDAOImpl.findGroupById(groupId);
        if (group == null) {
            return null;
        }

        Expenses expense = expensesDAO.findExpenseById(expenseId);
        if (expense == null) {
            return null;
        }

        GroupMembers gmDetails = groupMembersDAO.getDetails(groupId,username);

        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("expenseName", expense.getExpenseName());
        expenseDetails.put("amount", expense.getAmount());
        expenseDetails.put("dateCreated", expense.getDateCreated());
        expenseDetails.put("addedBy", expense.getAddedBy().getUsername());
        expenseDetails.put("isPayment",expense.isPayment());
        User user = expense.getUpdatedBy();
        if(user != null) {
            expenseDetails.put("updatedBy", expense.getUpdatedBy().getUsername());
            expenseDetails.put("lastUpdatedDate", expense.getLastUpdatedDate());
        }
        User deletedByUser = expense.getDeletedBy();
        if(deletedByUser != null)
        {
            expenseDetails.put("isDeleted", true);
            expenseDetails.put("deletedBy", expense.getDeletedBy().getUsername());
            expenseDetails.put("deletedDate", expense.getDeletedDate());
        }
        else {
            expenseDetails.put("isDeleted", false);
        }
        // Fetch participants and amounts involved
        List<ExpenseParticipants> participants = expenseParticipantsDAO.findByExpenseId(expenseId);
        List<Map<String, Object>> participantDetails = participants.stream().map(participant -> {
            Map<String, Object> details = new HashMap<>();

            BigDecimal zero = BigDecimal.ZERO;
            if(participant.getAmountpaid().compareTo(zero) != 0 || participant.getAmountOwed().compareTo(zero) != 0) {
                details.put("username", participant.getUser().getUsername());
                details.put("amountPaid", participant.getAmountpaid());
                details.put("amountOwed", participant.getAmountOwed());

                if(participant.getAmountOwed().compareTo(zero) != 0)
                    details.put("isChecked", true);
            }
            return details;
        }).collect(Collectors.toList());

        expenseDetails.put("participants", participantDetails);

        if(gmDetails.getRemovedBy() != null)
            expenseDetails.put("gmRemovedDate", gmDetails.getRemovedDate());
        else {
            expenseDetails.put("gmRemovedDate", null);
        }


        return expenseDetails;
    }


}
