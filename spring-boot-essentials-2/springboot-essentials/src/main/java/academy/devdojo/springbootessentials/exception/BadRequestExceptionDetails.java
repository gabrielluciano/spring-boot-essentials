package academy.devdojo.springbootessentials.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BadRequestExceptionDetails {

    private String title;
    private int status;
    private String details;
    private String developerMessage;
    private LocalDateTime timestamp;

}
