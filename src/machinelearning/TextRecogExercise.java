package machinelearning;
import org.ejml.equation.Equation;

public class TextRecogExercise {
	
	
	/*
	 * Attributes
	 * ----------
	 * friend => personal
	 * hello => personal
	 * reward => spam
	 * money => spam
	 * 
	 * Message                  |  friend   | hello   | reward   | money    | Conclusion
	 * ---------------------------------------------------------------------------------
	 * I am good                |  0        | 1       | 0        | 0        | P
	 * Collect my money         |  0        | 0       | 0        | 1        | S
	 * Hello? How are you       |  0        | 1       | 0        | 0        | P
	 * Reward will be big       |  0        | 0       | 1        | 0        | S  
	 * My friend is coming      |  1        | 0       | 0        | 0        | P
	 * friend needs money       |  1        | 0       | 0        | 1        | P
 	 * The reward will be money |  0        | 0       | 1        | 1        | S
 	 * Hello, I need some money |  0        | 1       | 0        | 1        | P
 	 * Hello friend!            |  1        | 1       | 0        | 0        | P
 	 * I lost my money          |  0        | 0       | 0        | 1        | S
	 * Hello! you want money?   |  0        | 1       | 0        | 1        | P
	 * granting great reward!   |  0        | 0       | 1        | 0        | S
	 * hello! reward is here    |  0        | 1       | 1        | 0        | P
	 * hello friend!!           |  1        | 1       | 0        | 0        | P
	 * 
	 */
	
	public static void main(String [] args) {
		
		Equation eq = new Equation();
		
		eq.process("A = [ " +
						" 0, 1, 0, 0, 1;" +
						" 0, 0, 0, 1, 0;" +
						" 0, 1, 0, 0, 1;" +
						" 0, 0, 1, 0, 0;" +
						" 1, 0, 0, 0, 1;" +
						" 1, 0, 0, 1, 1;" +
						" 0, 0, 1, 1, 0;" +
						" 0, 1, 0, 1, 1;" +
						" 1, 1, 0, 0, 1;" +
						" 0, 0, 0, 1, 0;" +
						" 0, 1, 0, 1, 1;" +
						" 0, 0, 1, 0, 0;" +
						" 0, 1, 1, 0, 1;" +
						" 1, 1, 0, 0, 1 " +
					"]");
		

		// Freq(Personal) = #T(Personal) / #T                         = 9 / 14
		// Freq(Personal, friend) = #T(Personal, friend) / #T(friend) = 4 / 4
		// Freq(Personal, hello) = #T(Personal, hello) / #T(hello)    = 7 / 7		
		// Freq(Personal, reward) = #T(Personal, reward) / #T(reward) = 1 / 4 		
		// Freq(Personal, money) = #T(Personal, money) / #T(money)    = 3 / 6
	
		// Freq(Spam) = #T(Spam) / #T                                 = 5 / 14		
		// Freq(Spam, friend) = #T(Spam, friend) / #T(friend)         = 0				
		// Freq(Spam, hello) = #T(Spam, hello) / #T(hello)            = 0 	
		// Freq(Spam, reward) = #T(Spam, reward) / #T(reward)         = 3 / 4 		
		// Freq(Spam, money) = #T(Spam, money) / #T(money)            = 3 / 6

		
		
		// hello world!            = [ 0, 1, 0, 0, ? ] 
		// Personal: 9 / 14 * 7 / 7 = 9 / 14
		// Spam:     5 / 14 * 0     = 0
		// Conclusion [ 0, 1, 0, 0 ] is personal
		
		// money is my friend!!    = [ 1, 0, 0, 1, ? ]
		// Personal: 9 / 14 * 4 / 4 * 3 / 6 = 9 / 28
		// Spam:     5 / 14 * 0     * 3 / 6 = 0
		// Conclusion [ 1, 0, 0, 1 ] is personal
	}

}
