package com.sheva.services;

import com.sheva.data.Color;
import com.sheva.data.Food;
import com.sheva.data.Person;
import com.sheva.db.DatabaseTestHelper;
import com.sheva.api.exceptions.AlreadyExistsException;
import com.sheva.api.exceptions.EntityNotFoundException;
import junit.framework.Assert;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static junit.framework.Assert.*;
import static com.sheva.db.DatabaseTestHelper.deleteAllData;
import static com.sheva.db.DatabaseTestHelper.loadTestData;


/**
 * Tests for PersonDAO class.
 *
 * Created by Sheva on 9/29/2016.
 */
public class TestPersonService {

    private PersonDAO dao = new PersonDAO();

    @Before
    public void setUp() throws Exception {
        loadTestData();
    }

    @After
    public void tearDown() throws Exception {
        deleteAllData();
    }

    @Test
    public void testFindAll() {
        List<Person> people = dao.findAll();
        assertNotNull(people);
        people.sort((h1, h2) -> (h1.getId() - h2.getId()));
        assertEquals(3, people.size());
        assertEquals("Vasya", people.get(0).getFirstName());
        assertEquals("Ivanov", people.get(1).getLastName());
        assertEquals("1957-02-12", people.get(2).getDateOfBirth().toString());
        assertEquals("59", people.get(2).getAge().toString());
    }

    @Test
    public void testFindById() {
        Person person = dao.findById(1);
        assertEquals("Vasya", person.getFirstName());
        assertEquals("Ivanov", person.getLastName());
        assertEquals("1985-12-18", person.getDateOfBirth().toString());
        assertEquals(calculateYearsFromDateOfBirth("1985-12-18"), person.getAge().intValue());
        assertNotNull(person.getFood());
    }

    private int calculateYearsFromDateOfBirth(String dateFormat) {
        return Period.between(LocalDate.parse(dateFormat), LocalDate.now()).getYears();
    }

    @Test
    public void testFindByName() {
        List<Person> people = dao.searchByParams(buildParams(null, "Ivanov"));
        assertEquals(2, people.size());
        people.sort((h1, h2) -> (h1.getId() - h2.getId()));
        assertEquals("Vasya", people.get(0).getFirstName());
        assertEquals("Ivanov", people.get(0).getLastName());
        assertEquals("Irena", people.get(1).getFirstName());
        assertEquals("Ivanov", people.get(1).getLastName());

        people = dao.searchByParams(buildParams("Vasya", ""));
        assertEquals(2, people.size());
        people.sort((h1, h2) -> (h1.getId() - h2.getId()));
        assertEquals("Vasya", people.get(0).getFirstName());
        assertEquals("Ivanov", people.get(0).getLastName());
        assertEquals("Vasya", people.get(1).getFirstName());
        assertEquals("Petrov", people.get(1).getLastName());

        people = dao.searchByParams(buildParams("as", null));
        assertEquals(2, people.size());
        assertEquals("Vasya", people.get(0).getFirstName());

        people = dao.searchByParams(buildParams("as", "Pet"));
        assertEquals(1, people.size());
        assertEquals("Vasya", people.get(0).getFirstName());
        assertEquals("Petrov", people.get(0).getLastName());

        assertTrue(dao.searchByParams(buildParams("Lena", null)).isEmpty());
    }

    @Test
    public void testNotFound() {
        try {
            dao.findById(56);
            fail();
        } catch (EntityNotFoundException e) {
            assertTrue(e.getException() instanceof EntityNotFoundException.EntityNotFound);
        }
    }

    @Test
    public void testFindByParamsWhenEmptyOrNotSet() {
        assertEquals(3, dao.searchByParams(buildParams(null, "")).size());
        assertEquals(3, dao.searchByParams(buildParams("", null)).size());
        assertEquals(3, dao.searchByParams(buildParams("", "")).size());
        assertEquals(3, dao.searchByParams(buildParams("    ", "    ")).size());
        assertEquals(3, dao.searchByParams(buildParams(null, null)).size());
    }

    @Test
    public void testUpdate() {
        Person person = dao.findById(1);
        String oldName = person.getFirstName();
        String oldLastName = person.getLastName();
        LocalDate birthday = person.getDateOfBirth();
        Set<Color> oldColors = person.getColor();
        Set<Food> oldFood = person.getFood();

        person.setFirstName("test");
        person.setLastName("Sheva");
        person.setDateOfBirth(LocalDate.of(1985, 12 ,18));
        person.setColor(new HashSet<Color>(){{
            add(Color.blue);
            add(Color.orange);
        }});
        person.setFood(new HashSet<Food>(){
            {
                Food food = new Food();
                food.setName("test");
                add(food);
            }
        });

        dao.update(person);
        person = dao.searchByParams(buildParams("test", null)).get(0);
        assertNotSame(oldName, person.getFirstName());
        assertEquals("test", person.getFirstName());
        assertNotSame(oldLastName, person.getLastName());
        assertEquals("Sheva", person.getLastName());
        assertNotSame(birthday, person.getDateOfBirth());
        assertEquals(LocalDate.of(1985, 12 ,18), person.getDateOfBirth());
        assertNotSame(oldColors, person.getColor());
        assertTrue(person.getColor().contains(Color.blue));
        assertTrue(person.getColor().contains(Color.orange));
        assertNotSame(oldFood, person.getFood());
        assertEquals(1, new FoodDAO().findByName("test").size());
    }

    @Test
    public void testUpdateWhenExist() {
        Person person = dao.findById(1);
        person.setFirstName("Vasya");
        person.setLastName("Petrov");
        person.setDateOfBirth(LocalDate.of(1957, 02 ,12));
        person.setColor(new HashSet<Color>(){{add(Color.blue); add(Color.orange);}});
        person.setFood(new HashSet<Food>(){{
            Food food = new Food();
            food.setName("test");
            add(food);
        }});

        try {
            dao.update(person);
            fail("AlreadyExistsException was not thrown");
        } catch (AlreadyExistsException e) {
            assertTrue(e.getCause() instanceof ConstraintViolationException);
        }
    }

    @Test
    public void testUpdateByIdBirthday() {
        Person person = dao.findById(1);
        String oldFirstName = person.getFirstName();
        String oldLastName = person.getLastName();
        LocalDate oldDateOfBirth = person.getDateOfBirth();
        Integer oldAge = person.getAge();
        Set<Food> oldFavoriteFood = person.getFood();
        Set<Color> oldFavoriteColor = person.getColor();

        LocalDate newBirthDate = LocalDate.of(1975, 4, 6);
        person.setDateOfBirth(newBirthDate);
        Person personModified = dao.updateById(1, person);

        assertEquals(oldFirstName, personModified.getFirstName());
        assertEquals(oldLastName, personModified.getLastName());
        assertNotSame(oldDateOfBirth, personModified.getDateOfBirth().toString());
        assertEquals(newBirthDate.toString(), personModified.getDateOfBirth().toString());
        assertNotSame(oldAge, personModified.getDateOfBirth());
        assertEquals(calculateYearsFromDateOfBirth(newBirthDate.toString()), personModified.getAge().intValue());
        assertEquals(oldFavoriteFood, personModified.getFood());
        assertEquals(oldFavoriteColor, personModified.getColor());
    }

    @Test
    public void testUpdateByNames() {
        Person person = dao.findById(1);
        String oldFirstName = person.getFirstName();
        String oldLastName = person.getLastName();
        LocalDate oldDateOfBirth = person.getDateOfBirth();
        Integer oldAge = person.getAge();
        Set<Color> oldFavoriteColor = person.getColor();
        Set<Food> oldFavoriteFood = person.getFood();

        String newFirstName = "TEST";
        String newLastName = "SOME";
        person.setFirstName(newFirstName);
        person.setLastName(newLastName);

        Person personModified = dao.updateById(person.getId(), person);

        assertNotSame(oldFirstName, personModified.getFirstName());
        assertEquals(newFirstName, personModified.getFirstName());
        assertNotSame(oldLastName, personModified.getLastName());
        assertEquals(newLastName, personModified.getLastName());
        assertEquals(oldDateOfBirth, personModified.getDateOfBirth());
        assertEquals(oldAge, personModified.getAge());
        assertEquals(oldFavoriteFood, personModified.getFood());
        assertEquals(oldFavoriteColor, personModified.getColor());
    }

    @Test
    public void testUpdateByFavoriteColors() {
        Person person = dao.findById(1);
        String oldFirstName = person.getFirstName();
        String oldLastName = person.getLastName();
        LocalDate oldDateOfBirth = person.getDateOfBirth();
        Integer oldAge = person.getAge();
        Set<Food> oldFavoriteFood = person.getFood();
        Set<Color> oldFavoriteColor = person.getColor();

        assertEquals(1, person.getColor().size());
        assertTrue(person.getColor().contains(Color.yellow));
        person.setColor(new HashSet<Color>(){{add(Color.blue); add(Color.green);}});
        Person personModified = dao.updateById(1, person);

        assertEquals(oldFirstName, personModified.getFirstName());
        assertEquals(oldLastName, personModified.getLastName());
        assertEquals(oldDateOfBirth, personModified.getDateOfBirth());
        assertEquals(oldAge, personModified.getAge());
        assertNotSame(oldFavoriteColor, personModified.getColor());
        assertEquals(2, personModified.getColor().size());
        assertTrue(personModified.getColor().containsAll(new HashSet<Color>(){{add(Color.blue); add(Color.green);}}));
    }

    @Test
    public void testUpdateByFavoriteFood() {
        Person person = dao.findById(1);
        String oldFirstName = person.getFirstName();
        String oldLastName = person.getLastName();
        LocalDate oldDateOfBirth = person.getDateOfBirth();
        Integer oldAge = person.getAge();
        Set<Color> oldFavoriteColor = person.getColor();
        Set<Food> oldFavoriteFood = person.getFood();
        Set<Food> newFavoriteFood = new HashSet<>();
        Food food = new Food();
        food.setName("test");
        newFavoriteFood.add(food);
        person.setFood(newFavoriteFood);

        Person personModified = dao.updateById(person.getId(), person);

        assertEquals(oldFirstName, personModified.getFirstName());
        assertEquals(oldLastName, personModified.getLastName());
        assertEquals(oldDateOfBirth, personModified.getDateOfBirth());
        assertEquals(oldAge, personModified.getAge());
        assertEquals(oldFavoriteColor, personModified.getColor());
        assertNotSame(oldFavoriteFood, personModified.getFood());
    }

    @Test
    public void testUpdateByIdWhenNotFound() {
        try {
            Person person = new Person();
            person.setFirstName("test");
            person.setLastName("test");
            person.setDateOfBirth(LocalDate.parse("2012-03-14"));
            dao.updateById(45, person);
            fail("EntityNotFoundException not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof EntityNotFoundException);
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testDeleteById() {
        Person person = dao.findById(1);
        dao.deleteById(1);
        assertEquals("Vasya", person.getFirstName());
        try {
            assertNull(dao.findById(person.getId()));
            fail("Entity not found should be thrown");
        } catch (Exception ignored) {}
        try {
            assertNull(new FoodDAO().findById(person.getFood().iterator().next().getId()));
            fail("Entity not found should be thrown");
        } catch (Exception ignored) {}

        List<Person> people = dao.findAll();
        assertEquals(2, people.size());
        assertEquals(6, DatabaseTestHelper.executeSqlQuery("select * from FOOD").size());
        assertEquals(2, DatabaseTestHelper.executeSqlQuery("select * from PERSON_COLOR_PREFERENCES").size());
        assertEquals(2, DatabaseTestHelper.executeSqlQuery("select * from PERSON_FOOD_PREFERENCES").size());
    }

    @Test
    public void testDelete() {
        Person person = dao.findById(1);
        assertEquals("Vasya", person.getFirstName());
        dao.delete(person);
        List<Person> people = dao.findAll();
        assertEquals(2, people.size());
        assertEquals(6, DatabaseTestHelper.executeSqlQuery("select * from FOOD").size());
    }

    @Test
    public void testDeleteByIdNotExisting() {
        try {
            dao.deleteById(122);
            fail("Exception EntityNotFoundException not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof EntityNotFoundException);
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testCreateNewPerson() {
        Set<Food> food = new HashSet<>();
        Food food1 = new Food();
        food1.setName("name");
        food.add(food1);
        dao.create("Lena", "Sheva", LocalDate.of(1985, 12 ,18), new HashSet<Color>(){
            {
                add(Color.blue); add(Color.orange);
            }}, food);
        List<Person> people = dao.searchByParams(buildParams("Lena", null));
        assertEquals(1, people.size());
        Person person = people.get(0);
        assertEquals("Lena", person.getFirstName());
        assertEquals("Sheva", person.getLastName());
        assertEquals("1985-12-18", person.getDateOfBirth().toString());
        assertEquals("30", person.getAge().toString());
        assertTrue(person.getColor().contains(Color.blue));
        assertTrue(person.getColor().contains(Color.orange));
        assertEquals(food1.getName(), person.getFood().iterator().next().getName());
    }

    @Test
    public void testCreateNewPersonWhenAlreadyExists() {
        try {
            Set<Food> food = new HashSet<>();
            Food food1 = new Food();
            food1.setName("name");
            food.add(food1);
            dao.create("Irena", "Ivanov", LocalDate.of(1985, 03 ,23), new HashSet<Color>(){{add(Color.blue); add(Color.orange);}}, food);
            fail("UNIQUE Constraint failed");
        } catch (AlreadyExistsException e) {
            assertTrue(e.getCause() instanceof ConstraintViolationException);
        }
    }

    @Test
    public void testCreateNewPersonWhenFoodAlreadyExists() {
        Person person = dao.findById(1);
        person.setFirstName("test");
        Food food = new FoodDAO().findById(1);
        Set<Food> foodlist = new HashSet<>();
        foodlist.add(food);
        person.setFood(foodlist);
        dao.create(person);
    }

    @Test
    public void testCreate() {
        Person person = new Person();
        person.setFirstName("Lena");
        person.setLastName("Sheva");
        person.setDateOfBirth(LocalDate.of(1985, 12 ,18));
        person.setColor(new HashSet<Color>(){{add(Color.blue); add(Color.orange);}});
        person.setFood(new HashSet<Food>(){{Food food = new Food();food.setName("test"); add(food);}});
        dao.create(person);
        List<Person> people = dao.searchByParams(buildParams("Lena", null));
        assertEquals(1, people.size());
        person = people.get(0);
        assertEquals("Lena", person.getFirstName());
        assertEquals("Sheva", person.getLastName());
        assertEquals("1985-12-18", person.getDateOfBirth().toString());
        assertEquals("30", person.getAge().toString());
        assertTrue(person.getColor().contains(Color.blue));
        assertTrue(person.getColor().contains(Color.orange));
        assertEquals(1, new FoodDAO().findByName("test").size());
    }

    static Map<String, Object> buildParams(String firstName,  String lastName) {
        Map<String, Object> params = new HashMap<>();
        if (firstName != null) {
            params.put("firstName", firstName);
        }
        if (lastName != null) {
            params.put("lastName", lastName);
        }
        return params;
    }
}
