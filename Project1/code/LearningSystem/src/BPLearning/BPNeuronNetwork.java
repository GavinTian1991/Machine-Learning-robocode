package BPLearning;

/*
 * author:Xiaojun Tian:11240587
 * 
 * 
 */

import java.util.*;
import java.lang.Math;

public class BPNeuronNetwork {
	
	
	private double weights[][][];
	private double weightsChange[][][];
	
	private double neuronCell[][];
	private double layerError[][];
	
	private double error,weightSum,learningRate;
	
	
	
	public BPNeuronNetwork() {
		
		error = 0.0;
		weightSum = 0.0;
		learningRate = 0.2;
		
		initMemory();
		initWeights(); 
		
	}
	
	
	
	public double errorCalculation(int arg, int p1, int p2) {
			
		error = 0;
		weightSum = 0.0;
		
		neuronCell[0][0] = p1;
		neuronCell[0][1] = p2;
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; ++j) {
				weightSum += neuronCell[0][j] * weights[0][j][i];
			}
			//neuronCell[1][i] = (sigmoid(weightSum));
			neuronCell[1][i] = (sigmoid2(weightSum));
			weightSum = 0;
		}

		
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 5; ++j) {
				weightSum += neuronCell[1][j] * weights[1][j][i];
			}
			//neuronCell[2][0] = sigmoid(weightSum);
			neuronCell[2][0] = sigmoid2(weightSum);
			weightSum = 0;
		}
		
		error = 0.5 * (neuronCell[2][0] - arg) * (neuronCell[2][0] - arg);
		return error;
		
	}
	
	
	public void weightsTrain(double argValue) {
		
		double weightTmp = 0.0;
		
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 4; ++j) {
			 //layerError[2][0] = (argValue - neuronCell[2][0]) * neuronCell[2][0] * (1 - neuronCell[2][0]);
			 layerError[2][0] = (argValue - neuronCell[2][0]) /2 * (1 - neuronCell[2][0]*neuronCell[2][0]);
			}
		}

		for (int i = 0; i < 4; i++) {
			//layerError[1][i] = weights[1][i][0] * layerError[2][0] * neuronCell[1][i] * (1 - neuronCell[1][i]);
		    layerError[1][i] = weights[1][i][0] * layerError[2][0] * 2*neuronCell [1][i] *(1 - neuronCell[1][i]);
		}

		for (int i = 0; i < 5; i++) {

			//weightTmp = weights[1][i][0] + 0.9 * weightsChange[1][i][0] + learningRate * layerError[2][0] * neuronCell[1][i];
			weightTmp = weights[1][i][0] + learningRate * layerError[2][0] * neuronCell[1][i];
			weightsChange[1][i][0] = weightTmp - weights[1][i][0];
			weights[1][i][0] = weightTmp;

		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; ++j) {
				//weightTmp = weights[0][i][j] + 0.9 * weightsChange[0][i][j] + learningRate * layerError[1][j] * neuronCell[0][i];
				weightTmp = weights[0][i][j] + learningRate * layerError[1][j] * neuronCell[0][i];
				weightsChange[0][i][j] = weightTmp - weights[0][i][j];
				weights[0][i][j] = weightTmp;
			}
		}
	
	}
	
	
	public void initMemory() {
		
		weights = new double[2][][];
		weights[0] = new double[3][4];  
		weights[1] = new double[5][1];
		
		weightsChange = new double[2][][];
		weightsChange[0] = new double[3][4]; 
		weightsChange[1] = new double[5][1];
		
		
		neuronCell = new double[3][];
		neuronCell[0] = new double[3];
		neuronCell[1] = new double[5];
		neuronCell[2] = new double[1];
		
		neuronCell[0][2] = sigmoid(1);
		neuronCell[1][4] = sigmoid(1);
		
			
		layerError = new double[3][];
		layerError[0] = new double[3];
		layerError[1] = new double[5];
		layerError[2] = new double[1];
	}
	
	public void initWeights() {

		Random rDom = new Random();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; ++j) {
				weights[0][i][j] = rDom.nextDouble() - 0.5;  
			}
		}

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 1; ++j) {
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
