package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;

import java.util.List;

public interface ExpensesDAO {

    void save(Expenses expense);

    List<Expenses> groupExpenses (int groupId);

    //boolean deleteExpense(int expenseId);

    //boolean undoDeletion(int expenseId);

    Expenses findExpenseById(int expenseId);

    void updateExpense(Expenses expense);

}
