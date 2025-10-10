package org.example.common.client;

import org.example.common.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url:http://localhost:8001/api/users}")
public interface UserClient {

    @GetMapping("/{userId}/info")
    UserInfoDTO getUserInfo(@PathVariable("userId") Long userId);

}

