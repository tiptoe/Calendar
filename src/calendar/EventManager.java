package calendar;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Jiri Stary
 */
public interface EventManager {
    
    /**
     * Stores new event into database. Id for the new grave is automatically
     * generated and stored into id attribute.
     * 
     * @param event to be created.
     * @throws IllegalArgumentException when event or attribute name is null, or grave has already 
     * assigned id.
     */
    void createEvent(Event event);
    
    void updateEvent(Event event);
    
    void deleteEvent(Event event);
    
    /**
     * Returns event with given id.
     * 
     * @param id primary key of requested event.
     * @return grave with given id or null if such grave does not exist.
     * @throws IllegalArgumentException when given id is null.
     */
    Event getEventById(Integer id);
    
    List<Event> findEventsByDate(Date startDate, Date endDate);
}
