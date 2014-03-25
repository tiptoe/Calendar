package calendar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jiri Stary
 */
public class EventManagerImpl implements EventManager {
    public static final Logger logger = Logger.getLogger(EventManagerImpl.class.getName());
    
    private Connection connection;
    
    public EventManagerImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createEvent(Event event) throws ServiceFailureException{
            
        if (event == null) {
            throw new IllegalArgumentException("event is null");            
        }
        if (event.getId() != null) {
            throw new IllegalArgumentException("event id is already set");            
        }
        if (event.getName() == null) {
            throw new IllegalArgumentException("name is null");            
        }
        if (event.getStartDate() == null) {
            throw new IllegalArgumentException("startDate is null");            
        }
        if (event.getEndDate() == null) {
            throw new IllegalArgumentException("endDate is null");            
        }
        if ( event.getStartDate().getTime() == event.getEndDate().getTime() ) {
            throw new IllegalArgumentException("startDate and endDate are same");            
        }
        if (event.getStartDate().getTime() > event.getEndDate().getTime()) {
            throw new IllegalArgumentException("startDate is greater than endDate");            
        }

        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "INSERT INTO EVENT (name,startDate,endDate,note) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, event.getName());
            st.setTimestamp(2, dateToTimestamp(event.getStartDate()) );
            st.setTimestamp(3, dateToTimestamp(event.getEndDate()));
            st.setString(4, event.getNote());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows "
                        + "inserted when trying to insert event " + event);
            }            
            
            ResultSet keyRS = st.getGeneratedKeys();
            event.setId(getKey(keyRS,event));
            
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting event " + event, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        } 
    }
    
    private Integer getKey(ResultSet keyRS, Event event) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert event " + event
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Integer result = keyRS.getInt(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert event " + event
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert grave " + event
                    + " - no key found");
        }
    }

    @Override
    public void updateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event is null");            
        }
        if (event.getId() == null) {
            throw new IllegalArgumentException("event id is null");            
        }
        if (event.getName() == null) {
            throw new IllegalArgumentException("name is null");            
        }
        if (event.getStartDate() == null) {
            throw new IllegalArgumentException("startDate is null");            
        }
        if (event.getEndDate() == null) {
            throw new IllegalArgumentException("endDate is null");            
        }
        if ( event.getStartDate().getTime() == event.getEndDate().getTime() ) {
            throw new IllegalArgumentException("startDate and endDate are same");            
        }
        if (event.getStartDate().getTime() > event.getEndDate().getTime()) {
            throw new IllegalArgumentException("startDate is greater than endDate");            
        }

        Event original = getEventById(event.getId());
        if (!(event.equals(original))) {
            throw new IllegalArgumentException("Event being updated "
                    + "is not equal to the event stored in database");
        }
        
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "UPDATE EVENT SET name=?, startDate=?, endDate=?, note=? WHERE ID=?");
            st.setString(1, event.getName());
            st.setTimestamp(2, dateToTimestamp(event.getStartDate()) );
            st.setTimestamp(3, dateToTimestamp(event.getEndDate()));
            st.setString(4, event.getNote());
            st.setInt(5, event.getId());
            int modifiedRows = st.executeUpdate();
            if (modifiedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows "
                        + "modified when trying to update event " + event);
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating event " + event, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void deleteEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }
        if (event.getId() == null) {
            throw new IllegalArgumentException("event id is null.");
        }
        
        Event original = getEventById(event.getId());
        if (!(event.equals(original))) {
            throw new IllegalArgumentException("Internal Error: Event being deleted "
                    + "is not equal to the event stored in database");
        }
        
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "DELETE FROM EVENT WHERE id=?");
            st.setInt(1, event.getId());
            int deletedRows = st.executeUpdate();
            if (deletedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows "
                        + "deleted when trying to delete event " + event);
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when deleting event " + event, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        } 
    }

    @Override
    public Event getEventById(Integer id) throws ServiceFailureException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "SELECT id,name,startDate,endDate,note FROM event WHERE id = ?");
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                Event event = resultSetToEvent(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + event + " and " + resultSetToEvent(rs));                    
                }            
                
                return event;
            } else {
                return null;
            }
            
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving event with id " + id, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        } 
    }

    @Override
    public List<Event> findEventsByDate(Date startDate, Date endDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private Event resultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setName(rs.getString("name"));
        event.setStartDate( timestampToDate(rs.getTimestamp("startDate")) );
        event.setEndDate( timestampToDate(rs.getTimestamp("endDate")) );
        event.setNote(rs.getString("note"));
        return event;
    }
    
    private Timestamp dateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }
    
    private Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }
}
