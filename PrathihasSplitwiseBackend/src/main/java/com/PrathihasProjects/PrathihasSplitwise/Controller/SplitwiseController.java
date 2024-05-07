package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.DAO.*;
import com.PrathihasProjects.PrathihasSplitwise.DTO.ExpenseDTO;
import com.PrathihasProjects.PrathihasSplitwise.DTO.GroupDTO;
import com.PrathihasProjects.PrathihasSplitwise.DTO.MemberInfo;
import com.PrathihasProjects.PrathihasSplitwise.Helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.Helper.Transaction;
import com.PrathihasProjects.PrathihasSplitwise.Jwt.JwtUtil;
import com.PrathihasProjects.PrathihasSplitwise.compositeKey.ExpenseParticipantsId;
import com.PrathihasProjects.PrathihasSplitwise.compositeKey.GroupMembersId;
import com.PrathihasProjects.PrathihasSplitwise.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
public class SplitwiseController {


    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private groupMembersDAOImpl groupMembersDAO;


    @Autowired
    private groupsDAOImpl theGroupsDAOImpl;


    @Autowired
    private userDAOImpl theUserDAOImpl;

    @Autowired
    private expensesDAOImpl expensesDAO;

    @Autowired
    private expenseParticipantsDAOImpl expenseParticipantsDAO;


    public SplitwiseController(userDAOImpl theUserDAOImpl)
    {
        this.theUserDAOImpl = theUserDAOImpl;
    }


    @GetMapping("/splitwise/")
    public String sampleConnection()
    {
        return "Connected";
    }


    @PostMapping("/splitwise/creategroup")
    public ResponseEntity<?> createGroup(@RequestBody GroupDTO groupDTO , Authentication authentication)
    {
        try
        {
            String currentUsername = authentication.getName();

            User user = theUserDAOImpl.findUserByName(currentUsername);

            if(user == null)
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("technical error");
            }

            Groups newGroup = new Groups();
            newGroup.setGroupName(groupDTO.getGroupName());
            newGroup.setSettledUp(false);
            newGroup.setDeleted(false);
            newGroup.setCreatedBy(user);
            newGroup.setGroupDescription(groupDTO.getGroupDescription());
            newGroup.setDateCreated(new Date());

            theGroupsDAOImpl.save(newGroup);

            GroupMembers groupMember = new GroupMembers();

            groupMember.setGroup(newGroup);
            groupMember.setUser(user);
            groupMember.setId(new GroupMembersId(newGroup.getId(), user.getUsername()));

            groupMembersDAO.save(groupMember);

            return ResponseEntity.ok(newGroup);

        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create group: " + e.getMessage());
        }


    }


    @PostMapping("/splitwise/groups")
    public ResponseEntity<?> getGroups() {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Get username from authentication

            List<Map<String, Object>> groupDetails = new ArrayList<>();

            List<Groups> groups = groupMembersDAO.findGroupsOfUser(username);

            for(Groups group : groups)
            {
                Map<String,Object> groupDetail = new HashMap<>();
                groupDetail.put("id", group.getId());
                groupDetail.put("groupName", group.getGroupName());
                groupDetail.put("groupDescription", group.getGroupDescription());
                groupDetail.put("dateCreated", group.getDateCreated());
                groupDetail.put("settledUp", group.isSettledUp());
                groupDetail.put("deleted", group.isDeleted());
                groupDetail.put("createdBy", group.getCreatedBy().getUsername());

                if(group.isSettledUp())
                    groupDetail.put("settledBy", group.getSettledBy().getUsername());

                if(group.isDeleted())
                    groupDetail.put("deletedBy", group.getDeletedBy().getUsername());

                GroupMembers gmDetails = groupMembersDAO.getDetails(group.getId(), username);
                groupDetail.put("removedDate", gmDetails.getRemovedDate());

                groupDetails.add(groupDetail);
            }

            return ResponseEntity.ok(groupDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

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
            //List<Expenses> expenses = expensesDAO.groupExpenses(groupId);

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
                expenseDetails.put("addedBy", expense.getAddedBy());
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

            //GroupMembers gmDetails = groupMembersDAO.getDetails(groupId, username);

            response.put("group", group);
            response.put("gmDetails", gmDetails);
            response.put("members", members);
            response.put("detailedExpenses", detailedExpenses);
            response.put("transactions", transactions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
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

    @PostMapping("/splitwise/groups/{groupId}/addmember")
    public ResponseEntity<?> addMemberToGroup(@PathVariable int groupId, @RequestBody Map<String, String> requestBody, Authentication authentication) {
        try {
            // Validate that the username is provided
            String newUsername = requestBody.get("newUsername");
            if (newUsername == null || newUsername.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }

            String username = authentication.getName();

            User curUser = theUserDAOImpl.findUserByName(username);

            User userToAdd = theUserDAOImpl.findUserByName(newUsername);
            if (userToAdd == null) {
                return ResponseEntity.badRequest().body("User does not exist");
            }

            // Check if group exists
            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if user is already a member of the group
            boolean isAlreadyMember = groupMembersDAO.isMember(newUsername, groupId);
            if (isAlreadyMember) {
                return ResponseEntity.badRequest().body("User is already a member of this group");
            }

            // Create and save the new group member
            GroupMembers newMember = new GroupMembers();
            newMember.setUser(userToAdd);
            newMember.setGroup(group);
            newMember.setId(new GroupMembersId(groupId, newUsername));
            newMember.setAddedBy(curUser);
            newMember.setAddedDate(new Date());
            groupMembersDAO.save(newMember);
            List<MemberInfo> members = groupMembersDAO.findMembersByGroupId(groupId);
            return ResponseEntity.ok().body(members);
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: please try again later!");
        }
    }

    @PutMapping("/splitwise/groups/{groupId}/removemember")
    public ResponseEntity<?> removeMember(@PathVariable int groupId, @RequestBody Map<String, String> requestBody, Authentication authentication)
    {
        try
        {
            String memberUsername = requestBody.get("username");
            if (memberUsername == null || memberUsername.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Member username is required");
            }

            String removedByUsername = authentication.getName();

            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            User removedByUser = theUserDAOImpl.findUserByName(removedByUsername);
            User memberUser = theUserDAOImpl.findUserByName(memberUsername);

            if (group == null) {
                return ResponseEntity.badRequest().body("Group not found");
            }
            if (memberUser == null) {
                return ResponseEntity.badRequest().body("Member user not found");
            }

            GroupMembers member = groupMembersDAO.getDetails(groupId, memberUsername);
            if (member == null) {
                return ResponseEntity.badRequest().body("User is not part of this group");
            }

            member.setRemovedBy(removedByUser);
            member.setRemovedDate(new Date());

            groupMembersDAO.save(member);

            return ResponseEntity.ok().body("Succesfull");
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: please try again later!");
        }
    }

    @PutMapping ("/splitwise/groups/{groupid}/delete")
    public ResponseEntity<?> deleteGroup(@PathVariable int groupid, Authentication authentication)
    {
        try
        {
            String username = authentication.getName();
            theGroupsDAOImpl.deletegroupById(groupid, username, new Date());
            return ResponseEntity.ok().body("succesfully deleted group");
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: Please try again later");
        }
    }


    @PutMapping ("/splitwise/groups/{groupid}/settlegroup")
    public ResponseEntity<?> settleGroup(@PathVariable int groupid, Authentication authentication)
    {
        try
        {
            String username = authentication.getName();
            theGroupsDAOImpl.settlegroupById(groupid, username, new Date());
            return ResponseEntity.ok().body("succesfully settled up group");
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: Please try again later");
        }
    }

    @PutMapping ("/splitwise/groups/{groupId}/update")
    public ResponseEntity<?> updateGroupName(@PathVariable int groupId, @RequestBody Map<String, String> requestBody) {
        try {
            String newGroupName = requestBody.get("groupName");
            if (newGroupName == null || newGroupName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Group name is required");
            }

            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.notFound().build();
            }

            group.setGroupName(newGroupName);
            theGroupsDAOImpl.updateGroupName(group);

            return ResponseEntity.ok().body(group);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: Please try again later");
        }
    }

    @PostMapping ("/splitwise/login")
    public ResponseEntity<String> loginController (@RequestBody User authenticationRequest) throws Exception
    {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));


            SecurityContextHolder.getContext().setAuthentication(authentication);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());


            String username = userDetails.getUsername();
            final String jwtToken = jwtUtil.generateToken(username);

            return ResponseEntity.ok(jwtToken);

        }
        catch(BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: "+ e.getMessage());
        }
        catch(AuthenticationException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error ocuured!");
        }
    }

    @PutMapping ("/splitwise/groups/{groupId}/restore")
    public ResponseEntity<?> restoreGroup(@PathVariable int groupId) {
        try {
            boolean isRestored = theGroupsDAOImpl.restoreGroup(groupId);
            if (!isRestored) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found or already active.");
            }
            return ResponseEntity.ok("Group restored successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to restore group: " + e.getMessage());
        }
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
                        } else {
                            // If not already a payer, create new record
                            ExpenseParticipants newParticipant = new ExpenseParticipants(expense, participant, amountOwed, BigDecimal.ZERO);
                            newParticipant.setId(new ExpenseParticipantsId(expense.getId(), participantUsername));
                            expenseParticipantsDAO.save(newParticipant);
                        }
                    }
                }
            });

            Map<String, Object> expenseDetails = new HashMap<>();
            expenseDetails.put("id", expense.getId());
            expenseDetails.put("expenseName", expense.getExpenseName());
            expenseDetails.put("dateCreated", expense.getDateCreated());
            expenseDetails.put("amount", expense.getAmount());

            ExpenseParticipants participant = expenseParticipantsDAO.findParticipant(expense.getId(),username);
            if(participant != null) {
                BigDecimal zero = BigDecimal.ZERO;
                if (participant.getAmountpaid().compareTo(zero) == 0 && participant.getAmountOwed().compareTo(zero) == 0) {
                    expenseDetails.put("not involved", 0);
                } else {
                    expenseDetails.put("involved", participant.getAmountpaid().subtract(participant.getAmountOwed()));
                }
            }

            return ResponseEntity.ok(expenseDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add expense: " + e.getMessage());
        }
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

            //Expenses updatedExpense = expensesDAO.findExpenseById(expenseId);
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
                    //ExpenseParticipants participantTemp = expenseParticipantsDAO.findParticipant(expense.getId(), payerUsername);
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


    @PostMapping("/splitwise/signup")
    public ResponseEntity<?> signupController(@RequestBody User newUser)
    {
        try {
            User findUser = theUserDAOImpl.findUserByName(newUser.getUsername());

            if(findUser != null){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
            }

            theUserDAOImpl.save(newUser);
            return ResponseEntity.ok("Signup successfull");

        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error ocuured!");
        }
    }
}
