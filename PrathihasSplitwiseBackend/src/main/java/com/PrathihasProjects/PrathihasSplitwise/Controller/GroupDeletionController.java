package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@CrossOrigin
public class GroupDeletionController {
    private final GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    public GroupDeletionController(GroupsDAOImpl theGroupsDAOImpl) {
        this.theGroupsDAOImpl = theGroupsDAOImpl;
    }

    @PutMapping("/splitwise/groups/{groupid}/delete")
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

}
