import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The database in which stores all client information, with related functions.
 * 
 * @author Jarvis Huang
 * 
 */
public final class ClientDatabase {

	private class Client {
		public String username;
		public String password;

		public boolean online;

		/**
		 * Public constructor for Client class.
		 */
		public Client(String username, String password) {
			this.username = username;
			this.password = password;

			this.online = false;
		}
	}

	/**
	 * The database in which stores all client information. A Map is used as the
	 * representation of the database where key is the user-name and value is
	 * the password.
	 */
	private Set<Client> database;

	private static final String DATABASE_FILE_NAME = "client_database.txt";

	/**
	 * Public constructor.
	 */
	public ClientDatabase() {
		this.database = new HashSet<Client>();

		File file = new File(DATABASE_FILE_NAME);

		// Read from existed database file
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE_NAME));
				String username = reader.readLine();
				while (username != null) {
					String password = reader.readLine();
					this.database.add(new Client(username, password));
					reader.readLine();
					username = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Allow user to create a new account with a unique user name and a
	 * password.
	 * 
	 * @param username
	 *            the user name for the new account to be created
	 * @param password
	 *            the password for the new account to be created
	 * @require username is not null and does not exist in the current database
	 */
	public void register(String username, String password) {
		assert username != null : "Violation: username is not null";

		this.database.add(new Client(username, password));

		// Write new client information to the databases file
		try {
			BufferedWriter databaseWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(DATABASE_FILE_NAME, true)));
			databaseWriter.write(username + "\n" + password + "\n\n");
			databaseWriter.close();
		} catch (IOException e) {
		}
	}

	public boolean usernameExists(String username) {
		boolean exist = false;
		for (Client c : this.database) {
			if (c.username.equals(username)) {
				exist = true;
			}
		}
		return exist;
	}

	public String getPassword(String s) {
		String password = "";
		for (Client c : this.database) {
			if (c.username.equals(s)) {
				password = c.password;
			}
		}
		return password;
	}

	public List<String> getAllOnlineUsers() {
		List<String> list = new ArrayList<String>();
		for (Client c : this.database) {
			if (c.online) {
				list.add(c.username);
			}
		}
		return list;
	}

	public boolean isOnline(String username) {
		boolean isOnline = false;
		for (Client c : this.database) {
			if (c.username.equals(username)) {
				isOnline = c.online;
			}
		}
		return isOnline;
	}

	public void setOnline(String username, boolean online) {
		for (Client c : this.database) {
			if (c.username.equals(username)) {
				c.online = online;
			}
		}
	}
}
