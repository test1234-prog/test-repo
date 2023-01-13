package uz.momoit.makesense_dbridge.web.rest.errors;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrRespModel {

    private URI type;
    private String title;
    private HttpStatus status;
    private String detail;
    private String entityName;
    private String errorKey;
    private LocalDateTime timestamp;
}
