package com.tzmall.api.user.feign;

import com.tzmall.api.user.dto.UserDTO;
import com.tzmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务Feign客户端
 * 
 * 职责说明:
 * 定义用户服务的远程调用接口
 * 
 * 使用说明:
 * - 在其他服务中注入该接口即可调用用户服务
 * - Feign会自动处理负载均衡和服务发现
 */
@FeignClient(name = "service-user", path = "/api/user")
public interface UserFeignClient {

    /**
     * 根据ID查询用户详情
     * 创建订单时获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    Result<UserDTO> getUserById(@PathVariable("id") Long id);
}
