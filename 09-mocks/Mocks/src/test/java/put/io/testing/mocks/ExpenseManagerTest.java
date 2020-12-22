package put.io.testing.mocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.*;

import put.io.students.fancylibrary.database.FancyDatabase;
import put.io.students.fancylibrary.database.IFancyDatabase;
import put.io.students.fancylibrary.service.FancyService;

import java.net.ConnectException;
import java.util.Collections;
import java.util.List;

public class ExpenseManagerTest {

    @Test
    public void calculateTotalTest() {

        IExpenseRepository mockExpenseRepo = mock(IExpenseRepository.class);

        when(mockExpenseRepo.getExpenses())
                .thenReturn(List.of(
                        createExpense("First"),
                        createExpense("Second"),
                        createExpense("Third")));
        ExpenseManager expenseManager = new ExpenseManager(mockExpenseRepo, new FancyService());
        long total = expenseManager.calculateTotal();

        assertEquals(27, total);

    }

    @Test
    void calculateTotalForCategoryTest() {

        IExpenseRepository mockExpenseRepo = mock(IExpenseRepository.class);
        when(mockExpenseRepo.getExpensesByCategory(argThat(s ->
                        !s.equals("Home") && !s.equals("Car"))))
                .thenReturn(Collections.emptyList());
        //
        when(mockExpenseRepo.getExpensesByCategory("Home"))
                .thenReturn(
                        List.of(createExpenseForCategory("Home"),
                                createExpenseForCategory("Home"),
                                createExpenseForCategory("Home")));
        //
        when(mockExpenseRepo.getExpensesByCategory("Car"))
                .thenReturn(
                        List.of(createExpenseForCategory("Car"),
                                createExpenseForCategory("Car")));

        ExpenseManager expenseManager = new ExpenseManager(mockExpenseRepo, new FancyService());
        long totalForHome = expenseManager.calculateTotalForCategory("Home");
        long totalForCars = expenseManager.calculateTotalForCategory("Car");
        long totalForFood = expenseManager.calculateTotalForCategory("Food");
        long totalForSport = expenseManager.calculateTotalForCategory("Sport");

        verify(mockExpenseRepo, times(4)).getExpensesByCategory(anyString());

        assertEquals(27, totalForHome);
        assertEquals(18, totalForCars);
        assertEquals(0, totalForFood);
        assertEquals(0, totalForSport);
    }

    @Test
    void calculateTotalInDollarsTest() throws ConnectException {
        IExpenseRepository mockExpenseRepo = mock(IExpenseRepository.class);
        when(mockExpenseRepo.getExpenses()).thenReturn(List.of(
                createExpense("first"), createExpense("second")));

        FancyService mockFancyService = mock(FancyService.class);
/*        when(mockFancyService.convert(anyDouble(), eq("PLN"), eq("USD")))
                .thenReturn(72.0);
        when(mockFancyService.convert(anyDouble(), eq("PLN"), eq("USD")))
                .thenThrow(new ConnectException());*/

        when(mockFancyService.convert(anyDouble(), eq("PLN"), eq("USD")))
                .thenAnswer(invocationOnMock ->
                { double arg = (double)invocationOnMock.getArgument(0);
                    return arg * 4;
                });

        ExpenseManager expenseManager = new ExpenseManager(mockExpenseRepo, mockFancyService);

        long inDolars = expenseManager.calculateTotalInDollars();

        assertEquals(72.0, inDolars);

    }

    private Expense createExpenseForCategory(String category) {
        Expense expense = new Expense();
        expense.setAmount(9);
        expense.setCategory(category);
        expense.setTitle("DefaultTitle");
        return expense;
    }

    private Expense createExpense(String title) {
        Expense expense = new Expense();
        expense.setAmount(9);
        expense.setCategory("DefaultCategory");
        expense.setTitle(title);
        return expense;
    }

}
