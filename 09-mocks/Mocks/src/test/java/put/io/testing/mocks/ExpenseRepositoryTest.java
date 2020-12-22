package put.io.testing.mocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import put.io.students.fancylibrary.database.FancyDatabase;
import put.io.students.fancylibrary.database.IFancyDatabase;

import java.util.Collections;

public class ExpenseRepositoryTest {

    private ExpenseRepository expenseRepository;

    @AfterEach
    void tearDown() {
        expenseRepository = null;
    }

    @Test
    void loadExpensesTest() {
        IFancyDatabase mockObject = mock(IFancyDatabase.class);
        when(mockObject.queryAll()).thenReturn(Collections.emptyList());

        expenseRepository = new ExpenseRepository(mockObject);
        expenseRepository.loadExpenses();

        InOrder inOrder = inOrder(mockObject);
        inOrder.verify(mockObject).connect();
        inOrder.verify(mockObject).queryAll();
        inOrder.verify(mockObject).close();

        assertTrue(expenseRepository.getExpenses().isEmpty());
    }

    @Test
    void saveExpensesTest() {
        IFancyDatabase mockObject = mock(IFancyDatabase.class);
        when(mockObject.queryAll()).thenReturn(Collections.emptyList());

        expenseRepository = new ExpenseRepository(mockObject);
        expenseRepository.loadExpenses();
        expenseRepository.addExpense(getExpense("FirstTitle"));
        expenseRepository.addExpense(getExpense("SecondTitle"));
        expenseRepository.addExpense(getExpense("ThirdTitle"));
        expenseRepository.addExpense(getExpense("FourthTitle"));
        expenseRepository.addExpense(getExpense("FifthTitle"));
        expenseRepository.saveExpenses();

        InOrder inOrder = inOrder(mockObject);
        inOrder.verify(mockObject).connect();
        inOrder.verify(mockObject).queryAll();
        inOrder.verify(mockObject).close();
        verify(mockObject, times(5)).persist(any(Expense.class));

        assertFalse(expenseRepository.getExpenses().isEmpty());

    }

    @org.jetbrains.annotations.NotNull
    private Expense getExpense(String title) {
        Expense expense = new Expense();
        expense.setAmount(12345);
        expense.setCategory("DefaultCategory");
        expense.setTitle(title);
        return expense;
    }
}
