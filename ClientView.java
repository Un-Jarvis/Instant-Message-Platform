import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;

public class ClientView extends JFrame implements View {

	/**
	 * Client object registered with this view to observe events.
	 */
	private IM_Client client;

	/**
	 * Input and output field.
	 */
	private final JTextArea inputField, outputField;

	/**
	 * Operator buttons.
	 */
	private final JButton bEnter, bClear, bExit;

	/**
	 * List of online users.
	 */
	private JTextArea onlineList;

	private JComboBox<String> receiverSelection;

	/**
	 * Default constructor.
	 */
	public ClientView() {
		/*
		 * Call the JFrame (superclass) constructor with a String parameter to
		 * name the window in its title bar
		 */
		super("Instant Message Platform");

		// Set up the GUI widgets

		this.inputField = new JTextArea("", 5, 20);
		this.outputField = new JTextArea("", 15, 25);

		this.bEnter = new JButton("Enter");
		this.bClear = new JButton("Clear");
		this.bExit = new JButton("Exit");

		this.onlineList = new JTextArea("", 20, 10);

		this.receiverSelection = new JComboBox<String>();

		/*
		 * Input field are initialized to be not editable
		 */
		this.inputField.setEditable(false);
		/*
		 * Output field should wrap lines, and should be read-only
		 */
		this.outputField.setEditable(false);
		this.outputField.setLineWrap(true);
		/*
		 * Online user list should be read-only
		 */
		this.onlineList.setEditable(false);

		/*
		 * Create scroll panes
		 */
		JScrollPane inputScrollPane = new JScrollPane(this.inputField);
		JScrollPane outputScrollPane = new JScrollPane(this.outputField);
		JScrollPane onlineListScrollPane = new JScrollPane(this.onlineList);

		/*
		 * Set borders for scroll panes
		 */
		outputScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5),
				BorderFactory.createEtchedBorder()));
		outputScrollPane.setBackground(null);
		inputScrollPane.setBorder(BorderFactory.createEtchedBorder());
		onlineListScrollPane.setBorder(BorderFactory.createEtchedBorder());

		/*
		 * Buttons are initialized to be disabled
		 */
		this.bEnter.setEnabled(false);
		this.bClear.setEnabled(false);
		this.bExit.setEnabled(false);

		/*
		 * Button panel
		 */
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
		buttonPanel.add(this.bEnter);
		buttonPanel.add(this.bClear);
		buttonPanel.add(this.bExit);

		/*
		 * Receiver panel
		 */
		JPanel receiverPanel = new JPanel();
		receiverPanel.setLayout(new BoxLayout(receiverPanel, BoxLayout.X_AXIS));
		JLabel sentToLabel = new JLabel("Message Sending To: ", JLabel.CENTER);
		sentToLabel.setFont(sentToLabel.getFont().deriveFont(Font.BOLD));
		receiverPanel.add(sentToLabel);
		receiverPanel.add(this.receiverSelection);
		receiverPanel.setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 5));

		/*
		 * Input panel
		 */
		JPanel inputPanel = new JPanel(new FlowLayout());
		inputPanel.add(inputScrollPane);
		inputPanel.add(buttonPanel);

		/*
		 * Set up input/output window
		 */
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new BoxLayout(ioPanel, BoxLayout.Y_AXIS));
		ioPanel.add(outputScrollPane);
		ioPanel.add(receiverPanel);
		ioPanel.add(inputPanel);

		/*
		 * Online user list
		 */
		JPanel onlineListPanel = new JPanel();
		onlineListPanel.setLayout(new BoxLayout(onlineListPanel, BoxLayout.Y_AXIS));
		JLabel onlineListLabel = new JLabel("Online Users", JLabel.CENTER);
		onlineListLabel.setFont(onlineListLabel.getFont().deriveFont(Font.BOLD));
		onlineListPanel.add(onlineListLabel);
		onlineListPanel.add(onlineListScrollPane);
		onlineListPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));

		// Set up the observers

		/*
		 * Register this object as the observer for all GUI events
		 */
		this.bEnter.addActionListener(this);
		this.bClear.addActionListener(this);
		this.bExit.addActionListener(this);

		this.inputField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
					bEnter.doClick();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		// Set up the main application window

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		this.add(ioPanel);
		this.add(onlineListPanel);
		this.pack();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (logoutView() == 0) {
					client.logout();
					System.exit(0);
				}
			}
		});
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(false);
	}

	/**
	 * Prompt for and return the address of the server.
	 */
	public String getServerAddress() {
		return JOptionPane.showInputDialog(this, "Please enter IP address of the server: ", "Server IP Address",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Prompt for sign-up or log-in.
	 */
	public int signupOrLoginView() {
		String[] options = { "New User", "Old User" };
		return JOptionPane.showOptionDialog(this, "", "New/Old User?", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
	}

	/**
	 * Prompt for and return the username and password.
	 */
	public String[] signUpView() {
		String[] usernamePassword = new String[3];
		JTextField username = new JTextField();
		JTextField password = new JPasswordField();
		JTextField passwordComfirmed = new JPasswordField();
		Object[] message = { "Username:", username, "Password:", password, "Comfirm Password:", passwordComfirmed };

		int option = JOptionPane.showConfirmDialog(this, message, "Sign Up", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.OK_OPTION) {
			usernamePassword[0] = username.getText();
			usernamePassword[1] = password.getText();
			usernamePassword[2] = passwordComfirmed.getText();
		} else {
			System.exit(0);
		}

		return usernamePassword;
	}

	/**
	 * Prompt for and return the username and password.
	 */
	public String[] loginView() {
		String[] usernamePassword = new String[2];
		JTextField username = new JTextField();
		JTextField password = new JPasswordField();
		Object[] message = { "Username:", username, "Password:", password };

		int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.OK_OPTION) {
			usernamePassword[0] = username.getText();
			usernamePassword[1] = password.getText();
		} else {
			System.exit(0);
		}

		return usernamePassword;
	}

	public void showApp() {
		this.setVisible(true);
		this.inputField.setEditable(true);
		this.bEnter.setEnabled(true);
		this.bClear.setEnabled(true);
		this.bExit.setEnabled(true);
	}

	public void updateChat() throws IOException {
		String username = this.client.getInputStream().readLine();
		this.outputField.append(username + ": \n");
		String message = this.client.getInputStream().readLine();
		this.outputField.append(message + "\n\n");
	}

	public void passwordNotMatchError() {
		JOptionPane.showConfirmDialog(this, "Password does not match!", "Error!", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	public void alreadyLoggedInError() {
		JOptionPane.showConfirmDialog(this, "Login failed! The user has already logged in.", "Error!",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	public void loginFailError() {
		JOptionPane.showConfirmDialog(this, "Login failed! Please check username and password.", "Error!",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	public void signUpFailError() {
		JOptionPane.showConfirmDialog(this, "Username already existed!", "Error!", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	public void signUpSuccess() {
		JOptionPane.showConfirmDialog(this, "Success!", "Success!", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	public void invalidIPAddress() {
		JOptionPane.showConfirmDialog(this, "IP address not valid!", "Error!", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	public void invalidUsername() {
		JOptionPane.showConfirmDialog(this, "Invalid Username!", "Error!", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	public void invalidPassword() {
		JOptionPane.showConfirmDialog(this, "Password cannot be empty!", "Error!", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	public void loginSuccess() {
		this.setVisible(true);
		JOptionPane.showConfirmDialog(this, "Welcome to the multi-user instant message platform!", "Welcome!",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	public int logoutView() {
		return JOptionPane.showConfirmDialog(this, "Are you sure to exit the current session?", "Logout",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	public void updateList(String[] strList, String selfName) {
		this.onlineList.setText("");
		this.receiverSelection.removeAllItems();
		this.receiverSelection.addItem("All users");
		for (int i = 0; i < strList.length; i++) {
			this.onlineList.append("  " + strList[i] + "\n");
			if (!strList[i].equals(selfName)) {
				this.receiverSelection.addItem(strList[i]);
			}
		}
	}

	@Override
	public void registerObserver(IM_Client c) {
		this.client = c;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == this.bEnter) {
			String receiver = this.receiverSelection.getSelectedItem().toString();
			this.client.getOutputStream().println("MSG_TO");
			this.client.getOutputStream().println(receiver);
			this.client.getOutputStream().println(inputField.getText());
			inputField.setText("");
		} else if (source == this.bClear) {
			inputField.setText("");
		} else if (source == this.bExit) {
			if (this.logoutView() == 0) {
				this.client.logout();
				System.exit(0);
			}
		}
	}
}