import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IM_Client {

	private static final int PORT = 9072;

	private BufferedReader in;
	private PrintWriter out;

	private String username;
	private String password;

	private ClientView view;

	public IM_Client() {
		// GUI
		this.view = new ClientView();
		this.view.registerObserver(this);

		// Personal Info
		this.username = "";
		this.password = "";

		// Initialize input and output streams
		this.in = null;
		this.out = null;
	}

	public PrintWriter getOutputStream() {
		return this.out;
	}
	
	public BufferedReader getInputStream() {
		return this.in;
	}

	public void ChangePassword(String newPassword) {
		this.password = newPassword;
	}

	private void signUpOrLogin() {
		int flag = this.view.signupOrLoginView();
		if (flag == 0) {
			this.signUp();
		} else if (flag == 1) {
			this.login();
		}
	}

	private void signUp() {
		String[] usernamePassword = this.view.signUpView();
		this.username = usernamePassword[0];
		this.password = usernamePassword[1];
		while (this.username.equals("") || this.password.equals("") || !this.password.equals(usernamePassword[2])) {
			// Pop-up message
			if (this.username.equals("")) {
				this.view.invalidUsername();
			} else if (this.password.equals("")) {
				this.view.invalidPassword();
			} else if (!this.password.equals(usernamePassword[2])) {
				this.view.passwordNotMatchError();
			}

			usernamePassword = this.view.signUpView();
			this.username = usernamePassword[0];
			this.password = usernamePassword[1];
		}

		/*
		 * Send username and password to the server so that the server can add
		 * the client to the database.
		 */
		this.out.println("SIGN_UP");
	}

	private void login() {
		String[] usernamePassword = this.view.loginView();
		this.username = usernamePassword[0];
		this.password = usernamePassword[1];

		/*
		 * Send username and password to the server so that the server can check
		 * client's information in the database.
		 */
		this.out.println("LOGIN");
	}

	public void logout() {
		this.out.println("LOGOUT");
	}

	private void run() throws IOException {
		// Make connection
//		String serverAddress = this.view.getServerAddress();
//		while (serverAddress.equals("")) {
//			this.view.invalidIPAddress();
//			serverAddress = this.view.getServerAddress();
//		}
		Socket socket = new Socket("192.168.1.9", PORT);

		try {
			// Initialize streams
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(), true);

			// New window
			this.signUpOrLogin();

			// Process all messages from server, according to the protocol
			while (true) {
				String line = this.in.readLine();
				if (line.startsWith("SIGNUP_SUCCESS")) {
					// Show pop-up message
					this.view.signUpSuccess();

					this.signUpOrLogin();
				} else if (line.startsWith("SIGNUP_FAIL")) {
					// Show pop-up message
					view.signUpFailError();

					this.signUpOrLogin();
				} else if (line.startsWith("LOGIN_SUCCESS")) {
					// Show pop-up message
					this.view.loginSuccess();

					this.view.showApp();
					
					// Get online user list
					this.out.println("LIST_REQUEST");
				} else if (line.startsWith("ALREADY_LOGGED_IN")) {
					// Show pop-up message
					this.view.alreadyLoggedInError();
					
					this.signUpOrLogin();
				} else if (line.startsWith("LOGIN_FAIL")) {
					// Show pop-up message
					this.view.loginFailError();

					this.signUpOrLogin();
				} else if (line.startsWith("ENTER_USERNAME")) {
					this.out.println(this.username);
				} else if (line.startsWith("ENTER_PASSWORD")) {
					this.out.println(this.password);
				} else if (line.startsWith("MESSAGE")) {
					this.view.updateChat();
				} else if (line.startsWith("UPDATE")) {
					int num = Integer.parseInt(this.in.readLine());
					String[] onlineUsers = new String[num];
					for (int i = 0; i < num; i++) {
						onlineUsers[i] = this.in.readLine();
					}
					this.view.updateList(onlineUsers, this.username);
				}
			}
		} catch (IOException e) {
		} finally {
			socket.close();
		}
	}

	public static void main(String[] args) throws Exception {
		IM_Client client = new IM_Client();
		client.run();
	}
}