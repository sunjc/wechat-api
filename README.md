# [Spring Boot集成Spring Security实现GitHub、微信OAuth 2.0登录](https://blog.51cto.com/7308310/2457336)

## 初始配置
1. 登录GitHub，进入Settings -> Developer settings -> OAuth Apps，创建OAuth App；获取Client ID和Client Secret
替换application.yml github配置中的client-id和client-secret。
2. 注册[微信开放平台](https://open.weixin.qq.com/)或[微信公众平台](https://mp.weixin.qq.com/)帐号，进入开发 -> 
基本配置，获取AppID和AppSecret替换application.yml weixin配置中的client-id和client-secret; 根据您使用的域名修改redirect-uri。

## 启动应用
启动WeChatApplication，访问http://yourhost/heroes则会自动跳转到登录页面，登录成功后会输出hero信息。
如使用微信公众平台帐号，则只能在微信客户端或微信开发者工具中访问页面。