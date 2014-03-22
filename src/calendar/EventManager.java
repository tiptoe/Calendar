package calendar;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Jiri Stary
 */
public interface EventManager {
    
    /**
     * Stores new event into database. Id for the new event is automatically
     * generated and stored into id attribute.
     * 
     * @param event to be created.
     * @throws IllegalArgumentException when event or attribute name is null, or event has already 
     * assigned id.
     */
    void createEvent(Event event);
    
    /**
     * Updates the event with same ID.
     * 
     * @param event The Event object being updated in database.
     * @throws IllegalArgumentException when event is null.
     */
    void updateEvent(Event event);
    
    /**
     * Deletes Event entry from database.
     * 
     * @param event The Event object being deleted from database.
     * @throws IllegalArgumentException when event is null.
     */
    void deleteEvent(Event event);
    
    /**
     * Returns event with given id.
     * 
     * @param id primary key of requested event.
     * @return event with given id or null if such event does not exist.
     * @throws IllegalArgumentException when given id is null.
     */
    Event getEventById(Integer id);
    
    /**
     * Finds and returns all events occuring in specified timeframe, including 
     * startDate and endDate
     * 
     * @param startDate The first day of the searched range
     * @param endDate The last day of the searched range
     * @return List of all events occuring in specified timeframe
     */
    List<Event> findEventsByDate(Date startDate, Date endDate);
}
