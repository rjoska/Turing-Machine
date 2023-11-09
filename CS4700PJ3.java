
/*
 *  Name: Roman Joska
 *  Class: CS4700
 *  Description: Make code that can make a D.T.M., and print output files for every machine and the final log
 */

//imports
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
//import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class CS4700PJ3 {

	public static void main(String[] args) throws IOException{
		//need to make input files and outFile for log
				BufferedWriter writer = new BufferedWriter(new FileWriter("tm.log"));
				
				//call machines while we have machines to make
				File machinesFolder = new File("machines/");
				File[] machineFiles = machinesFolder.listFiles();
				//go through each .tm file
				for (File machineFile : machineFiles) {
				    if (machineFile.isFile() && machineFile.getName().endsWith(".tm")) {
				    	//make the name and then make the machine
				        String machineName = machineFile.getName().replace(".tm", "");
				        //print to test
				        System.out.printf("we got one called %s\n", machineName);
				        int logOutput = processMachineFile(machineFile, machineName);
				        String outputString = machineName + "," + logOutput + "\n";
				        writer.write(outputString);
				    }
				}
				//close the log writer
				writer.close();

	}

public static int processMachineFile(File machineFile, String machineName) {
	//inputs needed
    int acceptStrings = 0;
    
        //Obtain all the transition functions
        //FromState, ReadSymbol, ToState, WriteSymbol, HeadDirection
        // Create a Scanner object to read in the contents of the file
        Scanner scanner;
        Set<inputs> machineInputs = new HashSet<>();
		try {
			scanner = new Scanner(machineFile);
		

        // Loop through each line of the file
        while (scanner.hasNextLine()) {
            // Read in the line of text
            String line = scanner.nextLine();

            // Split the line into its individual parts
            
            String[] parts = line.split(",");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            // Parse each part into the desired data type
            int fromState = Integer.parseInt(parts[0]);
            String readSymbol = parts[1];
            int toState = Integer.parseInt(parts[2]);
            String writeSymbol = parts[3];
            char headDirection = parts[4].charAt(0);

            // Add to an inputs
            inputs input1 = new inputs(fromState, readSymbol, toState, writeSymbol, headDirection);
            machineInputs.add(input1);
            
            /*
            // or printing them out
            System.out.printf("From State: %d, ", fromState);
            System.out.printf("Read Symbol: %s, ", readSymbol);
            System.out.printf("To State: %d, ", toState);
            System.out.printf("Write Symbol: %s, ", writeSymbol);
            System.out.printf("Head Direction: %c \n", headDirection);
            */
        }

        // Close the Scanner object
        scanner.close();

        /*
        //this is a check for printing characters
        int printable = 0;
        for (inputs value : machineInputs) {
            	printable += value.printChars();
        }
        */
        
        //Make the machine
        machine dtm = new machine(0, machineName, machineInputs);
        acceptStrings = dtm.readStrings();
        System.out.println("Num accepted by " + machineName + " is " + acceptStrings);
        
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    return acceptStrings;
    
   }

}

//classes
class machine{
	private int numAccept;
	private String machineName;
	private Set<inputs> tranistions;
	
	//constructor
	public machine(int acceptNum, String name, Set<inputs> information) {
		numAccept = acceptNum;
		machineName = name;	
		tranistions = information;
	}
	//read input and make output functions
	public int readStrings() {
		int numAccepted = 0;
		//start reading the files and make the output folder
		String directoryPath = "./outputs";
		File directory = new File(directoryPath);
		directory.mkdirs(); // create the directory if it doesn't exist
		String fileName = machineName + ".txt";
    	File outputFile = new File(directory, fileName);
    	
       /* for (int i = 0; i < acceptStates.length; i++) {
            //print to test
            System.out.printf("Accept state #%d, is %d \n", i, acceptStates[i]);
        }*/
		//make the machine if it is a D.T.M
		try (BufferedReader br = new BufferedReader(new FileReader("machines/strings.txt"))) {
            String line;
            int currentState = 0;
        	try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            while ((line = br.readLine()) != null) {
                // loop through each character in the line
            	currentState = 0;
            	//System.out.printf("reset\n");
            	
            	//make tape
                ArrayList<String> tape = new ArrayList<String>();
                for (int i = 0; i < line.length(); i++) {
                    tape.add(String.valueOf(line.charAt(i)));
                }
                //System.out.println(tape.size());
                tape.add("@0");
                
                /*
                for (int i = 0; i < tape.size(); i++) {
                    if (i == 0) {
                        System.out.print("[" + tape.get(i) + "]");
                    } else {
                        System.out.print(tape.get(i));
                    }
                }
                System.out.println(tape.size());
                */
            	
                //make vars for while
                int tapeHead = 0;
                String nextInput = tape.get(tapeHead);
                
                //we are making a decider
                boolean acceptOrReject = false;
                while(acceptOrReject == false) {
                    boolean found = false;
                    /*
                    for (int i = 0; i < tape.size(); i++) {
                        if (i == tapeHead) {
                            System.out.print(" [" + tape.get(i) + "] ");
                        } else {
                            System.out.print("," + tape.get(i) + ",");
                        }
                    }
                    System.out.println("    state = " + currentState);
                    */
                    
                    // check current state and then do the transition also make sure you only take 1 tranistion per character
                    boolean notDidOneAlready = true;
                    for (inputs input : tranistions) {
                        if (input.getStart() == currentState && input.getRead().equals(nextInput) && notDidOneAlready) {
                        	//System.out.printf("Current state %d, transition %s to state %d to the %c\n", currentState, nextInput, input.getTo(), input.getLorR());
                        	currentState = input.getTo();
                        	tape.set(tapeHead, input.getWrite());
                        	found = true;
                        	if(input.getLorR() == 'L' | input.getLorR() == 'l') {
                        		if(tapeHead == 0) {
                        			nextInput = tape.get(tapeHead);
                        		}
                        		else {
                        			tapeHead = tapeHead-1;
                        			nextInput = tape.get(tapeHead);
                        		}
                        	}
                        	else if(input.getLorR() == 'R' | input.getLorR() == 'r') {
                        		if(tape.size() == tapeHead+1) {
                        			tape.add("@0");
                        			tapeHead = tapeHead+1;
                            		nextInput = tape.get(tapeHead);
                        		}
                        		else {
                        			tapeHead = tapeHead+1;
                            		nextInput = tape.get(tapeHead);
                        		}
                        	}
                        	notDidOneAlready = false;
                        }
                    }//send to trap state and kill it if the input is not in the alphabet
                    if(!found) {
                    	acceptOrReject = true;
                    	currentState = 255;
                    	//System.out.printf("trap card\n");
                    }
                    
                    //check for if any transition got rid of the last element being '@0'
                    if (!(tape.get(tape.size() - 1).equals("@0"))) {
                    	tape.add("@0");
                    }
                    
                    //if it is copy the string
                    if (currentState == 254) {
                    	acceptOrReject = true;
                    	String copyStrng = line + "\n";
                    	//System.out.println(line);
                    	writer.write(copyStrng);
                    	numAccepted += 1;
                    }else if(currentState == 255) {
                    	acceptOrReject = true;
                    }
                    
                }// end of for
                
                //System.out.printf("Current state #%d\n", currentState); 
                
            }//end of while
            
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}//end of write
        	
        } catch (IOException e) {//error line just in case
            System.err.println("Error reading file: " + e.getMessage());
        }
		return numAccepted;
	}// end of read strings
}//end of machine class

class inputs{
	private int startS;
	private String readSymbol;
	private int toState;
	private String writeSymbol;
	private char direction;
	
	//constructor
	public inputs(int startState, String read, int too, String write, char lorR) {
		startS = startState;
		readSymbol = read;
		toState = too;
		writeSymbol = write;
		direction = lorR;
	}
	
	//getters
	public int getStart() {
		return startS;
	}
	public int getTo() {
		return toState;
	}
	public String getRead() {
		return readSymbol;
	}
	public String getWrite() {
		return writeSymbol;
	}
	public char getLorR() {
		return direction;
	}
	
	//check if char == valid
	/*
	public int printChars() {
		if(readSymbol.isEmpty()) {
			return 0;
		}
		else {
			boolean yesNo = !Character.isISOControl(readSymbol.charAt(0)) && readSymbol.charAt(0) != KeyEvent.CHAR_UNDEFINED;
			if(yesNo == true) {
				return 0;
			}
			else {
				return 1;
			}
		}
	}
	*/
}