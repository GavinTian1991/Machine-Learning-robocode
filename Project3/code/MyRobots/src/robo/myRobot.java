package robo;

/*
 * author:Xiaojun Tian:#11240587
 * 
 * 
 */

import robocode.*;

import java.awt.*;   
import java.io.*;
import java.util.*;

public class myRobot extends AdvancedRobot {
	
	int state_old, state, action_old, action;
	double reward = 0.0;
	double oppoDist, oppoBearing;
	boolean found = false;
	double epsilon = 0; 
	boolean useImmediateReward = true;  
	boolean NNFlag = true;
	
	private int NN_last_action = 0;
	private double[] NN_last_states = new double[6]; 
	private static double NN_epsilon = 0.0;

	private LUT lut = new LUT();
	private int round = 1;
	
	
	//============NN parameters==========
	
	private BPNeuronNetwork BPTrain = new BPNeuronNetwork();
	
	private int actionNN;
	
	private double[] new_statesNN = new double[5];
	private double[] old_statesNN = new double[5];
	private double[] stateActionPair = new double[6];
	private int new_actionsNN ,old_actionsNN;
	
	public void run() {
		
		stateActionPair[0] = States.fourStates[0];
		stateActionPair[1] = States.fourStates[1];
		stateActionPair[2] = States.fourStates[2];
		stateActionPair[3] = States.eightStates[3];
		stateActionPair[4] = States.sixStates[4];
		stateActionPair[5] = 1.0;
		
		setColors(Color.gray, Color.green, Color.red);
	    setAdjustGunForRobotTurn(false); 
	    setAdjustRadarForGunTurn(false); 
	    turnRadarRightRadians(2 * Math.PI);
	    
	    
	    if(!NNFlag) {
//	    	action = lut.selectAction(state, Math.random() < epsilon);
//	    	state_old = state;
//	    	action_old = action;
	    }
	    else {
	    	old_statesNN = getNNStates(); 
	    	old_actionsNN = getActionwithHighQFromNN(old_statesNN);
		    actionNN = old_actionsNN;
	    }
    	    
	    
	    while (true) {
	    	
	    	reward = 0.0;
	    	switch (actionNN)
  			{ 
  				case Action.RobotAheadTurnLeft:
  					setAhead(Action.RobotForwardDistance);
  					setTurnLeft(Action.RobotTurnDegree);
  					break;
  				case Action.RobotAheadTurnRight:
  					setAhead(Action.RobotForwardDistance);
  					setTurnRight(Action.RobotTurnDegree);
  					break;
  				case Action.RobotBackTurnLeft:
  					setBack(Action.RobotBackDistance);
  					setTurnRight(Action.RobotTurnDegree);
  					break;
  				case Action.RobotBackTurnRight:
  					setBack(Action.RobotBackDistance);
  					setTurnLeft(Action.RobotTurnDegree);
  					break;
  				case Action.RobotFire:
  					ahead(0);
  					turnLeft(0);
  					scanAndFire();
  					break;
  			}
	    	
	    	execute();
	    	while (getDistanceRemaining() != 0 || getTurnRemaining() != 0) execute();
	    	turnRadarRightRadians(2 * Math.PI);
	    	
	    	if(!NNFlag) {
//		    	action = lut.selectAction(state, Math.random() < epsilon);
//		    	//lut.updateLUTOff(state_old, state, action, reward);
//		    	lut.updateLUTOn(state_old, state, action_old, action, reward);
//		    	state_old = state;
//		    	action_old = action;
	    	}
	    	else {    		
	    		new_statesNN = getNNStates(); 	
	    		new_actionsNN = getActionwithHighQFromNN(new_statesNN);
			    
	    		double newQValue = BPTrain.errorCalculation(MergeStatesActions(new_statesNN,new_actionsNN));
	    		double oldQValue = BPTrain.errorCalculation(MergeStatesActions(old_statesNN,old_actionsNN));
	    		
	    		
	    		double deltaQValue = lut.LearningRate * (reward + lut.DiscountRate * newQValue - oldQValue);
	    		double[] old_stateActionTmp = MergeStatesActions(old_statesNN,old_actionsNN);
	    		
	    		int count = 0;
	    		for(int i = 0; i < 6;i++) {
	    			if(doubleEqual(old_stateActionTmp[i],stateActionPair[i])) {
	    				count++;	
	    			}
	    		}
	    		if(count==6) {
	    			saveDeltaQ(deltaQValue);	
	    		}
	    		
	    		double qTrainValue = oldQValue + deltaQValue;
	    		BPTrain.weightsTrain(qTrainValue);
	    		
			    actionNN = new_actionsNN;
			    
			    for(int i = 0 ; i < 5;i++) {
			    	old_statesNN[i] = new_statesNN[i];
			    }
			    old_actionsNN = new_actionsNN;
	    	}	
	    }
	}
	

	public void scanAndFire() {
		found = false;
		while (!found) {
			setTurnRadarLeft(360);
			execute();
		}
		turnGunLeft(getGunHeading() - getHeading() - oppoBearing);
		fire(3);
	}
	
	private double[] updateState() {
		int heading = States.getHeading(getHeadingRadians());
	    int targetDistance = States.getTargetDistance(oppoDist);
	    int targetBearing = States.getTargetBearing(oppoBearing);
	    int xPosition = States.getXPosition(getX());
		int yPosition = States.getYPosition(getY());

		double[] X = {heading, targetDistance, targetBearing, xPosition, yPosition};
		state = lut.indexFor(X);
		
		double[] stateActionTmp = {heading, targetDistance, targetBearing, xPosition, yPosition,0.0};
		return stateActionTmp;
	}
	
	
	private double[] getNNStates() {
		
		double []qStates = new double[5];
		
		qStates[0] = States.dimReduce_Heading(getHeadingRadians());
		qStates[1] = States.dimReduce_enemy_Distance(oppoDist);
		qStates[2] = States.dimReduce_enemy_Bearing(oppoBearing);
		qStates[3] = States.dimReduce_X_Axes(this.getX());
		qStates[4] = States.dimReduce_Y_Axes(this.getY());	
				
		return qStates;
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		oppoDist = e.getDistance();     
		oppoBearing = e.getBearing();   
		found = true;
	}
	
	public void onBulletHit(BulletHitEvent e) {
		if (useImmediateReward) reward += 28;
	}
	
	public void onBulletMissed(BulletMissedEvent e) {
		if (useImmediateReward) reward -= 5;
	}
	
	public void onHitByBullet(HitByBulletEvent e) {
		if (useImmediateReward) reward -= e.getBullet().getPower() * 6;
	}
	
	public void onHitWall(HitWallEvent e) {
		if (useImmediateReward) reward -= 5;
	}
	
	public void onWin(WinEvent event) {
		saveBattle(1);
		reward += 100;
	}
	
	public void onDeath(DeathEvent event) {
		saveBattle(0);
		reward -= 15;
	}
	
	public void loadData() { 
		try {
			lut.loadFile(getDataFile("LUT.dat"));
	    }
		catch (Exception e) {
		}
	}
	
	public void saveData() { 
		try {
			lut.save(getDataFile("LUT.dat"));
		}
		catch (Exception e) {
			out.println("Exception trying to write: " + e);
		}
	}
	
	public void saveBattle(int win) {
		PrintStream write = null;
		try {
			write = new PrintStream(new RobocodeFileOutputStream(getDataFile("battle_history.dat").getAbsolutePath(), true));
			write.println(new Double(win));
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
	
	public void saveDeltaQ(double deltaQ) {
		PrintStream write = null;
		try {
			write = new PrintStream(new RobocodeFileOutputStream(getDataFile("deltaQ.dat").getAbsolutePath(), true));
			write.println(deltaQ);
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
	
	private int getActionwithHighQFromNN(double[] state)
	{
		
		double q = Double.MIN_VALUE;
		double StateAction[] = new double[6]; 
		for(int i = 0 ;i < 5;i++) {
			StateAction[i] = state[i];
		}
		
		int action = 0;
		
		for(int i = 0 ; i < Action.numActions ;i++) {
			StateAction[5] = ((double)i) / 5;
			double qNN = BPTrain.errorCalculation(StateAction); 
			if(qNN > q) {
				q = qNN;
				action = i;
			}
			
		}		
		return action;
	}
	
	
	private double[] MergeStatesActions(double[] Arr1,int action) {
		
		double[] tmp = new double[6];
		for(int i = 0 ; i < 5;i++) {
			tmp[i] = Arr1[i];
		}
		tmp[5] = action;
		return tmp;
	}
	
	public boolean doubleEqual(double a, double b) {
        if ((a- b> -0.000001) && (a- b) < 0.000001)
            return true;
        else
            return false;
    }
	
}
