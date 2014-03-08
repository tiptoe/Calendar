/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

/**
 *
 * @author Honza
 */
public class Person {
    
    private Integer id;
    private String name;
    private String email;
    private String note;

    public Integer getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
        
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNote() {
        return note;
    }
 
    public void setNote(String note) {
        this.note = note;
    }
    
    
    
}
