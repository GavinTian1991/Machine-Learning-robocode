package robo;

/*
 * author:Xiaojun Tian:11240587
 * 
 * 
 */

import robocode.AdvancedRobot;
import robocode.*;

import java.awt.*;   
import java.io.*;


public class myRobot extends AdvancedRobot {
	
	int state_old, state, action_old, action;
	double reward;
	double oppoDist, oppoBearing;
	boolean found = false;
	double epsilon = 0; 
	boolean useImmediateReward = true;  

	LUT lut = new LUT();
	int round = 1;
	
	public void run() {
		
		loadData();
		setColors(Color.pink, Color.green, Color.red);
	    setAdjustGunForRobotTurn(false); 
	    setAdjustRadarForGunTurn(false); 
	    turnRadarRightRadians(2 * Math.PI);
	    
    	updateState();
    	action = lut.selectAction(state, Math.random() < epsilon);
    	state_old = state;
    	action_old = action;
	    while (true) {
	    	reward = 0.0;
	    	switch (action)
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
	    	updateState();
	    	action = lut.selectAction(state, Math.random() < epsilon);
	    	
	    	//lut.updateLUTOff(state_old, state, action, reward);
	    	lut.updateLUTOn(state_old, state, action_old, action, reward);
	    	state_old = state;
	    	action_old = action;
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
	
	private void updateState() {
		int heading = States.getHeading(getHeadingRadians());
	    int targetDistance = States.getTargetDistance(oppoDist);
	    int targetBearing = States.getTargetBearing(oppoBearing);
	    int xPosition = States.getXPosition(getX());
		int yPosition = States.getYPosition(getY());

		double[] X = {heading, targetDistance, targetBearing, xPosition, yPosition};
	    state = lut.indexFor(X);
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
		saveData();
		reward += 100;
	}
	
	public void onDeath(DeathEvent event) {
		saveData();
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
}
