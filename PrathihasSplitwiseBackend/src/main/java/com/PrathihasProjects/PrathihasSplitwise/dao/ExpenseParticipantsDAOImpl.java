package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
//import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
//import java.util.stream.Collectors;

@Repository
public class ExpenseParticipantsDAOImpl implements ExpenseParticipantsDAO {

    private final EntityManager entityManager;

    public ExpenseParticipantsDAOImpl(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(ExpenseParticipants participants) {
        if (participants.getId() == null) {
            entityManager.persist(participants);
        } else {
            entityManager.merge(participants);
        }
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

    @Override
    @Transactional
    public void deleteParticipantByExpenseAndUser(int expenseId, String username) {
        ExpenseParticipants participant = entityManager.createQuery(
                        "SELECT ep FROM ExpenseParticipants ep WHERE ep.expense.id = :expenseId AND ep.user.username = :username", ExpenseParticipants.class)
                .setParameter("expenseId", expenseId)
                .setParameter("username", username)
                .getSingleResult();
        if (participant != null) {
            entityManager.remove(participant);
        }
    }


}
