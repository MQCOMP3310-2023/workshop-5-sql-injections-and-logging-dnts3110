package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if(!line.matches("[a-z]+") || line.length() != 4){
                    logger.log(Level.SEVERE,"The word '" + line + "' from data.txt is invalid");
                } else {
                    logger.log(Level.INFO,"The word '" + line + "' from data.txt is valid");
                    wordleDatabaseConnection.addValidWord(i, line);
                i++;
                }
            }

        } catch (IOException e) {
            System.out.println("Not able to load . Sorry!");
            logger.log(Level.WARNING,"The data.txt file is not able to be read");
            //System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess+"'.");

                if (!guess.matches("[a-z]+") || guess.length() != 4) { 
                    logger.log(Level.INFO,"The word '" + guess + "' inputted from user is invalid");
                    System.out.println("Please input only a 4 letter word, and no special characters or numbers\n");
                }else if (wordleDatabaseConnection.isValidWord(guess)){
                    System.out.println("Success! It is in the the list.\n");
                }else{
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING,"Exception error while trying to get user input");
            //e.printStackTrace();
        }

    }
}