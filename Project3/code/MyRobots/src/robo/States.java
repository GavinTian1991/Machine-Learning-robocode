package robo;

/*
 * author:Xiaojun Tian:11240587
 * 
 * 
 */

public class States {
	
	public static int NumHeading = 4;
	public static int NumTargetDistance = 4;
	public static int NumTargetBearing = 4;
	public static int NumXPosition = 8;
	public static int NumYPosition = 6;

	public static final int[][][][][] statesMap = new int[NumHeading][NumTargetDistance][NumTargetBearing][NumXPosition][NumYPosition];
	public static int numStates = 0;
	
	public static double []fourStates;
	public static double []eightStates;
	public static double []sixStates;
	
	public static void init() {
		
		int count = 0;
		for (int i = 0; i < NumHeading; i++)
			for (int j = 0; j < NumTargetDistance; j++)
				for (int k = 0; k < NumTargetBearing; k++)
					for (int l = 0; l < NumXPosition; l++)
						for (int m = 0; m < NumYPosition; m++)
							statesMap[i][j][k][l][m] = count++;
		numStates = count;
		
		fourStates = new double[4];
		for(int i = 0 ; i < 4 ; i++) {
			double t1 = ((Math.floor((double)i) % 4 ) / (4 - 1))*2-1;
			fourStates[i] = t1;
		}
		
		
		eightStates = new double[8];
		for(int i = 0 ; i < 8 ; i++) {
			double t1 = ((Math.floor((double)i) % 8 ) / (8 - 1))*2-1;
			eightStates[i] = t1;
		}
		
		
		sixStates = new double[6];
		for(int i = 0 ; i < 6 ; i++) {
			double t1 = ((Math.floor((double)i) % 6 ) / (6 - 1))*2-1;
			sixStates[i] = t1;
		}
		
	}
	
	public static int getHeading(double arg){
		double unit = 2 * Math.PI / NumHeading;
		return (int)Math.floor(arg / unit);
	}

	public static int getTargetDistance(double arg){
		int temp=(int)(arg/100);
		if(temp > NumTargetDistance - 1) temp = NumTargetDistance - 1;
		return temp;
	}

	public static int getTargetBearing(double arg){
		double unit = 360.0 / NumTargetBearing;
		return (int)Math.floor((arg + 180) / unit);
	}

	public static int getXPosition(double arg) {
		double unit = 800 / NumXPosition;
		return (int)Math.floor(arg / unit);
	}

	public static int getYPosition(double arg) {
		double unit = 600 / NumYPosition;
		return (int)Math.floor(arg / unit);
	}
	
	
	public static double dimReduce_Heading(double Heading)
	{
		
		if(Heading==0)
			return fourStates[0];
		
		double unit = 2 * Math.PI / NumHeading;
		int hState = (int) Math.floor(Heading / unit);
		return fourStates[hState];
	}
	
	
	public static double dimReduce_enemy_Distance(double enemy_Distance)
	{	
		
		if(enemy_Distance<100)
			return fourStates[0];
		else if (enemy_Distance<200)
			return fourStates[1];
		else if (enemy_Distance<400)
			return fourStates[2];
		else 
			return fourStates[3];	
	}
	
	
	public static double dimReduce_enemy_Bearing(double enemy_Bearing)
	{	
		
		enemy_Bearing += 180;
		if(enemy_Bearing >= 0 && enemy_Bearing < 90)
			return fourStates[0];
		else if (enemy_Bearing >= 90 && enemy_Bearing < 180)
			return fourStates[1];
		else if (enemy_Bearing >= 180 && enemy_Bearing < 270)
			return fourStates[2];	
		else
			return fourStates[3];	
	}
	
	
	public static double dimReduce_X_Axes(double x)
	{
		double unit = 800 / NumXPosition;
		int unitpos = (int)Math.floor(x / unit);
		return eightStates[unitpos];
	}
	
	
	public static double dimReduce_Y_Axes(double y)
	{
		double unit = 600 / NumYPosition;
		int unitpos = (int)Math.floor(y / unit);
		return sixStates[unitpos];
	}
	
	
	
	
	
	
	

}
