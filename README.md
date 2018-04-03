# scdemo

注意：
	. 负载均衡注册服务名最好与项目一样，否则出现模板请求时报空指针异常；

常用启动命令
	. 指定配置文件，配置文件名规则application-xxx.properties
		--spring.profiles.active=xxx 
	. 指定端口号，覆盖配置文件中的端口号设置
		--server.port=xxx 