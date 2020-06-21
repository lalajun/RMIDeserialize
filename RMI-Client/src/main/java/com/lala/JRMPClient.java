package com.lala;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JRMPClient {
    public static void main(String[] args) throws Exception{
        Registry registry = LocateRegistry.getRegistry(1199);
        registry.lookup("hello");
    }
}