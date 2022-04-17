package com.api01.controller;

import com.api01.Service.TodoService;
import com.api01.dto.ResponseDTO;
import com.api01.dto.TodoDTO;
import com.api01.model.TodoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired
    private TodoService service;

    @PostMapping
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){ //dto 에 사용자 요청 파라미터 담김
        try{
            log.warn("userId : "+userId + " dto : "+dto.toString());
            //String temporaryUserId = "temporary-user";

            // 1.TodoEntity로 변환
            TodoEntity entity = TodoDTO.todoEntity(dto);

            // 2.id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
            entity.setId(null);
            
            // 3.임시 사용자 아이디 생성 temporaryUserId -> userId
            entity.setUserId(userId);

            // 4.서비스를 이용해 Todo 엔티티를 생성한다.
            List<TodoEntity> entities = service.crate(entity); // 서비스를 통해서 디비상에 데이터 저장

            // 5.자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환한다.
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            // 6.변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화한다. (데이터와 에러를 전달한다)
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            // 7. ResponseDTO를 리턴한다.
            return ResponseEntity.ok().body(response);
        }catch (Exception e){

            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId){

        //String temporaryUserId = "temporary-user";

        //1.서비스 메소드의 retrieve()메소드를 사용해 Todo 리스트를 가져온다. temporaryUserId -> userId
        List<TodoEntity> entities = service.retrieve(userId);

        //2.자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환한다.
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        //3.변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화한다.
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        //4.리턴
        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){

        //String temporaryUserId = "temporary-user";

        //1.dto를 entity로 변환
        TodoEntity entity = TodoDTO.todoEntity(dto);
        log.warn("entity : " +entity.toString());
        //2.userId를 temporaryUserId로 초기화한다. temporaryUserId ->
        entity.setUserId(userId);

        //3.서비스를 이용해 entity를 업데이트한다.
        List<TodoEntity> entities = service.update(entity);

        //4.자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환한다.
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
        log.warn("updated : "+dtos.toString());
        //5.변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화한다.
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        //6.ResponseDTO를 리턴한다.
        return ResponseEntity.ok().body(response);

    }
    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){
        try{
            //String temporaryUserId = "temporary-user";

            //1.TodoENtity로 변환한다.
            TodoEntity entity = TodoDTO.todoEntity(dto);
            log.warn("dto : "+dto.toString());
            //2.임시 사용자 아이디를 설정해 준다. temporaryUserId -> userId
            entity.setUserId(userId);

            //3.서비스를 이용해 entity를 삭제한다.
            List<TodoEntity> entities = service.delete(entity);
            log.warn("entities : "+entities.toString());
            //4.자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환한다.
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            log.warn("dtos : "+dtos.toString());
            //5.변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화한다.
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            //6.ResponseDTO를 리턴한다.
            return ResponseEntity.ok().body(response);
        }catch(Exception e){
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/test")
    public ResponseEntity<?> testTodo(){
        String str = service.testService();
        List<String> list = new ArrayList<>();
        list.add(str);

        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.ok().body(response);
    }
}
