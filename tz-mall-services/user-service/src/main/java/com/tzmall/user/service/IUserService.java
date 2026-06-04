package com.tzmall.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tzmall.api.user.dto.*;
import com.tzmall.common.core.result.Result;
import com.tzmall.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口(业务层)
 * 
 * 接口说明:
 * 1. 定义用户管理的业务方法
 * 2. 面向Controller层提供服务
 * 3. 由UserServiceImpl具体实现
 * 
 * 为什么使用接口:
 * - 降低耦合度: Controller依赖接口而不是具体实现类
 * - 提高可测试性: 可以轻松使用Mock对象进行单元测试
 * - 便于扩展: 可以通过多实现来支持不同策略
 * 
 * 继承IService说明:
 * - IService<User>是MyBatis-Plus提供的服务层接口
 * - 需要指定对应的实体类型
 * - 继承后自动获得基本的CRUD方法
 * - 自定义方法写在接口中,业务方法在实现类中实现
 */
public interface IUserService extends IService<User> {

    /**
     * 用户注册
     * 
     * @param registerDTO 注册信息(用户名、密码、手机号)
     * @return 统一响应格式,包含操作结果和用户信息
     */
    Result<UserDTO> register(RegisterDTO registerDTO);

    /**
     * 管理员创建用户
     *
     * @param dto 管理员创建用户的请求信息（包含用户名、密码、手机号、角色）
     * @return 统一响应格式，包含创建的用户信息
     */
    Result<UserDTO> createUserByAdmin(AdminCreateUserDTO dto);

    /**
     * 用户登录
     * 
     * @param loginDTO 登录信息(用户名、密码)
     * @return 统一响应格式,包含操作结果和登录响应信息(token+用户信息)
     */
    Result<LoginResponseDTO> login(LoginDTO loginDTO);

    /**
     * 根据ID查询用户详情
     * 
     * @param id 用户ID
     * @return 统一响应格式,包含查询到的用户信息
     */
    Result<UserDTO> getUserById(Long id);

    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param userDTO 要更新的用户信息
     * @return 统一响应格式,包含更新后的用户信息
     */
    Result<UserDTO> updateUser(Long id, UserDTO userDTO);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 统一响应格式
     */
    Result<Void> deleteUser(Long id);

    /**
     * 分页查询用户列表
     * 
     * @param page 当前页码
     * @param size 每页数量
     * @return 统一响应格式,包含分页后的用户列表
     */
    Result<IPage<UserDTO>> listUsers(int page, int size);
}
