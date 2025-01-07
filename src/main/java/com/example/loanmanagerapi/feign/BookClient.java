package com.example.loanmanagerapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "book-service", url = "http://localhost:8083")
public interface BookClient {

    @PatchMapping("/decreaseCopies")
    boolean decreaseCopies(@RequestParam("title") String title, @RequestParam("author") String author);

    @PatchMapping("/incrementCopies")
    boolean incrementCopies(@RequestParam("title") String title, @RequestParam("author") String author);

}
