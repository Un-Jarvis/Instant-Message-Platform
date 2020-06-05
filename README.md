# Multi-User Instant Message Platform Project

CSE 3461
Multi-User Instant Message Platform Project
Jarvis Huang
Oct 16, 2018


Project description:
The project is an application of multi-user instant message platform. It provides both private message from one user to another user and broadcast message sending to all online users. 

The user connects to the server by entering the server IP address. The user can either create a new account or login using a existing username and password. After logging into the application, user can send message to and receive message from other online users. The application displays all messages, and a list of online users which gets updated every time when a client logs out and when a new client logs in. There are three button in the application: "Send" to send a new message, "Clear" to clear the input field (it does not clear the output field), "Exit" to log out. The close button of the application works the same as the "Exit" button. The user can choose receiver by selecting an option from the drop-down combo-box. By selecting "All users", the user sends message to all other users. (Therefore, user whose username is "All users" may cause some error.)

Server registers each user with a unique username and password. When the server is built, the client database inside the server will read existing client information from client_database.txt if it exists, and create a client_database.txt if it does not exist. Every time when a new account is created, the database will write the username and password of the new account to the client_database.txt. The database will not overwrite the existing content in client_database.txt.


The project contains following file:
 * IM_Server.java
 * * The server file builds a server and deals with multiple user connections.

 * IM_Client.java
 * * The client file runs a client application.

 * ClientThread.java
 * * The class deals with a single client thread.

 * ClientDatabase.java
 * * The class represents a client database. It can read existing client information from client_database.txt when it is initialized, and can write new client information to client_database.txt once a client signs up a new account without overwriting existing client information in client_database.txt.

 * View.java
 * * It is the interface for ClientView.java.

 * ClientView.java
 * * The class provides the graphical user interface for the client side.

 * readme.txt
 * * It contains description of the project, a list of files in the project with a brief description of the purpose for each file, and instructions on how to compile and execute the program.

 * (client_database.txt)
 * * This file is not included in the project, but can be created automatically by the server side if it does not exist. It contains all client usernames and passwords. 

How to compile and execute the program:
 1. Open the terminal and go to the project directory.
 2. Compile server and client files by entering following commands:
	 $ javac IM_Server.java
	 $ javac IM_Client.java
 3. Run a server by entering:
	 $ java IM_Server &
    The terminal will show "The server is running." if the server runs successfully.
 4. Run a client application by entering: 
	 $ java IM_Client &
    The client application GUI will show up.

Note:
 1. The project was written and tested on Mac OS. I am not sure whether it also works on other operation systems.
 2. Username cannot be "All users", as explained in project description.
