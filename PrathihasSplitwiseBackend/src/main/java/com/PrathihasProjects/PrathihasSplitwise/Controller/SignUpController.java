package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SignUpController {
    private final UserDAOImpl theUserDAOImpl;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignUpController(UserDAOImpl theUserDAOImpl) {
        this.theUserDAOImpl = theUserDAOImpl;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/splitwise/signup")
    public ResponseEntity<?> signupController(@RequestBody User newUser)
    {
        try {
            User findUser = theUserDAOImpl.findUserByName(newUser.getUsername());

            if(findUser != null){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
            }

            String hashedPassword = passwordEncoder.encode(newUser.getPassword());
            newUser.setPassword(hashedPassword);

            theUserDAOImpl.save(newUser);
            return ResponseEntity.ok("Signup successfull");

        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred!");
        }
    }
}
