package calendar;

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
    
    @Before
    public void setUp() {
        manager = new EventManagerImpl();
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
     * Test of createEvent method of class EventManagerImpl with wrong or tricky
     * arguments.
     */
    @Test
    public void testCreateEventWithWrongAttributes() {
        
        Date startDate;
        Date endDate;
        Event event;
        Event result;

        try {
            manager.createEvent(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        startDate = new Date(1262277038988L);
        endDate = new Date(1262299038988L);
        event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        event.setId(10);
        try {
            manager.createEvent(event);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        startDate = new Date(1262277038988L);
        endDate = new Date(1262299038988L);
        event = newTestEvent(null, startDate, endDate, "Super awesome event!");
        try {
            manager.createEvent(event);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        startDate = new Date(10L);
        endDate = new Date(8L);
        event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        try {
            manager.createEvent(event);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        startDate = new Date(1262277038988L);
        endDate = new Date(1262277038988L);
        event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        try {
            manager.createEvent(event);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // these variants should be ok
        startDate = new Date(-1262277038988L);
        endDate = new Date(1262277038988L);
        event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
        result = manager.getEventById(event.getId()); 
        assertNotNull(result);
        
        startDate = new Date(-1262277038988L);
        endDate = new Date(-1262277038966L);
        event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
        result = manager.getEventById(event.getId()); 
        assertNotNull(result);
        
        startDate = new Date(0L);
        endDate = new Date(1262277038966L);
        event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
        result = manager.getEventById(event.getId()); 
        assertNotNull(result);
        
        startDate = new Date(-1262277038988L);
        endDate = new Date(0L);
        event = newTestEvent("My event", startDate, endDate, "Super awesome event!");
        manager.createEvent(event);
        result = manager.getEventById(event.getId()); 
        assertNotNull(result);
        
        startDate = new Date(10L);
        endDate = new Date(12L);
        event = newTestEvent("My event", startDate, endDate, null);
        manager.createEvent(event);
        result = manager.getEventById(event.getId()); 
        assertNotNull(result);
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
