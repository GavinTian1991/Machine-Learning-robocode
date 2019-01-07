package robo;

/*
 * author:Xiaojun Tian:11240587
 * 
 * 
 */

import java.util.*;
import java.io.*;
import java.lang.Math;

public class BPNeuronNetwork {
	
	private double weights[][][];
	private double weightsChange[][][];
	
	private double neuronCell[][];
	private double layerError[][];
	
	private int hiddenLayer;
	private int numInput;
	private int numOutput;
	private int numLayers;
	
	public int numLUTActions;
	public int numLUTStates;
	public double[][] table;
	public  double[][] tableInput;
	
	private double error,weightSum,learningRate,mon;
	
	public BPNeuronNetwork() {	
		initParams();
		initMemory();
		initWeights(); 
	}
	
	public double errorCalculation(double actionStates[]) {
			
		error = 0;
		weightSum = 0.0;
		
		for(int i = 0 ; i < numInput ; i++) {
			neuronCell[0][i] = actionStates[i];
		}
		
		for (int i = 0; i < hiddenLayer; i++) {
			for (int j = 0; j < (numInput + 1); ++j) {
				weightSum += neuronCell[0][j] * weights[0][j][i];
			}
			neuronCell[1][i] = (sigmoid(weightSum));
			//neuronCell[1][i] = (sigmoid2(weightSum));
			weightSum = 0;
		}

		
		for (int i = 0; i < numOutput; i++) {
			for (int j = 0; j < (hiddenLayer + 1); ++j) {
				weightSum += neuronCell[1][j] * weights[1][j][i];
			}
			neuronCell[2][0] = sigmoid(weightSum);
			//neuronCell[2][0] = sigmoid2(weightSum);  
			weightSum = 0;
		}
		
		return neuronCell[2][0];
		
//		error = 0.5 * (neuronCell[2][0] - arg) * (neuronCell[2][0] - arg); 
//		return error;
	}
	
	public void weightsTrain(double argValue) {
		
		double weightTmp = 0.0;
		
		
		for (int i = 0; i < numOutput; i++) {
			 layerError[2][0] = (argValue - neuronCell[2][0]) * neuronCell[2][0] * (1 - neuronCell[2][0]);
			 //layerError[2][i] = (argValue - neuronCell[2][i]) /2 * (1 - neuronCell[2][i]*neuronCell[2][i]);
		}
		
		
		for (int i = 0; i < hiddenLayer; i++) {
			layerError[1][i] = weights[1][i][0] * layerError[2][0] * neuronCell[1][i] * (1 - neuronCell[1][i]);
		    //layerError[1][i] = weights[1][i][0] * layerError[2][0] * 2*neuronCell [1][i] *(1 - neuronCell[1][i]);
		}

		for (int i = 0; i < (hiddenLayer + 1); i++) {

			weightTmp = weights[1][i][0] + mon * weightsChange[1][i][0] + learningRate * layerError[2][0] * neuronCell[1][i];
			//weightTmp = weights[1][i][0] + learningRate * layerError[2][0] * neuronCell[1][i];
			weightsChange[1][i][0] = weightTmp - weights[1][i][0];
			weights[1][i][0] = weightTmp;

		}
		
		for (int i = 0; i < (numInput + 1); i++) {
			for (int j = 0; j < hiddenLayer; ++j) {
				weightTmp = weights[0][i][j] + mon * weightsChange[0][i][j] + learningRate * layerError[1][j] * neuronCell[0][i];
				//weightTmp = weights[0][i][j] + learningRate * layerError[1][j] * neuronCell[0][i];
				weightsChange[0][i][j] = weightTmp - weights[0][i][j];
				weights[0][i][j] = weightTmp;
			}
		}
	
	}
	
	/*public  double[] selectTableInput(int state, int action) {
		double[] input = new double[numInput];
		for (int i = 0; i < numInput; i++) {		
			input[i] =tableInput[state][i] ;
		}
		return input;
	}
	
	public void loadFile(File file) throws IOException {
		BufferedReader read = null;
	    try {
	    	read = new BufferedReader(new FileReader(file));
	    	for (int i = 0; i < numLUTStates; i++)
	    		for (int j = 0; j < numLUTActions; j++)
	    			table[i][j] = Double.parseDouble(read.readLine());
	    }
	    catch (IOException e) {
	        System.out.println("IOException trying to open reader: " + e);
	    }
	    catch (NumberFormatException e) {
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
	}*/
	
	public void initParams() {
		
		hiddenLayer = 30;
		numInput = 6; //5 states,1 actions
		numOutput = 1;
		numLayers = 3;
		
		numLUTActions = 5;
		numLUTStates = 3072;  //4 * 4 * 4 * 8 * 6
		
		error = 0.0;
		weightSum = 0.0;
		learningRate = 0.2;
		mon = 0.9;
		
	}
	
	public void initMemory() {
		
		weights = new double[numLayers - 1][][];
		weights[0] = new double[numInput + 1][hiddenLayer];  
		weights[1] = new double[hiddenLayer + 1][numOutput];
		
		weightsChange = new double[numLayers - 1][][];
		weightsChange[0] = new double[numInput + 1][hiddenLayer];  
		weightsChange[1] = new double[hiddenLayer + 1][numOutput];
			
		neuronCell = new double[numLayers][];
		neuronCell[0] = new double[numInput + 1];
		neuronCell[1] = new double[hiddenLayer + 1];
		neuronCell[2] = new double[numOutput];
		
		neuronCell[0][numInput] = sigmoid(1);
		neuronCell[1][hiddenLayer] = sigmoid(1);
		
			
		layerError = new double[numLayers][];
		layerError[0] = new double[numInput + 1];
		layerError[1] = new double[hiddenLayer + 1];
		layerError[2] = new double[numOutput];
		
		tableInput = new double[3072][5];  //5 actions
		
		for(int state = 0 ; state < 3072; state++) {
			tableInput[state][0] = ((Math.floor((double)state) % 4 ) / (4 - 1))*2-1;
			tableInput[state][1] = ((Math.floor(((double)state / 4)) % 4 ) / (4 - 1))*2-1;
			tableInput[state][2] = ((Math.floor(((double)state / 4 / 4)) % 4)  / (4 - 1))*2-1;
			tableInput[state][3] = ((Math.floor(((double)state / 4 / 4 / 4)) % 8 ) / (8 - 1))*2-1;
			tableInput[state][4] = ((Math.floor(((double)state / 4 / 4 / 4 / 8)) % 6) / (6 - 1))*2-1;
		}
	}
	
	public void initWeights() {
		
		Random rDom = new Random();
		for (int i = 0; i < (numInput + 1); i++) {
			for (int j = 0; j < hiddenLayer; ++j) {
				weights[0][i][j] = rDom.nextDouble() - 0.5;  
			}
		}

		for (int i = 0; i < (hiddenLayer + 1); i++) {
			for (int j = 0; j < numOutput; ++j) {
				weights[1][i][j] = rDom.nextDouble() - 0.5;
			}
		}	
	}
	
	public double sigmoid(double x) {

		return 1 / (1 + Math.exp(-x));
	}
	
	public double sigmoid2(double x) {

		return 2 / (1 + Math.exp(-x)) - 1;
	}
}
