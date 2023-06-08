package com.example.demo.services;

import com.example.demo.entity.CustomUserDetail;
import com.example.demo.entity.User;
import com.example.demo.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user =  userRepository.findByUsername(username);
        User user2 = userRepository.findByEmail(username);
        if (user2 != null)
            return new CustomUserDetail(user2, userRepository);
        else if (user != null)
            return new CustomUserDetail(user, userRepository);
        throw new UsernameNotFoundException("User not found");
    }
}