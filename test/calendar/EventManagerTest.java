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
public class EventManagerTest {
    
    private EventManagerImpl manager;
    
    @Before
    public void setUp() {
        manager = new EventManagerImpl();
    }
    
    
    public Event newEvent(String name, Date startDate, Date endDate, String note) {
        Event event = new Event();
        event.setName(name);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setNote(note);
        return event;
    }
}