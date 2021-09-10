package hello.Repository;

import hello.Domain.Alarm;
import hello.Domain.UserAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AlarmUser_Repository extends JpaRepository<UserAlarm,Integer> {
    List<UserAlarm> findAll();
    void deleteById(Integer id);



}
