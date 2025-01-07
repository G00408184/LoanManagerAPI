package com.example.loanmanagerapi.feign;

import com.example.loanmanagerapi.entity.UserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Admin-service", url = "http://localhost:8081/admin")
public interface AdminClient {

    @PostMapping("/CheckAdmin")
    boolean checkIfAdmin(@RequestBody UserDetails userDetails);

}
