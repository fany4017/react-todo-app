package com.api01.security;

import com.api01.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    private static final String SECRET_KEY = "NMA8JPctFuna59f5";

    public String create(UserEntity userEntity){
        //기한은 지금부터 1일로 설정
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        //JWT 토큰 생성
        return Jwts.builder()
                //header에 들어갈 내용 및 서명을 하기 위한 알고리즘 & SECRET_KEY
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                //payload에 들어갈 내용
                .setSubject(userEntity.getId())//sub 토큰의 주인
                .setIssuer("api app")//iss 발행주체
                .setIssuedAt(new Date())//발생된 날짜와 시간
                .setExpiration(expiryDate)//만료일
                .compact();
    }

    public String validateAndGetUserId(String token){
        //parseClaimsJws 메소드가 Base64로 디코딩 및 파싱
        //헤더와 페이로드를 setSigningKey로 넘어온 시크릿을 이용해 서명한 후 token의 서명과 비교
        //위조되지 않았다면 페이로드(Claims) 리턴, 위조라면 예외를 날림
        //그중 우리는 userId가 필요하므로 getBody를 부른다.
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY) //앞부분 {헤더}.{페이로드}를 떼서 SECRET_KEY로 전자 서명 -> 결과 Y
                .parseClaimsJws(token)//BASE64로 toekn 디코딩 및 파싱 -> 결과는 {헤더}.{페이로드}.X
                .getBody();

        return claims.getSubject();
    }

}
