package com.api01.controller;

import com.api01.Service.UserService;
import com.api01.dto.ResponseDTO;
import com.api01.dto.UserDTO;
import com.api01.model.UserEntity;
import com.api01.security.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    /*private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }*/

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
        try{
            //요청을 이용해 저장할 사용자 만들기 (email,username,password 넘어옴)
            /*
            post
            http://localhost:8080/auth/signup
            {
                "email" : "user1@naver.com",
                "username" : "user1",
                "password" : "12345"
            }
            -> {"token":null,"id":"402880877fd48ad6017fd48bd59f0000","username":"user1","email":"user1@naver.com","password":null}

            {
                "email" : "user2@naver.com",
                "username" : "user2",
                "password" : "54321"
            }
            -> {"token":null,"id":"402880877fd48ad6017fd48c84880001","username":"user2","email":"user2@naver.com","password":null}


            */
            //리퀘스트를 이용해 저장할 유저 만들기
            UserEntity user = UserEntity.builder()
                    .email(userDTO.getEmail())
                    .username(userDTO.getUsername())
                    //.password(userDTO.getPassword())
                    .password(passwordEncoder.encode(userDTO.getPassword())) //패스워드 인코더 사용해서 저장
                    .build();
            //서비스를 이용해 리포지터리에 사용자 저장
            UserEntity registerdUser = userService.create(user);
            UserDTO responseUserDTO = UserDTO.builder()
                    .id(registerdUser.getId()) //id
                    .username(registerdUser.getUsername()) //username
                    .email(registerdUser.getEmail()) //email
                    .password(registerdUser.getPassword())
                    .build();
            //사용자 정보는 항상 하나이므로 리스트로 만들어야하는 ResponseDTO 사용하지 않고 그냥 UserDTO 리턴
            return ResponseEntity.ok().body(responseUserDTO);

        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        //로그인 시도
        /*
        post
        http://localhost:8080/auth/signin
        {
            "email" : "user1@naver.com",
            "password" : "12345"
        }
        -> {"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0MDI4ODA4NzdmZDQ4YWQ2MDE3ZmQ0OGJkNTlmMDAwMCIsImlzcyI6ImFwaSBhcHAiLCJpYXQiOjE2NDg1Mzg1MDIsImV4cCI6MTY0ODYyNDkwMn0.7EXhhq_grF-CcZSKizSHgPLdR5dCsHul5PiKYRCKF6OZtpZNOsSjPqy2KFD21BwsjObgVo3G1_T2JaAIm3DDLA","id":"402880877fd48ad6017fd48bd59f0000","username":null,"email":"user1@naver.com","password":null}
        -> {"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0MDI4ODA4NzdmZDQ4YWQ2MDE3ZmQ0OGJkNTlmMDAwMCIsImlzcyI6ImFwaSBhcHAiLCJpYXQiOjE2NDg1MzkwNDIsImV4cCI6MTY0ODYyNTQ0Mn0.-6c5XIWgd6HcHL7NG7xnQH9A2RSbS3IYCS5TcuaJknlY1d1f1y_9QiGQ6vR7F77IiGGSzd3hiL80DauuHLSsLg","id":"402880877fd48ad6017fd48bd59f0000","username":null,"email":"user1@naver.com","password":null}

        {
            "email" : "user2@naver.com",
            "password" : "54321"
        }
        -> {"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0MDI4ODA4NzdmZDQ4YWQ2MDE3ZmQ0OGM4NDg4MDAwMSIsImlzcyI6ImFwaSBhcHAiLCJpYXQiOjE2NDg1Mzg3NzYsImV4cCI6MTY0ODYyNTE3Nn0.01PfqT4Q0MiTr8eY0Ej8R0UGgEBEUPs_0VvXWblz22jTQUVQf7SbOiEjTTaa68HuPXh4G7eRA8iy4Zj3XZoHWA","id":"402880877fd48ad6017fd48c84880001","username":null,"email":"user2@naver.com","password":null}

        //결과
        {
            "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0MDI4ODA4NzdmZDM4YzM3MDE3ZmQzOGNhZmE4MDAwMCIsImlzcyI6ImFwaSBhcHAiLCJpYXQiOjE2NDg1MjE3MTksImV4cCI6MTY0ODYwODExOX0.xa7PjRDEkg1FN7_g8SQN1JOT_mdokrvrx1ltP39VFLdph6YpjKJ_66sUFVIaZQP_5dhRfiL5PPzgVZRIyTaFhg",
            "id": "402880877fd38c37017fd38cafa80000",
            "username": null,
            "email": "user1@naver.com",
            "password": null
        }

        toekn 디코드 :
        {"alg":"HS512"}{"sub":"402880877fd38c37017fd38cafa80000","iss":"api app","iat":1648521719,"exp":1648608119}1kD1$QM<Iu$$[OE-ac(T@v_O?8e2Ma
        */

        //email과 패스워드로 사용자 찾음..
        UserEntity user = userService.getByCredentials(userDTO.getEmail(), userDTO.getPassword(), passwordEncoder);

        if (user != null) { //사용자 있으면

            //토큰 생성
            final String token = tokenProvider.create(user);
            final UserDTO responseUserDTO = UserDTO.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .token(token) //토큰 추가
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        }else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login failed")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
