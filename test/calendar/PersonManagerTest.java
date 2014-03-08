package calendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Honza
 */
public class PersonManagerTest {
    
    private PersonManagerImpl manager;
    
    @Before
    public void setUp() {
        manager = new PersonManagerImpl();
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
                
    }

    
    
    
    public Person newPerson(String name, String email, String note) {
        Person person = new Person();
        person.setName(name);
        person.setEmail(email);
        person.setNote(note);
        return person;
    }
}