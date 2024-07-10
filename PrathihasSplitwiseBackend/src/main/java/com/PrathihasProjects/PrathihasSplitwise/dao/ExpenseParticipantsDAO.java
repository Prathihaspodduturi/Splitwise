package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
//import com.PrathihasProjects.PrathihasSplitwise.entity.User;

import java.util.List;

public interface ExpenseParticipantsDAO {

    void save(ExpenseParticipants participants);

    //List<User> getParticipants(int expenseId);

    ExpenseParticipants findParticipant(int expenseId, String username);

    List<ExpenseParticipants> findByExpenseId(int expenseId);

    void updateExpenseParticipants(ExpenseParticipants participants);

    void deleteParticipantByExpenseAndUser(int expenseId, String username);

}
