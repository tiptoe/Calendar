/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.DBUtils;
import common.ServiceFailureException;
import javax.sql.DataSource;

/**
 *
 * @author Jan Smerda
 */
public class PersonManagerImpl implements PersonManager {

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
    public void createPerson(Person person) {
        checkDataSource();
        validate(person);

        if (person.getId() != null) {
            throw new IllegalArgumentException("Person's id is already set.");
        }

        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(
                    "INSERT INTO PERSON (name,email,note) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, person.getName());
            statement.setString(2, person.getEmail());
            statement.setString(3, person.getNote());
            int newRows = statement.executeUpdate();
            DBUtils.checkUpdatesCount(newRows, person, true);
            Integer id = DBUtils.getId(statement.getGeneratedKeys());
            person.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String message = "Error when inserting person " + person;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, statement);
        }


    }

    @Override
    public void updatePerson(Person person) {
        checkDataSource();
        validate(person);
        if (person.getId() == null) {
            throw new IllegalArgumentException("Person's id is not set.");
        }
        Person original = getPersonById(person.getId());
        if (!(person.equals(original))) {
            throw new IllegalArgumentException("Person being updated "
                    + "is not equal to the person stored in database");
        }

        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(
                    "UPDATE PERSON SET name=?, email=?, note=? WHERE ID=?");
            statement.setString(1, person.getName());
            statement.setString(2, person.getEmail());
            statement.setString(3, person.getNote());
            statement.setInt(4, person.getId());
            int modifiedRows = statement.executeUpdate();
            DBUtils.checkUpdatesCount(modifiedRows, person, false);
            conn.commit();
        } catch (SQLException ex) {
            String message = "Error when updating person " + person;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, statement);
        }
    }

    @Override
    public void deletePerson(Person person) {
        checkDataSource();
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getId() == null) {
            throw new IllegalArgumentException("Person's id is not set.");
        }
        Person original = getPersonById(person.getId());
        if (!(person.equals(original))) {
            throw new IllegalArgumentException("Internal Error: Person being deleted "
                    + "is not equal to the person stored in database");
        }

        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(
                    "DELETE FROM PERSON WHERE id=?");
            statement.setInt(1, person.getId());
            int deletedRows = statement.executeUpdate();
            DBUtils.checkUpdatesCount(deletedRows, person, false);
            conn.commit();
        } catch (SQLException ex) {
            String message = "Error when deleting person " + person;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, statement);
        }
    }

    @Override
    public Person getPersonById(Integer id) {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(
                    "SELECT id,name,email,note FROM person WHERE id = ?");
            statement.setInt(1, id);
            return executeQueryForSinglePerson(statement);
        } catch (SQLException ex) {
            String message = "Error when retrieving person with id " + id;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeQuietly(conn, statement);
        }
    }

    @Override
    public List<Person> findAllPersons() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(
                    "SELECT id,name,email,note FROM person");
            return executeQueryForMultiplePersons(statement);
        } catch (SQLException ex) {
            String message = "Error when retrieving all persons";
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeQuietly(conn, statement);
        }
    }

    private void validate(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getName() == null) {
            throw new IllegalArgumentException("Person's name is not entered.");
        }
        if (person.getEmail() == null) {
            throw new IllegalArgumentException("Person's email is not entered.");
        }
    }

    static Person executeQueryForSinglePerson(PreparedStatement statement) throws SQLException, ServiceFailureException {
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            Person result = resultToPerson(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more persons with the same id found!");
            }
            rs.close();
            return result;
        } else {
            rs.close();
            return null;
        }
    }

        static List<Person> executeQueryForMultiplePersons(PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();
        List<Person> result = new ArrayList<Person>();
        while (rs.next()) {
            result.add(resultToPerson(rs));
        }
        rs.close();
        return result;
    }
        
    private static Person resultToPerson(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getInt("id"));
        person.setName(rs.getString("name"));
        person.setEmail(rs.getString("email"));
        person.setNote(rs.getString("note"));
        return person;
    }
}
