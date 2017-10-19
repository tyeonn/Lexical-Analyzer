/**
 * Project1 is a lexical analyzer defined by the grammar: ⟨letter⟩ → a | b | ...
 * | z | A | B | ... | Z ⟨digit⟩ → 0 | 1 | ... | 9 ⟨basic id⟩ → ⟨letter⟩
 * {⟨letter⟩ | ⟨digit⟩} ⟨letters and digits⟩ → {⟨letter⟩ | ⟨digit⟩}+ ⟨id⟩ →
 * ⟨basic id⟩ { "_" ⟨letters and digits⟩ } // Note: "_" is the underscore char
 * ⟨int⟩ → [+|−] {⟨digit⟩}+ ⟨float⟩ → [+|−] ( {⟨digit⟩}+ "." {⟨digit⟩} | "."
 * {⟨digit⟩}+ ) ⟨floatE⟩ → (⟨float⟩ | ⟨int⟩) (e|E) [+|−] {⟨digit⟩}+ ⟨add⟩ → +
 * ⟨sub⟩ → − ⟨mul⟩ → * ⟨div⟩ → / ⟨lt⟩ → "<" ⟨le⟩ → "<=" ⟨gt⟩ → ">" ⟨ge⟩ → ">="
 * ⟨eq⟩ → "=" ⟨LParen⟩ → "(" ⟨RParen⟩ → ")" ⟨comma⟩ → ","
 * 
 * This class implements a DFA that will accept the above tokens.
 * 
 * The DFA states are represented by the Enum type "State". The DFA has the
 * following 27 final states represented by enum-type literals:
 * 
 * state token accepted
 * 
 * Id identifiers Int integers Float floats without exponentiation part FloatE
 * floats with exponentiation part Plus + Minus - Times * Div / LParen ( RParen
 * ) LThan < LEqual <= GThan > GEqual >= Comma , Eq = Keyword_int int
 * Keyword_float float Keyword_boolean boolean Keyword_if if Keyword_then then
 * Keyword_else else Keyword_and and Keyword_or or Keyword_not not Keyword_false
 * false Keyword_true true
 * 
 * The DFA also uses the following 4 non-final states:
 * 
 * state string recognized
 * 
 * Start the empty string Period float parts ending with "." E float parts
 * ending with E or e EPlusMinus float parts ending with + or - in
 * exponentiation part Underscore For the Id with '_'
 * 
 * The function "driver" operates the DFA. The function "nextState" returns the
 * next state given the current state and the input character.
 * 
 * @author Timothy Wu
 *
 */
public class Project1 extends IO {

	public static void main(String[] args) {

		/*
		 * Sets the input and output files. Set first argument as input.txt and
		 * second argument as output.txt
		 */
		setIO(args[0], args[1]);

		int i; // Will be set to the next token

		while (a != -1) { // while "a" is not end-of-stream

			i = driver(); // extract the next token
			if (i == 1)
				displayln(t + "   : " + state.toString()); // outputs to the
															// file the current
															// token.
			else if (i == 0)
				displayln(t + " : Lexical Error, invalid token"); // outputs if
																	// the token
																	// is
																	// invalid.
		}

		closeIO();

	}
/**
 * State class will set the enum for the non-final and final states
 *
 */
	public enum State {
		// non-final states ordinal number
		Start, // 1
		Period, E, EPlusMinus, Underscore, // 5

		// final states
		Id, // 6
		Int, Float, FloatE, Plus, Minus, Times, Div, LParen, RParen, LThan, LEqual, GThan, GEqual, Comma, Eq, // 21

		// keywords
		Keyword_int, // 22
		Keyword_float, Keyword_boolean, Keyword_if, Keyword_then, Keyword_else, Keyword_and, Keyword_or, Keyword_not, Keyword_false, Keyword_true, // 32

		UNDEF; // 33

		private boolean isFinal() { // Will decide whether the state is a final
									// state.
			return (this.compareTo(State.Id) >= 0);
		}
	}

	public static String t; // holds an extracted token
	public static State state; // the current state of the FA

	/**
	 * driver() will determine the state of the token
	 * @return 1 if valid token, 0 if invalid
	 */
	private static int driver()

	// This is the driver of the FA.
	// If a valid token is found, assigns it to "t" and returns 1.
	// If an invalid token is found, assigns it to "t" and returns 0.
	// If end-of-stream is reached without finding any non-whitespace character,
	// returns -1.

	{
		State nextSt; // the next state of the FA

		t = ""; // set token to an empty string
		state = State.Start; // set the state to a starting state

		if (Character.isWhitespace((char) a))
			a = getChar(); // get the next non-whitespace character
		if (a == -1) // end-of-stream is reached
			return -1;

		while (a != -1) // do the body if "a" is not end-of-stream
		{
			c = (char) a; // sets c to the current character

			nextSt = nextState(state, c, t); // Get the state of the next state
			if (nextSt == State.UNDEF) // The FA will halt.
			{
				if (state.isFinal())
					return 1; // valid token extracted
				else // "c" is an unexpected character
				{
					t = t + c;
					a = getNextChar();
					return 0; // invalid token found
				}
			} else // The FA will go on.
			{
				state = nextSt;
				t = t + c;
				a = getNextChar();
			}
		}

		// end-of-stream is reached while a token is being extracted

		if (state.isFinal())
			return 1; // valid token extracted
		else
			return 0; // invalid token found
	} // end driver

	/**
	 * nextState uses a switch case to determine the state
	 * @param s current state
	 * @param c current character
	 * @param t the string of token
	 * @return
	 */
	private static State nextState(State s, char c, String t)

	// Returns the next state of the FA given the current state and input char;
	// if the next state is undefined, UNDEF is returned.

	{
		switch (state) {
		case Start: // These are the only symbols that can start a new token.
			if (Character.isLetter(c))
				return State.Id;
			else if (Character.isDigit(c))
				return State.Int;
			else if (c == '+')
				return State.Plus;
			else if (c == '-')
				return State.Minus;
			else if (c == '*')
				return State.Times;
			else if (c == '/')
				return State.Div;
			else if (c == '(')
				return State.LParen;
			else if (c == ')')
				return State.RParen;
			else if (c == '.')
				return State.Period;
			else if (c == '=')
				return State.Eq;
			else if (c == ',')
				return State.Comma;
			else if (c == '>')
				return State.GThan;
			else if (c == '<')
				return State.LThan;
			else
				return State.UNDEF;
		case Id: // Handles the keywords as well as other tokens including
					// letters.
			String temp = t + c;
			switch (temp) { // This will see if the token is a keyword before
							// moving onto checking if it is an Id.
			case "int":
				return State.Keyword_int;
			case "float":
				return State.Keyword_float;
			case "boolean":
				return State.Keyword_boolean;
			case "if":
				return State.Keyword_if;
			case "then":
				return State.Keyword_then;
			case "else":
				return State.Keyword_else;
			case "and":
				return State.Keyword_and;
			case "or":
				return State.Keyword_or;
			case "not":
				return State.Keyword_not;
			case "false":
				return State.Keyword_false;
			case "true":
				return State.Keyword_true;
			default:
				break;
			}
			if (Character.isLetterOrDigit(c)) // sets the state to Id.
				return State.Id;
			else if (c == '_')
				return State.Underscore; // Underscore is allowed in Id after an
											// initial letter
			else
				return State.UNDEF;

		case Keyword_int:
		case Keyword_float:
		case Keyword_boolean:
		case Keyword_if:
		case Keyword_then:
		case Keyword_else:
		case Keyword_and:
		case Keyword_or:
		case Keyword_not:
		case Keyword_false:
		case Keyword_true:
			if (Character.isLetterOrDigit(c)) // If the next token after keyword
												// is a letter or digit, sets
												// state to Id
				return State.Id;
			if (c == '_') // Sets next state to underscore
				return State.Underscore;
			else
				return State.UNDEF;
		case Int: // Sets state to Int
			if (Character.isDigit(c))
				return State.Int;
			else if (c == '.') // If there is a period, becomes a float
				return State.Float;
			else if (c == 'e' || c == 'E') // sets next state to E which will
											// lead to a float.
				return State.E;
			else
				return State.UNDEF;
		case Period:
			if (Character.isDigit(c)) // sets the state to float.
				return State.Float;
			else
				return State.UNDEF;
		case Float:
			if (Character.isDigit(c))
				return State.Float;
			else if (c == 'e' || c == 'E')
				return State.E;
			else
				return State.UNDEF;
		case E:
			if (Character.isDigit(c))
				return State.FloatE;
			else if (c == '+' || c == '-')
				return State.EPlusMinus;
			else
				return State.UNDEF;
		case EPlusMinus:
			if (Character.isDigit(c))
				return State.FloatE;
			else
				return State.UNDEF;
		case FloatE:
			if (Character.isDigit(c))
				return State.FloatE;
			else
				return State.UNDEF;
		case Underscore: // Sets state to Id
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Plus: // Plus can be stand for a positive number or add sign
			if (Character.isDigit(c))
				return State.Int;
			else if (c == '.')
				return State.Period;
			else
				return State.UNDEF;
		case Minus: // Can stand for negative number or minus sign
			if (Character.isDigit(c))
				return State.Int;
			else if (c == '.')
				return State.Period;
			else
				return State.UNDEF;
		case GThan: // Can set state to greater than or equal if necessary
			if (c == '=')
				return State.GEqual;
			else
				return State.UNDEF;
		case LThan: // can set state to less than or equal if necessary
			if (c == '=')
				return State.LEqual;
			else
				return State.UNDEF;
		default:
			return State.UNDEF;
		}
	} // end nextState

}
