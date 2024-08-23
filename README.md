# IdentityVerification
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FColdeZhang%2FIdentityVerification.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FColdeZhang%2FIdentityVerification?ref=badge_shield)


 外置登录与实名认证（带web后台）

## 🔏 隐私声明

1. 本系统不会收集任何用户的个人信息，包括但不限于：姓名、身份证号、密码、IP地址、设备信息、浏览器信息、操作系统信息、地理位置信息等。
2. 实名认证调用由[苏州云亿互通信息服务有限公司](https://yunyidata.com)提供的身份证实名认证查询校验接口。
3. 本系统不会在后台保存任何身份证信息，仅对身份证号做哈希计算后（无法逆向得到真实身份证号码）存库用于唯一性验证。（可参见源码的[SignWhiteList接口](https://github.com/ColdeZhang/IdentityVerification/blob/main/src/main/java/site/deercloud/identityverification/HttpServer/Api/SignWhiteList.java)设计）
4. 任何使用本系统的个人或组织都有义务保证用户的个人信息安全，不得明文存储用户个人信息或将用户的个人信息泄露给任何第三方。
5. 使用本系统请确保从本项目的Release页面下载并校验md5或自行拉取源码编译，不要下载任何第三方修改的版本，否则可能会造成信息泄露。

## 🧾 功能介绍 （TODO List）

### 系统功能部分

- [x] 非白名单拦截
- [x] 黑名单（封禁）拦截
- [x] 活跃度计算

### 外置登录服务部分

- [x] 用户登录
- [x] 刷新令牌
- [x] 验证令牌
- [x] 吊销令牌
- [x] 客户端进入服务器
- [x] 服务端验证客户端
- [x] 查询角色属性
- [x] 按名称批量查询
- [ ] 材质（皮肤）上传/删除
- [ ] 多角色支持

### Web服务部分

- [x] 用户注册
- [x] 邀请码生成、管理
- [x] 邮箱验证码
- [ ] 实名认证白名单
- [ ] 用户管理
- [ ] 用户登录
- [ ] 密码修改
- [ ] 用户信息修改
- [ ] 材质（皮肤）上传/删除
- [ ] 多角色创建删除



## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FColdeZhang%2FIdentityVerification.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FColdeZhang%2FIdentityVerification?ref=badge_large)