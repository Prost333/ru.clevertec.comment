package ru.clevertec.comment.multiFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "user-aut", url = "http://app:8081")
public interface UserClient {
    @GetMapping("/auth/getBody")
    String  getDto();

}
