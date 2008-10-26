package de.jacavi.appl.track;

public class Statistics {

	private int lastLap = 0;

	private int bestLap = 0;

	private void setLastLap(int lastLap) {
		this.lastLap = lastLap;
	}

	public int getLastLap() {
		return lastLap;
	}

	private void setBestLap(int bestLap) {
		this.bestLap = bestLap;
	}

	public int getBestLap() {
		return bestLap;
	}

	/**
	 * @param lapTime
	 *            (ms)
	 */
	public void insertLapTime(int lapTime) {
		setLastLap(lapTime);
		if (bestLap == 0) {
			setBestLap(lastLap);
		} else {
			setBestLap(Math.min(lapTime, bestLap));
		}
	}

}
