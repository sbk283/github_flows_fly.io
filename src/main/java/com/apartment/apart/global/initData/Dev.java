package com.apartment.apart.global.initData;

import com.apartment.apart.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class Dev {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner init(UserService userService) {
        return args -> {
//            userService.create("admin1", "관리자1", "admin", "01000000001","admin1@apart.com",100,001,true,false);
//            userService.create("admin2", "관리자2", "admin", "01000000002","admin2@apart.com",100,002,true, false);
//            userService.create("user1", "유저1","123123","01011111111","user1@apart.com",101,101,false,false);
//            userService.create("user2", "유저2","123123","01022222222","user2@apart.com",102,102,false,false);
//            userService.create("user3", "유저3","123123","01033333333","user3@apart.com",103,103,false,false);
//            userService.create("user4", "유저4","123123","01044444444","user4@apart.com",104,104,false,false);
//            userService.create("user5", "유저5","123123","01055555555","user5@apart.com",105,105,false,false);
//            userService.create("user6", "유저6","123123","01066666666","user6@apart.com",106,106,false,false);
//            userService.create("user7", "유저7","123123","01077777777","user7@apart.com",107,107,false,false);
//            userService.create("user8", "유저8","123123","01088888888","user8@apart.com",108,108,false,false);
//            userService.create("user9", "유저9","123123","01099999999","user9@apart.com",109,109,false,false);
//            userService.create("user10", "유저10","123123","01010101010","user10apart.com",109,109,false,false);
        };
    }
}