import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientThread extends Thread {

	private final Socket clientSocket;

	private ClientDatabase database;
	private Map<String, PrintWriter> broadcastList;

	private BufferedReader in;
	private PrintWriter out;

	private String username;

	public ClientThread(Socket socket, ClientDatabase clientDatabase, Map<String, PrintWriter> broadcastList)
			throws IOException {
		this.clientSocket = socket;
		this.database = clientDatabase;
		this.broadcastList = broadcastList;

		// Initialize streams
		this.in = null;
		this.out = null;
	}

	private void signUp() throws IOException {
		// Request username
		this.out.println("ENTER_USERNAME");
		this.username = in.readLine();

		if (!this.database.usernameExists(this.username)) {
			// Request password
			out.println("ENTER_PASSWORD");
			String password = in.readLine();

			// Create a new account for the new client
			this.database.register(this.username, password);

			this.out.println("SIGNUP_SUCCESS");
		} else {
			this.out.println("SIGNUP_FAIL");
		}
	}

	private void login() throws IOException {
		// Request username
		this.out.println("ENTER_USERNAME");
		this.username = this.in.readLine();

		// Request password
		this.out.println("ENTER_PASSWORD");
		String password = this.in.readLine();

		// Check whether the information provided by the client matches the one
		// in the database
		if (this.database.usernameExists(this.username) && !this.database.isOnline(this.username)
				&& this.database.getPassword(this.username).equals(password)) {
			this.database.setOnline(username, true);
			this.out.println("LOGIN_SUCCESS");
		} else if (this.database.isOnline(this.username)) {
			this.out.println("ALREADY_LOGGED_IN");
		} else {
			this.out.println("LOGIN_FAIL");
		}
	}

	private void updateOnlineUserList(PrintWriter writer) throws IOException {
		List<String> list = this.database.getAllOnlineUsers();
		writer.println("UPDATE");
		writer.println(list.size());
		for (String s : list) {
			writer.println(s);
		}
	}

	private void handleClient() throws InterruptedException, SocketException {
		try {
			// Initialize streams
			this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);

			// Process all messages from client, according to the protocol
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				} else if (line.startsWith("SIGN_UP")) {
					this.signUp();
				} else if (line.startsWith("LOGIN")) {
					this.login();
					this.broadcastList.put(this.username, this.out);
					// Update online users for all client
					for (Map.Entry<String, PrintWriter> user : this.broadcastList.entrySet()) {
						this.updateOnlineUserList(user.getValue());
					}
				} else if (line.startsWith("MSG_TO")) {
					String receiverName = in.readLine();
					String message = in.readLine();
					if (receiverName == null) {
						break;
					} else if (receiverName.equals("All users")) {
						for (Map.Entry<String, PrintWriter> user : this.broadcastList.entrySet()) {
							user.getValue().println("MESSAGE");
							user.getValue().println(this.username);
							user.getValue().println(message);
						}
					} else {
						for (Map.Entry<String, PrintWriter> user : this.broadcastList.entrySet()) {
							if (user.getKey().equals(receiverName)) {
								user.getValue().println("MESSAGE");
								user.getValue().println("Private Message From [" + this.username + "]");
								user.getValue().println(message);
							}
							if (user.getKey().equals(this.username)) {
								user.getValue().println("MESSAGE");
								user.getValue().println("Private Message To [" + receiverName + "]");
								user.getValue().println(message);
							}
						}
					}
				} else if (line.startsWith("LIST_REQUEST")) {
					this.updateOnlineUserList(this.out);
				} else if (line.startsWith("LOGOUT")) {
					this.database.setOnline(this.username, false);
					this.broadcastList.remove(this.username, this.out);
					// Update online users for all client
					for (Map.Entry<String, PrintWriter> user : this.broadcastList.entrySet()) {
						this.updateOnlineUserList(user.getValue());
					}
					break;
				}
			}
		} catch (IOException e) {
		} finally {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			this.handleClient();
		} catch (InterruptedException e) {
		} catch (SocketException e) {
		}
	}
}