package com.kreuterkeule.food.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil {

    private static final String SECRET = "twvO5MNnAHY7zYuTkEJqZMg1KUuRqr7Igu1P59MbIYRW9114gW6ohyJP7a65aao6NBP4FdvR3w0D4GFOEHbPkqBgFVvysEkpZad3BLurdZPl7Y0YD9o6cQPXfz8IRf0b7AnBK8sfndPgH9FXmx67cg2cRoTHDnpKI2XD57IMUOZGAPGJF0kuKYxk8as7GLB2DckU6YKru61JnyhFp21pEAqHILWtF23L8W3GdXxUNcRgKFQBQt91PknTWdfXjjTQkqv60E25gpPZ5o9OlSoboNkvIkbIv0vu43rslV5OfrzlrdfdEyFSEpTDyQb9XTECnFChLJuiShweXwOXcIZjv8i7dQKgQLxHl5Yt6AVREjm770iW2mCFruKVBJViRwF2JGp7gAgCWTS06aLBUk5Y6KuMdGslMWmoiyMBoH3HeEJtajlP4F4fHLOaRvexFqanRIJC1R0Q8HmAATwo5cogqfkrKmkrDOe573kU7au6deS6MMxz2jol0LWG0HsrsSEH";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days / 10 / 48 = 30 min

    private final UserDetailsManager users;

    @Autowired
    public JwtTokenUtil(UserDetailsManager userDetailsManager) {
        this.users = userDetailsManager;
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public boolean validate(String token) {
        final String username = getUsername(token);

        UserDetails user = users.loadUserByUsername(username);
        if (user == null) return false;
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        boolean result = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().before(new Date());
        System.out.println(result ? "token expired :(" : "token not expired :)");
        return result;
    }

    public String getUsername(String token) {
        String username = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        System.out.println("Got username '" + username + "' from token");
        return username;
    }

}
