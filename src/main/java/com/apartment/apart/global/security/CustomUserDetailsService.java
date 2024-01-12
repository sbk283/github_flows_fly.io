//package com.apartment.apart.global.security;
//
//import com.apartment.apart.domain.user.SiteUser;
//import com.apartment.apart.domain.user.UserRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Optional;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    public CustomUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<SiteUser> siteUser = userRepository.findByUserId(username);
//        if (siteUser == null) {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//        return new org.springframework.security.core.userdetails.User(
//                siteUser.get().getUserId(),
//                siteUser.get().getPassword(),
//                !siteUser.get().isCheckedWithdrawal(),
//                true,
//                true,
//                true,
//                Collections.emptyList()
//        );
//    }
//}