package smu.capstone.common.errorcode;

import org.springframework.http.HttpStatus;

public interface StatusCode {
    HttpStatus status();
    String code();
    String message();
}
