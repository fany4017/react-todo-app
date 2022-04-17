package com.api01.Service;

import com.api01.model.TodoEntity;
import com.api01.persistence.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

    public String testService(){
        //TodoEntity 생성
        TodoEntity entity = TodoEntity.builder().title("첫번째 할 일").build();
        //TodoEntity 저장
        repository.save(entity);
        //TodoEntity 검색
        TodoEntity savedEntity = repository.findById(entity.getId()).get();
        return savedEntity.getTitle();
    }

    public List<TodoEntity> crate(final TodoEntity entity){
        //검증
        validate(entity);
        
        repository.save(entity);

        log.info("Entity Id : {} is saved ", entity.getId());

        return repository.findByUserId(entity.getUserId());
    }

    public List<TodoEntity> retrieve(final String userId){
        return repository.findByUserId(userId);
    }

    public List<TodoEntity> update(final TodoEntity entity){
        //1.저장할 엔티티가 유효한지 확인
        validate(entity);

        //2.넘겨받은 엔티티 id를 이용해 TodoEntity를 가져온다.
        final Optional<TodoEntity> original = repository.findById(entity.getId()); //null인지 체크

        original.ifPresent(todo -> {
            //3.반환된 TodoEntity가 존재하면 값을 새 entity 값으로 덮어 씌운다.
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
            //4.데이터베이스에 새 값을 저장한다.
            repository.save(todo);
        });
        /* 다른 방법
        if(original.isPresent()){
            final TodoEntity todo = original.get();
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
        }*/

        //사용자의 모든 todo 리스트를 리턴한다.
        return retrieve(entity.getUserId());
    }
    
    public List<TodoEntity> delete(final TodoEntity entity){
        //1.유효성 검증
        validate(entity);
        
        try{
            //2.엔티티 삭제
            repository.delete(entity);
        }catch(Exception e){
            //3.에러로그 찍음
            log.error("error deleteing entity ", entity.getId(), e);
            throw new RuntimeException("error deleting entity "+entity.getId());
        }

        //4.새로 Todo 리스트를 가져와 리턴한다.
        return retrieve(entity.getUserId());
    }
    private void validate(final TodoEntity entity){ //검증 메소드
        if(entity == null){
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null.");
        }
        if(entity.getUserId() == null){
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user.");
        }
    }
}
