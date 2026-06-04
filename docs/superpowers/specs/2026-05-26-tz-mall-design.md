# TZ-Mall 微服务商城项目设计文档

> 创建时间：2026-05-26
> 项目用途：个人求职项目，展示微服务架构能力

---

## 一、项目概述

### 1.1 项目背景

TZ-Mall 是一个基于 Spring Cloud Alibaba 的微服务商城项目，参考黑马商城架构设计，用于求职展示微服务技术能力。

### 1.2 项目目标

- 掌握 Spring Cloud Alibaba 全家桶使用
- 学习 RabbitMQ 消息队列、Elasticsearch 搜索引擎
- 理解分布式事务、服务保护等高级特性
- 产出可展示的完整项目

### 1.3 功能范围（精简版）

| 模块 | 功能 |
|------|------|
| 用户服务 | 用户注册、登录、信息管理 |
| 商品服务 | 商品CRUD、商品搜索、库存管理 |
| 订单服务 | 订单创建、订单查询、订单超时取消 |

---

## 二、系统架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         客户端请求                               │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Cloud Gateway                         │
│         (路由转发 / 统一鉴权 / 限流 / 跨域处理)                    │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│                        Nacos 注册中心                            │
│                  (服务注册发现 / 配置管理)                        │
└──────────────────────────────────────────────────────────────────┘
        ┌──────────────┬──────────────┬──────────────┐
        ▼              ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ user-service │ │item-service  │ │order-service │ │  公共模块     │
│   用户服务    │ │   商品服务    │ │   订单服务    │ │   common     │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └──────────────┘
       │                │                │
       ▼                ▼                ▼
┌──────────────────────────────────────────────────────────────────┐
│                        基础设施层                                │
│  MySQL ── Redis ── RabbitMQ ── Elasticsearch ── Sentinel        │
└──────────────────────────────────────────────────────────────────┘
```

### 2.2 模块结构

```
tz-mall/
├── tz-mall-api/                       # API接口定义模块
│   ├── api-user/                      # 用户API（Feign客户端、DTO）
│   ├── api-item/                      # 商品API
│   └── api-order/                     # 订单API
│
├── tz-mall-common/                    # 公共模块
│   ├── common-core/                   # 核心工具类（Result、异常处理）
│   ├── common-redis/                  # Redis配置
│   ├── common-mq/                     # RabbitMQ配置
│   ├── common-es/                     # Elasticsearch配置
│   └── common-swagger/                # Knife4j配置
│
├── tz-mall-gateway/                   # 网关服务
│
├── tz-mall-services/                  # 业务服务
│   ├── user-service/                  # 用户服务
│   ├── item-service/                  # 商品服务
│   └── order-service/                 # 订单服务
│
└── pom.xml                            # 父工程
```

---

## 三、技术栈

### 3.1 版本清单

| 分类 | 组件 | 版本 | 说明 |
|------|------|------|------|
| **基础框架** | Spring Boot | 3.2.5 | 基础框架 |
| | Spring Cloud | 2023.0.3 | 微服务框架 |
| | Spring Cloud Alibaba | 2023.0.1.0 | 阿里巴巴微服务套件 |
| **注册/配置中心** | Nacos | 2.3.x | 服务注册发现、配置管理 |
| **网关** | Spring Cloud Gateway | 4.1.x | 路由、鉴权、限流 |
| **服务调用** | OpenFeign | 4.1.x | 声明式HTTP客户端 |
| | LoadBalancer | 4.1.x | 客户端负载均衡 |
| **服务保护** | Sentinel | 1.8.8 | 熔断降级、限流 |
| **分布式事务** | Seata | 2.0.x | AT模式分布式事务 |
| **消息队列** | RabbitMQ | 3.12.x | 异步消息、延迟队列 |
| **搜索引擎** | Elasticsearch | 8.12.x | 商品全文搜索 |
| **缓存** | Redis | 7.x | 缓存、分布式锁 |
| **数据库** | MySQL | 8.0.x | 数据持久化 |
| **ORM框架** | MyBatis-Plus | 3.5.5 | 简化SQL操作 |
| **连接池** | HikariCP | 内置 | 数据库连接池 |
| **API文档** | Knife4j | 4.5.x | Swagger增强版 |
| **工具库** | Hutool | 5.8.x | Java工具类库 |
| | Lombok | 内置 | 简化代码 |
| **容器化** | Docker | 最新 | 中间件部署 |
| | Docker Compose | 最新 | 容器编排 |

### 3.2 版本兼容性说明

Spring Boot 3.x 要求 JDK 17+，当前项目使用 JDK 21。

Spring Cloud Alibaba 2023.0.1.0 与 Spring Boot 3.2.x 完全兼容。

---

## 四、环境配置

### 4.1 部署规划

| 组件 | 部署位置 | 端口 | 说明 |
|------|----------|------|------|
| **Nacos** | Linux Docker | 8848 | 注册/配置中心 |
| **MySQL** | Windows | 3306 | 已安装，需创建数据库 |
| **Redis** | Linux | 6379 | 已安装 |
| **RabbitMQ** | Linux Docker | 5672/15672 | 消息队列 |
| **Elasticsearch** | Linux Docker | 9200 | 搜索引擎 |
| **Sentinel Dashboard** | Linux Docker | 8080 | 控制台 |

### 4.2 Docker Compose 配置

```yaml
# docker-compose.yml
version: '3.8'

services:
  # Nacos 注册配置中心
  nacos:
    image: nacos/nacos-server:v2.3.2
    container_name: nacos
    environment:
      - MODE=standalone
      - PREFER_HOST_MODE=hostname
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=<Windows_IP>
      - MYSQL_SERVICE_PORT=3306
      - MYSQL_SERVICE_DB_NAME=nacos_config
      - MYSQL_SERVICE_USER=root
      - MYSQL_SERVICE_PASSWORD=<password>
      - MYSQL_SERVICE_DB_PARAM=characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false&serverTimezone=Asia/Shanghai
    ports:
      - "8848:8848"
      - "9848:9848"
      - "9849:9849"
    restart: always

  # RabbitMQ 消息队列
  rabbitmq:
    image: rabbitmq:3.12-management
    container_name: rabbitmq
    environment:
      - RABBITMQ_DEFAULT_USER=admin
      - RABBITMQ_DEFAULT_PASS=admin123
    ports:
      - "5672:5672"
      - "15672:15672"
    restart: always

  # Elasticsearch 搜索引擎
  elasticsearch:
    image: elasticsearch:8.12.2
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
      - "9300:9300"
    restart: always

  # Sentinel 控制台
  sentinel:
    image: bladex/sentinel-dashboard:1.8.8
    container_name: sentinel
    ports:
      - "8080:8080"
    restart: always
```

### 4.3 数据库设计

```sql
-- Nacos配置库
CREATE DATABASE nacos_config CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 商城业务库
CREATE DATABASE tz_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE `item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `image` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `description` text COMMENT '商品描述',
  `status` tinyint DEFAULT '1' COMMENT '状态 1-上架 0-下架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `status` tinyint DEFAULT '0' COMMENT '状态 0-待支付 1-已支付 2-已发货 3-已完成 4-已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE `order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `item_id` bigint NOT NULL,
  `item_name` varchar(200) NOT NULL,
  `item_price` decimal(10,2) NOT NULL,
  `num` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
```

---

## 五、核心功能设计

### 5.1 各服务功能清单

| 服务 | 功能 | 涉及技术点 |
|------|------|------------|
| **user-service** | 用户注册、登录、信息查询 | JWT认证、Redis缓存Token |
| **item-service** | 商品CRUD、商品搜索、库存扣减 | ES全文搜索、Redis缓存、Sentinel热点参数限流 |
| **order-service** | 创建订单、订单查询、订单超时取消 | Seata分布式事务、RabbitMQ延迟队列 |

### 5.2 技术亮点场景

| 场景 | 技术实现 | 面试亮点 |
|------|----------|----------|
| 商品搜索 | Elasticsearch | 全文搜索、倒排索引、高亮显示 |
| 商品详情缓存 | Redis | 缓存穿透/击穿/雪崩解决方案 |
| 订单超时取消 | RabbitMQ延迟队列 | 死信队列、TTL、消息可靠性 |
| 下单扣库存 | Seata AT模式 | 分布式事务、两阶段提交 |
| 热点商品限流 | Sentinel | 热点参数限流、熔断降级 |
| 服务调用 | OpenFeign | 声明式调用、负载均衡 |

---

## 六、实施计划

### 6.1 分阶段实施

| 阶段 | 内容 | 预计时间 |
|------|------|----------|
| **第一阶段** | 基础框架搭建（Nacos + Gateway + Feign + MySQL） | 2-3天 |
| **第二阶段** | Redis缓存集成 | 1-2天 |
| **第三阶段** | RabbitMQ消息队列（订单超时取消） | 2-3天 |
| **第四阶段** | Elasticsearch商品搜索 | 2-3天 |
| **第五阶段** | Sentinel服务保护 + Seata分布式事务 | 2-3天 |

### 6.2 里程碑

- [ ] 环境搭建完成（Docker中间件启动成功）
- [ ] 基础框架搭建完成（三个服务可正常调用）
- [ ] Redis缓存功能完成
- [ ] RabbitMQ延迟队列功能完成
- [ ] ES搜索功能完成
- [ ] 服务保护与分布式事务完成
- [ ] 项目文档完善

---

## 七、注意事项

1. **版本兼容性**：Spring Boot 3.x 与 Spring Cloud Alibaba 版本必须匹配
2. **网络配置**：Linux虚拟机与Windows网络需互通，注意防火墙设置
3. **资源分配**：ES较耗内存，建议虚拟机至少4G内存
4. **学习顺序**：建议按阶段顺序实施，逐步深入

---

## 八、参考资源

- [Spring Cloud Alibaba 官方文档](https://sca.aliyun.com/)
- [Nacos 官方文档](https://nacos.io/zh-cn/)
- [RabbitMQ 官方文档](https://www.rabbitmq.com/)
- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [黑马商城学习笔记](https://blog.csdn.net/qq_64225133/article/details/138930848)
