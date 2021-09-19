package hello.Repository;

import hello.Domain.Alarm_User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface User_Repository extends JpaRepository<Alarm_User,Integer> {

    Alarm_User findByEmailAndPass(String email,String pass);
    Alarm_User findByEmail(String email);
 //   Alarm_User findByFirst_nameAndLast_nameAndPass(String First_name,String Last_name,String Pass);

    @Transactional
    @Modifying
    @Query("UPDATE Alarm_User user SET user.pass = :pass WHERE user.email = :email")
    void resetPass(@Param("email") String email, @Param("pass") String pass);

}
