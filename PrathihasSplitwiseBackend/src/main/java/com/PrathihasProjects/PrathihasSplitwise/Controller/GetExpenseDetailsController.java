package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.*;
import com.PrathihasProjects.PrathihasSplitwise.entity.*;
import com.PrathihasProjects.PrathihasSplitwise.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@CrossOrigin
public class GetExpenseDetailsController {
    private final GroupsDAOImpl theGroupsDAOImpl;
    private final ExpensesDAOImpl expensesDAO;

    private final ExpenseService expenseService;

    @Autowired
    public GetExpenseDetailsController(GroupsDAOImpl theGroupsDAOImpl,
                                       ExpensesDAOImpl expensesDAO,
                                       ExpenseService expenseService) {
        this.theGroupsDAOImpl = theGroupsDAOImpl;
        this.expensesDAO = expensesDAO;
        this.expenseService = expenseService;
    }

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

            Map<String, Object> expenseDetails = expenseService.getExpenseDetails(expenseId, groupId, username);
            if (expenseDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found or has been deleted");
            }
            return ResponseEntity.ok(expenseDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve expense details: " + e.getMessage());
        }
    }

}
