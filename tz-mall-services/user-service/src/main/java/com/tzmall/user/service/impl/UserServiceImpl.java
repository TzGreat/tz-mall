package com.tzmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tzmall.api.user.dto.*;
import com.tzmall.common.core.result.Result;
import com.tzmall.common.core.utils.JwtUtil;
import com.tzmall.common.redis.util.RedisUtil;
import com.tzmall.user.entity.User;
import com.tzmall.user.mapper.UserMapper;
import com.tzmall.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 * 
 * 核心职责:
 * 1. 实现IUserService接口定义的所有业务方法
 * 2. 处理具体的业务逻辑
 * 3. 与数据库进行交互
 * 4. 负责事务管理(在方法级别通过@Transactional注解)
 * 
 * 继承说明:
 * - ServiceImpl<UserMapper, User>是MyBatis-Plus提供的Service实现基类
 * - 提供了常用的CRUD方法: save(), getById(), updateById(), removeById(), page()等
 * - 无需手动编写基本的增删改查SQL
 * 
 * 密码加密说明:
 * - 使用MD5进行密码加密存储(简单示例,生产环境建议使用BCrypt)
 * - MD5是单向哈希算法,无法反向解密
 * - 用户登录时,将输入的密码同样用MD5加密后比对
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final RedisUtil redisUtil;

    public UserServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    /**
     * 用户注册业务逻辑
     * 
     * 注册流程:
     * 1. 先查询数据库,检查用户名是否已被占用
     * 2. 如果用户名存在,返回错误信息
     * 3. 如果用户名可用,创建新用户对象
     * 4. 对密码进行MD5加密存储(安全性考虑,明文密码不能存库)
     * 5. 设置创建时间和更新时间
     * 6. 保存到数据库
     * 7. 返回成功信息和用户信息
     * 
     * 安全考虑:
     * - 密码必须加密存储,不能明文保存
     * - 使用MD5简单加密(实际项目中推荐BCrypt等更安全的加密方式)
     * - 用户名需要唯一性校验
     * 
     * @param registerDTO 包含用户名、密码、手机号的注册信息
     * @return 统一响应格式,成功返回用户信息,失败返回错误信息
     */
    @Override
    public Result<UserDTO> register(RegisterDTO registerDTO) {
        // 创建查询条件: 查询username字段等于registerDTO.getUsername()的记录
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDTO.getUsername());
        
        // 执行查询,获取一条记录
        User existingUser = this.getOne(queryWrapper);
        
        // 如果查询到用户,说明用户名已被占用
        if (existingUser != null) {
            return Result.fail(400, "用户名已存在");
        }

        // 创建新的用户对象
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        
        // 密码加密: 使用MD5对密码进行哈希处理
        // 注意: 这里使用DigestUtils.md5DigestAsHex将密码转为16进制字符串
        // 实际项目中建议使用BCrypt等更安全的加密方式
        user.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
        user.setPhone(registerDTO.getPhone());
        user.setRole(1);
        
        // 设置时间戳
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户到数据库
        this.save(user);
        
        // 返回成功信息,并将用户实体转换为DTO返回
        return Result.success("注册成功", convertToDTO(user));
    }

     /**
     * 管理员创建用户业务逻辑
     *
     * @param dto 管理员创建用户的请求信息（包含用户名、密码、手机号、角色）
     * @return
     */
    @Override
    public Result<UserDTO> createUserByAdmin(AdminCreateUserDTO dto) {
        if(dto.getRole()!=0&&dto.getRole()!=1){
            return Result.fail(400, "角色只能是0或1");
        }
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, dto.getUsername());
        User existingUser = this.getOne(queryWrapper);
        if(existingUser!=null){
            return Result.fail(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        this.save(user);

        return Result.success("创建成功", convertToDTO(user));
    }

    /**
     * 用户登录业务逻辑
     * 
     * 登录流程:
     * 1. 根据用户名查询用户记录
     * 2. 如果用户不存在,返回"用户名或密码错误"(不明确告知具体哪个错误,防止暴力破解)
     * 3. 如果用户存在,将输入的密码用相同方式加密后与数据库中的密码比对
     * 4. 密码匹配则登录成功,返回用户信息
     * 
     * 安全考虑:
     * - 错误信息统一返回"用户名或密码错误",不明确指出是用户名错还是密码错
     * - 这样可以防止恶意攻击者通过错误信息推断哪些用户名已注册
     * - 密码必须用相同加密方式处理后再比对
     * 
     * @param loginDTO 包含用户名和密码的登录信息
     * @return 统一响应格式,成功返回用户信息,失败返回错误信息
     */
    @Override
    public Result<LoginResponseDTO> login(LoginDTO loginDTO) {
        // 创建查询条件: 根据用户名查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        
        // 执行查询
        User user = this.getOne(queryWrapper);

        // 用户不存在,返回统一错误信息(不明确指出是用户名还是密码错误)
        if (user == null) {
            return Result.fail(400, "用户名或密码错误");
        }

        // 将输入的密码用MD5加密后与数据库中的密码比对
        String encryptedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            return Result.fail(400, "用户名或密码错误");
        }
        Map<String, Object> claims = Map.of("userId", user.getId(), "username", user.getUsername(), "role", user.getRole());
        String token = JwtUtil.genToken(claims);
        redisUtil.set("token:" + token, user.getId(), 12, TimeUnit.HOURS);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(token);
        loginResponse.setUserInfo(convertToDTO(user));

        // 登录成功,返回token和用户信息
        return Result.success("登录成功", loginResponse);
    }

    /**
     * 根据ID查询用户详情
     * 
     * 查询流程:
     * 1. 调用getById()方法根据ID查询用户
     * 2. 如果用户不存在,返回404错误
     * 3. 如果用户存在,转换为DTO并返回
     * 
     * @param id 用户ID
     * @return 统一响应格式,成功返回用户信息,用户不存在返回404错误
     */
    @Override
    public Result<UserDTO> getUserById(Long id) {
        // 调用MyBatis-Plus提供的getById方法
        User user = this.getById(id);
        
        // 检查用户是否存在
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        
        // 转换为DTO并返回
        return Result.success(convertToDTO(user));
    }

    /**
     * 更新用户信息
     * 
     * 更新策略:
     * 1. 先查询用户是否存在
     * 2. 如果不存在,返回404错误
     * 3. 如果存在,只更新传入的非空字段(部分更新)
     * 4. 自动更新更新时间戳
     * 5. 保存更新后的用户信息
     * 
     * 部分更新说明:
     * - 前端只需要传递要修改的字段,其他字段传null
     * - 后端只更新非null的字段,保留原有数据
     * - 这种方式更灵活,减少前端的数据传输量
     * 
     * 注意:
     * - 没有提供密码更新功能,密码修改应该单独提供接口
     * - 密码修改应该要求输入原密码进行验证
     * 
     * @param id 用户ID
     * @param userDTO 包含要更新的用户信息的DTO
     * @return 统一响应格式,成功返回更新后的用户信息
     */
    @Override
    public Result<UserDTO> updateUser(Long id, UserDTO userDTO) {
        // 先查询用户是否存在
        User user = this.getById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }

        // 只更新传入的非空字段(部分更新)
        // 使用if判断,只更新有值的字段
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        
        // 更新修改时间
        user.setUpdateTime(LocalDateTime.now());

        // 调用MyBatis-Plus的updateById方法更新
        this.updateById(user);
        
        return Result.success("更新成功", convertToDTO(user));
    }

    /**
     * 删除用户
     * 
     * 删除流程:
     * 1. 先查询用户是否存在
     * 2. 如果不存在,返回404错误
     * 3. 如果存在,执行删除操作
     * 
     * 软删除vs硬删除:
     * - 当前使用的是硬删除(直接从数据库删除)
     * - 实际项目中通常使用软删除(在表中添加deleted字段标记)
     * - 软删除可以保留数据,便于数据恢复和审计
     * 
     * @param id 用户ID
     * @return 统一响应格式,删除成功返回成功信息
     */
    @Override
    public Result<Void> deleteUser(Long id) {
        // 先查询用户是否存在
        User user = this.getById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }

        // 调用MyBatis-Plus的removeById方法删除
        this.removeById(id);
        
        return Result.success();
    }

    /**
     * 分页查询用户列表
     * 
     * 分页原理:
     * 1. 创建一个Page对象,指定当前页码和每页数量
     * 2. 调用page()方法,MyBatis-Plus会自动进行分页查询
     * 3. 返回的分页对象包含: 总记录数、总页数、当前页数据列表等
     * 4. 将实体分页对象转换为DTO分页对象返回
     * 
     * 分页参数说明:
     * - page: 当前页码,从1开始
     * - size: 每页显示的记录数
     * 
     * MyBatis-Plus分页特性:
     * - 需要配置分页插件才能正常工作
     * - 会自动在SQL中添加LIMIT和OFFSET
     * - 支持返回总记录数和分页数据
     * 
     * @param page 当前页码
     * @param size 每页数量
     * @return 统一响应格式,包含分页后的用户列表
     */
    @Override
    public Result<IPage<UserDTO>> listUsers(int page, int size) {
        // 创建分页参数对象
        // Page<>(page, size)构造函数: 第一个参数是当前页码,第二个参数是每页数量
        Page<User> pageParam = new Page<>(page, size);
        
        // 执行分页查询
        // page()方法是MyBatis-Plus提供的方法,会自动进行分页处理
        IPage<User> userPage = this.page(pageParam);

        // 将User类型的Page转换为UserDTO类型的Page
        // convert()方法是MyBatis-Plus提供的方法,会对列表中的每个元素执行转换
        // this::convertToDTO是方法引用,等同于 user -> convertToDTO(user)
        IPage<UserDTO> dtoPage = userPage.convert(this::convertToDTO);
        
        return Result.success(dtoPage);
    }

    /**
     * 将User实体转换为UserDTO
     * 
     * 转换原因(实体与DTO分离的好处):
     * 1. 保护内部数据: DTO只暴露需要对外公开的字段,隐藏敏感信息(如password)
     * 2. 解耦: 实体与API接口解耦,数据库字段变化不影响前端
     * 3. 减少数据传输: 只传输必要的字段,减少网络流量
     * 4. 格式化: 可以对字段进行格式化转换
     * 
     * 字段映射说明:
     * - User实体的所有字段都会被映射到UserDTO
     * - 注意: 这里没有映射password字段,实现数据保护
     * 
     * @param user 用户实体对象
     * @return 用户DTO对象
     */
    private UserDTO convertToDTO(User user) {
        // 创建新的DTO对象
        UserDTO dto = new UserDTO();
        
        // 复制字段值
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        
        // 注意: 没有设置password字段,这样可以保护用户密码不被泄露
        return dto;
    }
}
