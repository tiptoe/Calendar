/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import java.util.List;

/**
 *
 * @author Honza
 */
public class AttendanceManagerImpl implements AttendanceManager {

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
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public List<Attendance> findAttendancesForEvent(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public List<Attendance> findAttendancesForPerson(Person person) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
