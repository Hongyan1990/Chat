import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	public static final int PORT = 8888;
	public List<Client> clients = new ArrayList<Client>();
	
	
	public void start() {
		try {
			ServerSocket ss = new ServerSocket(PORT);
			while(true) {
				Socket s = ss.accept();
				Client c = new Client(s);
				this.clients.add(c);
				new Thread(c).start();
				System.out.println("connected Server!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class Client implements Runnable {
		public Socket s = null;
		public DataInputStream dis = null;
		public DataOutputStream dos = null;
		public boolean connected = false;
		
		public Client(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				this.connected = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch(SocketException e) {
				System.out.println("client closed");
			}  catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(connected) {
					String str;
					str = dis.readUTF();
					System.out.println(str);
					for(int i=0; i<clients.size(); i++) {
						Client c = clients.get(i);
						c.send(str);
					}
				}
				
			}catch (EOFException e) {
				System.out.println("client closed");
			}  catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(dis != null)
					try {
						dis.close();
						if(s != null) s.close();
						connected = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		}
		
	}
	
	public static void main(String[] args) {
		new ChatServer().start();
	}
}
