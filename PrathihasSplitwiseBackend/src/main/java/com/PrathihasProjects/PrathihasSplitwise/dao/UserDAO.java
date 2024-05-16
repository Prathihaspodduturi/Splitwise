package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.User;

public interface UserDAO {

    void save(User user);

    User findUserByName(String userName);

}
