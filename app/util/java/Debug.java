package app.util.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Debug {

	public static String debug(String input) throws Exception {
		Process proc = Runtime.getRuntime().exec(input);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        
		String output = "";
		
		String stdIn;
		while ((stdIn = stdInput.readLine()) != null) {
			output += stdIn + "\n";
        }
        
		String stdErr;
        while ((stdErr = stdError.readLine()) != null) {
        	output += stdErr + "\n";
        }
		
		return output;
	}
}