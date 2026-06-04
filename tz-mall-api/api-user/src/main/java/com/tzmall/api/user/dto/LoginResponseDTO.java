package com.tzmall.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录响应DTO")
public class LoginResponseDTO {

    @Schema(description = "登录token")
    private String token;

    @Schema(description = "用户信息")
    private UserDTO userInfo;
}
