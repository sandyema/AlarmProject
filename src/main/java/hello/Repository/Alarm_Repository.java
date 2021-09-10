package hello.Repository;

import hello.Domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface Alarm_Repository extends JpaRepository<Alarm,Integer>{
    List<Alarm> findAll();

    void deleteById(Integer id_alarm);


    @Modifying
    @Transactional
    @Query("update Alarm set alarm_name = ?1 , wanted_percent = ?2 ,over_price = ?3 , less_price = ?4 where id = ?5 ")
    void update(String alarm_name,Double wanted_percent,Integer over_price,Integer less_price,Integer id);
}
