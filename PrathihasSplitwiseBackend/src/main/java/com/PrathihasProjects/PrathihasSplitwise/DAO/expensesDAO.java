package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;

import java.util.List;

public interface expensesDAO {

    void save(Expenses expense);

    List<Expenses> groupExpenses (int groupId);

    public boolean deleteExpense(int expenseId);

    public boolean undoDeletion(int expenseId);

}
