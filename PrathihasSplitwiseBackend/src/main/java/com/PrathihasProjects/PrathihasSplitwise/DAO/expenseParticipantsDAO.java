package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;

import java.util.List;

public interface expenseParticipantsDAO {

    void save(ExpenseParticipants participants);

    List<User> getParticipants(int expenseId);



}
