package game_engine.util;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class ListItemPairerTest {

    @Test
    public void shouldNotReturnAnyPairsForEmptyList() {
        ListItemPairer<Integer> pairer = new ListItemPairer<>(List.of(), Optional.empty(), Optional.empty());
        assertFalse(pairer.iterator().hasNext());
    }

    @Test
    public void shouldReturnANullPairForTheFirstElement() {
        List<Integer> sourceList = List.of(1);
        ListItemPairer<Integer> pairer = new ListItemPairer<>(sourceList, Optional.empty(), Optional.empty());
        Pair<Integer> pair = pairer.iterator().next();
        assertNull(pair.getFirst());
        assertEquals(1, pair.getSecond());
    }

    @Test
    public void shouldReturnANullPairForTheLastElement() {
        List<Integer> sourceList = List.of(1);
        ListItemPairer<Integer> pairer = new ListItemPairer<>(sourceList, Optional.empty(), Optional.empty());
        Iterator<Pair<Integer>> iterator = pairer.iterator();
        iterator.next();
        Pair<Integer> pair = iterator.next();
        assertEquals(1, pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void shouldCreateMultiplePairsForElements() {
        List<Integer> sourceList = List.of(1,2,3);
        ListItemPairer<Integer> pairer = new ListItemPairer<>(sourceList, Optional.empty(), Optional.empty());

        Iterator<Pair<Integer>> iterator = pairer.iterator();
        Pair<Integer> pair = iterator.next();
        assertNull(pair.getFirst());
        assertEquals(1, pair.getSecond());

        pair = iterator.next();
        assertEquals(1, pair.getFirst());
        assertEquals(2, pair.getSecond());

        pair = iterator.next();
        assertEquals(2, pair.getFirst());
        assertEquals(3, pair.getSecond());

        pair = iterator.next();
        assertEquals(3, pair.getFirst());
        assertNull(pair.getSecond());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldPairOnlyBetweenSpecificListItems() {
        Employee e1 = new Employee(1, 20);
        Employee e2 = new Employee(2, 30);
        Employee e3 = new Employee(3, 40);
        Employee e4 = new Employee(4, 50);
        Employee e5 = new Employee(5, 60);

        List<Employee> sourceList = List.of(e1, e2, e3, e4, e5);
        Predicate<Employee> starter = employee -> employee.getAge() > 20;
        Predicate<Employee> ender = employee -> employee.getAge() < 60;
        ListItemPairer<Employee> pairer = new ListItemPairer<>(sourceList, Optional.of(starter), Optional.of(ender));

        Iterator<Pair<Employee>> iterator = pairer.iterator();
        Pair<Employee> pair = iterator.next();
        assertEquals(30, pair.getFirst().getAge());
        assertEquals(40, pair.getSecond().getAge());
        pair = iterator.next();
        assertEquals(40, pair.getFirst().getAge());
        assertEquals(50, pair.getSecond().getAge());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldStartPairingOnlyFromSpecificElementButShouldAddNullPairForEnding() {
        Employee e1 = new Employee(1, 20);
        Employee e2 = new Employee(2, 30);
        Employee e3 = new Employee(3, 40);
        Employee e4 = new Employee(4, 50);
        Employee e5 = new Employee(5, 60);

        List<Employee> sourceList = List.of(e1, e2, e3, e4, e5);
        Predicate<Employee> starter = employee -> employee.getAge() > 20;
        ListItemPairer<Employee> pairer = new ListItemPairer<>(sourceList, Optional.of(starter), Optional.empty());

        Iterator<Pair<Employee>> iterator = pairer.iterator();
        Pair<Employee> pair = iterator.next();
        assertEquals(30, pair.getFirst().getAge());
        assertEquals(40, pair.getSecond().getAge());
        pair = iterator.next();
        assertEquals(40, pair.getFirst().getAge());
        assertEquals(50, pair.getSecond().getAge());
        pair = iterator.next();
        assertEquals(50, pair.getFirst().getAge());
        assertEquals(60, pair.getSecond().getAge());
        pair = iterator.next();
        assertEquals(60, pair.getFirst().getAge());
        assertNull(pair.getSecond());
    }

    @Test
    public void shouldAddNullPairForStartingAndShouldEndPairingAtSpecificElement() {
        Employee e1 = new Employee(1, 20);
        Employee e2 = new Employee(2, 30);
        Employee e3 = new Employee(3, 40);
        Employee e4 = new Employee(4, 50);
        Employee e5 = new Employee(5, 60);

        List<Employee> sourceList = List.of(e1, e2, e3, e4, e5);
        Predicate<Employee> ender = employee -> employee.getAge() < 60;
        ListItemPairer<Employee> pairer = new ListItemPairer<>(sourceList, Optional.empty(), Optional.of(ender));

        Iterator<Pair<Employee>> iterator = pairer.iterator();
        Pair<Employee> pair = iterator.next();
        assertNull(pair.getFirst());
        assertEquals(20, pair.getSecond().getAge());
        pair = iterator.next();
        assertEquals(20, pair.getFirst().getAge());
        assertEquals(30, pair.getSecond().getAge());
        pair = iterator.next();
        assertEquals(30, pair.getFirst().getAge());
        assertEquals(40, pair.getSecond().getAge());
        pair = iterator.next();
        assertEquals(40, pair.getFirst().getAge());
        assertEquals(50, pair.getSecond().getAge());
    }

    class Employee {
        private int empId;
        private int age;

        public Employee(int empId, int age) {
            this.empId = empId;
            this.age = age;
        }

        public int getEmpId() {
            return empId;
        }

        public int getAge() {
            return age;
        }
    }

}