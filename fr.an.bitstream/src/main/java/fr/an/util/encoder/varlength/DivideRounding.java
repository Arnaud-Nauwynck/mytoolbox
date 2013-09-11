package fr.an.util.encoder.varlength;

public class DivideRounding {

	/**
	 * 
	 */
	public static enum DivideRoundingMode {
		alwaysLower, // simpler implementation, but almost uniform 
		alwaysUpper, // simpler implementation, but almost uniform
		alternateLowerUpper // <= almost random...
		
		// TOADD: fillMaxPower2Lower // <= round as lower as as possible to fill all least significant bits
		// TOADD: fillMaxPower2Upper // <= round as lower as as possible to fill all most significant bits
		
//		others?...
	}
	
	private DivideRoundingMode divideRoundingMode;
	private boolean currentDivideRoundingUpper;

	// ------------------------------------------------------------------------
	
	public DivideRounding(DivideRoundingMode divideRoundingMode, boolean currentDivideRoundingUpper) {
		this.divideRoundingMode = divideRoundingMode;
		this.currentDivideRoundingUpper = currentDivideRoundingUpper;
	}

	public DivideRounding(DivideRounding src) {
		this(src.divideRoundingMode, src.currentDivideRoundingUpper);
	}
	
	public DivideRounding() {
		this(DivideRoundingMode.alternateLowerUpper, false);
	}
		
	// ------------------------------------------------------------------------
	
	public void setCurrentDivideRoundingUpper(boolean p) {
		this.currentDivideRoundingUpper = p;
	}

	public boolean getCurrentDivideRoundingUpper() {
		return currentDivideRoundingUpper;
	}

	public int roundDiv2(int value) {
		int res = value >> 1;
		switch(divideRoundingMode) {
		case alwaysLower:
			// do nothing
			break;
		case alwaysUpper:
			if (isOdd(value)) {
				res++;
			}
			break;
		case alternateLowerUpper:
			if (currentDivideRoundingUpper) {
				if (isOdd(value)) {
					res++;
				}
			}
			currentDivideRoundingUpper = !currentDivideRoundingUpper;
			break;
			
		// TODO add more 
		// case fillMaxPower2Lower:
		// case fillMaxPower2Upper:
			
		}
		return res;
	}
	
	private static boolean isOdd(int p) {
		return 0 != (p & 1);
	}
	
	
}
