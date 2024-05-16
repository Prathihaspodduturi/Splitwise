package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;

import java.util.List;

public interface ExpensesDAO {

    void save(Expenses expense);

    List<Expenses> groupExpenses (int groupId);

    public boolean deleteExpense(int expenseId);

    public boolean undoDeletion(int expenseId);

    Expenses findExpenseById(int expenseId);

    public void updateExpense(Expenses expense);

}
