package BPLearning;

/*
 * author:Xiaojun Tian:#11240587
 * 
 * 
 */

import robocode.AdvancedRobot;
import robocode.*;

import java.io.*;
import java.util.*;


public class BPTraning {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
        double errorBound = 0.05;            
        double totalError = 0.0;
        File lutFile = new File("src/LUT.dat");
     
        long epoch = 0;
        double errorTmp = 0; 
        BPNeuronNetwork BPTrain = new BPNeuronNetwork();
        BPTrain.loadFile(lutFile); 
        	
        do {
            for (int i = 0; i < BPTrain.numLUTStates; ++i) {
            	for(int j = 0 ; j < BPTrain.numLUTActions ; j++) {
	            	totalError += BPTrain.errorCalculation(BPTrain.table[i][j], 
	            			BPTrain.selectTableInput(i,j)); 
	                BPTrain.weightsTrain(BPTrain.table[i][j]);
            	}
            }
            System.out.println("error = " + totalError + " epoch = " + epoch);
            errorTmp = totalError;
            totalError = 0;
            epoch++;
            if(epoch == 10000)
            	break;
            
        }while(errorTmp > errorBound);   
        
        System.out.println("errorTmp = "+ errorTmp);
	}	
}
