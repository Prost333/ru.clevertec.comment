package ru.clevertec.comment.multiFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "user-aut", url = "localhost:8081")
public interface UserClient {
    @GetMapping("/auth/getBody")
    String  getDto();

}
