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

/**
 *
 * @author Jan Smerda
 */
public class PersonManagerImpl implements PersonManager {

    public static final Logger logger = Logger.getLogger(PersonManagerImpl.class.getName());
    private Connection conn;

    public PersonManagerImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void createPerson(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getId() != null) {
            throw new IllegalArgumentException("Person's id is already set.");
        }
        if (person.getName() == null) {
            throw new IllegalArgumentException("Person's name is not entered.");
        }
        if (person.getEmail() == null) {
            throw new IllegalArgumentException("Person's email is not entered.");
        }

        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(
                    "INSERT INTO PERSON (name,email,note) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, person.getName());
            statement.setString(2, person.getEmail());
            statement.setString(3, person.getNote());
            int addedRows = statement.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows "
                        + "inserted when trying to insert person " + person);
            }

            ResultSet keyRS = statement.getGeneratedKeys();
            person.setId(getKey(keyRS, person));

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting person " + person, ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private Integer getKey(ResultSet keyRS, Person person) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert person " + person
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Integer result = keyRS.getInt(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert person " + person
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert person " + person
                    + " - no key found");
        }
    }

    @Override
    public void updatePerson(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getId() == null) {
            throw new IllegalArgumentException("Person's id is not set.");
        }
        if (person.getName() == null) {
            throw new IllegalArgumentException("Person's name is not entered.");
        }
        if (person.getEmail() == null) {
            throw new IllegalArgumentException("Person's email is not entered.");
        }

        Person original = getPersonById(person.getId());
        if (!(person.equals(original))) {
            throw new IllegalArgumentException("Person being updated "
                    + "is not equal to the person stored in database");
        }
        
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(
                    "UPDATE PERSON SET name=?, email=?, note=? WHERE ID=?");
            statement.setString(1, person.getName());
            statement.setString(2, person.getEmail());
            statement.setString(3, person.getNote());
            statement.setInt(4, person.getId());
            int modifiedRows = statement.executeUpdate();
            if (modifiedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows "
                        + "modified when trying to update person " + person);
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating person " + person, ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void deletePerson(Person person) {
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
        
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(
                    "DELETE FROM PERSON WHERE id=?");
            statement.setInt(1, person.getId());
            int deletedRows = statement.executeUpdate();
            if (deletedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows "
                        + "deleted when trying to delete person " + person);
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when deleting person " + person, ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public Person getPersonById(Integer id) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(
                    "SELECT id,name,email,note FROM person WHERE id = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                Person person = resultSetToPerson(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + person + " and " + resultSetToPerson(rs));                    
                }            
                
                return person;
            } else {
                return null;
            }
            
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving person with id " + id, ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private Person resultSetToPerson(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getInt("id"));
        person.setName(rs.getString("name"));
        person.setEmail(rs.getString("email"));
        person.setNote(rs.getString("note"));
        return person;
    }

    @Override
    public List<Person> findAllPersons() {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(
                    "SELECT id,name,email,note FROM person");
            ResultSet rs = statement.executeQuery();
            
            List<Person> result = new ArrayList<Person>();
            while (rs.next()) {
                result.add(resultSetToPerson(rs));
            }
            return result;
            
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all persons", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
