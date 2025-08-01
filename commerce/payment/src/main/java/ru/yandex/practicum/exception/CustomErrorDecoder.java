package ru.yandex.practicum.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());
        log.error("Payment feign error: method {}, status {}, reason {}", methodKey, response.status(), response.reason());

        return switch (response.status()) {
            case 400 -> new BadRequestException("Payment bad Request");
            case 500 -> new InternalServerErrorException("Payment internal server error");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}