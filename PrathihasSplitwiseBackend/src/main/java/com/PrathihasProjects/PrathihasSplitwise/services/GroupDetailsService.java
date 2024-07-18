package com.PrathihasProjects.PrathihasSplitwise.services;

import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import com.PrathihasProjects.PrathihasSplitwise.helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.helper.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class GroupDetailsService {
    private final ExpenseParticipantsDAOImpl expenseParticipantsDAO;
    private final GroupMembersDAOImpl groupMembersDAO;

    @Autowired
    public GroupDetailsService(ExpenseParticipantsDAOImpl expenseParticipantsDAO,
                               GroupMembersDAOImpl groupMembersDAO) {
        this.expenseParticipantsDAO = expenseParticipantsDAO;
        this.groupMembersDAO = groupMembersDAO;
    }

    public List<Transaction> getAllTransactions(List<Expenses>expenses)
    {
        List<ExpenseParticipants> participants = new ArrayList<>();

        for (Expenses expense : expenses) {
            if (!expense.isDeleted()) {
                List<ExpenseParticipants> tempParticipants = expenseParticipantsDAO.findByExpenseId(expense.getId());

                if (!tempParticipants.isEmpty()) {
                    participants.addAll(tempParticipants);
                }
            }
        }

        Map<String, BigDecimal> netBalances = new HashMap<>();
        for (ExpenseParticipants participant : participants) {
            if(!participant.isDeleted()) {
                String usernameFromParticipant = participant.getUser().getUsername();
                netBalances.putIfAbsent(usernameFromParticipant, BigDecimal.ZERO);
                BigDecimal paid = participant.getAmountpaid();
                BigDecimal owed = participant.getAmountOwed();
                BigDecimal balance = netBalances.get(usernameFromParticipant).add(paid).subtract(owed);
                netBalances.put(usernameFromParticipant, balance);
            }
        }

        Map<String, BigDecimal> creditors = new HashMap<>();
        Map<String, BigDecimal> debtors = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : netBalances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.put(entry.getKey(), entry.getValue());
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.put(entry.getKey(), entry.getValue().abs());
            }
        }

        return resolveDebts(creditors, debtors);

    }

    private List<Transaction> resolveDebts(Map<String, BigDecimal> creditors, Map<String, BigDecimal> debtors) {
        List<Transaction> transactions = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> creditor : creditors.entrySet()) {
            BigDecimal amountToSettle = creditor.getValue();
            Iterator<Map.Entry<String, BigDecimal>> debtorIterator = debtors.entrySet().iterator();
            while (debtorIterator.hasNext() && amountToSettle.compareTo(BigDecimal.ZERO) > 0) {
                Map.Entry<String, BigDecimal> debtor = debtorIterator.next();
                BigDecimal possiblePayment = debtor.getValue().min(amountToSettle);
                transactions.add(new Transaction(debtor.getKey(), creditor.getKey(), possiblePayment));
                amountToSettle = amountToSettle.subtract(possiblePayment);
                debtor.setValue(debtor.getValue().subtract(possiblePayment));
                if (debtor.getValue().compareTo(BigDecimal.ZERO) == 0) {
                    debtorIterator.remove();
                }
            }
        }
        return transactions;
    }


    public List<Map<String,Object>> getDetailedExpenses(List<Expenses>expenses, String username)
    {
        List<Map<String,Object>> detailedExpenses = new ArrayList<>();

        for (Expenses expense : expenses) {
            Map<String, Object> expenseDetails = new HashMap<>();
            expenseDetails.put("id", expense.getId());
            expenseDetails.put("expenseName", expense.getExpenseName());
            expenseDetails.put("dateCreated", expense.getDateCreated());
            expenseDetails.put("amount", expense.getAmount());
            expenseDetails.put("addedBy", expense.getAddedBy().getUsername());
            expenseDetails.put("deleted", expense.isDeleted());
            expenseDetails.put("isPayment", expense.isPayment());

            User deletedByUser = expense.getDeletedBy();
            User updatedByUser = expense.getUpdatedBy();

            if(updatedByUser != null) {
                expenseDetails.put("updatedBy", updatedByUser.getUsername());
                expenseDetails.put("lastUpdatedDate", expense.getLastUpdatedDate());
            }

            if(deletedByUser != null)
            {
                expenseDetails.put("deletedBy", deletedByUser.getUsername());
                expenseDetails.put("deletedDate", expense.getDeletedDate());
            }

            ExpenseParticipants participant = expenseParticipantsDAO.findParticipant(expense.getId(),username);
            if(participant == null){
                expenseDetails.put("notInvolved", true);
                detailedExpenses.add(expenseDetails);
                continue;
            }

            BigDecimal zero = BigDecimal.ZERO;
            if(participant.getAmountpaid().compareTo(zero) == 0 && participant.getAmountOwed().compareTo(zero) == 0)
            {
                expenseDetails.put("notInvolved", true);
            }
            else
            {
                expenseDetails.put("involved", participant.getAmountpaid().subtract(participant.getAmountOwed()));
            }

            detailedExpenses.add(expenseDetails);
        }
        return detailedExpenses;
    }


    public GroupMembersHelper getGmDetails(int groupId, String username)
    {
        GroupMembers gmGroupMembers = groupMembersDAO.getDetails(groupId,username);

        GroupMembersHelper gmDetails = new GroupMembersHelper(gmGroupMembers.getUser().getUsername(), gmGroupMembers.getGroup().getId(), gmGroupMembers.getAddedBy().getUsername(), gmGroupMembers.getAddedDate());

        if(gmGroupMembers.getRemovedBy() != null)
        {
            gmDetails.setRemovedBy(gmGroupMembers.getRemovedBy().getUsername());
            gmDetails.setRemovedDate(gmGroupMembers.getRemovedDate());
        }
        return gmDetails;
    }
}
