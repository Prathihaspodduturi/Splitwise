package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;

import java.util.List;

public interface groupsDAO {

    void save(Groups groups);

    public boolean deletegroupById(int id);

    public boolean undoDeletionOfGroup(int id);

}
