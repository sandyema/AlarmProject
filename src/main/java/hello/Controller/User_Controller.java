package hello.Controller;

import hello.Domain.Alarm;
import hello.Domain.Alarm_User;
import hello.Domain.UserAlarm;
import hello.Repository.AlarmUser_Repository;
import hello.Repository.Alarm_Repository;
import hello.Repository.User_Repository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/StockAlarms")
public class User_Controller {

    @Autowired
    private User_Repository user_repository;

    @Autowired
    private Alarm_Repository alarm_repository;

    @Autowired
    private AlarmUser_Repository alarmUser_repository;

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @Scheduled(fixedRate = 50000)
    public void scheduleFixedTask(){

        String alarmName,destinationEmail="";
        Double price=0.0;
        for (UserAlarm userAlarm : alarmUser_repository.findAll())
        {
            for (Alarm_User user : user_repository.findAll()) {
                for (Alarm alarm : alarm_repository.findAll()) {
                    if (userAlarm.getId_alarm().equals(alarm.getId()) && userAlarm.getId_user().equals(user.getId()) && alarm.getActive().equals(1)) {
                        try {
                            alarmName=alarm.getAlarm_name();
                            price=getPriceStock(alarmName);

                            if((alarm.getOver_price().equals(1) && df2.format(price).equals(df2.format(alarm.getInitial_price()*(1+alarm.getWanted_percent()/100)))) || (alarm.getLess_price().equals(1) && df2.format(price).equals(df2.format(alarm.getInitial_price()*(1-alarm.getWanted_percent()/100)))))

                            {
                                 destinationEmail=user.getEmail();

                                sendMail(destinationEmail,alarmName,price);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject login(@RequestBody JSONObject user) {
        System.out.println("Try to login for :" + user.getAsString("email") + " : " + user.getAsString("pass"));
        HashMap<String, String> response = new HashMap<String, String>();


        String user_email = user.getAsString("email");
        String user_pass = user.getAsString("pass");

        System.out.println(user_email + user_pass + "\n");
        System.out.println(user_pass + "\n");

        Alarm_User user_found = user_repository.findByEmailAndPass(user_email, user_pass);
        System.out.println(user_found.getEmail());
        System.out.println(user_found.getPass());
        System.out.println(user_found.getFirst_name());

        if (user_found != null) {
            response.put("response", "true");
            //response.put("token", TokenManager.generateToken(username));
            System.out.println("Succes to login for " + user_email + " : " + user_pass);
        } else {
            response.put("response", "false");
            System.out.println("Failed to login for " + user_email + " : " + user_pass);
        }
        return new JSONObject(response);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject addStudent(@RequestBody JSONObject user) {
        System.out.println("Try to add user  :" + user.getAsString("first_name") + " : " + user.getAsString("last_name") + " : " + user.getAsString("email") + " : " + user.getAsString("pass"));
        HashMap<String, String> response = new HashMap<String, String>();

        String user_firstname = user.getAsString("first_name");
        String user_lastname = user.getAsString("last_name");
        String user_email = user.getAsString("email");
        String user_pass = user.getAsString("pass");


        Alarm_User user_registed = user_repository.save(new Alarm_User(user_firstname, user_lastname, user_email, user_pass));


        if (user_registed != null) {
            response.put("response", "true");
            response.put("id_user", String.valueOf(user_registed.getId()));
            System.out.println("Succes to add for " + user_firstname + user_lastname + " : " + user_pass);
        } else {
            response.put("response", "false");
            System.out.println("Error to add for " + user_firstname + user_lastname + " : " + user_pass);
        }

        return new JSONObject(response);
    }

    @RequestMapping(value = "/findID", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject findID(@RequestBody JSONObject user) {
        System.out.println("Try to find id  for :" + user.getAsString("email") + " : " + user.getAsString("pass"));
        HashMap<String, Integer> response = new HashMap<String, Integer>();

        String user_email = user.getAsString("email");
        String user_pass = user.getAsString("pass");

        System.out.println(user_email + user_pass + "\n");
        System.out.println(user_pass + "\n");

        Alarm_User user_found = user_repository.findByEmailAndPass(user_email, user_pass);
        System.out.println(user_found.getEmail());
        System.out.println(user_found.getPass());
        System.out.println(user_found.getFirst_name());

        System.out.println(user_found.toString());

        if (user_found != null) {
            response.put("response", user_found.getId());

            //response.put("token", TokenManager.generateToken(username));
            System.out.println("Succes to find id for " + user_email + " : " + user_pass + " : " + user_found.getId());
        } else {
            response.put("response", -1);
            System.out.println("Failed to find id for " + user_email + " : " + user_pass);
        }
        return new JSONObject(response);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllAlarms", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getAllAlarms() {
        List<Alarm> list = alarm_repository.findAll();
        JSONObject object = new JSONObject();
        object.put("Alarms", list);
        return new ResponseEntity<JSONObject>(object, HttpStatus.OK);
    }

    private Double getPriceStock(String stockName) throws IOException{
        String price="not found";
        Double priceInDouble=0.0;
        URL url=new URL("https://www.google.com/finance/quote/"+stockName+":NYSE?ei=ga4QWNiFOobBe4LShnAF");
        URLConnection urlConnection=url.openConnection();
        InputStreamReader inStream=new InputStreamReader(urlConnection.getInputStream());
        BufferedReader bufferedReader=new BufferedReader(inStream);
        String line=bufferedReader.readLine();
        while(line!=null)
        {
            if(line.contains("[\""+stockName+"\",")){
                int target=line.indexOf(("[\""+stockName+"\","));
                int deci=line.indexOf(".",target);
                int start=deci;
                while(line.charAt(start)!='\"'){
                    start--;
                }
                price= line.substring(start+3,deci+3);
            }
            line=bufferedReader.readLine();
        }
        priceInDouble=Double.valueOf(price);

        return priceInDouble;
    }

    @ResponseBody
    @RequestMapping(value = "/getAllAlarms/{id_user}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getAlarms(@PathVariable Integer id_user) {
        List<Alarm> alarmsList = new ArrayList<>();
        JSONObject object = new JSONObject();
        Double price=0.0;
        for (UserAlarm userAlarm : alarmUser_repository.findAll())
            {
                for (Alarm_User user : user_repository.findAll()) {
                    for (Alarm alarm : alarm_repository.findAll()) {
                        if (userAlarm.getId_user().equals(id_user) && userAlarm.getId_alarm().equals(alarm.getId()) && userAlarm.getId_user().equals(user.getId()) && alarm.getActive().equals(1)) {
                            try {
                                price=getPriceStock(alarm.getAlarm_name());
                                alarm.setCurrent_price(price);
                                alarmsList.add(alarm);


                                if(alarm.getOver_price().equals(1) && df2.format(alarm.getCurrent_price()).equals(df2.format(alarm.getInitial_price()*(1+alarm.getWanted_percent()/100))))

                                {
                                    alarm.setActive(0);
                                    }

                                if(alarm.getLess_price().equals(1) && df2.format(alarm.getCurrent_price()).equals(df2.format(alarm.getInitial_price()*(1-alarm.getWanted_percent()/100))))
                                {

                                    alarm.setActive(0);
                                    }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }



            object.put("Alarms", alarmsList);
            return new ResponseEntity<JSONObject>(object, HttpStatus.OK);
        }

//    @RequestMapping(value = "/addAlarm", method = RequestMethod.POST)
//    public @ResponseBody
//    JSONObject addAlarm(@RequestBody JSONObject alarm) {
//        System.out.println("Try to add alarm  :" + " : " + alarm.getAsString("alarm_name")+ " : " + alarm.getAsString("initial_price")+ " : " + alarm.getAsString("wanted_percent"));
//        HashMap<String, String> response = new HashMap<String, String>();
//
//        String alarm_name = alarm.getAsString("alarm_name");
//        Double initial_price=(Double) alarm.getAsNumber("initial_price") ;
//        Double wanted_percent=(Double)alarm.getAsNumber("wanted_percent");
//        Integer over_price= Integer.valueOf(alarm.getAsString("over_price"));
//        Integer less_price= Integer.valueOf(alarm.getAsString("less_price"));
//
//        Alarm alarm_added = alarm_repository.save(new Alarm(alarm_name,initial_price,wanted_percent,over_price,less_price));
//
//        if (alarm_added != null) {
//            response.put("response", "true");
//            System.out.println("Succes to add for " + alarm_name );
//        } else {
//            response.put("response", "false");
//            System.out.println("Error to add for " +  alarm_name);
//        }
//
//        return new JSONObject(response);
//    }

    @ResponseBody
    @RequestMapping(value = "/editAlarm", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> editAlarm(@RequestBody JSONObject alarm) {
        System.out.println("Try to edit alarm  :" + " : " + alarm.getAsString("alarm_name")+ " : " + alarm.getAsString("wanted_percent"));
        JSONObject response = new JSONObject();

        Integer id= Integer.valueOf(alarm.getAsString("id"));
        String alarm_name = alarm.getAsString("alarm_name");
        Double wanted_percent=Double.valueOf(alarm.getAsString("wanted_percent"));
        Integer over_price= Integer.valueOf(alarm.getAsString("over_price"));
        Integer less_price= Integer.valueOf(alarm.getAsString("less_price"));

        try {
            alarm_repository.update(alarm_name,wanted_percent,over_price,less_price,id);
            response.put("response", "true");

        }
        catch (Exception e){
            response.put("response", "false");

        };


        return new ResponseEntity<JSONObject>(response, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteAlarm", method = RequestMethod.POST)
    public JSONObject deleteAlarm(@RequestBody JSONObject alarm) {
        System.out.println("Try to delete alarm  :" + alarm.getAsString("id_alarm")+ alarm.getAsString("id_user"));
        Integer id_alarm=Integer.valueOf(alarm.getAsString("id_alarm"));
        Integer id_user=Integer.valueOf(alarm.getAsString("id_user"));
        Boolean deleted = false;

        HashMap<String, String> response = new HashMap<String, String>();

        ArrayList<UserAlarm> useralarms = (ArrayList<UserAlarm>) alarmUser_repository.findAll();
        ArrayList<Alarm> alarms = (ArrayList<Alarm>) alarm_repository.findAll();
        ArrayList<Alarm_User> users = (ArrayList<Alarm_User>) user_repository.findAll();

        for (UserAlarm a : useralarms) {
            System.out.println(useralarms);
            if (a.getId_user().equals(id_user) ) {
                for (Alarm aa : alarms) {
                    System.out.println(alarms.toString());
                    if (aa.getId().equals(a.getId_alarm()) && aa.getId().equals(id_alarm)) {
                        for (Alarm_User u : users) {
                            System.out.println(users);
                            if (u.getId().equals(a.getId_user())) {
                                alarmUser_repository.delete(a);
                                deleted = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (deleted) {
            response.put("response", "true");
            System.out.println("Succes to delete for " + alarm.getAsString("id_alarm")+ alarm.getAsString("id_user"));
        } else {
            response.put("response", "false");
            System.out.println("Error to delete for " + alarm.getAsString("id_alarm")+ alarm.getAsString("id_user"));
        }
        return new JSONObject(response);

    }
    @RequestMapping(value = "/addAlarm", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject addAlarm(@RequestBody JSONObject alarm) throws IOException {
        System.out.println("Try to add alarm  :" + alarm.getAsString("idUser") + " : " + alarm.getAsString("alarmName"));
        HashMap<String, String> response = new HashMap<String, String>();

        String alarmName = alarm.getAsString("alarmName");
        Double wantedPercente = Double.valueOf(alarm.getAsString("wantedPercente"));
        Integer idUser = Integer.valueOf(alarm.getAsString("idUser"));
        Integer over = Integer.valueOf(alarm.getAsString("over"));
        Integer less = Integer.valueOf(alarm.getAsString("less"));
        Double initial_price=getPriceStock(alarmName);


        Alarm alarm_added = alarm_repository.save(new Alarm(alarmName,initial_price,initial_price,wantedPercente,over,less,1));


        if (alarm_added != null) {
            alarmUser_repository.save(new UserAlarm(alarm_added.getId(),idUser));
            response.put("response", "true");
            System.out.println("Succes to add for " + alarm.getAsString("idUser") + " : " + alarm.getAsString("alarmName"));
        } else {
            response.put("response", "false");
            System.out.println("Error to add for " + alarm.getAsString("idUser") + " : " + alarm.getAsString("alarmName"));
        }

        return new JSONObject(response);
    }

    public static void sendMail(String recepient,String stockName,Double price) throws Exception {
        System.out.println("Preparing to send email");
        Properties properties = new Properties();

        //Enable authentication
        properties.put("mail.smtp.auth", "true");
        //Set TLS encryption enabled
        properties.put("mail.smtp.starttls.enable", "true");
        //Set SMTP host
        properties.put("mail.smtp.host", "smtp.gmail.com");
        //Set smtp port
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        //Your gmail address
        String myAccountEmail = "sandyema43@gmail.com";
        //Your gmail password
        String password = "********";

        //Create a session with account credentials
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });

        //Prepare email message
        Message message = prepareMessage(session, myAccountEmail, recepient,stockName,price);
        //Send mail
        Transport.send(message);
        System.out.println("Message sent successfully");
    }

    private static Message prepareMessage(Session session, String myAccountEmail, String recepient,String stock,Double price) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject("Your stock has reached your desired price");
            String htmlCode = "<h1> CONGRATULATION </h1> <br/> <h2><b>Your stock "+stock+"have the price "+ price +" now "+"</b></h2>";
            message.setContent(htmlCode, "text/html");
            return message;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

}


