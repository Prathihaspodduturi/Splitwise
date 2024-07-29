package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserDAOImpl userDAO;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        openMocks.close();
    }


    @Test
    void testSave() {
        User user = new User("testUser", "password");

        userDAO.save(user);
        verify(entityManager).persist(user);
    }

    @Test
    void testFindUserByName()
    {
        String userName = "testUser";

        User expectedUser = new User();
        expectedUser.setUsername(userName);

        when(entityManager.find(User.class, userName)).thenReturn(expectedUser);

        User actualUser = userDAO.findUserByName(userName);

        verify(entityManager).find(User.class, userName);

        assertSame(expectedUser, actualUser, "The user returned was not the user expected");

    }
}
