package edu.cbsystematics.com.libraryprojectcbs;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.repository.LogsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LibraryProjectCbsApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private LogsRepository logsRepository;

    @Test
    void testCountUserActions() {
        // Створюємо тестові дані для параметрів
        ActionType actionType = ActionType.ACCESS;
        LocalDateTime startOfMonth = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2022, 1, 31, 23, 59, 59);

        // Викликаємо метод countUserActions з тестовими параметрами
        Long count = logsRepository.countUserActions(actionType, startOfMonth, endOfMonth);

        // Перевіряємо, чи повернуто очікуване значення
        assertEquals(10L, count.longValue()); // Припустимо, що очікуване значення - 10
    }



}



