package calendar;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import common.DBUtils;

/**
 *
 * @author Jan Smerda
 */
public class PersonManagerImplTest {
    
    private PersonManagerImpl manager; 
    private DataSource ds;
    
    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        //we will use in memory database
        ds.setUrl("jdbc:derby:memory:gravemgr-test;create=true");
        return ds;
    }
    
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,PersonManager.class.getResource("createTables.sql"));
        manager = new PersonManagerImpl();
        manager.setDataSource(ds);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds,PersonManager.class.getResource("dropTables.sql"));
    }
    
    @Test
    public void createPerson() {
        Person person = newPerson("Jara Cimrman", "cimr@gmail.com", "Poznamka");
        manager.createPerson(person);
        
        Integer personId = person.getId();
        assertNotNull(personId);
        Person result = manager.getPersonById(personId);
        assertEquals(person, result);
        assertNotSame(person, result);
        assertPersonDeepEquals(person, result);
                
    }
    
    @Test
    public void createPersonWithWrongAttributes1() {
        
        //Creating null person
        try {
            manager.createPerson(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }
    
    @Test
    public void createPersonWithWrongAttributes2() {
        //Person should not have empty name.
        Person person = newPerson(null, "email", "note");
        try {
            manager.createPerson(person);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }
    
    @Test
    public void createPersonWithWrongAttributes3() {
        //Person should not have empty email.
        Person person = newPerson("name", null, "note");
        try {
            manager.createPerson(person);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }
    
    @Test
    public void createPersonWithWrongAttributes4() {
        //Empty note is ok
        Person person = newPerson("name", "email", null);
        manager.createPerson(person);
        Person result = manager.getPersonById(person.getId());
        assertNotNull(result);
        assertNull(result.getNote());
        
    }
    
    @Test
    public void updatePerson() {
        Person person = newPerson("First Name", "first email", "first note");
        Person person2 = newPerson("Second Name", "second email", "second note");
        manager.createPerson(person);
        manager.createPerson(person2);
        
        Integer personId = person.getId();
        
        //change person's name
        person = manager.getPersonById(personId);
        person.setName("New Name");
        manager.updatePerson(person);
        assertEquals("New Name", person.getName());
        assertEquals("first email", person.getEmail());
        assertEquals("first note", person.getNote());
        
        //change person's email address
        person = manager.getPersonById(personId);
        person.setEmail("new email");
        manager.updatePerson(person);
        assertEquals("New Name", person.getName());
        assertEquals("new email", person.getEmail());
        assertEquals("first note", person.getNote());
                
        //change person's note
        person = manager.getPersonById(personId);
        person.setNote("new note");
        manager.updatePerson(person);
        assertEquals("New Name", person.getName());
        assertEquals("new email", person.getEmail());
        assertEquals("new note", person.getNote());
        
         //changing person's note to null should be ok
        person = manager.getPersonById(personId);
        person.setNote(null);
        manager.updatePerson(person);
        assertEquals("New Name", person.getName());
        assertEquals("new email", person.getEmail());
        assertNull(person.getNote());
        
        //Make sure that only correct record was changed
        assertPersonDeepEquals(person2, manager.getPersonById(person2.getId()));
    }

    @Test
    public void updatePersonWithWrongAttributes() {
        Person person = newPerson("name", "email", "note");
        manager.createPerson(person);
        person.setId(1);
        Integer personId = person.getId();
        
        //try changing person to null
        try {
            manager.updatePerson(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //pass test
        }
    }
    
    @Test
    public void updatePersonWithWrongAttributes2() {
        Person person = newPerson("name", "email", "note");
        manager.createPerson(person);
        person.setId(1);
        Integer personId = person.getId();
        
        //try changing id to null
        try {
            person = manager.getPersonById(personId);
            person.setId(null);
            manager.updatePerson(person);        
            fail();
        } catch (IllegalArgumentException ex) {
            //pass test
        }
    }
    
    @Test
    public void updatePersonWithWrongAttributes3() {
        Person person = newPerson("name", "email", "note");
        manager.createPerson(person);
        person.setId(1);
        Integer personId = person.getId();
        
        //try changing the id
        try {
            person = manager.getPersonById(personId);
            person.setId(personId - 1);
            manager.updatePerson(person);        
            fail();
        } catch (IllegalArgumentException ex) {
            //pass test
        }
    }
    
    @Test
    public void updatePersonWithWrongAttributes4() {
        Person person = newPerson("name", "email", "note");
        manager.createPerson(person);
        person.setId(1);
        Integer personId = person.getId();
        
        //try changing name to null
        try {
            person = manager.getPersonById(personId);
            person.setName(null);
            manager.updatePerson(person);        
            fail();
        } catch (IllegalArgumentException ex) {
            //pass test
        }
    }
    
    @Test   
    public void updatePersonWithWrongAttributes5() {
        Person person = newPerson("name", "email", "note");
        manager.createPerson(person);
        person.setId(1);
        Integer personId = person.getId();
        
        //try changing email to null
        try {
            person = manager.getPersonById(personId);
            person.setEmail(null);
            manager.updatePerson(person);        
            fail();
        } catch (IllegalArgumentException ex) {
            //pass test
        }
    }
    
    @Test
    public void deletePerson() {
        Person person1 = newPerson("First Name", "first email", "first note");
        Person person2 = newPerson("Second Name", "second email", "second note");
        manager.createPerson(person1);
        manager.createPerson(person2);

        assertNotNull(manager.getPersonById(person1.getId()));
        assertNotNull(manager.getPersonById(person2.getId()));
        
        manager.deletePerson(person1);

        assertNull(manager.getPersonById(person1.getId()));
        assertNotNull(manager.getPersonById(person2.getId()));
    }
    
    @Test
    public void deletePersonWithWrongAttributes1() {
        
        Person person = newPerson("First Name", "first email", "first note");
        
        //try deleting null person
        try {
            manager.deletePerson(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //pass test
        }
    }
    
    public void deletePersonWithWrongAttributes2() {
        
        Person person = newPerson("First Name", "first email", "first note");
        
        //try deleting person with null id
        try {
            person.setId(null);
            manager.deletePerson(person);
            fail();
        } catch (IllegalArgumentException ex) {
            //pass test
        }
    }
    public void deletePersonWithWrongAttributes3() {
        
        Person person = newPerson("First Name", "first email", "first note");
        
        //try deleting person with changed id
        try {
            person.setId(2);
            manager.deletePerson(person);
            fail();
        } catch (IllegalArgumentException ex) {
            //pass test
        }
        
    }
    
    @Test
    public void findAllPersons() {
        
        assertTrue(manager.findAllPersons().isEmpty());

        Person person1 = newPerson("First Name", "first email", "first note");
        Person person2 = newPerson("Second Name", "second email", "second note");
        manager.createPerson(person1);
        manager.createPerson(person2);

        List<Person> expected = Arrays.asList(person1,person2);
        List<Person> actual = manager.findAllPersons();

        Collections.sort(actual,idComparator);
        Collections.sort(expected,idComparator);

        assertEquals(expected, actual);
        assertPersonCollectionDeepEquals(expected, actual);
    }
    
    
    public static Person newPerson(String name, String email, String note) {
        Person person = new Person();
        person.setName(name);
        person.setEmail(email);
        person.setNote(note);
        return person;
    }
    
    public static void assertPersonDeepEquals(Person expected, Person actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getNote(), actual.getNote());
    }
    
    public static void assertPersonCollectionDeepEquals(List<Person> expectedList, List<Person> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Person expectedPerson = expectedList.get(i);
            Person actualPerson = actualList.get(i);
            assertPersonDeepEquals(expectedPerson, actualPerson);
        }
    }
     
    private static Comparator<Person> idComparator = new Comparator<Person>() {

        @Override
        public int compare(Person o1, Person o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}