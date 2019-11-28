package org.alicebot.ab;

public class Timer {

	private long startTimeMillis;

	public Timer() {
		start();
	}

	public void start() {
		this.startTimeMillis = System.currentTimeMillis();
	}

	public long elapsedTimeMillis() {
		return System.currentTimeMillis() - this.startTimeMillis + 1L;
	}

	public long elapsedRestartMs() {
		long ms = System.currentTimeMillis() - this.startTimeMillis + 1L;
		start();
		return ms;
	}

	public float elapsedTimeSecs() {
		return (float) elapsedTimeMillis() / 1000.0F;
	}

	public float elapsedTimeMins() {
		return elapsedTimeSecs() / 60.0F;
	}

}
