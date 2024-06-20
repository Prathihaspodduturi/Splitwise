package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dto.MemberInfo;
import com.PrathihasProjects.PrathihasSplitwise.entity.*;
import com.PrathihasProjects.PrathihasSplitwise.helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.helper.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@CrossOrigin
public class GetGroupDetailsController {

    @Autowired
    private GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    private GroupMembersDAOImpl groupMembersDAO;

    @Autowired
    private ExpensesDAOImpl expensesDAO;

    @Autowired
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @GetMapping("/splitwise/groups/{groupId}")
    public ResponseEntity<?> getGroupDetails(@PathVariable int groupId, Authentication authentication) {
        try {
            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
            }

            String username = authentication.getName();

            GroupMembers gmGroupMembers = groupMembersDAO.getDetails(groupId,username);

            GroupMembersHelper gmDetails = new GroupMembersHelper(gmGroupMembers.getUser().getUsername(), gmGroupMembers.getGroup().getId(), gmGroupMembers.getAddedBy().getUsername(), gmGroupMembers.getAddedDate());

            if(gmGroupMembers.getRemovedBy() != null)
            {
                gmDetails.setRemovedBy(gmGroupMembers.getRemovedBy().getUsername());
                gmDetails.setRemovedDate(gmGroupMembers.getRemovedDate());
            }
            // Fetch expenses related to the group
            //List<Expenses> expenses = ExpensesDAO.groupExpenses(groupId);

            List<MemberInfo> members = groupMembersDAO.findMembersByGroupId(groupId);


            List<Expenses> expenses = expensesDAO.groupExpenses(groupId);


            Map<String, Object> response = new HashMap<>();

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

            List<ExpenseParticipants> participants = new ArrayList<>();

            for(int i=0;i<expenses.size();i++)
            {
                if(!expenses.get(i).isDeleted()) {
                    List<ExpenseParticipants> tempParticipants = expenseParticipantsDAO.findByExpenseId(expenses.get(i).getId());

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

            List<Transaction> transactions = resolveDebts(creditors, debtors);

            //GroupMembers gmDetails = GroupMembersDAO.getDetails(groupId, username);

            Map<String, Object> groupDetails = new HashMap<>();
            groupDetails.put("id", group.getId());
            groupDetails.put("groupName", group.getGroupName());
            groupDetails.put("dateCreated", group.getDateCreated());
            groupDetails.put("deleted", group.isDeleted());
            groupDetails.put("createdBy", group.getCreatedBy().getUsername());
            groupDetails.put("groupDescription", group.getGroupDescription());
            groupDetails.put("settledUp", group.isSettledUp());

            if(group.isDeleted())
            {
                groupDetails.put("deletedBy", group.getDeletedBy().getUsername());
                groupDetails.put("deletedDate", group.getDeletedDate());
            }
            else
            {
                groupDetails.put("deletedBy",null);
                groupDetails.put("deletedDate", null);
            }

            if(group.isSettledUp())
            {
                groupDetails.put("settledBy", group.getSettledBy().getUsername());
                groupDetails.put("settledDate", group.getSettledDate());
            }
            else
            {
                groupDetails.put("settledBy", null);
                groupDetails.put("settledDate", null);
            }

            response.put("group", groupDetails);
            response.put("gmDetails", gmDetails);
            response.put("members", members);
            response.put("detailedExpenses", detailedExpenses);
            response.put("transactions", transactions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
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
}
