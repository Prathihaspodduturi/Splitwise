package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class expensesDAOImpl implements expensesDAO{

    public EntityManager entityManager;

    public expensesDAOImpl(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Expenses expense) {
        entityManager.persist(expense);
    }

    @Override
    @Transactional
    public void updateExpense(Expenses expense)
    {
        entityManager.merge(expense);
    }

    @Override
    public List<Expenses> groupExpenses(int groupId) {
        return entityManager.createQuery(
                        "SELECT e FROM Expenses e WHERE e.groupId.id = :groupId", Expenses.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }



    @Override
    public boolean deleteExpense(int expenseId) {
        Expenses expense = entityManager.find(Expenses.class, expenseId);

        if(expense != null)
        {
            expense.setDeleted(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean undoDeletion(int expenseId) {
        Expenses expense = entityManager.find(Expenses.class, expenseId);

        if(expense != null)
        {
            expense.setDeleted(false);
            return true;
        }
        return false;
    }

    @Override
    public Expenses findExpenseById(int expenseId) {
        return entityManager.find(Expenses.class, expenseId);
    }
}
