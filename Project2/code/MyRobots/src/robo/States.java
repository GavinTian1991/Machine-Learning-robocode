package robo;

public class States {
	
	public static int NumHeading = 4;
	public static int NumTargetDistance = 4;
	public static int NumTargetBearing = 4;
	public static int NumXPosition = 8;
	public static int NumYPosition = 6;

	public static final int[][][][][] statesMap = new int[NumHeading][NumTargetDistance][NumTargetBearing][NumXPosition][NumYPosition];
	public static int numStates = 0;
	
	public static void init() {
		
		int count = 0;
		for (int i = 0; i < NumHeading; i++)
			for (int j = 0; j < NumTargetDistance; j++)
				for (int k = 0; k < NumTargetBearing; k++)
					for (int l = 0; l < NumXPosition; l++)
						for (int m = 0; m < NumYPosition; m++)
							statesMap[i][j][k][l][m] = count++;
		numStates = count;
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

}
