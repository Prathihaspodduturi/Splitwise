package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.ExpenseParticipantsId;
import com.PrathihasProjects.PrathihasSplitwise.dao.*;
import com.PrathihasProjects.PrathihasSplitwise.dto.ExpenseDTO;
import com.PrathihasProjects.PrathihasSplitwise.entity.*;
import com.PrathihasProjects.PrathihasSplitwise.helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.helper.Transaction;
import com.PrathihasProjects.PrathihasSplitwise.services.GroupDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@CrossOrigin
public class AddExpenseController {

    private final UserDAOImpl theUserDAOImpl;
    private final ExpensesDAOImpl expensesDAO;
    private final ExpenseParticipantsDAOImpl expenseParticipantsDAO;
    private final GroupsDAOImpl theGroupsDAOImpl;
    private final GroupDetailsService groupDetailsService;

    @Autowired
    public AddExpenseController(UserDAOImpl theUserDAOImpl,
                                ExpensesDAOImpl expensesDAO,
                                ExpenseParticipantsDAOImpl expenseParticipantsDAO,
                                GroupsDAOImpl theGroupsDAOImpl,
                                GroupDetailsService groupDetailsService) {
        this.theUserDAOImpl = theUserDAOImpl;
        this.expensesDAO = expensesDAO;
        this.expenseParticipantsDAO = expenseParticipantsDAO;
        this.theGroupsDAOImpl = theGroupsDAOImpl;
        this.groupDetailsService = groupDetailsService;
    }

    @PostMapping("/splitwise/groups/{groupId}/addExpense")
    public ResponseEntity<?> addExpense(@PathVariable int groupId, @RequestBody ExpenseDTO expenseDTO, Authentication authentication)
    {
        try {
            String username = authentication.getName();
            User addedBy = theUserDAOImpl.findUserByName(username);
            if (addedBy == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user information.");
            }

            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
            }

            Expenses expense = new Expenses();
            expense.setGroupId(group);
            expense.setExpenseName(expenseDTO.getExpenseName());
            expense.setAmount(expenseDTO.getAmount());
            expense.setDateCreated(new Date());
            expense.setAddedBy(addedBy);
            expense.setDeleted(false);
            expense.setPayment(expenseDTO.getIsPayment());


            expensesDAO.save(expense);

            Map<String, BigDecimal> payers = expenseDTO.getPayers();
            payers.forEach((payerUsername, amountPaid) -> {
                User payer = theUserDAOImpl.findUserByName(payerUsername);
                if (payer != null) {
                    BigDecimal actualAmountPaid = amountPaid != null ? amountPaid : BigDecimal.ZERO;
                    ExpenseParticipants participant = new ExpenseParticipants(expense, payer, BigDecimal.ZERO, actualAmountPaid);
                    participant.setId(new ExpenseParticipantsId(expense.getId(), payerUsername));
                    expenseParticipantsDAO.save(participant);
                }
            });


            // Then handle participants
            expenseDTO.getParticipants().forEach((participantUsername, isParticipating) -> {
                if (isParticipating) {
                    User participant = theUserDAOImpl.findUserByName(participantUsername);
                    if (participant != null) {
                        // Calculate the owed amount based on total amount divided by number of participants
                        BigDecimal amountOwed = expenseDTO.getAmount().divide(new BigDecimal(expenseDTO.getParticipants().size()), 2, RoundingMode.HALF_UP);
                        ExpenseParticipants existingParticipant = expenseParticipantsDAO.findParticipant(expense.getId(), participant.getUsername());
                        if (existingParticipant != null) {
                            // Update owed amount if already a payer
                            existingParticipant.setAmountOwed(amountOwed);
                            expenseParticipantsDAO.updateExpenseParticipants(existingParticipant);
                        } else {
                            // If not already a payer, create new record
                            ExpenseParticipants newParticipant = new ExpenseParticipants(expense, participant, amountOwed, BigDecimal.ZERO);
                            newParticipant.setId(new ExpenseParticipantsId(expense.getId(), participantUsername));
                            expenseParticipantsDAO.save(newParticipant);
                        }
                    }
                }
            });

            GroupMembersHelper gmDetails = groupDetailsService.getGmDetails(groupId, username);

            Map<String, Object> response = new HashMap<>();
            response.put("gmDetails",gmDetails);



            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add expense: " + e.getMessage());
        }
    }

}
