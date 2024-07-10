package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;

import java.util.Date;

public interface GroupsDAO {

    void save(Groups groups);

    Groups findGroupById(int id);

    boolean deletegroupById(int id, String username, Date date);

    boolean settlegroupById(int id, String username, Date date);

    boolean restoreGroup(int id);

    void updateGroupName(Groups group);
}
