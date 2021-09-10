package hello.Domain;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class Alarm_User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Access(AccessType.PROPERTY)
    @Column(name = "first_name")
    String first_name;

    @Access(AccessType.PROPERTY)
    @Column(name = "last_name")
    String last_name;

    @Access(AccessType.PROPERTY)
    @Column(name = "email")
    String email;

    @Access(AccessType.PROPERTY)
    @Column(name = "pass")
    String pass;

    public Alarm_User(String first_name, String last_name, String email, String pass) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.pass = pass;
    }

    public Alarm_User() {
    }


    public Alarm_User(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
