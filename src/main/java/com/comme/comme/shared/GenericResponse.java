package com.comme.comme.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Data
@NoArgsConstructor
public class GenericResponse {

    private String message;

    public GenericResponse(String message){
        this.message = message;
    }
}
