package com.sysflame.netdroid.models;

import static com.sysflame.netdroid.utils.LogUtils.LOGE;

/**
 * The type Data info.
 */
public class DataInfo {
	private long date;
	private double ping;
	private double download;
	private double upload;

	/**
	 * Instantiates a new Data info.
	 */
	public DataInfo () {
		this.date = 1577833200000L;
		this.ping = 0;
		this.download = 0;
		this.upload = 0;
	}

	/**
	 * Instantiates a new Data info.
	 *
	 * @param date     the date
	 * @param ping     the ping
	 * @param download the download
	 * @param upload   the upload
	 */
	public DataInfo (long date, double ping, double download, double upload) {
		this.date = date;
		this.ping = ping;
		this.download = download;
		this.upload = upload;
		LOGE ("TAG", "value added");
	}

	/**
	 * Gets date.
	 *
	 * @return the date
	 */
	public long getDate () {
		return date;
	}

	/**
	 * Sets date.
	 *
	 * @param date the date
	 */
	public void setDate (long date) {
		this.date = date;
	}

	/**
	 * Gets ping.
	 *
	 * @return the ping
	 */
	public double getPing () {
		return ping;
	}

	/**
	 * Sets ping.
	 *
	 * @param ping the ping
	 */
	public void setPing (double ping) {
		this.ping = ping;
	}

	/**
	 * Gets download.
	 *
	 * @return the download
	 */
	public double getDownload () {
		return download;
	}

	/**
	 * Sets download.
	 *
	 * @param download the download
	 */
	public void setDownload (double download) {
		this.download = download;
	}

	/**
	 * Gets upload.
	 *
	 * @return the upload
	 */
	public double getUpload () {
		return upload;
	}

	/**
	 * Sets upload.
	 *
	 * @param upload the upload
	 */
	public void setUpload (double upload) {
		this.upload = upload;
	}
}
