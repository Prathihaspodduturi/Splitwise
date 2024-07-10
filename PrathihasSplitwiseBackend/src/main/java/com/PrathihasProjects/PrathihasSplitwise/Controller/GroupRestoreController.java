package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class GroupRestoreController {

    private final GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    public GroupRestoreController(GroupsDAOImpl theGroupsDAOImpl) {
        this.theGroupsDAOImpl = theGroupsDAOImpl;
    }
    @PutMapping("/splitwise/groups/{groupId}/restore")
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

}
