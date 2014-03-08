/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import java.util.Date;
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
public class AttendancemanagerTest {
    
    private AttendanceManagerImpl manager;
    
    @Before
    public void setUp() {
        manager = new AttendanceManagerImpl();
    }
    
   
    
    
     public Person newPerson(String name, String email, String note) {
        Person person = new Person();
        person.setName(name);
        person.setEmail(email);
        person.setNote(note);
        return person;
    }
    
    public Event newEvent(String name, Date startDate, Date endDate, String note) {
        Event event = new Event();
        event.setName(name);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setNote(note);
        return event;
    }
    
    public Attendance newAttendance(Event event, Person person, Date plannedArrivalTime) {
        Attendance attendance = new Attendance();
        attendance.setEvent(event);
        attendance.setPerson(person);
        attendance.setPlannedArrivalTime(plannedArrivalTime);
        return attendance;
    }
}