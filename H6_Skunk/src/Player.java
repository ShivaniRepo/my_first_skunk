
public class Player
{
	private static final int STARTING_NUMBER_OF_CHIPS = 50;
	private static final int RESET_SCORE = 0;
	
	public int rollScore;
	public int turnScore;
	public int gameScore; // for now, same as roundScore
	public int numberChips;

	public Player()
	{
		this.rollScore = 0;
		this.turnScore = 0;
		this.gameScore = 0;
		this.numberChips = STARTING_NUMBER_OF_CHIPS; // for now
	}

	public Player(int startingChipsPerPlayer)
	{
		this();
		this.numberChips = startingChipsPerPlayer;
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
	}

	public void addToRollScore(int lastTotal)
	{
		rollScore += lastTotal;
	}

	public void setRollScore(int newRollScore)
	{
		this.rollScore = newRollScore;
	}

	public int getRollScore()
	{
		return this.rollScore;
	}

	public int getNumberChips()
	{
		return this.numberChips;
	}

	public void setNumberChips(int newChips)
	{
		this.numberChips = newChips;
	}

	public void setTurnScore(int newScore)
	{
		this.turnScore = newScore;
	}

	public int getTurnScore()
	{
		return this.turnScore;
	}

	public String getName()
	{
		return null;
	}

	public void setGameScore(int i)
	{
		this.gameScore = i;
	}

	public int getGameScore()
	{
		return this.gameScore;
	}

	public void scoreSkunkRoll(int SkunkPenalty)
	{
		setNumberChips( getNumberChips() - SkunkPenalty );
		setTurnScore( RESET_SCORE );	
	}



}
