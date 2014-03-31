/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import common.DBUtils;
import common.ServiceFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Jan Smerda, Jiri Stary
 */
public class AttendanceManagerImpl implements AttendanceManager {

    public static final Logger logger = Logger.getLogger(PersonManagerImpl.class.getName());
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
    public void createAttendance(Attendance attendance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateeAttendance(Attendance attendance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteAttendance(Attendance attendance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Attendance getAttendanceById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Attendance> findAllAttendances() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(
                    "SELECT id,event,person,plannedArrivalTime FROM attendance");
            return executeQueryForMultipleAttendances(statement);
        } catch (SQLException ex) {
            String message = "Error when retrieving all Attendances";
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeQuietly(conn, statement);
        }
    }

    @Override
    public List<Attendance> findAttendancesForEvent(Event event) {
        checkDataSource();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(
                    "SELECT id,event,person,plannedArrivalTime FROM attendance WHERE eventid = ?");
            statement.setInt(1, event.getId());
            return executeQueryForMultipleAttendances(statement);
        } catch (SQLException ex) {
            String message = "Error when retrieving Attendances associated with event " + event;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeQuietly(conn, statement);
        }
    }

    @Override
    public List<Attendance> findAttendancesForPerson(Person person) {
        checkDataSource();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(
                    "SELECT id,event,person,plannedArrivalTime FROM attendance WHERE personid = ?");
            statement.setInt(1, person.getId());
            return executeQueryForMultipleAttendances(statement);
        } catch (SQLException ex) {
            String message = "Error when retrieving Attendances associated with person " + person;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeQuietly(conn, statement);
        }
    }

    static List<Attendance> executeQueryForMultipleAttendances(PreparedStatement statement) throws SQLException {
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
        att.setId(rs.getInt("id"));
        EventManagerImpl eventManager = new EventManagerImpl();
        att.setEvent(eventManager.getEventById(new Integer(rs.getInt("eventid"))));
        PersonManagerImpl personManager = new PersonManagerImpl();
        att.setPerson(personManager.getPersonById(new Integer(rs.getInt("personId"))));
        att.setPlannedArrivalTime(timestampToDate(rs.getTimestamp("plannedArrivalTime")));
        rs.close();
        return att;
    }

    private static Timestamp dateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    private static Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }
}
