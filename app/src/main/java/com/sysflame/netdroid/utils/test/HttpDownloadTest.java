package com.sysflame.netdroid.utils.test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.sysflame.netdroid.utils.LogUtils.LOGE;

/**
 * The type Http download test.
 */
public class HttpDownloadTest extends Thread {
	/**
	 * The File url.
	 */
	public String fileURL = "";
	/**
	 * The Start time.
	 */
	long startTime = 0;
	/**
	 * The End time.
	 */
	long endTime = 0;
	/**
	 * The Download elapsed time.
	 */
	double downloadElapsedTime = 0;
	/**
	 * The Downloaded byte.
	 */
	int downloadedByte = 0;
	/**
	 * The Final download rate.
	 */
	double finalDownloadRate = 0.0;
	/**
	 * The Finished.
	 */
	boolean finished = false;
	/**
	 * The Instant download rate.
	 */
	double instantDownloadRate = 0;
	/**
	 * The Timeout.
	 */
	int timeout = 15;
	/**
	 * The Http conn.
	 */
	HttpURLConnection httpConn = null;

	/**
	 * Instantiates a new Http download test.
	 *
	 * @param fileURL the file url
	 */
	public HttpDownloadTest (String fileURL) {
		this.fileURL = fileURL;
	}

	private double round (double value, int places) {
		if (places < 0) throw new IllegalArgumentException ();
		BigDecimal bd = BigDecimal.valueOf (value);
		bd = bd.setScale (places, RoundingMode.HALF_UP);
		return bd.doubleValue ();
	}

	/**
	 * Gets instant download rate.
	 *
	 * @return the instant download rate
	 */
	public double getInstantDownloadRate () {
		return instantDownloadRate;
	}

	/**
	 * Sets instant download rate.
	 *
	 * @param downloadedByte the downloaded byte
	 * @param elapsedTime    the elapsed time
	 */
	public void setInstantDownloadRate (int downloadedByte, double elapsedTime) {
		if (downloadedByte >= 0) {
			this.instantDownloadRate = round (((downloadedByte * 8) / (1000 * 1000)) / elapsedTime, 2);
		} else {
			this.instantDownloadRate = 0.0;
		}
	}

	/**
	 * Gets final download rate.
	 *
	 * @return the final download rate
	 */
	public double getFinalDownloadRate () {
		return round (finalDownloadRate, 2);
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
		URL url = null;
		downloadedByte = 0;
		int responseCode = 0;
		List<String> fileUrls = new ArrayList<> ();
		fileUrls.add (fileURL + "random4000x4000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		startTime = System.currentTimeMillis ();
		outer:
		for (String link : fileUrls) {
			try {
				url = new URL (link);
				httpConn = (HttpURLConnection) url.openConnection ();
				responseCode = httpConn.getResponseCode ();
			} catch (Exception ex) {
				ex.printStackTrace ();
			}
			try {
				if (responseCode == HttpURLConnection.HTTP_OK) {
					byte[] buffer = new byte[ 10240 ];
					InputStream inputStream = httpConn.getInputStream ();
					int len = 0;
					while ((len = inputStream.read (buffer)) != -1) {
						downloadedByte += len;
						endTime = System.currentTimeMillis ();
						downloadElapsedTime = (endTime - startTime) / 1000.0;
						setInstantDownloadRate (downloadedByte, downloadElapsedTime);
						if (downloadElapsedTime >= timeout) {
							break outer;
						}
					}
					inputStream.close ();
					httpConn.disconnect ();
				} else {
					LOGE ("TAG", "Link not found...");
				}
			} catch (Exception ex) {
				ex.printStackTrace ();
			}
		}
		endTime = System.currentTimeMillis ();
		downloadElapsedTime = (endTime - startTime) / 1000.0;
		finalDownloadRate = ((downloadedByte * 8) / (1000 * 1000.0)) / downloadElapsedTime;
		finished = true;
	}
}
