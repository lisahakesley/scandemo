package models;

import static play.data.validation.Constraints.*;

public class Label { 

	@Required
	public String restaurant;

	@Required
	public String itemName;

	@Required
	@Pattern(
		value="\\d{1,2}\\/\\d{1,2}\\/\\d{4}",
		message="Invalid date.")
	public String expirationDate;

	@Required
	@Pattern(
		value="[1-9]",
		message="Invalid number of copies.  Enter an integer from 1 to 9."
		)
	public String numCopies;
}