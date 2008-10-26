package de.jacavi.appl.racelogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.track.Statistics;

public class RaceStatisticsManager {

	/**
	 * Listeners, which have to be invoked
	 */
	private final List<LapCompletionListener> listener = new ArrayList<LapCompletionListener>();

	/**
	 * Map that holds for each player the last inserted timestamp for lap
	 * calculation
	 */
	private final Map<Player, Long> offsetTimestamp = new HashMap<Player, Long>();

	public void addListener(LapCompletionListener l) {
		listener.add(l);
	}

	public void removeListener(LapCompletionListener l) {
		listener.remove(l);
	}

	public void fireLapCompleted(Player p) {
		Statistics stats = p.getRaceStatistic();
		long now = System.currentTimeMillis();
		long offsetTime = offsetTimestamp.get(p).longValue();
		stats.insertLapTime((int) (now - offsetTime));
		offsetTimestamp.put(p, now);

		for (LapCompletionListener l : listener) {
			l.onLapCompleted(p);
		}
	}

	@SuppressWarnings("unchecked")
	public void init() {
		long now = System.currentTimeMillis();
		for (Player player : (ArrayList<Player>) ContextLoader
				.getBean("playersBean")) {
			player.setRaceStatistic(new Statistics());
			offsetTimestamp.put(player, now);
		}
	}
}
