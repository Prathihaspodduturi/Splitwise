package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class GetAllGroupsController {
    private final GroupMembersDAOImpl groupMembersDAO;

    @Autowired
    public GetAllGroupsController(GroupMembersDAOImpl groupMembersDAO) {
        this.groupMembersDAO = groupMembersDAO;
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
}
