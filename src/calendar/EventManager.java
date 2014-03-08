/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Honza
 */
public interface EventManager {
    
    void createEvent(Event event);
    
    void updateEvent(Event event);
    
    void deleteEvent(Event event);
    
    Event getEventById(Integer id);
    
    List<Event> findEventsByDate(Date startDate, Date endDate);
}
