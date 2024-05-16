package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ExpenseRestoreController {

    @Autowired
    private ExpensesDAOImpl expensesDAO;

    @Autowired
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @PutMapping("splitwise/groups/{groupId}/expenses/{expenseId}/restore")
    public ResponseEntity<?> restoreExpense(@PathVariable int groupId, @PathVariable int expenseId)
    {
        try {
            Expenses expense = expensesDAO.findExpenseById(expenseId);

            if(expense == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
            }
            expense.setDeleted(false);
            expense.setDeletedBy(null);
            expense.setDeletedDate(null);

            expensesDAO.updateExpense(expense);

            List<ExpenseParticipants> participantsList = expenseParticipantsDAO.findByExpenseId(expenseId);

            for (ExpenseParticipants participant : participantsList) {
                participant.setDeleted(false);
                expenseParticipantsDAO.updateExpenseParticipants(participant);
            }

            return ResponseEntity.ok().body("Expense restored successfully");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to restore expense: " + e.getMessage());
        }
    }

}
