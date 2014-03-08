/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import java.util.List;

/**
 *
 * @author Honza
 */
public interface PersonManager {
    
    void createPerson(Person person);
    
    void updatePerson(Person person);
     
    void deletePerson(Person person);
    
    Person getPersonById(Integer id);
    
    List<Person> findAllPersons();
}
