package com.sysflame.netdroid.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The type Get speed test hosts handler.
 */
public class GetSpeedTestHostsHandler extends Thread {
	/**
	 * The Map key.
	 */
	HashMap<Integer, String> mapKey = new HashMap<> ();
	/**
	 * The Map value.
	 */
	HashMap<Integer, List<String>> mapValue = new HashMap<> ();
	/**
	 * The Self lat.
	 */
	double selfLat = 0.0;
	/**
	 * The Self lon.
	 */
	double selfLon = 0.0;
	/**
	 * The Finished.
	 */
	boolean finished = false;

	/**
	 * Gets map key.
	 *
	 * @return the map key
	 */
	public HashMap<Integer, String> getMapKey () {
		return mapKey;
	}

	/**
	 * Gets map value.
	 *
	 * @return the map value
	 */
	public HashMap<Integer, List<String>> getMapValue () {
		return mapValue;
	}

	/**
	 * Gets self lat.
	 *
	 * @return the self lat
	 */
	public double getSelfLat () {
		return selfLat;
	}

	/**
	 * Gets self lon.
	 *
	 * @return the self lon
	 */
	public double getSelfLon () {
		return selfLon;
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
			URL url = new URL ("https://www.speedtest.net/speedtest-config.php");
			InputStream is = url.openStream ();
			int ptr = 0;
			StringBuilder buffer = new StringBuilder ();
			while ((ptr = is.read ()) != -1) {
				buffer.append ((char) ptr);
				if (!buffer.toString ().contains ("isp=")) {
					continue;
				}
				selfLat = Double.parseDouble (buffer.toString ().split ("lat=\"")[ 1 ].split (" ")[ 0 ].replace ("\"", ""));
				selfLon = Double.parseDouble (buffer.toString ().split ("lon=\"")[ 1 ].split (" ")[ 0 ].replace ("\"", ""));
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace ();
			return;
		}
		String uploadAddress = "";
		String name = "";
		String country = "";
		String cc = "";
		String sponsor = "";
		String lat = "";
		String lon = "";
		String host = "";
		int count = 0;
		try {
			URL url = new URL ("https://www.speedtest.net/speedtest-servers-static.php");
			InputStream is = url.openStream ();
			BufferedReader br = new BufferedReader (new InputStreamReader (is));
			String line;
			while ((line = br.readLine ()) != null) {
				if (line.contains ("<server url")) {
					uploadAddress = line.split ("server url=\"")[ 1 ].split ("\"")[ 0 ];
					lat = line.split ("lat=\"")[ 1 ].split ("\"")[ 0 ];
					lon = line.split ("lon=\"")[ 1 ].split ("\"")[ 0 ];
					name = line.split ("name=\"")[ 1 ].split ("\"")[ 0 ];
					country = line.split ("country=\"")[ 1 ].split ("\"")[ 0 ];
					cc = line.split ("cc=\"")[ 1 ].split ("\"")[ 0 ];
					sponsor = line.split ("sponsor=\"")[ 1 ].split ("\"")[ 0 ];
					host = line.split ("host=\"")[ 1 ].split ("\"")[ 0 ];
					List<String> ls = Arrays.asList (lat, lon, name, country, cc, sponsor, host);
					mapKey.put (count, uploadAddress);
					mapValue.put (count, ls);
					count++;
				}
			}
			br.close ();
		} catch (Exception ex) {
			ex.printStackTrace ();
		}
		finished = true;
	}
}
