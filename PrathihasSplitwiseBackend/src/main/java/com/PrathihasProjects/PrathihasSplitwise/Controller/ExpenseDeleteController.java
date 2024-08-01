package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class ExpenseDeleteController {
    private final ExpensesDAOImpl expensesDAO;
    private final ExpenseParticipantsDAOImpl expenseParticipantsDAO;
    private final UserDAOImpl theUserDAOImpl;

    @Autowired
    public ExpenseDeleteController(ExpensesDAOImpl expensesDAO,
                                   ExpenseParticipantsDAOImpl expenseParticipantsDAO,
                                   UserDAOImpl theUserDAOImpl) {
        this.expensesDAO = expensesDAO;
        this.expenseParticipantsDAO = expenseParticipantsDAO;
        this.theUserDAOImpl = theUserDAOImpl;
    }

    @PutMapping("/splitwise/groups/{groupId}/expenses/{expenseId}/delete")
    public ResponseEntity<?> deleteExpense(@PathVariable int groupId, @PathVariable int expenseId, Authentication authentication) {
        try {
            Expenses expense = expensesDAO.findExpenseById(expenseId);
            if (expense == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
            }
            expense.setDeleted(true);
            String user = authentication.getName();
            User userFromDb = theUserDAOImpl.findUserByName(user);


            if (userFromDb == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user information.");
            }

            // Ensure we are working with a managed instance of User
            userFromDb = theUserDAOImpl.findUserByName(userFromDb.getUsername());

            expense.setDeletedBy(userFromDb);
            expense.setDeletedDate(new Date());
            expensesDAO.updateExpense(expense);

            List<ExpenseParticipants> participantsList = expenseParticipantsDAO.findByExpenseId(expenseId);

            for (ExpenseParticipants participant : participantsList) {
                participant.setDeleted(true);
                expenseParticipantsDAO.updateExpenseParticipants(participant);
            }

            return ResponseEntity.ok().body("Expense deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete expense: " + e.getMessage());
        }
    }
}
