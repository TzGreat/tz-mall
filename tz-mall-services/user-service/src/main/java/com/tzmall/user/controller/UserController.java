package com.tzmall.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tzmall.api.user.dto.LoginDTO;
import com.tzmall.api.user.dto.LoginResponseDTO;
import com.tzmall.api.user.dto.RegisterDTO;
import com.tzmall.api.user.dto.UserDTO;
import com.tzmall.common.core.result.Result;
import com.tzmall.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 * 
 * 职责说明:
 * 1. 接收HTTP请求,处理参数验证
 * 2. 调用Service层处理业务逻辑
 * 3. 返回统一的响应格式
 * 4. 提供Swagger API文档
 * 
 * 架构说明:
 * - Controller层是MVC架构中的"控制层"
 * - 负责接收用户请求、协调Model和View
 * - 在RESTful API中,Controller返回JSON数据,而不是View
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户注册、登录、CRUD操作")
public class UserController {

    /**
     * 用户服务接口(依赖注入)
     * 
     * 使用构造器注入的好处:
     * 1. 比@Autowired字段注入更安全,可以提前发现循环依赖
     * 2. 更容易进行单元测试(可以手动创建Controller并注入mock对象)
     * 3. 明确标注了依赖关系,代码更清晰
     */
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口
     * 
     * HTTP方法: POST
     * 请求路径: /api/user/register
     * 
     * 工作流程:
     * 1. 接收前端提交的注册信息(JSON格式)
     * 2. @Valid注解触发参数校验,验证RegisterDTO中的校验规则
     * 3. 调用userService.register()处理注册逻辑
     * 4. 返回统一的Result响应格式
     * 
     * @param registerDTO 注册信息(包含用户名、密码、手机号)
     *                     使用@RequestBody自动将JSON反序列化为对象
     * @return 统一响应格式,包含操作结果和用户信息
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public Result<UserDTO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    /**
     * 用户登录接口
     * 
     * HTTP方法: POST
     * 请求路径: /api/user/login
     * 
     * 工作流程:
     * 1. 接收用户名和密码
     * 2. 调用Service层验证用户身份
     * 3. 验证成功后返回用户信息
     * 
     * @param loginDTO 登录信息(包含用户名和密码)
     * @return 统一响应格式,包含登录结果和用户信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录验证")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 根据ID查询用户详情
     * 
     * HTTP方法: GET
     * 请求路径: /api/user/{id}
     * 
     * RESTful设计说明:
     * - 使用@PathVariable从URL路径中获取ID
     * - GET方法表示查询操作,符合RESTful规范
     * 
     * @param id 用户ID,通过URL路径传递
     * @return 统一响应格式,包含查询到的用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "根据用户ID查询用户信息")
    public Result<UserDTO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 更新用户信息
     * 
     * HTTP方法: PUT
     * 请求路径: /api/user/{id}
     * 
     * RESTful设计说明:
     * - PUT方法表示更新操作
     * - URL中的{id}指定要更新的资源
     * - 请求体包含更新的数据
     * 
     * @param id 用户ID,通过URL路径传递
     * @param userDTO 要更新的用户信息(部分更新,只更新非空字段)
     * @return 统一响应格式,包含更新后的用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    public Result<UserDTO> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    /**
     * 删除用户
     * 
     * HTTP方法: DELETE
     * 请求路径: /api/user/{id}
     * 
     * RESTful设计说明:
     * - DELETE方法表示删除操作
     * - 直接在URL中指定要删除的资源ID
     * 
     * @param id 用户ID,通过URL路径传递
     * @return 统一响应格式,不返回具体数据
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 分页查询用户列表
     * 
     * HTTP方法: GET
     * 请求路径: /api/user/list
     * 
     * 分页参数说明:
     * - page: 页码,从1开始,默认为1
     * - size: 每页数量,默认为10
     * 
     * 使用场景:
     * - 用户列表展示
     * - 后台管理系统用户管理
     * 
     * @param page 页码,默认从1开始
     * @param size 每页显示的记录数,默认10条
     * @return 统一响应格式,包含分页后的用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询用户列表", description = "分页查询所有用户")
    public Result<IPage<UserDTO>> listUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return userService.listUsers(page, size);
    }
}
