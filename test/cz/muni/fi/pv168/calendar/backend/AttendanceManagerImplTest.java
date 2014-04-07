/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.calendar.backend;

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
import cz.muni.fi.pv168.common.DBUtils;
import java.util.Date;

import static cz.muni.fi.pv168.calendar.backend.PersonManagerImplTest.newPerson;
import static cz.muni.fi.pv168.calendar.backend.PersonManagerImplTest.assertPersonDeepEquals;
import static cz.muni.fi.pv168.calendar.backend.PersonManagerImplTest.assertPersonCollectionDeepEquals;
import cz.muni.fi.pv168.common.IllegalEntityException;

/**
 *
 * @author Jan Smerda, Jiri Stary
 */
public class AttendanceManagerImplTest {

    private AttendanceManagerImpl attendanceManager;
    private PersonManagerImpl personManager;
    private EventManagerImpl eventManager;
    private DataSource ds;

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        //we will use in memory database
        ds.setUrl("jdbc:derby:memory:gravemgr-test;create=true");
        return ds;
    }
    private Event event1, event2, eventWithNullId, eventNotInDB;
    private Person person1, person2, personWithNullId, personNotInDB;

    private void setUpTestData() {

        person1 = newPerson("First Name", "first email", "first note");
        person2 = newPerson("Second Name", "second email", "second note");
        event1 = newEvent("First Event", new Date(100L), new Date(125L), "First note");
        event2 = newEvent("Second Event", new Date(125L), new Date(150L), "Second note");
        /*        attendance1 = newAttendance(event1, person1, new Date(100L));
         attendance2 = newAttendance(event2, person2, new Date(130L));
         attendance3 = newAttendance(event2, person1, new Date(135L));
         */
        personManager.createPerson(person1);
        personManager.createPerson(person2);
        eventManager.createEvent(event1);
        eventManager.createEvent(event2);

        eventWithNullId = newEvent("Event", new Date(500L), new Date(750L), "Event with null id");
        eventNotInDB = newEvent("Event", new Date(1000L), new Date(1205L), "Event not in DB");
        eventNotInDB.setId(event2.getId() + 1000);
        personWithNullId = newPerson("Some Name", "Some email", "Person with null id");
        personNotInDB = newPerson("Fantomas", "fantomas@gmail.com", "Person not in DB");
        personNotInDB.setId(person2.getId() + 1000);

    }

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, AttendanceManager.class.getResource("createTables.sql"));
        attendanceManager = new AttendanceManagerImpl();
        attendanceManager.setDataSource(ds);
        personManager = new PersonManagerImpl();
        personManager.setDataSource(ds);
        eventManager = new EventManagerImpl();
        eventManager.setDataSource(ds);
        setUpTestData();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, AttendanceManager.class.getResource("dropTables.sql"));
    }
    
    /**
     * Test of createAttendance method, of class AttendanceManagerImpl.
     */
    @Test
    public void testCreateAttendance() {
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance);

        Integer attendanceId = attendance.getId();
        assertNotNull(attendanceId);
        Attendance result = attendanceManager.getAttendanceById(attendanceId);
        assertEquals(attendance, result);
        assertNotSame(attendance, result);
        assertAttendanceDeepEquals(attendance, result);
    }
    
    /**
     * Tests of createAttendance method of class AttendanceManagerImpl with wrong 
     * attributes.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testCreateAttendanceWithWrongAttributes1() {
        attendanceManager.createAttendance(null);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void testCreateAttendanceWithWrongAttributes2() {
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendance.setId(10);
        attendanceManager.createAttendance(attendance);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateAttendanceWithWrongAttributes3() {   
        Attendance attendance = newAttendance(null, person1, new Date(110L));
        attendanceManager.createAttendance(attendance);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateAttendanceWithWrongAttributes4() {   
        Attendance attendance = newAttendance(event1, null, new Date(110L));
        attendanceManager.createAttendance(attendance);
    }
    
    /**
     * Test of getAttendanceById method of class AttendanceManagerImpl.
     */
    @Test
    public void testGetAttendanceById() {
        
        assertNull(attendanceManager.getAttendanceById(10));
        
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance);
        
        Integer attendanceId = attendance.getId();

        Attendance result = attendanceManager.getAttendanceById(attendanceId);
        assertEquals(attendance, result);
        assertAttendanceDeepEquals(attendance, result);
    }
    
    /**
     * Tests of updateAttendance method of class AttendanceManagerImpl.
     */
    @Test
    public void updateAttendance() {
       
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance);
        
        Attendance attendance2 = newAttendance(event2, person2, new Date(120L));
        attendanceManager.createAttendance(attendance2);
        
        Integer attendanceId = attendance.getId();
        
        //attendance = attendanceManager.getAttendanceById(attendanceId);

        attendance = attendanceManager.getAttendanceById(attendanceId);
        attendance.setPlannedArrivalTime(null);
        attendanceManager.updateAttendance(attendance);
        assertNull(attendance.getPlannedArrivalTime());
        
        // Check if updates didn't affected other records
        assertAttendanceDeepEquals(attendance2, attendanceManager.getAttendanceById(attendance2.getId()));
    }
    
    /**
     * Tests of updateAttendance method of class AttendanceManagerImpl
     * with wrong attributes.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateAttendanceWithWrongAttributes1() {
        attendanceManager.updateAttendance(null);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void testUpdateAttendanceWithWrongAttributes2() {
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance);   

        attendance.setId(null);
        attendanceManager.updateAttendance(attendance);        
    }
    
    @Test (expected = IllegalEntityException.class)
    public void testUpdateAttendanceWithWrongAttributes3() {
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance);  
        
        attendance.setId(attendance.getId() - 1);
        attendanceManager.updateAttendance(attendance);

    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateAttendanceWithWrongAttributes4() {    
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance); 
        
        attendance.setEvent(null);
        attendanceManager.updateAttendance(attendance);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateAttendanceWithWrongAttributes5() {    
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance); 
        
        attendance.setPerson(null);
        attendanceManager.updateAttendance(attendance);
    }
    
    /**
     * Tests of deleteAttendance method of class AttendanceManagerImpl.
     */
    @Test
    public void testDeleteAttendance() {
        Attendance a1 = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(a1);
        
        Attendance a2 = newAttendance(event2, person2, new Date(120L));
        attendanceManager.createAttendance(a2);
        
        assertNotNull(attendanceManager.getAttendanceById(a1.getId()));
        assertNotNull(attendanceManager.getAttendanceById(a2.getId()));

        attendanceManager.deleteAttendance(a1);
        
        assertNull(attendanceManager.getAttendanceById(a1.getId()));
        assertNotNull(attendanceManager.getAttendanceById(a2.getId()));          
    }
    
    /**
     * Tests of deleteAttendance method of class AttendanceManagerImpl
     * with wrong attributes.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testDeleteAttendanceWithWrongAttributes() {    
        attendanceManager.deleteAttendance(null);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void testDeleteAttendanceWithWrongAttributes2() {     
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance);
        
        attendance.setId(null);
        attendanceManager.deleteAttendance(attendance);
    }
        
    @Test (expected = IllegalEntityException.class)
    public void testDeleteAttendanceWithWrongAttributes3() { 
        Attendance attendance = newAttendance(event1, person1, new Date(110L));
        attendanceManager.createAttendance(attendance);
        
        attendance.setId(2);
        attendanceManager.deleteAttendance(attendance);
    }

    @Test
    public void findAllAttendances() {
        Attendance attendance1 = newAttendance(event1, person1, new Date(100L));
        Attendance attendance2 = newAttendance(event2, person2, new Date(130L));
        Attendance attendance3 = newAttendance(event2, person1, new Date(135L));

        attendanceManager.createAttendance(attendance1);
        attendanceManager.createAttendance(attendance2);
        attendanceManager.createAttendance(attendance3);

        List<Attendance> expected = Arrays.asList(attendance1, attendance2, attendance3);
        List<Attendance> actual = attendanceManager.findAllAttendances();

        Collections.sort(actual,idComparator);
        Collections.sort(expected,idComparator);
        
        assertEquals(expected, actual);
        assertAttendanceCollectionDeepEquals(expected, actual);
    }
    
    @Test
    public void findAttendancesForEvent() {
        Attendance attendance1 = newAttendance(event1, person1, new Date(100L));
        Attendance attendance2 = newAttendance(event2, person2, new Date(130L));
        Attendance attendance3 = newAttendance(event2, person1, new Date(135L));

        attendanceManager.createAttendance(attendance1);
        attendanceManager.createAttendance(attendance2);
        attendanceManager.createAttendance(attendance3);
        
        List<Attendance> expected = Arrays.asList(attendance2, attendance3);
        List<Attendance> actual = attendanceManager.findAttendancesForEvent(event2);

        Collections.sort(actual,idComparator);
        Collections.sort(expected,idComparator);
        
        assertEquals(expected, actual);
        assertAttendanceCollectionDeepEquals(expected, actual);
    }
    
    public void findAttendancesForPerson() {
        Attendance attendance1 = newAttendance(event1, person1, new Date(100L));
        Attendance attendance2 = newAttendance(event2, person2, new Date(130L));
        Attendance attendance3 = newAttendance(event2, person1, new Date(135L));

        attendanceManager.createAttendance(attendance1);
        attendanceManager.createAttendance(attendance2);
        attendanceManager.createAttendance(attendance3);
        
        List<Attendance> expected = Arrays.asList(attendance1, attendance3);
        List<Attendance> actual = attendanceManager.findAttendancesForPerson(person1);

        Collections.sort(actual,idComparator);
        Collections.sort(expected,idComparator);
        
        assertEquals(expected, actual);
        assertAttendanceCollectionDeepEquals(expected, actual);
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

    private void assertAttendanceDeepEquals(Attendance expected, Attendance actual) {
        assertEquals(expected.getId(), actual.getId());
        EventManagerImplTest.assertEventDeepEquals(expected.getEvent(), actual.getEvent());
        assertPersonDeepEquals(expected.getPerson(), actual.getPerson());
        assertEquals(expected.getPlannedArrivalTime(), actual.getPlannedArrivalTime());
    }

    private void assertAttendanceCollectionDeepEquals(List<Attendance> expectedList, List<Attendance> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Attendance expectedAttendance = expectedList.get(i);
            Attendance actualAttendance = actualList.get(i);
            assertAttendanceDeepEquals(expectedAttendance, actualAttendance);
        }
    }
    private static Comparator<Attendance> idComparator = new Comparator<Attendance>() {
        @Override
        public int compare(Attendance o1, Attendance o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}