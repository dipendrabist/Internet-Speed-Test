package com.sysflame.netdroid.utils.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * The type Ping test.
 */
public class PingTest extends Thread {
	/**
	 * The Result.
	 */
	HashMap<String, Object> result = new HashMap<> ();
	/**
	 * The Server.
	 */
	String server = "";
	/**
	 * The Count.
	 */
	int count;
	/**
	 * The Instant rtt.
	 */
	double instantRtt = 0;
	/**
	 * The Avg rtt.
	 */
	double avgRtt = 0.0;
	/**
	 * The Finished.
	 */
	boolean finished = false;
	/**
	 * The Started.
	 */
	boolean started = false;

	/**
	 * Instantiates a new Ping test.
	 *
	 * @param serverIpAddress the server ip address
	 * @param pingTryCount    the ping try count
	 */
	public PingTest (String serverIpAddress, int pingTryCount) {
		this.server = serverIpAddress;
		this.count = pingTryCount;
	}

	/**
	 * Gets avg rtt.
	 *
	 * @return the avg rtt
	 */
	public double getAvgRtt () {
		return avgRtt;
	}

	/**
	 * Gets instant rtt.
	 *
	 * @return the instant rtt
	 */
	public double getInstantRtt () {
		return instantRtt;
	}

	/**
	 * Is finished boolean.
	 *
	 * @return the boolean
	 */
	public boolean isFinished () {
		return finished;
	}

	@Override
	public void run () {
		try {
			ProcessBuilder ps = new ProcessBuilder ("ping", "-c " + count, this.server);
			ps.redirectErrorStream (true);
			Process pr = ps.start ();
			BufferedReader in = new BufferedReader (new InputStreamReader (pr.getInputStream ()));
			String line;
			String output = "";
			while ((line = in.readLine ()) != null) {
				if (line.contains ("icmp_seq")) {
					instantRtt = Double.parseDouble (line.split (" ")[ line.split (" ").length - 2 ].replace ("time=", ""));
				}
				if (line.startsWith ("rtt ")) {
					avgRtt = Double.parseDouble (line.split ("/")[ 4 ]);
					break;
				}
			}
			pr.waitFor ();
			in.close ();
		} catch (Exception e) {
			e.printStackTrace ();
		}
		finished = true;
	}
}
