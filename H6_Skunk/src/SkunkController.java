import java.util.ArrayList;
import edu.princeton.cs.introcs.*;

public class SkunkController
{
	private static final int TARGET_OF_100_POINTS = 100;
	private static final int STARTING_NUMBER_OF_CHIPS = 50;
	private static final int RESET_SCORE = 0;
	private static final int REGULAR_SKUNK_PENALTY = 1;
	private static final int SKUNK_DEUCE_PENALTY = 2;
	private static final int DOUBLE_SKUNK_PENALTY = 4;
	private static final int SKUNK_CONSTANT = 1;
	private static final int DOUBLE_SKUNK_SUM = 2;
	private static final int SKUNK_DEUCE_SUM = 3;
	
	//**********************************************************
	
	public SkunkUI skunkUI;
	public UI ui;
	public int numberOfPlayers;
	public String[] playerNames;
	public ArrayList<Player> players;
	public int kitty;

	public Player activePlayer;
	public int activePlayerIndex;

	public boolean wantsToQuit;
	public boolean oneMoreRoll;

	public Dice skunkDice;

	
	//**********************************************************
	
	public SkunkController(SkunkUI ui)
	{
		this.skunkUI = ui;
		this.ui = ui; // hide behind the interface UI
		
		this.playerNames = new String[20];
		this.players = new ArrayList<Player>();
		this.skunkDice = new Dice();
		this.wantsToQuit = false;
		this.oneMoreRoll = false;
	}

	//**********************************************************
	
	public boolean run()
	{
		ui.println("Welcome to Skunk 0.47\n");

		//
		//Try, catch code is added to catch the exception if the user entered non numeric input
		//
		try
		{
			this.numberOfPlayers = getNumberOfPlayers();
		}
		catch( NumberFormatException e )
		{
			ui.print("NumberFormatException: Invalid input.\n" );
		}
		
		//
		//getNumberOfPlayers() funtion added to give user chances to fix the wrong player number.
		//
		while( this.numberOfPlayers <= 1 )
		{
			ui.print("Invalid number of players entered: " +  this.numberOfPlayers + " Enter 2 players or higher number to start the game.\n");
			this.numberOfPlayers = getNumberOfPlayers();			
		}

		for (int playerNumber = 0; playerNumber < numberOfPlayers; playerNumber++)
		{
			ui.print("Enter name of player " + (playerNumber + 1) + ": ");
			playerNames[playerNumber] = StdIn.readLine();
		
			this.players.add( new Player( STARTING_NUMBER_OF_CHIPS ));
		}
		
		activePlayerIndex = 0;
		activePlayer = players.get(activePlayerIndex);

		ui.println("Starting game...\n");
		boolean gameNotOver = true;

		while (gameNotOver)
		{
			ui.println("Next player is " + playerNames[activePlayerIndex] + ".");
			activePlayer.setTurnScore( RESET_SCORE );
			
			boolean wantsToRoll = getRollChoice();
			ui.println("wantsToRoll: " + wantsToRoll );
			
			while( wantsToRoll )
			{
				activePlayer.setRollScore( RESET_SCORE );
				skunkDice.roll();
				
				//ui.println("*** Remove this, this is for debugging: " + skunkDice.toString() ); 
				
				if (isDoubleSkunk())
				{
					processPenaltyFor_DoubleSkunk();
					wantsToRoll = false;
					break;
				}
				else if (isSkunkDeuce())
				{
					processPenaltyFor_SkunkDeuce();					
					wantsToRoll = false;
					break;
				}
				else if (isRegularSkunk())
				{
					processPenaltyFor_RegularSkunk();					
					wantsToRoll = false;
					break;
				}

				activePlayer.setRollScore( skunkDice.getLastRoll() );
				activePlayer.setTurnScore( activePlayer.getTurnScore() + skunkDice.getLastRoll() );
				ui.println(	"Roll of " + skunkDice.toString() + ", gives new turn score of " + activePlayer.getTurnScore());

				wantsToRoll = getRollChoice();

			}

			ui.println("End of turn for " + playerNames[activePlayerIndex]);
			ui.println("Score for this turn is " + activePlayer.getTurnScore() + ", added to...");
			ui.println("Previous game score of " + activePlayer.getGameScore());
			activePlayer.setGameScore(activePlayer.getGameScore() + activePlayer.getTurnScore());
			ui.println("Gives new game score of " + activePlayer.getGameScore());

			
			ui.println("");
			if (activePlayer.getGameScore() >= TARGET_OF_100_POINTS)
				gameNotOver = false;

			ui.println("Scoreboard: ");
			ui.println("Kitty has " + kitty + " chips.");
			ui.println("Player name -- Turn score -- Game score -- Total chips");
			ui.println("-----------------------");

			for (int i = 0; i < numberOfPlayers; i++)
			{
				ui.println(playerNames[i] + " -- " + players.get(i).getTurnScore() + " -- " + players.get(i).getGameScore()
						+ " -- " + players.get(i).getNumberChips());
			}
			ui.println("-----------------------");

			ui.println("Turn passes to right...");

			activePlayerIndex = (activePlayerIndex + 1) % numberOfPlayers;
			activePlayer = players.get(activePlayerIndex);

		}
		
		//
		// last round: everyone but last activePlayer gets another shot
		//
		
		ui.println("**** Last turn for all... ****");

		for (int i = activePlayerIndex, count = 0; count < numberOfPlayers-1; i = (i++) % numberOfPlayers, count++)
		{
			ui.println("Last turn for player " + playerNames[activePlayerIndex] + "...");
			activePlayer.setTurnScore(0);

			boolean wantsToRoll = getRollChoice();

			while( wantsToRoll )
			{
				skunkDice.roll();
				ui.println("Roll is " + skunkDice.toString() + "\n");

				if (isDoubleSkunk())
				{
					processPenaltyFor_DoubleSkunk();
					wantsToRoll = false;
					
					break;
				}
				else if (isSkunkDeuce())
				{
					processPenaltyFor_SkunkDeuce();
					wantsToRoll = false;

				}
				else if (isRegularSkunk())
				{
					processPenaltyFor_RegularSkunk();					
					wantsToRoll = false;
				}
				else
				{
					activePlayer.setTurnScore(activePlayer.getRollScore() + skunkDice.getLastRoll());
					ui.println("Roll of " + skunkDice.toString() + ", giving new turn score of "
							+ activePlayer.getTurnScore());

					ui.println("Scoreboard: ");
					ui.println("Kitty has " + kitty);
					ui.println("Player name -- Turn score -- Game score -- Total chips");
					ui.println("-----------------------");

					for (int pNumber = 0; pNumber < numberOfPlayers; pNumber++)
					{
						ui.println(playerNames[pNumber] + " -- " + players.get(pNumber).turnScore + " -- "
								+ players.get(pNumber).getGameScore() + " -- " + players.get(pNumber).getNumberChips());
					}
					ui.println("-----------------------");

					wantsToRoll = getRollChoice();
					
				}

			}

			activePlayer.setTurnScore(activePlayer.getRollScore() + skunkDice.getLastRoll());
			ui.println("Final roll of " + skunkDice.toString() + ", giving final game score of "
					+ activePlayer.getRollScore());

		}

		int winner = 0;
		int winnerScore = 0;

		for (int player = 0; player < numberOfPlayers; player++)
		{
			Player nextPlayer = players.get(player);
			
			ui.println("Final game score for " + playerNames[player] + " is " + nextPlayer.getGameScore());
			if (nextPlayer.getGameScore() > winnerScore)
			{
				winner = player;
				winnerScore = nextPlayer.getGameScore();
			}
		}

		ui.println( "Game winner is " + playerNames[winner] + " with score of " + players.get(winner).getGameScore());
		players.get(winner).setNumberChips(players.get(winner).getNumberChips() + kitty);
		ui.println("Game winner earns " + kitty + " chips , finishing with " + players.get(winner).getNumberChips());

		ui.println("\nFinal scoreboard for this game:");
		ui.println("Player name -- Game score -- Total chips");
		ui.println("-----------------------");

		for (int pNumber = 0; pNumber < numberOfPlayers; pNumber++)
		{
			ui.println(playerNames[pNumber] + " -- " + players.get(pNumber).getGameScore() + " -- "
					+ players.get(pNumber).getNumberChips());
		}

		ui.println("-----------------------");
		return true;
	}

	//**********************************************************
	//This part of the code is repeated at two places, that motivated me to to create 
	// a separate function and replaced code with this function.
	//**********************************************************
	
	private void processPenaltyFor_DoubleSkunk()
	{
		ui.println("Two Skunks! You lose the turn, zeroing out both turn and game scores and paying 4 chips to the kitty");
		
		kitty += DOUBLE_SKUNK_PENALTY;
		activePlayer.scoreSkunkRoll( DOUBLE_SKUNK_PENALTY );
		
		activePlayer.setGameScore( RESET_SCORE );
	}

	//**********************************************************
	//This part of the code is repeated at two places, that motivated me to to create 
	// a separate function and replaced code with this function.
	//**********************************************************
		
	private void processPenaltyFor_SkunkDeuce()
	{
		ui.println("Skunks and Deuce! You lose the turn, zeroing out the turn score and paying 2 chips to the kitty");
		
		kitty += SKUNK_DEUCE_PENALTY;
		activePlayer.scoreSkunkRoll( SKUNK_DEUCE_PENALTY );
	}

	//**********************************************************
	// This part of the code is repeated at two places, that motivated me to to create 
	// a separate function and replaced code with this function.
	//**********************************************************
		
	private void processPenaltyFor_RegularSkunk()
	{
		ui.println("One Skunk!  You lose the turn, zeroing out the turn score and paying 1 chip to the kitty");
		
		kitty += REGULAR_SKUNK_PENALTY;
		activePlayer.scoreSkunkRoll( REGULAR_SKUNK_PENALTY );		
	}

	//**********************************************************
	// Added this code to handle case if user enters invalid player number.
	// This function is used in the function above the gives user multiple tries till the number of players is correct.
	//**********************************************************		
	
	private int getNumberOfPlayers()
	{
		String numberPlayersString = skunkUI.promptReadAndReturn("How many players?");
		int numOfPlayers = Integer.parseInt(numberPlayersString);
		return numOfPlayers;
	}

	//**********************************************************
	// This code was at two places, so refactoring it as a function.
	//**********************************************************		
	
	private boolean isSkunkDeuce()
	{
		return skunkDice.getLastRoll() == SKUNK_DEUCE_SUM;
	}

	//**********************************************************
	// This code was at two places, so refactoring it as a function.
	//**********************************************************	
	
	private boolean isDoubleSkunk()
	{
		return skunkDice.getLastRoll() == DOUBLE_SKUNK_SUM;
	}

	//**********************************************************
	// This code was at two places, so refactoring it as a function.
	//**********************************************************	
	
	private boolean isRegularSkunk()
	{
		return skunkDice.getDie1().getLastRoll() == SKUNK_CONSTANT || skunkDice.getDie2().getLastRoll() == SKUNK_CONSTANT;
	}

	//**********************************************************
	// This code was at multiple places, so refactoring it as a function.
	//**********************************************************	
	
	private boolean getRollChoice()
	{
		String wantsToRollStr = ui.promptReadAndReturn("\nRoll? y or n");
		ui.println("wantsToRollStr: " + wantsToRollStr );
		
		boolean bState = ('y' == wantsToRollStr.toLowerCase().charAt(0) );
		ui.println("bState: " + bState );
		
		return ( bState );
	}
	
	//**********************************************************
	
}
