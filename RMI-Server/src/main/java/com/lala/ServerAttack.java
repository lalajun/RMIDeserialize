package com.lala;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class ServerAttack {



    public static class UserImpl extends UnicastRemoteObject implements User{

        protected UserImpl() throws RemoteException{
            super();
        }
        public String name(String name) throws RemoteException{
            return name;
        }
//        public void say(String say) throws  RemoteException{
//            System.out.println("you speak" + say);
//        }
//        public void say(Integer say) throws  RemoteException{
//            System.out.println("you speak" + say);
//        }
//        public void say(int[] say) throws  RemoteException{
//            System.out.println("you speak" + say);
//        }
        public void say(int say) throws  RemoteException{
            System.out.println("you speak" + say);
        }
        public void dowork(Object work) throws  RemoteException{
            System.out.println("your work is " + work);
        }
    }

    public static void main(String[] args) throws Exception{
        String url = "rmi://127.0.0.1:1099/Attack";
        UserImpl user = new UserImpl();
        Naming.bind(url,user);
        System.out.println("rmi bind");
    }
}

