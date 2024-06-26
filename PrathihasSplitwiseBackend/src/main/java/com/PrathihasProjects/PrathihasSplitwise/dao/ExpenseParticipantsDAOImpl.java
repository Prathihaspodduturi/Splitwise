package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ExpenseParticipantsDAOImpl implements ExpenseParticipantsDAO {

    private EntityManager entityManager;

    public ExpenseParticipantsDAOImpl(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(ExpenseParticipants participants) {
        entityManager.persist(participants);
    }

    @Override
    public List<User> getParticipants(int expenseId) {
        List<ExpenseParticipants> participants = entityManager.createQuery(
                        "SELECT ep FROM ExpenseParticipants ep WHERE ep.expense.id = :expenseId", ExpenseParticipants.class)
                .setParameter("expenseId", expenseId)
                .getResultList();
        return participants.stream()
                .map(ExpenseParticipants::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseParticipants findParticipant(int expenseId, String username)
    {
        try {
            return entityManager.createQuery("SELECT ep FROM ExpenseParticipants ep WHERE ep.expense.id = :expenseId AND ep.user.username = :username", ExpenseParticipants.class)
                    .setParameter("expenseId", expenseId)
                    .setParameter("username", username)
                    .getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
    }

    @Override
    public List<ExpenseParticipants> findByExpenseId(int expenseId) {
        return entityManager.createQuery(
                        "SELECT ep FROM ExpenseParticipants ep WHERE ep.expense.id = :expenseId", ExpenseParticipants.class)
                .setParameter("expenseId", expenseId)
                .getResultList();
    }

    @Override
    @Transactional
    public void updateExpenseParticipants(ExpenseParticipants participant) {
        entityManager.merge(participant);
    }
}
