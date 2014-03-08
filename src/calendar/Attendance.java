/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import java.util.Date;

/**
 *
 * @author Honza
 */
public class Attendance {
    
    private Integer id;
    private Event event;
    private Person person;
    private Date plannedArrivalTime;

    public Integer getId() {
        return id;
    }
    
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getPlannedArrivalTime() {
        return plannedArrivalTime;
    }

    public void setPlannedArrivalTime(Date plannedArrivalTime) {
        this.plannedArrivalTime = plannedArrivalTime;
    }
    
    
    
}
