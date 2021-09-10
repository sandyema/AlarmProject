package hello;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.util.HashMap;

public class TokenManager {
    private static HashMap<String,String> tokens;
    public static void logout(String username){
        tokens.remove(username);
    }

    public static boolean checkToken(String token){
        if(tokens.containsValue(token))
                return true;
        return false;
    }
    public static String generateToken(String username){
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withIssuer(username)
                    .sign(algorithm);
            tokens.put(username,token);
            return token;
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return null;
    }
}