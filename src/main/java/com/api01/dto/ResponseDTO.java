package com.api01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO<T>{ //TodoDTO 뿐만 아니라 다른 모델의 DTO도 ResponseDTO를 이용해 리턴할수있도록 Generic 사용함
    private String error;
    private List<T> data;
}
