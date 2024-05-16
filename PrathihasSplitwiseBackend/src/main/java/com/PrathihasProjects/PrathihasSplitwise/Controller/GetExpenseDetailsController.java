package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.*;
import com.PrathihasProjects.PrathihasSplitwise.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class GetExpenseDetailsController {

    @Autowired
    private GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    private ExpensesDAOImpl expensesDAO;

    @Autowired
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @Autowired
    private GroupMembersDAOImpl groupMembersDAO;

    @GetMapping("/splitwise/groups/{groupId}/expenses/{expenseId}")
    public ResponseEntity<?> getExpenseDetails(@PathVariable int groupId, @PathVariable int expenseId, Authentication authentication) {
        try {
            String username = authentication.getName();
            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
            }

            Expenses expense = expensesDAO.findExpenseById(expenseId);
            if (expense == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found or has been deleted");
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
            expenseDetails.put("gmDetails", gmDetails);

            return ResponseEntity.ok(expenseDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve expense details: " + e.getMessage());
        }
    }

}
