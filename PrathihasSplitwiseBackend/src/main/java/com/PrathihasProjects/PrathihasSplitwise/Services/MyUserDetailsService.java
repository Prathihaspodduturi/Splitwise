package com.PrathihasProjects.PrathihasSplitwise.Services;

import com.PrathihasProjects.PrathihasSplitwise.DAO.userDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private userDAOImpl theUserDAOImpl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userFromDb = theUserDAOImpl.findUserByName(username);


        if(userFromDb != null){
            return new org.springframework.security.core.userdetails.User(username, userFromDb.getPassword(), new ArrayList<>());
        }
        else {
            throw new UsernameNotFoundException("User not found with username: "+username);
        }

    }
}
