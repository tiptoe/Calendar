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

    public void setId(Integer id) {
        this.id = id;
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
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 23;
        hash = hash * 73+ (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    
    
    
}
