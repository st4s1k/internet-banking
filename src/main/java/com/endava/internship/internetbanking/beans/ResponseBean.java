package com.endava.internship.internetbanking.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

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

    private ResponseBean(int status,
                         Object message,
                         Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ResponseBean from(HttpStatus status,
                                    Object message,
                                    Object... data) {
        return new ResponseBean(status.value(), message, data);
    }
}
