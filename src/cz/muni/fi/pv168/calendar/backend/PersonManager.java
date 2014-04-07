/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.calendar.backend;

import java.util.List;

/**
 *
 * @author Jan Smerda
 */
public interface PersonManager {
    
    /**
     * Creates Person entry in the database. Id for the entry is 
     * automatically generated and stored into id attribute.
     * 
     * @param person The Person object being added to database.
     * @throws IllegalArgumentException when person is null, or person has already 
     * assigned id.
     */
    void createPerson(Person person);
    
    /**
     * Updates the person with same ID.
     * 
     * @param person The Person object being updated in database.
     * @throws IllegalArgumentException when person is null.
     */
    void updatePerson(Person person);
     
    /**
     * Deletes Person entry from database.
     * 
     * @param person The Person object being deleted from database.
     * @throws IllegalArgumentException when person is null.
     */
    void deletePerson(Person person);
    
     /**
     * Retrieves an entity from database with matching ID.
     * 
     * @param id Id of the database object.
     * @return Person object with matching ID
     * @throws IllegalArgumentException when given id is null.
     */
    Person getPersonById(Integer id);
    
    /**
     * Retrieves all Person entries from database.
     * 
     * @return List of all Persons
     */
    List<Person> findAllPersons();
}
