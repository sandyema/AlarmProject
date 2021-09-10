package hello.Domain;

import javax.persistence.*;

@Entity
@Table(name = "useralarm")
public class UserAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Access(AccessType.PROPERTY)
    @Column(name = "id_alarm")
    Integer id_alarm;

    @Access(AccessType.PROPERTY)
    @Column(name = "id_user")
    Integer id_user;

    public UserAlarm(Integer id_alarm, Integer id_user) {
        this.id_alarm = id_alarm;
        this.id_user = id_user;
    }

    public UserAlarm() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_alarm() {
        return id_alarm;
    }

    public void setId_alarm(Integer id_alarm) {
        this.id_alarm = id_alarm;
    }

    public Integer getId_user() {
        return id_user;
    }

    public void setId_user(Integer id_user) {
        this.id_user = id_user;
    }
}
