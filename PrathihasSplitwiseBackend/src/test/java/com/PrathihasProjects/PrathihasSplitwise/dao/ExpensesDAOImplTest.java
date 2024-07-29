package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ExpensesDAOImplTest {

    AutoCloseable openMocks;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ExpensesDAOImpl expensesDAO;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        openMocks.close();
    }

    @Test
    void saveTest ()
    {
        Expenses expense = new Expenses();

        expensesDAO.save(expense);

        verify(entityManager).persist(expense);
    }

    @Test
    void updateExpenseTest ()
    {
        Expenses expense = new Expenses();

        expensesDAO.updateExpense(expense);

        verify(entityManager).merge(expense);
    }

    @Test
    void groupExpensesTest ()
    {
        int groupId = 1;

        Expenses expense1 = new Expenses();
        Expenses expense2 = new Expenses();

        List<Expenses> expectedExpenses = Arrays.asList(expense1, expense2);

        TypedQuery<Expenses> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Expenses.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedExpenses);

        List<Expenses> result = expensesDAO.groupExpenses(groupId);

        assertTrue(result.containsAll(expectedExpenses));

        verify(entityManager).createQuery(anyString(), eq(Expenses.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).getResultList();
    }

    @Test
    void findExpenseByIdTest () {

        int expenseId = 1;

        Expenses expense = new Expenses();
        expense.setId(expenseId);

        when(entityManager.find(Expenses.class, expenseId)).thenReturn(expense);

        Expenses result = expensesDAO.findExpenseById(expenseId);

        assertEquals(result.getId(), 1, "Id of the expense must match to 1");

    }
}
