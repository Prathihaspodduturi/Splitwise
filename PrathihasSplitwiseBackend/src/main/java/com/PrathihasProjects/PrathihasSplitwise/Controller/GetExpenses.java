package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.services.GroupDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class GetExpenses {

    private final GroupsDAOImpl theGroupsDAOImpl;
    private final ExpensesDAOImpl expensesDAO;
    private final GroupDetailsService groupDetailsService;

    @Autowired
    public GetExpenses(GroupsDAOImpl theGroupsDAOImpl,
                                     ExpensesDAOImpl expensesDAO,
                                     GroupDetailsService groupDetailsService) {
        this.theGroupsDAOImpl = theGroupsDAOImpl;
        this.expensesDAO = expensesDAO;
        this.groupDetailsService = groupDetailsService;
    }


    @GetMapping("/splitwise/groups/{groupId}/expenses")
    public ResponseEntity<?> getExpenses(@PathVariable int groupId, Authentication authentication)
    {
        try
        {
            String username = authentication.getName();

            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
            }

            List<Expenses> expenses = expensesDAO.groupExpenses(groupId);

            List<Map<String,Object>> detailedExpenses = groupDetailsService.getDetailedExpenses(expenses, username);

            GroupMembersHelper gmDetails = groupDetailsService.getGmDetails(groupId, username);


            Map<String, Object> response = new HashMap<>();

            response.put("gmDetails", gmDetails);
            response.put("detailedExpenses", detailedExpenses);

            return ResponseEntity.ok(response);

        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());

        }
    }


}
