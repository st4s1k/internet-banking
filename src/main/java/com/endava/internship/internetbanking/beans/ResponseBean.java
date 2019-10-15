package com.endava.internship.internetbanking.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
public class ResponseBean {

    @NotNull
    private final String timestamp = LocalDateTime.now().toString();

    private int status;
    @JsonInclude(NON_NULL)
    private Object message;
    @JsonInclude(NON_NULL)
    private Object data;
}
