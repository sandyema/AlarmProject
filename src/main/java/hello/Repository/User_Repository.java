package hello.Repository;

import hello.Domain.Alarm_User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_Repository extends JpaRepository<Alarm_User,Integer> {

    Alarm_User findByEmailAndPass(String email,String pass);
 //   Alarm_User findByFirst_nameAndLast_nameAndPass(String First_name,String Last_name,String Pass);
}
