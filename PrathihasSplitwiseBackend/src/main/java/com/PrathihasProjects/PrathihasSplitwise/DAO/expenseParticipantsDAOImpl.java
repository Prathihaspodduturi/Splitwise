package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class expenseParticipantsDAOImpl implements expenseParticipantsDAO{

    private EntityManager entityManager;

    public expenseParticipantsDAOImpl(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
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


}
