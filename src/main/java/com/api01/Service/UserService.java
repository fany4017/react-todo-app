package com.api01.Service;

import com.api01.model.UserEntity;
import com.api01.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserEntity create(final UserEntity userEntity){
        if(userEntity == null || userEntity.getEmail() == null){
            throw new RuntimeException("Invalid arguments");
        }

        final String email = userEntity.getEmail();
        if(userRepository.existsByEmail(email)){ //이메일 중복체크
            log.warn("Email already exists {}",email);
            throw new RuntimeException("Email already exists");
        }

        return userRepository.save(userEntity); //문제없으면 저장
    }
    //스프링시큐리티 BCryptPasswordEncoder 구현
    public UserEntity getByCredentials(final String email, final String password, PasswordEncoder encoder){

        final UserEntity originalUser = userRepository.findByEmail(email);
        //matches 메소드를 이용해 패스워드가 같은지 확인
        if(originalUser != null && encoder.matches(password,originalUser.getPassword())){
            return originalUser;

        }
        return null;
        //return userRepository.findByEmailAndPassword(email, password);
    }

}
