package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
public class GroupUpdationController {
    private final GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    public GroupUpdationController(GroupsDAOImpl theGroupsDAOImpl) {
        this.theGroupsDAOImpl = theGroupsDAOImpl;
    }
    @PutMapping("/splitwise/groups/{groupId}/update")
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

}
