package BPLearning;

/*
 * author:Xiaojun Tian:11240587
 * 
 * 
 */

import java.io.*;
import java.util.*;

public class BPTraning {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
        double errorBound = 0.05;            
        double totalError = 0.0;
        int pattern[][] = new int[4][2];   
        
        
//	    pattern[0][0] = 0;
//	    pattern[0][1] = 0;
//	      
//	    pattern[1][0] = 1;
//	    pattern[1][1] = 0;
//	      
//	    pattern[2][0] = 0;
//	    pattern[2][1] = 1;
//	      
//	    pattern[3][0] = 1;
//	    pattern[3][1] = 1;
        
        pattern[0][0] = -1;
        pattern[0][1] = -1;
        
        pattern[1][0] = -1;
        pattern[1][1] = 1;
        
        pattern[2][0] = 1;
        pattern[2][1] = -1;
        
        pattern[3][0] = 1;
        pattern[3][1] = 1;
        
        
        int arg[] = new int[4];  //required output
        
//	    arg[0] = 0;
//	    arg[1] = 1;
//	    arg[2] = 1;
//	    arg[3] = 0;
        
        arg[0] = -1;
        arg[1] = 1;
        arg[2] = 1;
        arg[3] = -1;
        
        
        long epoch = 0;
        double errorTmp = 0;
        BPNeuronNetwork BPTrain = new BPNeuronNetwork();
        
        	
        do {
            for (int i = 0; i < 4; ++i) {
            	totalError += BPTrain.errorCalculation(arg[i], pattern[i][0], pattern[i][1]);
                BPTrain.weightsTrain(arg[i]);
            }
            errorTmp = totalError;
            totalError = 0;
            epoch++;
            if(epoch == 10000)
            	break;
            
        }while(errorTmp > errorBound);
	             
	}

}
