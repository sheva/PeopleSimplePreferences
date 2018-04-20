package com.sheva.services;

import com.sheva.data.Food;
import com.sheva.data.Person;
import com.sheva.api.exceptions.EntityNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static com.sheva.db.DatabaseTestHelper.deleteAllData;
import static com.sheva.db.DatabaseTestHelper.loadTestData;
import static org.junit.Assert.*;

/**
 * Tests for FoodDAO class.
 *
 * Created by Sheva on 10/1/2016.
 */
public class TestFoodService {

    private FoodDAO dao = new FoodDAO();

    @Before
    public void setUp() throws Exception {
        loadTestData();
    }

    @After
    public void tearDown() throws Exception {
        deleteAllData();
    }

    @Test
    public void testListAllFood() {
        List<Food> food = dao.findAll();
        assertEquals(7, food.size());
        food.sort((f1, f2) -> (f1.getName().compareToIgnoreCase(f2.getName())));
        assertEquals("some tasty", food.get(6).getName());
    }

    @Test
    public void testFindById() {
        Food food = dao.findById(3);
        assertEquals("bass", food.getName());
    }

    @Test
    public void testGetByName() {
        List<Food> food = dao.findByName("bana");
        food.sort((f1, f2) -> (f1.getId() - f2.getId()));
        assertEquals(1, food.size());
        assertEquals("bananas", food.get(0).getName());
        assertEquals(1, food.get(0).getId());
    }

    @Test
    public void testUpdateById() {
        Food food = dao.findById(3);
        String oldName = food.getName();
        Person person = new Person();
        person.setFirstName("testPerson");
        person.setDateOfBirth(LocalDate.of(1923,5,6));
        dao.updateById(3, "bass fish meat");
        food = dao.findById(3);
        assertNotSame(oldName, food.getName());
        assertEquals("bass fish meat", food.getName());
    }

    @Test
    public void testUpdate() {
        Food food = dao.findById(3);
        String oldName = food.getName();
        food.setName("bass fish meat");
        dao.update(food);
        food = dao.findById(3);
        assertNotSame(oldName, food.getName());
        assertEquals("bass fish meat", food.getName());
    }

    @Test
    public void testUpdateByIdWhenNotFound() {
        try {
            dao.updateById(43, "bass fish meat");
            fail("EntityNotFoundException not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof EntityNotFoundException);
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testFindByName() {
        List<Food> food = dao.findByName("c");
        assertEquals(3, food.size());
        food.sort((f1, f2) -> (f1.getName().compareToIgnoreCase(f2.getName())));
        assertEquals("cakes", food.get(0).getName());
        assertEquals("candies", food.get(1).getName());
        assertEquals("chocolate", food.get(2).getName());

        food = dao.findByName("cakes");
        assertEquals(1, food.size());

        assertTrue(dao.findByName("baba").isEmpty());
    }
}
