package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
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
import com.PrathihasProjects.PrathihasSplitwise.services.GroupDetailsService;

import java.util.*;

@RestController
@CrossOrigin
public class GetGroupDetailsController {

    private final GroupsDAOImpl theGroupsDAOImpl;
    private final GroupMembersDAOImpl groupMembersDAO;
    private final ExpensesDAOImpl expensesDAO;
    private final GroupDetailsService groupDetailsService;

    @Autowired
    public GetGroupDetailsController(GroupsDAOImpl theGroupsDAOImpl,
                                     GroupMembersDAOImpl groupMembersDAO,
                                     ExpensesDAOImpl expensesDAO,
                                     GroupDetailsService groupDetailsService) {
        this.theGroupsDAOImpl = theGroupsDAOImpl;
        this.groupMembersDAO = groupMembersDAO;
        this.expensesDAO = expensesDAO;
        this.groupDetailsService = groupDetailsService;
    }


    @GetMapping("/splitwise/groups/{groupId}")
    public ResponseEntity<?> getGroupDetails(@PathVariable int groupId, Authentication authentication) {
        try {
            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
            }

            String username = authentication.getName();

            GroupMembersHelper gmDetails = groupDetailsService.getGmDetails(groupId, username);

//            GroupMembers gmGroupMembers = groupMembersDAO.getDetails(groupId,username);
//
//            GroupMembersHelper gmDetails = new GroupMembersHelper(gmGroupMembers.getUser().getUsername(), gmGroupMembers.getGroup().getId(), gmGroupMembers.getAddedBy().getUsername(), gmGroupMembers.getAddedDate());
//
//            if(gmGroupMembers.getRemovedBy() != null)
//            {
//                gmDetails.setRemovedBy(gmGroupMembers.getRemovedBy().getUsername());
//                gmDetails.setRemovedDate(gmGroupMembers.getRemovedDate());
//            }
            // Fetch expenses related to the group
            //List<Expenses> expenses = ExpensesDAO.groupExpenses(groupId);

            List<String> members = groupMembersDAO.findMembersByGroupId(groupId);


            List<Expenses> expenses = expensesDAO.groupExpenses(groupId);


            Map<String, Object> response = new HashMap<>();

            List<Transaction> transactions = groupDetailsService.getAllTransactions(expenses);

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
            response.put("transactions", transactions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            //e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

//    private List<Transaction> resolveDebts(Map<String, BigDecimal> creditors, Map<String, BigDecimal> debtors) {
//        List<Transaction> transactions = new ArrayList<>();
//        for (Map.Entry<String, BigDecimal> creditor : creditors.entrySet()) {
//            BigDecimal amountToSettle = creditor.getValue();
//            Iterator<Map.Entry<String, BigDecimal>> debtorIterator = debtors.entrySet().iterator();
//            while (debtorIterator.hasNext() && amountToSettle.compareTo(BigDecimal.ZERO) > 0) {
//                Map.Entry<String, BigDecimal> debtor = debtorIterator.next();
//                BigDecimal possiblePayment = debtor.getValue().min(amountToSettle);
//                transactions.add(new Transaction(debtor.getKey(), creditor.getKey(), possiblePayment));
//                amountToSettle = amountToSettle.subtract(possiblePayment);
//                debtor.setValue(debtor.getValue().subtract(possiblePayment));
//                if (debtor.getValue().compareTo(BigDecimal.ZERO) == 0) {
//                    debtorIterator.remove();
//                }
//            }
//        }
//        return transactions;
//    }
}
