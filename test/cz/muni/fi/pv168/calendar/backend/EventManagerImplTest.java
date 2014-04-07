package cz.muni.fi.pv168.calendar.backend;

import cz.muni.fi.pv168.calendar.backend.EventManagerImpl;
import cz.muni.fi.pv168.calendar.backend.Event;
import cz.muni.fi.pv168.calendar.backend.PersonManager;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.dbcp.BasicDataSource;
import cz.muni.fi.pv168.common.ValidationException;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.DBUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Test class of GraveManagerImpl.
 *
 * @author Jiri Stary
 */
public class EventManagerImplTest {
    
    private EventManagerImpl manager;
    private DataSource ds;
    
    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        //we will use in memory database
        ds.setUrl("jdbc:derby:memory:eventmgr-test;create=true");
        return ds;
    }
    
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,PersonManager.class.getResource("createTables.sql"));
        manager = new EventManagerImpl();
        manager.setDataSource(ds);
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds,PersonManager.class.getResource("dropTables.sql"));
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
        assertEventDeepEquals(event, result);
    }
    
    /**
     * Tests of createEvent method of class EventManagerImpl with wrong 
     * attributes.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEventWithWrongAttributes1() {
        manager.createEvent(null);
    }
    
    @Test (expected = IllegalEntityException.class)
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
    public void testUpdateEvent() {
       
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
        assertEventDeepEquals(event2, manager.getEventById(event2.getId()));
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
    public void testUpdateEventWithWrongAttributes1() {
        manager.updateEvent(null);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void testUpdateEventWithWrongAttributes2() {
        Integer eventId = setUpTestEvent();    
        
        Event event = manager.getEventById(eventId);
        event.setId(null);
        manager.updateEvent(event);        
    }
    
    @Test (expected = IllegalEntityException.class)
    public void testUpdateEventWithWrongAttributes3() {
        Integer eventId = setUpTestEvent();     
 
        Event event = manager.getEventById(eventId);
        event.setId(eventId - 1);
        manager.updateEvent(event);

    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateEventWithWrongAttributes4() {    
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setName(null);
        manager.updateEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateEventWithWrongAttributes5() {    
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setStartDate(null);
        manager.updateEvent(event);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateEventWithWrongAttributes6() {    
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setEndDate(null);
        manager.updateEvent(event);
    }
    
    // startDate is equal to endDate
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateEventWithWrongAttributes7() {    
        Integer eventId = setUpTestEvent();

        Event event = manager.getEventById(eventId);
        Date startDate = new Date(12L);
        event.setStartDate(startDate);
        manager.updateEvent(event);
    }
    
    // endDate is equal to startDate
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateEventWithWrongAttributes8() {    
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        Date endDate = new Date(10L);
        event.setEndDate(endDate);
        manager.updateEvent(event);
    }
    
    // startDate is greater than endDate
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateEventWithWrongAttributes9() {
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        Date startDate = new Date(14L);
        event.setStartDate(startDate);
        manager.updateEvent(event);        
    }
    
    // endDate is less than startDate
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateEventWithWrongAttributes10() { 
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
    public void testDeleteEvent() {
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
    public void testDeleteEventWithWrongAttributes() {    
        manager.deleteEvent(null);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void testDeleteEventWithWrongAttributes2() {     
        Integer eventId = setUpTestEvent();
        
        Event event = manager.getEventById(eventId);
        event.setId(null);
        manager.deleteEvent(event);
    }
        
    @Test (expected = IllegalEntityException.class)
    public void testDeleteEventWithWrongAttributes3() { 
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
        assertEventDeepEquals(event, result);
    }
    
    /**
     * Test of getEventById method of class EventManagerImpl.
     */
    @Test
    public void testFindEventsByDate() {
        
        Date startDate = new Date(10L);
        Date endDate = new Date(13L);
        
        assertTrue(manager.findEventsByDate(startDate, endDate).isEmpty());
        
        
        Event e1 = newTestEvent("My event", new Date(0L), new Date(14L),"Super awesome event!");
        Event e2 = newTestEvent("My event", new Date(1L), new Date(8L),"Super awesome event!");
        Event e3 = newTestEvent("My event", new Date(9L), new Date(10L),"Super awesome event!");
        Event e4 = newTestEvent("My event", new Date(10L), new Date(11L),"Super awesome event!");
        Event e5 = newTestEvent("My event", new Date(13L), new Date(16L),"Super awesome event!");
        Event e6 = newTestEvent("My event", new Date(14L), new Date(16L),"Super awesome event!");

        manager.createEvent(e1);
        manager.createEvent(e2);
        manager.createEvent(e3);
        manager.createEvent(e4);
        manager.createEvent(e5);
        manager.createEvent(e6);

        List<Event> expected = Arrays.asList(e1, e3, e4, e5);
        List<Event> actual = manager.findEventsByDate(startDate, endDate);
        
        assertEventCollectionDeepEquals(expected, actual);
        
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

    public static void assertEventDeepEquals(Event expected, Event actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getNote(), actual.getNote());
    }
    
     private static Comparator<Event> eventKeyComparator = new Comparator<Event>() {

        @Override
        public int compare(Event o1, Event o2) {
            Integer k1 = o1.getId();
            Integer k2 = o2.getId();
            if (k1 == null && k2 == null) {
                return 0;
            } else if (k1 == null && k2 != null) {
                return -1;
            } else if (k1 != null && k2 == null) {
                return 1;
            } else {
                return k1.compareTo(k2);
            }
        }
    };
    
    static void assertEventCollectionDeepEquals(List<Event> expected, List<Event> actual) {
        
        assertEquals(expected.size(), actual.size());
        List<Event> expectedSortedList = new ArrayList<Event>(expected);
        List<Event> actualSortedList = new ArrayList<Event>(actual);
        Collections.sort(expectedSortedList,eventKeyComparator);
        Collections.sort(actualSortedList,eventKeyComparator);
        for (int i = 0; i < expectedSortedList.size(); i++) {
            assertEventDeepEquals(expectedSortedList.get(i), actualSortedList.get(i));
        }   
    }
}
