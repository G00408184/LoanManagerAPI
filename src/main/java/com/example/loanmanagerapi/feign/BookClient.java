package com.example.loanmanagerapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "book-service", url = "http://localhost:8081")
public interface BookClient {

    @PutMapping("/api/books/{bookId}/decrement")
    boolean decrementCopies(@PathVariable("bookId") Long bookId);

    @PutMapping("/api/books/{bookId}/increment")
    boolean incrementCopies(@PathVariable("bookId") Long bookId);

    // Other endpoints as needed
}
