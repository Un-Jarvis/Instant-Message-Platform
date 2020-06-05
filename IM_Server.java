import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IM_Server {

	private static final int PORT = 9072;


	public static void main(String[] args) throws Exception {

		// Client database
		ClientDatabase clientDB = new ClientDatabase();

		// The set of all the printWriter for all the clients
		Map<String, PrintWriter> broadcastList = new HashMap<String, PrintWriter>();

		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("The server is running.");
		
		try {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ClientThread thread = new ClientThread(clientSocket, clientDB, broadcastList);
				thread.start();
			}
		} finally {
			serverSocket.close();
		}
	}
}