package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.User;

public interface userDAO {

    void save(User user);

    User findUserByName(String userName);

}
