package calendar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class of GraveManagerImpl.
 *
 * @author Jiri Stary
 */
public class EventManagerImplTest {
    
    private EventManagerImpl manager;
    private Connection connection;
    
    @Before
    public void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby:memory:EventManagerTest;create=true");
        connection.prepareStatement("CREATE TABLE EVENT ("
                + "id int primary key generated always as identity,"
                + "name varchar(255),"
                + "startDate TIMESTAMP,"
                + "endDate TIMESTAMP,"
                + "note varchar(255))").executeUpdate();
        manager = new EventManagerImpl(connection);
    }
    
    @After
    public void tearDown() throws SQLException {
        connection.prepareStatement("DROP TABLE EVENT").executeUpdate();        
        connection.close();
    }
    
    /**
     * Test of createEvent method, of class EventManagerImpl.
     */
    @Test
    public void testCreateEvent() {
        Date startDate = new Date(1262277038988L);
        Date endDate = new Date(1262299038988L);
        Event event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);

        Integer eventId = event.getId();
        assertNotNull(eventId);
        Event result = manager.getEventById(eventId);
        assertEquals(event, result);
        assertNotSame(event, result);
        assertDeepEquals(event, result);
    }
    
    /**
     * Tests of createEvent method of class EventManagerImpl with wrong 
     * attributes.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEventWithWrongAttributes1() {
        manager.createEvent(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEventWithWrongAttributes2() {
        Date startDate = new Date(1262277038988L);
        Date endDate = new Date(1262299038988L);
        Event event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        event.setId(10);
        manager.createEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEventWithWrongAttributes3() {   
        Date startDate = new Date(1262277038988L);
        Date endDate = new Date(1262299038988L);
        Event event = newTestEvent(null, startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEventWithWrongAttributes4() {   
        Date endDate = new Date(8L);
        Event event = newTestEvent("My event", null, endDate, "Super awesome event!");
        manager.createEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEventWithWrongAttributes5() {    
        Date startDate = new Date(10L);
        Event event = newTestEvent("My event", startDate, null, "Super awesome event!");
        manager.createEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEventWithWrongAttributes6() {    
        Date startDate = new Date(10L);
        Date endDate = new Date(8L);
        Event event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEventWithWrongAttributes7() {    
        Date startDate = new Date(1262277038988L);
        Date endDate = new Date(1262277038988L);
        Event event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
    }
    
    /**
     * Tests of createEvent method of class EventManagerImpl with tricky 
     * attributes.
     * 
     * All variants should by OK!
     */
    @Test
    public void testCreateEventWithTrickyAttributes1() {    
        Date startDate = new Date(-1262277038988L);
        Date endDate = new Date(1262277038988L);
        Event event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
        Event result = manager.getEventById(event.getId()); 
        assertNotNull(result);
    }
    
    @Test
    public void testCreateEventWithTrickyAttributes2() {    
        Date startDate = new Date(-1262277038988L);
        Date endDate = new Date(-1262277038966L);
        Event event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
        Event result = manager.getEventById(event.getId()); 
        assertNotNull(result);
    }
    
    @Test
    public void testCreateEventWithTrickyAttributes3() {    
        Date startDate = new Date(0L);
        Date endDate = new Date(1262277038966L);
        Event event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
        Event result = manager.getEventById(event.getId()); 
        assertNotNull(result);
    }
    
    @Test
    public void testCreateEventWithTrickyAttributes4() {    
        Date startDate = new Date(-1262277038988L);
        Date endDate = new Date(0L);
        Event event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
        Event result = manager.getEventById(event.getId()); 
        assertNotNull(result);
    }
    
    @Test
    public void testCreateEventWithTrickyAttributes5() {    
        Date startDate = new Date(10L);
        Date endDate = new Date(12L);
        Event event = newTestEvent("My event", startDate, endDate, null);
        manager.createEvent(event);
        Event result = manager.getEventById(event.getId()); 
        assertNotNull(result);
    }
    
    /**
     * Tests of updateEvent method of class EventManagerImpl.
     */
    @Test
    public void updateEvent() {
       
        Date startDate = new Date(10L);
        Date endDate = new Date(12L);
        Event event = newTestEvent("My event", startDate, endDate, "Super event!");
        manager.createEvent(event);
        
        startDate = new Date(44L);
        endDate = new Date(66L);
        Event event2 = newTestEvent("My other event", startDate, endDate, "Awesome event!");
        manager.createEvent(event2);
        
        Integer eventId = event.getId();
        
        event = manager.getEventById(eventId);
        event.setName("Event");
        manager.updateEvent(event);        
        assertEquals("Event", event.getName());
        assertEquals(10L, event.getStartDate().getTime());
        assertEquals(12L, event.getEndDate().getTime());
        assertEquals("Super event!", event.getNote());
        
        event = manager.getEventById(eventId);
        startDate = new Date(0);
        event.setStartDate(startDate);
        manager.updateEvent(event);        
        assertEquals("Event", event.getName());
        assertEquals(0, event.getStartDate().getTime());
        assertEquals(12L, event.getEndDate().getTime());
        assertEquals("Super event!", event.getNote());
        
        event = manager.getEventById(eventId);
        endDate = new Date(1L);
        event.setEndDate(endDate);
        manager.updateEvent(event);        
        assertEquals("Event", event.getName());
        assertEquals(0, event.getStartDate().getTime());
        assertEquals(1L, event.getEndDate().getTime());
        assertEquals("Super event!", event.getNote());
        
        event = manager.getEventById(eventId);
        event.setNote("Bad event!");
        manager.updateEvent(event);        
        assertEquals("Event", event.getName());
        assertEquals(0, event.getStartDate().getTime());
        assertEquals(1L, event.getEndDate().getTime());
        assertEquals("Bad event!", event.getNote());

        event = manager.getEventById(eventId);
        event.setNote(null);
        manager.updateEvent(event);        
        assertEquals("Event", event.getName());
        assertEquals(0, event.getStartDate().getTime());
        assertEquals(1L, event.getEndDate().getTime());
        assertNull(event.getNote());
        
        // Check if updates didn't affected other records
        assertDeepEquals(event2, manager.getEventById(event2.getId()));
    }
    
    /**
     * Tests of updateEvent method of class EventManagerImpl
     * with wrong attributes.
     * 
     * TestEvent:
     * - name: "My event"
     * - startDate: 10L
     * - endDate: 12L
     * - note: "Super event!"
     */
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes1() {
        manager.updateEvent(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes2() {
        Integer eventId = setUpTestEvent();    
        
        Event event = manager.getEventById(eventId);
        event.setId(null);
        manager.updateEvent(event);        
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes3() {
        Integer eventId = setUpTestEvent();     
 
        Event event = manager.getEventById(eventId);
        event.setId(eventId - 1);
        manager.updateEvent(event);

    }
    
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes4() {    
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setName(null);
        manager.updateEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes5() {    
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setStartDate(null);
        manager.updateEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes6() {    
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setEndDate(null);
        manager.updateEvent(event);
    }
    
    // startDate is equal to endDate
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes7() {    
        Integer eventId = setUpTestEvent();

        Event event = manager.getEventById(eventId);
        Date startDate = new Date(12L);
        event.setStartDate(startDate);
        manager.updateEvent(event);
    }
    
    // endDate is equal to startDate
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes8() {    
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        Date endDate = new Date(10L);
        event.setEndDate(endDate);
        manager.updateEvent(event);
    }
    
    // startDate is greater than endDate
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes9() {
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        Date startDate = new Date(14L);
        event.setStartDate(startDate);
        manager.updateEvent(event);        
    }
    
    // endDate is less than startDate
    @Test (expected = IllegalArgumentException.class)
    public void updateEventWithWrongAttributes10() { 
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        Date endDate = new Date(8L);
        event.setEndDate(endDate);
        manager.updateEvent(event);
    }
    
    /**
     * Tests of deleteEvent method of class EventManagerImpl.
     */
    @Test
    public void deleteEvent() {
        Date startDate = new Date(10L);
        Date endDate = new Date(12L);
        Event e1 = newTestEvent("My event", startDate, endDate, "Super event!");
        manager.createEvent(e1);
        
        startDate = new Date(44L);
        endDate = new Date(66L);
        Event e2 = newTestEvent("My other event", startDate, endDate, "Awesome event!");
        manager.createEvent(e2);
        
        assertNotNull(manager.getEventById(e1.getId()));
        assertNotNull(manager.getEventById(e2.getId()));

        manager.deleteEvent(e1);
        
        assertNull(manager.getEventById(e1.getId()));
        assertNotNull(manager.getEventById(e2.getId()));          
    }
    
    /**
     * Tests of deleteEvent method of class EventManagerImpl
     * with wrong attributes.
     */
    @Test (expected = IllegalArgumentException.class)
    public void deleteEventWithWrongAttributes() {    
        manager.deleteEvent(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void deleteEventWithWrongAttributes2() {     
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setId(null);
        manager.deleteEvent(event);
    }
        
    @Test (expected = IllegalArgumentException.class)
    public void deleteEventWithWrongAttributes3() { 
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setId(2);
        manager.deleteEvent(event);
    }
    
    /**
     * Test of getEventById method of class EventManagerImpl.
     */
    @Test
    public void testGetEventById() {
        
        assertNull(manager.getEventById(10));
        
        Date startDate = new Date(1262277038988L);
        Date endDate = new Date(1262299038988L);
        Event event = newTestEvent("My event", startDate, endDate,"Super awesome event!");
        manager.createEvent(event);
        
        Integer eventId = event.getId();

        Event result = manager.getEventById(eventId);
        assertEquals(event, result);
        assertDeepEquals(event, result);
    }
    
    private static Event newTestEvent(String name, Date startDate, Date endDate, String note) {
        Event event = new Event();
        event.setName(name);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setNote(note);
        return event;
    }
    
    private int setUpTestEvent() {
        Date startDate = new Date(10L);
        Date endDate = new Date(12L);
        Event event = newTestEvent("My event", startDate, endDate, "Super event!");
        manager.createEvent(event);
        
        return event.getId();
    }
    
    private void assertDeepEquals(List<Event> expectedList, List<Event> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Event expected = expectedList.get(i);
            Event actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Event expected, Event actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getNote(), actual.getNote());
    }
}
