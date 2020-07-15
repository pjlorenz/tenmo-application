package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AccountCredentials;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.TransferDTO;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.TransferServiceException;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.view.ConsoleService;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN,
			MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS,
			MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS,
			MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	private AccountService accountService;
	private TransferService transferService;
	private UserService userService;

	public static void main(String[] args) throws AuthenticationServiceException {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL),
				new AccountService(API_BASE_URL), new TransferService(API_BASE_URL), new UserService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService,
			TransferService transferService, UserService userService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.transferService = transferService;
		this.userService = userService;
	}

	public void run() throws AuthenticationServiceException {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() throws AuthenticationServiceException {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		BigDecimal balance;
		try {
			balance = accountService.getAccountBalance(currentUser.getToken());
			System.out.println("Your current balance is " + balance);
		} catch (AuthenticationServiceException e) {
			e.printStackTrace();
			System.out.println("Error retrieving balance.");
		}
	}

	private void viewTransferHistory() {
		System.out.println("-------------------------------------------");
		System.out.println("Transfers");
		System.out.println("ID          From/To                 Amount");
		System.out.println("-------------------------------------------");

		Transfer[] usersTransferList;

		boolean doneWithTransfers = false;
		while (!doneWithTransfers) {
			usersTransferList = transferService.getArrayOfUsersTransfers(currentUser.getUser().getId(),
					currentUser.getToken());
			accountService.AUTH_TOKEN = currentUser.getToken();

			for (int i = 0; i < usersTransferList.length; i++) {
				int transferId2 = usersTransferList[i].getTransfer_id();
				BigDecimal amount = usersTransferList[i].getAmount();
				int accountIdFrom = usersTransferList[i].getAccount_from();
				int accountIdTo = usersTransferList[i].getAccount_to();

				Account accountFrom = accountService.getAccountFromAccountId(accountIdFrom, currentUser.getToken());
				String usernameFrom = userService.getUsernameFromId(accountFrom.getUserId(), currentUser.getToken());
				Account accountTo = accountService.getAccountFromAccountId(accountIdTo, currentUser.getToken());
				String usernameTo = userService.getUsernameFromId(accountTo.getUserId(), currentUser.getToken());

				if (accountFrom.getUserId() == currentUser.getUser().getId()) {
					System.out.println(transferId2 + "          To: " + usernameTo + "             $ " + amount);
				} else if (accountTo.getUserId() == currentUser.getUser().getId()) {
					System.out.println(transferId2 + "          From: " + usernameFrom + "             $ " + amount);
				}
			}
			System.out.println("");
			int transferId = console.getUserInputInteger("Enter ID of transfer you would like info on (0 to cancel): ");
			if (transferId == 0) {
				doneWithTransfers = true;
				break;
			}
			boolean transferIdIsValid = false;
			for (int i = 0; i < usersTransferList.length; i++) {
			if (transferId == usersTransferList[i].getTransfer_id()) {
				transferIdIsValid = true;
			}
			}
			
			if (transferIdIsValid == false) {
				System.out.println("You need to input an integer that is one of your past transfer id's. Please try again.");
				break;
			}
			
			System.out.println("--------------------------------------------");
			System.out.println("Transfer Details");
			System.out.println("--------------------------------------------");
			System.out.println("Id: " + transferId);

			Transfer transfer = transferService.getTransferFromTransferId(transferId, currentUser.getToken());
			Account accountFrom = accountService.getAccountFromAccountId(transfer.getAccount_from(),
					currentUser.getToken());
			int userIdFrom = accountFrom.getUserId();
			String usernameFrom = userService.getUsernameFromId(userIdFrom, currentUser.getToken());

			Account accountTo = accountService.getAccountFromAccountId(transfer.getAccount_to(),
					currentUser.getToken());
			int userIdTo = accountTo.getUserId();
			String usernameTo = userService.getUsernameFromId(userIdTo, currentUser.getToken());

			String transferTypeDescription = "";
			int transferTypeId = transfer.getTransfer_type_id();
			if (transferTypeId == 1) {
				transferTypeDescription = "Request";
			} else if (transferTypeId == 2) {
				transferTypeDescription = "Send";
			}

			String transferStatusDescription = "";
			int transferStatusId = transfer.getTransfer_status_id();
			if (transferStatusId == 1) {
				transferStatusDescription = "Pending";
			} else if (transferStatusId == 2) {
				transferStatusDescription = "Approved";
			} else if (transferStatusId == 3) {
				transferStatusDescription = "Rejected";
			}

			BigDecimal amount = transfer.getAmount();

			System.out.println("From: " + usernameFrom);
			System.out.println("To: " + usernameTo);
			System.out.println("Type: " + transferTypeDescription);
			System.out.println("Status: " + transferStatusDescription);
			System.out.println("Amount: $" + amount);

			doneWithTransfers = true;
		}
	}

	private void viewPendingRequests() {
		System.out.println("This method has not been set up yet. Please choose a new task.");
	}

	private void sendBucks() {
		boolean sentBucks = false;
		while (!sentBucks) {
			System.out.println("-------------------------------------------");
			System.out.println("Users");
			System.out.println("ID                 Name");
			System.out.println("-------------------------------------------");

			User[] usersList;
			try {
				usersList = userService.getArrayOfUsers(currentUser.getToken());

				for (int i = 0; i < usersList.length; i++) {
					int userId = usersList[i].getId();
					String userName = usersList[i].getUsername();
					System.out.println(userId + "             " + userName);
				}
				System.out.println("-------------------------------------------");
				int receivingUserId = console
						.getUserInputInteger("Enter ID of user you are sending to (0 to cancel): ");
				if (receivingUserId == 0) {
					sentBucks = true;
					break;
				}
				String amountToTransferString = console.getUserInput("Enter amount: ");
				Double amountToTransferDouble = (double) 0;
				try {
					amountToTransferDouble = Double.parseDouble(amountToTransferString);
				} catch (Exception e) {
					System.out.println(
							"You need to enter an amount as a positive number with no more than two decimal places. Transfer aborted.");
					break;
				}
				if (amountToTransferDouble <= 0) {
					System.out.println(
							"You need to enter an amount as a positive number with no more than two decimal places. Transfer aborted.");
					break;
				}
				
				BigDecimal amountToTransfer = new BigDecimal(amountToTransferDouble, MathContext.DECIMAL64);
				String receivingUserName = null;
				for (int i = 0; i < usersList.length; i++) {
					if (usersList[i].getId() == receivingUserId) {
						receivingUserName = usersList[i].getUsername();
					}
				}

				accountService.AUTH_TOKEN = currentUser.getToken();
				Integer receivingAccountId = accountService.getAccountIdFromUserName(receivingUserName,
						currentUser.getToken());
				Integer sendingAccountId = accountService.getAccountIdFromUserName(currentUser.getUser().getUsername(),
						currentUser.getToken());

				TransferDTO transferDTO = new TransferDTO(receivingAccountId, sendingAccountId, amountToTransfer, 2, 2);
				transferService.AUTH_TOKEN = currentUser.getToken();
				Account sendingAccount = accountService.getAccountFromUsername(currentUser.getUser().getUsername(),
						currentUser.getToken());
				Account receivingAccount = accountService.getAccountFromUsername(receivingUserName,
						currentUser.getToken());
				BigDecimal initialSendingAccountMoney = accountService
						.getAccountFromUsername(currentUser.getUser().getUsername(), currentUser.getToken())
						.getAccountBalance();
				Transfer transfer = transferService.createSendTransfer(transferDTO, currentUser.getToken());

				BigDecimal newSendingAccountBalance = sendingAccount.getAccountBalance().subtract(amountToTransfer);
				BigDecimal newReceivingAccountBalance = receivingAccount.getAccountBalance().add(amountToTransfer);

				sendingAccount.setAccountBalance(newSendingAccountBalance);
				receivingAccount.setAccountBalance(newReceivingAccountBalance);

				BigDecimal sendingBalance = sendingAccount.getAccountBalance();

				accountService.updateBalance(sendingAccount, currentUser.getToken());
				accountService.updateBalance(receivingAccount, currentUser.getToken());

				sentBucks = true;

				BigDecimal finalSendingAccountMoney = accountService
						.getAccountFromUsername(currentUser.getUser().getUsername(), currentUser.getToken())
						.getAccountBalance();

				if (initialSendingAccountMoney.equals(finalSendingAccountMoney)) {
					System.out.println("Transfer unsuccessful. No money was sent."
							+ " You may be trying to send more money than you currently have.");
					transfer.setTransfer_status_id(3);
					transferService.rejectSendTransfer(transfer.getTransfer_id(), currentUser.getToken());
				} else {
					System.out.println("Transer successful. You have sent the money.");
				}

			} catch (AuthenticationServiceException e) {
				e.printStackTrace();
			}
		}
	}

	private void requestBucks() {
		System.out.println("This method has not been set up yet. Please choose a new task.");
	}

	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) {
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) {
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private AccountCredentials setAccountCredentials(int userId) {
		return new AccountCredentials(userId);
	}
}
