package neuralNetwork;

public class Stage {
	
	public static final double signalMultiplier = .1;
	
	private Stage prev;
	double[] output;
	byte[][] coeffs;
	
	Stage(Stage prev, int size){
		this.prev = prev;
		output = new double[size];
		if (prev != null)
			coeffs = new byte[size][prev.output.length+1];
		else
			coeffs = new byte[0][0];
	}
	/**
	 * calculates the outputs based on the input values
	 */
	void calc(){
		if (prev == null) return;
		for (int i = 0; i < coeffs.length; i++){
			double sum = 0;
			for (int j = 0; j < coeffs[0].length-1; j++){
				sum += coeffs[i][j]*prev.output[j];
			}
			sum += coeffs[i][coeffs[0].length-1]*signalMultiplier;  //constant bias
			output[i] = sigmoid(sum);
		}
	}
	private static double sigmoid(double x) {
		return signalMultiplier/(1+Math.exp(-x/2d));  //range: 0 .. multiplier
	}
	public String toString(){
		StringBuilder k = new StringBuilder("[");
		for (byte[] coeff : coeffs) {
			k.append("[");
			for (int j = 0; j < coeffs[0].length; j++) {
				k.append(coeff[j]).append(" ");
			}
			k.append("]\n ");
		}
		k.append("]\n");
		return k.toString();
	}

}
