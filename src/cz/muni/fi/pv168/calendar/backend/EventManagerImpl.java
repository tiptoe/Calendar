package cz.muni.fi.pv168.calendar.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.muni.fi.pv168.common.ServiceFailureException;
import cz.muni.fi.pv168.common.ValidationException;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.ServiceFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author Jiri Stary
 */
public class EventManagerImpl implements EventManager {
    
    final static Logger logger = LoggerFactory.getLogger(EventManagerImpl.class);

    
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void createEvent(Event event) throws ServiceFailureException{
        logger.info("Creating new event {}", event);
        
        checkDataSource();
        validate(event);
        
        if (event.getId() != null) {
            throw new IllegalEntityException("event id is already set");
        }
        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            connection.setAutoCommit(false);
            st = connection.prepareStatement(
                    "INSERT INTO EVENT (name,startDate,endDate,note) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, event.getName());
            st.setTimestamp(2, dateToTimestamp(event.getStartDate()) );
            st.setTimestamp(3, dateToTimestamp(event.getEndDate()));
            st.setString(4, event.getNote());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, event, true);  
            
            Integer id = DBUtils.getId(st.getGeneratedKeys());
            event.setId(id);
            connection.commit(); 
            
        } catch (SQLException ex) {
            String msg = "Error when inserting event into db.";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, st);
        } 
    }

    @Override
    public void updateEvent(Event event) {
        logger.info("Updating event {}", event);
        
        checkDataSource();
        validate(event);
        
        if (event.getId() == null) {
            throw new IllegalEntityException("event id is null");
        }     
        
        Connection connection = null;
        PreparedStatement st = null;

        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            connection.setAutoCommit(false);
            st = connection.prepareStatement(
                    "UPDATE EVENT SET name=?, startDate=?, endDate=?, note=? WHERE ID=?");
            st.setString(1, event.getName());
            st.setTimestamp(2, dateToTimestamp(event.getStartDate()) );
            st.setTimestamp(3, dateToTimestamp(event.getEndDate()));
            st.setString(4, event.getNote());
            st.setInt(5, event.getId());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, event, false);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating event in the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, st);
        }
    }

    @Override
    public void deleteEvent(Event event) {
        logger.info("Removing event {}", event);
        
        checkDataSource();
        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }
        if (event.getId() == null) {
            throw new IllegalEntityException("event id is null.");
        }
        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            connection.setAutoCommit(false);
            st = connection.prepareStatement(
                    "DELETE FROM EVENT WHERE id=?");
            st.setInt(1, event.getId());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, event, false);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting event from the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, st);
        } 
    }

    @Override
    public Event getEventById(Integer id) throws ServiceFailureException {
        logger.info("Finding event with id {}", id);
        
        checkDataSource();
        
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,name,startDate,endDate,note FROM event WHERE id = ?");
            st.setInt(1, id);
            return executeQueryForSingleEvent(st);
        } catch (SQLException ex) {
            String msg = "Error when getting event with id = " + id + " from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(connection, st);
        } 
    }

    @Override
    public List<Event> findEventsByDate(Date startDate, Date endDate) {
        logger.info("Finding events by date - Start date {}, End date {}", startDate, endDate);
        
        checkDataSource();
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,name,startDate,endDate,note FROM event WHERE startDate <= ? AND endDate >= ?");
            st.setTimestamp(1, dateToTimestamp(endDate) );
            st.setTimestamp(2, dateToTimestamp(startDate) );
            return executeQueryForMultipleEvents(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all events from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(connection, st);
        }
    }
    
    private static Event rowToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setName(rs.getString("name"));
        event.setStartDate( timestampToDate(rs.getTimestamp("startDate")) );
        event.setEndDate( timestampToDate(rs.getTimestamp("endDate")) );
        event.setNote(rs.getString("note"));
        return event;
    }
    
    private static Timestamp dateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }
    
    private static Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }
    
    private static Event executeQueryForSingleEvent(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Event result = rowToEvent(rs);                
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more events with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }
    
    private static List<Event> executeQueryForMultipleEvents(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Event> result = new ArrayList<Event>();
        while (rs.next()) {
            result.add(rowToEvent(rs));
        }
        return result;
    }

    private static void validate(Event event) {       
        if (event == null) {
            throw new IllegalArgumentException("event is null");            
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
    }
}
