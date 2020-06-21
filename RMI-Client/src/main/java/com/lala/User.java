package com.lala;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface User extends Remote {
    public String name(String name) throws RemoteException;
    //服务端是String，故意改成Object看看能不能无中生有
//    public void say(Object say) throws RemoteException;
    //其他参数的实验
    //public void say(Integer say) throws RemoteException;
//    public void say(int[] say) throws RemoteException;
    public void say(int say) throws RemoteException;
    //正确的情况
    //public void say(String say) throws RemoteException;

    public void dowork(Object work) throws RemoteException;
}