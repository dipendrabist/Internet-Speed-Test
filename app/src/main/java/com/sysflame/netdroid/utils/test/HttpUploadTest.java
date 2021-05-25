package com.sysflame.netdroid.utils.test;

import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Http upload test.
 */
public class HttpUploadTest extends Thread {
	/**
	 * The Uploaded k byte.
	 */
	static int uploadedKByte = 0;
	/**
	 * The File url.
	 */
	public String fileURL = "";
	/**
	 * The Upload elapsed time.
	 */
	double uploadElapsedTime = 0;
	/**
	 * The Finished.
	 */
	boolean finished = false;
	/**
	 * The Elapsed time.
	 */
	double elapsedTime = 0;
	/**
	 * The Final upload rate.
	 */
	double finalUploadRate = 0.0;
	/**
	 * The Start time.
	 */
	long startTime;

	/**
	 * Instantiates a new Http upload test.
	 *
	 * @param fileURL the file url
	 */
	public HttpUploadTest (String fileURL) {
		this.fileURL = fileURL;
	}

	private double round (double value, int places) {
		if (places < 0) throw new IllegalArgumentException ();
		BigDecimal bd = BigDecimal.valueOf (value);
		bd = bd.setScale (places, RoundingMode.HALF_UP);
		return bd.doubleValue ();
	}

	/**
	 * Is finished boolean.
	 *
	 * @return the boolean
	 */
	public boolean isFinished () {
		return finished;
	}

	/**
	 * Gets instant upload rate.
	 *
	 * @return the instant upload rate
	 */
	public double getInstantUploadRate () {
		if (uploadedKByte >= 0) {
			long now = System.currentTimeMillis ();
			elapsedTime = (now - startTime) / 1000.0;
			return round (((uploadedKByte / 1000.0) * 8) / elapsedTime, 2);
		} else {
			return 0.0;
		}
	}

	/**
	 * Gets final upload rate.
	 *
	 * @return the final upload rate
	 */
	public double getFinalUploadRate () {
		return round (finalUploadRate, 2);
	}

	@Override
	public void run () {
		try {
			URL url = new URL (fileURL);
			uploadedKByte = 0;
			startTime = System.currentTimeMillis ();
			ExecutorService executor = Executors.newFixedThreadPool (4);
			for (int i = 0 ; i < 4 ; i++) {
				executor.execute (new HandlerUpload (url));
			}
			executor.shutdown ();
			while (!executor.isTerminated ()) {
				try {
					Thread.sleep (100);
				} catch (InterruptedException ex) {
					ex.printStackTrace ();
					Thread.currentThread ().interrupt ();
				}
			}
			long now = System.currentTimeMillis ();
			uploadElapsedTime = (now - startTime) / 1000.0;
			finalUploadRate = ((uploadedKByte / 1000.0) * 8) / uploadElapsedTime;
		} catch (Exception ex) {
			ex.printStackTrace ();
		}
		finished = true;
	}
}

/**
 * The type Handler upload.
 */
class HandlerUpload extends Thread {
	/**
	 * The Url.
	 */
	URL url;

	/**
	 * Instantiates a new Handler upload.
	 *
	 * @param url the url
	 */
	public HandlerUpload (URL url) {
		this.url = url;
	}

	public void run () {
		byte[] buffer = new byte[ 150 * 1024 ];
		long startTime = System.currentTimeMillis ();
		int timeout = 10;
		while (true) {
			try {
				HttpURLConnection conn = null;
				conn = (HttpURLConnection) url.openConnection ();
				conn.setDoOutput (true);
				conn.setRequestMethod ("POST");
				conn.setRequestProperty ("Connection", "Keep-Alive");
				DataOutputStream dos = new DataOutputStream (conn.getOutputStream ());
				dos.write (buffer, 0, buffer.length);
				dos.flush ();
				conn.getResponseCode ();
				HttpUploadTest.uploadedKByte += buffer.length / 1024.0;
				long endTime = System.currentTimeMillis ();
				double uploadElapsedTime = (endTime - startTime) / 1000.0;
				if (uploadElapsedTime >= timeout) {
					break;
				}
				dos.close ();
				conn.disconnect ();
			} catch (Exception ex) {
				ex.printStackTrace ();
			}
		}
	}
}
