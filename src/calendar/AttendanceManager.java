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
public interface AttendanceManager {
    
    void createAttendance(Attendance attendance);
    
    void updateeAttendance(Attendance attendance);
    
    void deleteAttendance(Attendance attendance);
    
    Attendance getAttendanceById(Integer id);
    
    List<Attendance> findAllAttendances();
    
    List<Attendance> findAttendancesForEvent(Event event);
    
    List<Attendance> findAttendancesForPerson(Person person);
    
}

