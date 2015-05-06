package SE350;

public class MarketMessage {
	String state;
	
	MarketMessage( String state) throws InvalidInputOperation {
		setState(state);
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) throws InvalidInputOperation{
		if (state == "OPEN" || state == "CLOSED" || state == "PREOPEN" ) {
		this.state = state;
	}
		else{
			throw new InvalidInputOperation("Invalid Market State!");
		}
	}
	
	

}
