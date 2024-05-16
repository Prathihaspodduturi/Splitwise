package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;

import java.util.Date;

public interface GroupsDAO {

    public void save(Groups groups);

    public Groups findGroupById(int id);

    public boolean deletegroupById(int id, String username, Date date);

    public boolean settlegroupById(int id, String username, Date date);

    public boolean restoreGroup(int id);

    public void updateGroupName(Groups group);
}
