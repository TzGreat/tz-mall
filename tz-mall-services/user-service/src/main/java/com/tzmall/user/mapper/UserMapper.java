package com.tzmall.user.mapper;

import com.tzmall.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户表 Mapper 接口
 * 
 * Mapper层职责:
 * 1. 负责与数据库进行交互
 * 2. 定义数据库操作方法
 * 3. 通常使用XML文件或注解编写SQL
 * 
 * 继承BaseMapper说明:
 * - BaseMapper<User>是MyBatis-Plus提供的Mapper接口
 * - 需要指定对应的实体类型
 * - 继承后自动获得基本的CRUD方法:
 *   1. insert(T entity) - 插入数据
 *   2. deleteById(Serializable id) - 根据ID删除
 *   3. updateById(T entity) - 根据ID更新
 *   4. selectById(Serializable id) - 根据ID查询
 *   5. selectList(Wrapper<T> queryWrapper) - 条件查询
 *   等等...
 * 
 * 使用好处:
 * - 无需手动编写基本的CRUD SQL
 * - 大大减少开发工作量
 * - 提高开发效率
 *
 * @author author
 * @since 2026-05-28
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 这里不需要定义额外方法
     * 因为继承了BaseMapper,已经拥有了所有基本的CRUD方法
     * 
     * 如果需要自定义复杂SQL,可以使用以下方式:
     * 1. 在XML文件中编写SQL
     * 2. 使用@Select、@Update等注解
     */
}
