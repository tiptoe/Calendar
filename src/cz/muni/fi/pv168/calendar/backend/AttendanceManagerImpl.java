/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.calendar.backend;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 *
 * @author Jan Smerda, Jiri Stary
 */
public class AttendanceManagerImpl implements AttendanceManager {

    final static Logger logger = LoggerFactory.getLogger(AttendanceManagerImpl.class);
    private static DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void createAttendance(Attendance attendance) throws ServiceFailureException {
        logger.info("Creating new attendance {}", attendance);
        
        checkDataSource();
        validate(attendance);
        
        if (attendance.getId() != null) {
            throw new IllegalEntityException("attendance id is already set");
        }
        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            connection.setAutoCommit(false);
            st = connection.prepareStatement(
                    "INSERT INTO ATTENDANCE (eventId,personId,plannedArrivalTime) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, attendance.getEvent().getId() );
            st.setInt(2, attendance.getPerson().getId() );
            st.setTimestamp(3, dateToTimestamp(attendance.getPlannedArrivalTime()) );
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, attendance, true);  
            
            Integer id = DBUtils.getId(st.getGeneratedKeys());
            attendance.setId(id);
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
    public void updateAttendance(Attendance attendance) {
        logger.info("Updating attendance {}", attendance);
        
        checkDataSource();
        validate(attendance);
        if (attendance.getId() == null) {
            throw new IllegalEntityException("attendance id is null");
        }        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE ATTENDANCE SET eventId = ?, personId = ?, plannedArrivalTime = ? WHERE id = ?");
            st.setInt(1, attendance.getEvent().getId() );
            st.setInt(2, attendance.getPerson().getId() );
            st.setTimestamp(3, dateToTimestamp(attendance.getPlannedArrivalTime()) );
            st.setInt(4, attendance.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, attendance, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating attendance in the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteAttendance(Attendance attendance) {
        logger.info("Removing attendance {}", attendance);
        
        checkDataSource();
        if (attendance == null) {
            throw new IllegalArgumentException("attendance is null");
        }        
        if (attendance.getId() == null) {
            throw new IllegalEntityException("attendance id is null");
        }        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM ATTENDANCE WHERE id = ?");
            st.setInt(1, attendance.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, attendance, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting attendance from the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Attendance getAttendanceById(Integer id) throws ServiceFailureException {
        logger.info("Finding attendance by id {}", id);
        
        checkDataSource();        
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,eventId,personId,plannedArrivalTime FROM attendance WHERE id = ?");
            st.setInt(1, id);
            return executeQueryForSingleAttendance(st);
        } catch (SQLException ex) {
            String msg = "Error when getting attendance with id = " + id + " from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(connection, st);
        } 
    }

    @Override
    public List<Attendance> findAllAttendances() {
        logger.info("Finding all attendances");
        
        checkDataSource();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(
                    "SELECT id,eventId,personId,plannedArrivalTime FROM attendance");
            return executeQueryForMultipleAttendances(statement);
        } catch (SQLException ex) {
            String message = "Error when retrieving all Attendances";
            logger.error(message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeQuietly(conn, statement);
        }
    }

    @Override
    public List<Attendance> findAttendancesForEvent(Event event) {
        logger.info("Finding all attendances for event {}", event);
        
        checkDataSource();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(
                    "SELECT id,eventId,personId,plannedArrivalTime FROM attendance WHERE eventId = ?");
            statement.setInt(1, event.getId());
            return executeQueryForMultipleAttendances(statement);
        } catch (SQLException ex) {
            String message = "Error when retrieving Attendances associated with event " + event;
            logger.error(message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeQuietly(conn, statement);
        }
    }

    @Override
    public List<Attendance> findAttendancesForPerson(Person person) {
        logger.info("Finding all attendances for person {}", person);
        
        checkDataSource();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(
                    "SELECT id,eventId,personId,plannedArrivalTime FROM attendance WHERE personId = ?");
            statement.setInt(1, person.getId());
            return executeQueryForMultipleAttendances(statement);
        } catch (SQLException ex) {
            String message = "Error when retrieving Attendances associated with person " + person;
            logger.error(message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeQuietly(conn, statement);
        }
    }
    
    private static Attendance executeQueryForSingleAttendance(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Attendance result = resultToAttendance(rs);                
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more attendances with the same id found!");
            }
            rs.close();
            return result;
        } else {
            rs.close();
            return null;
        }
    }

    private static List<Attendance> executeQueryForMultipleAttendances(PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();
        List<Attendance> result = new ArrayList<Attendance>();
        while (rs.next()) {
            result.add(resultToAttendance(rs));
        }
        rs.close();
        return result;
    }

    private static Attendance resultToAttendance(ResultSet rs) throws SQLException {
        Attendance att = new Attendance();
        EventManagerImpl eventManager = new EventManagerImpl();
        PersonManagerImpl personManager = new PersonManagerImpl();
        
        eventManager.setDataSource(dataSource);
        personManager.setDataSource(dataSource);
        
        att.setId(rs.getInt("id"));
        att.setEvent(eventManager.getEventById(new Integer(rs.getInt("eventId"))));
        att.setPerson(personManager.getPersonById(new Integer(rs.getInt("personId"))));
        att.setPlannedArrivalTime(timestampToDate(rs.getTimestamp("plannedArrivalTime")));
        //rs.close();
        return att;
    }

    private static Timestamp dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        
        return new Timestamp(date.getTime());
    }

    private static Date timestampToDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        
        return new Date(timestamp.getTime());
    }
    
    private static void validate(Attendance attendance) {       
        if (attendance == null) {
            throw new IllegalArgumentException("attendance is null");            
        }
        if (attendance.getEvent() == null) {
            throw new IllegalArgumentException("event is null");            
        }
        if (attendance.getPerson() == null) {
            throw new IllegalArgumentException("person is null");            
        }           
    }
}
