/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import java.util.List;

/**
 *
 * @author Jan Smerda, Jiri Stary
 */
public interface AttendanceManager {
    
    /**
     * Creates Attendance entry in the database. Id for the entry is 
     * automatically generated and stored into id attribute.
     * 
     * @param attendance The Attendance object being added to database.
     * @throws IllegalArgumentException when attendance or event is null, or attendance has already 
     * assigned id.
     */
    void createAttendance(Attendance attendance);
    
    /**
     * Updates the attendance with same ID.
     * 
     * @param attendance The Attendance object being updated in database.
     * @throws IllegalArgumentException when attendance is null.
     */
    void updateAttendance(Attendance attendance);
    
    /**
     * Deletes Attendance entry from database.
     * 
     * @param attendance The Attendance object being deleted from database.
     * @throws IllegalArgumentException when attendance is null.
     */
    void deleteAttendance(Attendance attendance);
    
    /**
     * Retrieves an entity from database with matching ID.
     * 
     * @param id Id of the database object.
     * @return Attendance object with matching ID
     * @throws IllegalArgumentException when given id is null.
     */
    Attendance getAttendanceById(Integer id);
    
    /**
     * Retrieves all Attendance entries from database.
     * 
     * @return List of all Attendancs
     */
    List<Attendance> findAllAttendances();
    
    /**
     * Returns all Attendances relating to a specified event.
     * 
     * @param event The Event neeeded to find
     * @return List of all Attendances associated with event.
     * @throws IllegalArgumentException when event is null
     */
    List<Attendance> findAttendancesForEvent(Event event);
    
    /**
     * Returns all Attendances relating to a specified person.
     * 
     * @param person The Person needed to find
     * @return List of all Attendances associated with person.
     * @throws IllegalArgumentException when person is null
     */
    List<Attendance> findAttendancesForPerson(Person person);
    
}

