import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ChatClient extends Frame {
	public Socket s = null;
	public DataInputStream dis = null;
	public DataOutputStream dos = null;
	public boolean connected = false;
	
	TextArea ta = new TextArea();
	TextField tf = new TextField();
	
	public void launchFrame() {
		this.setLocation(300, 200);
		this.setSize(200, 400);
		this.add(ta, BorderLayout.NORTH);
		this.add(tf, BorderLayout.SOUTH);
		this.pack();
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				disConnect();
				System.exit(0);
			}
			
		});
		
		tf.addActionListener(new TFListener());
		this.setVisible(true);
		connect();
		new Thread(new RecvThread()).start();
	}
	
	public void disConnect() {
		try {
			dos.close();
			dis.close();
			s.close();
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		try {
			s = new Socket("127.0.0.1", ChatServer.PORT);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			connected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class RecvThread implements Runnable {

		@Override
		public void run() {
			try {
				while(connected) {
					String str = dis.readUTF();
					ta.setText(ta.getText() + "\n" + str);
				}
				
			} catch(SocketException e) {
				System.out.println("ÍË³ö bye");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	class TFListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String txt = tf.getText().trim();
			tf.setText("");
			try {
				dos.writeUTF(txt);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		}
		
	}
	
	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}
}
