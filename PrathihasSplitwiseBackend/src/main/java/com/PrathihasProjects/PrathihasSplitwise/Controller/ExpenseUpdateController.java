package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.ExpenseParticipantsId;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dto.ExpenseDTO;
import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class ExpenseUpdateController {

    @Autowired
    private UserDAOImpl theUserDAOImpl;

    @Autowired
    private ExpensesDAOImpl expensesDAO;

    @Autowired
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @Autowired
    private GroupsDAOImpl theGroupsDAOImpl;

    @PutMapping("/splitwise/groups/{groupId}/expenses/{expenseId}/update")
    public ResponseEntity<?> updateExpense(@PathVariable int groupId, @PathVariable int expenseId, @RequestBody ExpenseDTO expenseDTO, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = theUserDAOImpl.findUserByName(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User authentication failed.");
            }

            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found.");
            }

            Expenses expense = expensesDAO.findExpenseById(expenseId);
            if (expense == null || expense.isDeleted()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found or has been deleted.");
            }

            // Update the expense details
            expense.setExpenseName(expenseDTO.getExpenseName());
            expense.setAmount(expenseDTO.getAmount());
            expense.setUpdatedBy(user);
            expense.setLastUpdatedDate(new Date());

            //Expenses updatedExpense = ExpensesDAO.findExpenseById(expenseId);
            expensesDAO.updateExpense(expense);

            int totalParticipants = (int) expenseDTO.getParticipants().values().stream()
                    .filter(isParticipating -> isParticipating)
                    .count();

            BigDecimal shareAmount = totalParticipants > 0 ? expenseDTO.getAmount().divide(BigDecimal.valueOf(totalParticipants), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;



            // Handle payers
            Map<String, BigDecimal> payers = expenseDTO.getPayers();

            payers.forEach((payerUsername, amountPaid) -> {
                ExpenseParticipants participantDB = expenseParticipantsDAO.findParticipant(expense.getId(), payerUsername);
                if (participantDB != null) {
                    participantDB.setAmountpaid(amountPaid);
                    expenseParticipantsDAO.updateExpenseParticipants(participantDB);
                } else {
                    // Create new payer record if not found
                    User payer = theUserDAOImpl.findUserByName(payerUsername);
                    participantDB = new ExpenseParticipants(expense, payer, BigDecimal.ZERO, amountPaid);
                    participantDB.setId(new ExpenseParticipantsId(expense.getId(), payerUsername));
                    expenseParticipantsDAO.save(participantDB);
                    //ExpenseParticipants participantTemp = ExpenseParticipantsDAO.findParticipant(expense.getId(), payerUsername);
                }
            });

            // Handle participants
            expenseDTO.getParticipants().forEach((participantUsername, isParticipating) -> {
                ExpenseParticipants participantDB = expenseParticipantsDAO.findParticipant(expense.getId(), participantUsername);
                BigDecimal amountOwed = isParticipating ? shareAmount : BigDecimal.ZERO;

                if (participantDB != null) {
                    participantDB.setAmountOwed(amountOwed);
                    expenseParticipantsDAO.updateExpenseParticipants(participantDB);

                } else {
                    // Create new participant record if not found
                    User participantUser = theUserDAOImpl.findUserByName(participantUsername);
                    ExpenseParticipants newParticipant = new ExpenseParticipants(expense, participantUser, amountOwed, BigDecimal.ZERO);
                    newParticipant.setId(new ExpenseParticipantsId(expense.getId(), participantUsername));
                    expenseParticipantsDAO.save(newParticipant);
                }
            });


            Map<String, Object> response = new HashMap<>();
            response.put("message", "Expense updated successfully.");
            response.put("updatedExpenseId", expense.getId());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update expense: " + e.getMessage());
        }
    }


}
