import java.net.*;
import java.io.*;

public class Client {
	public static void main(String args[]){
		System.out.println("hello");
		try {
			Runtime.getRuntime().exec("adb forward tcp:8002 tcp:9000");
		} catch(IOException e){
			e.printStackTrace();
		} 

		try{
			Socket socket = new Socket("127.0.0.1", 8002);
			System.out.println("Connect Success!");
			DataInputStream din = new DataInputStream(socket.getInputStream());
			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
			dout.writeUTF("Tell me the GPS location!");
			dout.flush();
			String serverResponse = din.readUTF();
			System.out.println("Server response: " + serverResponse);
			// dout.close();
			// socket.close();
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
