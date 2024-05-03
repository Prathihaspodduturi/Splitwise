package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;

import java.util.List;

public interface groupsDAO {

    public void save(Groups groups);

    public Groups findGroupById(int id);

    public boolean deletegroupById(int id, String username);

    public boolean settlegroupById(int id, String username);

    public boolean restoreGroup(int id);

    public void updateGroupName(Groups group);
}
