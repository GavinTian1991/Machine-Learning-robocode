package robo;

/*
 * author:Xiaojun Tian:11240587
 * 
 * 
 */

import java.io.*;
import robocode.RobocodeFileOutputStream;


public class LUT implements LUTInterface {

	public static final double LearningRate = 0.3;
	public static final double DiscountRate = 0.9;
	double[][] table;
	
	public LUT() {
		States.init();
		this.table = new double[States.numStates][Action.numActions];
		this.initialiseLUT();
	}

	public double outputFor(double[] X) {
		return maxD(this.table[indexFor(X)]);
	}
	
	public void updateLUTOff(int state_before, int state_after, int action, double r) {
		double delta = LearningRate * (r + DiscountRate * maxD(table[state_after]) - table[state_before][action]);
		updateQ(state_before, action, delta);
	}
	
	public void updateLUTOn(int state_before, int state_after, int action_before, int action_after, double r) {
		double delta = LearningRate * (r + DiscountRate * table[state_after][action_after] - table[state_before][action_before]);
		updateQ(state_before, action_before, delta);
	}
	
	public void updateQ(int state, int action, double delta) {
		table[state][action] += delta;
	}

	public double train(double[] X, double argValue) {
		return 0.0;
	}

	public void save(File argFile) {
		PrintStream write = null; 
		try { 
			write = new PrintStream(new RobocodeFileOutputStream(argFile)); 
			for (int i = 0; i < States.numStates; i++) 
				for (int j = 0; j < Action.numActions; j++) 
					write.println(new Double(table[i][j]));
			if (write.checkError()) 
				System.out.println("Could not save the data!"); 
			write.close(); 
		} 
		catch (IOException e) { 
	            System.out.println("IOException trying to write: " + e); 
		} 
		finally { 
			try { 
				if (write != null) 
					write.close(); 
			} 
			catch (Exception e) { 
	            System.out.println("Exception trying to close witer: " + e); 
			} 
		}
	}

	public void loadFile(File file) throws IOException {
		BufferedReader read = null;
	    try {
	    	read = new BufferedReader(new FileReader(file));
	    	for (int i = 0; i < States.numStates; i++)
	    		for (int j = 0; j < Action.numActions; j++)
	    			table[i][j] = Double.parseDouble(read.readLine());
	    }
	    catch (IOException e) {
	        System.out.println("IOException trying to open reader: " + e);
	    	initialiseLUT();
	    }
	    catch (NumberFormatException e) {
	    	initialiseLUT();
	    }
	    finally {
	    	try {
	    		if (read != null)
	    			read.close();
	    	}
	    	catch (IOException e) {
	            System.out.println("IOException trying to close reader: " + e);
	    	}
	    }
	}

	public void initialiseLUT() {
		for (int i = 0; i < this.table.length; i++) {
			for (int j = 0; j < this.table[0].length; j++)
				this.table[i][j] = 0.0;
		}
	}

	public int indexFor(double[] X) {		
		return States.statesMap[(int)X[0]][(int)X[1]][(int)X[2]][(int)X[3]][(int)X[4]];
	}

	public int selectAction(int state, boolean rand) {
		double[] row = table[state];
		if (rand) return (int)Math.random() * Action.numActions;
		else if (row[0]+row[1]+row[2]+row[3]+row[4] == 0) {
			return (int)Math.random() * Action.numActions;
		}
		else {
			return maxArgD(row);
		}
	}

	public double maxD(double[] X) {
		double m = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < X.length; i++) {
			if (X[i] > m) m = X[i];
		}
		return m;
	}
	
	public int maxArgD(double[] X) {
		int arg = -1;
		double m = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < X.length; i++) {
			if (X[i] > m) {
				m = X[i];
				arg = i;
			}
		}
		return arg;
	}

	@Override
	public void load(String argFileName) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
