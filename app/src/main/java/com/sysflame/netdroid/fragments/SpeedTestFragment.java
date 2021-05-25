package com.sysflame.netdroid.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sysflame.netdroid.R;
import com.sysflame.netdroid.custom_ui.TickProgressBar;
import com.sysflame.netdroid.models.AdsSpeedTest;
import com.sysflame.netdroid.utils.ConnectionDetector;
import com.sysflame.netdroid.utils.GetSpeedTestHostsHandler;
import com.sysflame.netdroid.utils.test.HttpDownloadTest;
import com.sysflame.netdroid.utils.test.HttpUploadTest;
import com.sysflame.netdroid.utils.test.PingTest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.sysflame.netdroid.utils.LogUtils.LOGE;
import static com.sysflame.netdroid.utils.LogUtils.LOGI;
import static com.sysflame.netdroid.utils.LogUtils.makeLogTag;

/**
 * The type Speed test fragment.
 */
public class SpeedTestFragment extends Fragment {
	private static final String TAG = makeLogTag (SpeedTestFragment.class);
	private final DecimalFormat dec = new DecimalFormat ("#.##");
	/**
	 * The View.
	 */
	View view;
	/**
	 * The Position.
	 */
	int position = 0;
	/**
	 * The Last position.
	 */
	int lastPosition = 0;
	/**
	 * The Get speed test hosts handler.
	 */
	GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
	/**
	 * The Temp black list.
	 */
	HashSet<String> tempBlackList;
	/**
	 * The Tv blink.
	 */
	TextView tvBlink;
	/**
	 * The Upload addr.
	 */
	String uploadAddr;
	/**
	 * The Info.
	 */
	List<String> info;
	/**
	 * The Distance.
	 */
	double distance;
	/**
	 * The Is internet present.
	 */
	Boolean isInternetPresent = false;
	/**
	 * The Cd.
	 */
	ConnectionDetector cd;
	/**
	 * The M context.
	 */
	Context mContext;
	private LineChart lcMeasure;
	private LineDataSet lineDataSet;
	private LineData lineData;
	private TickProgressBar tickProgressMeasure;
	private ImageView ivPBDownload;
	private ImageView ivPBUpload;
	private ImageView tvBegin;
	private TextView tvPingL;
	private TextView tvDownloadL;
	private TextView tvUploadL;
	private TextView tvPing;
	private TextView tvDownload;
	private TextView tvUpload;
	private float i = 0, j = 0, k = 0;
	private TextView tvDownloadU;
	private TextView tvUploadU;
	private SharedPreferences sharedPref;
	private AdsSpeedTest adsSpeedTest;
	private boolean testing = false;

	@Nullable
	@Override
	public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate (R.layout.fragment_speed_test, container, false);
		mContext = getActivity ();
		init ();
		return view;
	}

	@Override
	public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated (view, savedInstanceState);
	}

	/**
	 * Init.
	 */
	public void init () {
		adsSpeedTest = new AdsSpeedTest (getActivity ());
		adsSpeedTest.initInterstitial ();
		adsSpeedTest.getInterstitialAd ().setAdListener (new AdListener () {
			@Override
			public void onAdLoaded () {
				LOGI (TAG, "onAdLoaded()");
			}

			@Override
			public void onAdFailedToLoad (LoadAdError loadAdError) {
				super.onAdFailedToLoad (loadAdError);
				if (!adsSpeedTest.getInterstitialAd ().isLoading () && !adsSpeedTest.getInterstitialAd ().isLoaded ()) {
					adsSpeedTest.initInterstitial ();
				}
				if (testing) {
					testSpeed ();
				}
			}

			@Override
			public void onAdOpened () {
				LOGI (TAG, "onAdOpened()");
			}

			@Override
			public void onAdClicked () {
				LOGI (TAG, "onAdClicked()");
			}

			@Override
			public void onAdLeftApplication () {
				LOGI (TAG, "onAdLeftApplication()");
			}

			@Override
			public void onAdClosed () {
				LOGI (TAG, "onAdClosed()");
				if (!adsSpeedTest.getInterstitialAd ().isLoading () && !adsSpeedTest.getInterstitialAd ().isLoaded ()) {
					adsSpeedTest.initInterstitial ();
				}
				if (testing) {
					testSpeed ();
				}
			}
		});
		sharedPref = getActivity ().getSharedPreferences (
				"setting", Context.MODE_PRIVATE);
		cd = new ConnectionDetector (getActivity ());
		isInternetPresent = cd.isConnectingToInternet ();
		tvDownloadU = view.findViewById (R.id.tv_download_unit);
		tvDownloadU.setText (sharedPref.getString ("UNIT", "Mbps"));
		tvUploadU = view.findViewById (R.id.tv_upload_unit);
		tvUploadU.setText (sharedPref.getString ("UNIT", "Mbps"));
		tvBlink = view.findViewById (R.id.tv_information);
		tvBegin = view.findViewById (R.id.tv_start);
		tvPing = view.findViewById (R.id.tv_ping_value);
		tvDownload = view.findViewById (R.id.tv_download_value);
		tvUpload = view.findViewById (R.id.tv_upload_value);
		lcMeasure = view.findViewById (R.id.linechart);
		tickProgressMeasure = view.findViewById (R.id.tickProgressBar);
		ivPBDownload = view.findViewById (R.id.iv_download);
		ivPBUpload = view.findViewById (R.id.iv_upload);
		tvPingL = view.findViewById (R.id.tv_ping_label);
		tickProgressMeasure.setMax (100 * 100);
		tvPingL.post (() -> {
			int length = tvPingL.getMeasuredWidth ();
			float angle = 45;
			Shader textShader = new LinearGradient (0, 0, (int) (Math.sin (Math.PI * angle / 180) * length),
					(int) (Math.cos (Math.PI * angle / 180) * length),
					new int[] {0xFF30E3CA, 0xFFa5dee5},
					null,
					Shader.TileMode.CLAMP);
			tvPingL.getPaint ().setShader (textShader);
			tvPingL.invalidate ();
		});
		tvDownloadL = view.findViewById (R.id.tv_download_label);
		tvDownloadL.post (() -> {
			int length = tvDownloadL.getMeasuredWidth ();
			float angle = 45;
			Shader textShader = new LinearGradient (0, 0, (int) (Math.sin (Math.PI * angle / 180) * length),
					(int) (Math.cos (Math.PI * angle / 180) * length),
					new int[] {0xFF30E3CA, 0xFFa5dee5},
					null,
					Shader.TileMode.CLAMP);
			tvDownloadL.getPaint ().setShader (textShader);
			tvDownloadL.invalidate ();
		});
		tvUploadL = view.findViewById (R.id.tv_upload_label);
		tvUploadL.post (() -> {
			int length = tvUploadL.getMeasuredWidth ();
			float angle = 45;
			Shader textShader = new LinearGradient (0, 0, (int) (Math.sin (Math.PI * angle / 180) * length),
					(int) (Math.cos (Math.PI * angle / 180) * length),
					new int[] {0xFF30E3CA, 0xFFa5dee5},
					null,
					Shader.TileMode.CLAMP);
			tvUploadL.getPaint ().setShader (textShader);
			tvUploadL.invalidate ();
		});
		ivPBUpload.setAlpha (0.5f);
		ivPBDownload.setAlpha (0.5f);
		tvBegin.setImageResource (R.drawable.ic_play);
		List<Entry> entryList = new ArrayList<> ();
		entryList.add (new Entry (0, 0));
		lineDataSet = new LineDataSet (entryList, "");
		lineDataSet.setMode (LineDataSet.Mode.CUBIC_BEZIER);
		lineDataSet.setDrawValues (false);
		lineDataSet.setDrawCircleHole (false);
		lineDataSet.setColor (Color.rgb (145, 174, 210));
		lineDataSet.setCircleColor (Color.rgb (145, 174, 210));
		lineDataSet.setLineWidth (2f);
		lineDataSet.setDrawFilled (false);
		lineDataSet.setHighlightEnabled (false);
		lineDataSet.setDrawCircles (false);
		lineData = new LineData (lineDataSet);
		lcMeasure.setData (lineData);
		lcMeasure.getAxisRight ().setDrawGridLines (false);
		lcMeasure.getAxisRight ().setDrawLabels (false);
		lcMeasure.getAxisLeft ().setDrawGridLines (false);
		lcMeasure.getAxisLeft ().setDrawLabels (false);
		lcMeasure.fitScreen ();
		lcMeasure.setVisibleXRange (0, 10);
		lcMeasure.setNoDataText ("TAP SCAN");
		lcMeasure.setNoDataTextColor (R.color.cp_0);
		lcMeasure.getXAxis ().setDrawGridLines (false);
		lcMeasure.getXAxis ().setDrawLabels (false);
		lcMeasure.getLegend ().setEnabled (false);
		lcMeasure.getDescription ().setEnabled (false);
		lcMeasure.setScaleEnabled (true);
		lcMeasure.setDrawBorders (false);
		lcMeasure.getAxisLeft ().setEnabled (false);
		lcMeasure.getAxisRight ().setEnabled (false);
		lcMeasure.getXAxis ().setEnabled (false);
		lcMeasure.setViewPortOffsets (10f, 10f, 10f, 10f);
		lcMeasure.animateX (1000);
		tempBlackList = new HashSet<> ();
		getSpeedTestHostsHandler = new GetSpeedTestHostsHandler ();
		getSpeedTestHostsHandler.start ();
		defaultValues ();
		tvBegin.setOnClickListener (v -> {
			testing = true;
			if (adsSpeedTest.getInterstitialAd () != null && adsSpeedTest.getInterstitialAd ().isLoaded ()) {
				adsSpeedTest.getInterstitialAd ().show ();
			} else {
				LOGI (TAG, "d did not load");
				testSpeed ();
			}
		});
	}

	private int getPositionByRate (double rate) {
		if (rate <= 1) {
			return (int) (rate * 30);
		} else if (rate <= 2) {
			return (int) (rate * 3) + 30;
		} else if (rate <= 3) {
			return (int) (rate * 3) + 60;
		} else if (rate <= 4) {
			return (int) (rate * 3) + 90;
		} else if (rate <= 5) {
			return (int) (rate * 3) + 120;
		} else if (rate <= 10) {
			return (int) ((rate - 5) * 6) + 150;
		} else if (rate <= 50) {
			return (int) ((rate - 10) * 1.33) + 180;
		} else if (rate <= 100) {
			return (int) ((rate - 50) * 0.6) + 180;
		}
		return 0;
	}

	private void testSpeed () {
		tvBegin.setImageResource (R.drawable.ic_stop);
		tvBlink.setVisibility (View.VISIBLE);
		Animation anim = new AlphaAnimation (0.0f, 1.0f);
		anim.setDuration (650);
		anim.setStartOffset (20);
		anim.setRepeatMode (Animation.REVERSE);
		anim.setRepeatCount (Animation.INFINITE);
		tvBlink.startAnimation (anim);
		if (getSpeedTestHostsHandler == null) {
			getSpeedTestHostsHandler = new GetSpeedTestHostsHandler ();
			getSpeedTestHostsHandler.start ();
		}
		new Thread (() -> {
			if (getActivity () == null)
				return;
			try {
				getActivity ().runOnUiThread (() -> tvBlink.setText ("Find the Best Server"));
			} catch (Exception e) {
				e.printStackTrace ();
			}
			int timeCount = 600;
			while (!getSpeedTestHostsHandler.isFinished ()) {
				timeCount--;
				try {
					Thread.sleep (100);
				} catch (InterruptedException e) {
					e.printStackTrace ();
					Thread.currentThread ().interrupt ();
				}
				if (timeCount <= 0) {
					if (getActivity () == null)
						return;
					try {
						getActivity ().runOnUiThread (() -> {
							tvBlink.clearAnimation ();
							tvBlink.setVisibility (View.GONE);
							tvBlink.setText ("No Connection...");
							tvBegin.setImageResource (R.drawable.ic_play);
						});
						getSpeedTestHostsHandler = null;
						return;
					} catch (Exception e) {
						e.printStackTrace ();
					}
				}
			}
			HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey ();
			HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue ();
			double selfLat = getSpeedTestHostsHandler.getSelfLat ();
			double selfLon = getSpeedTestHostsHandler.getSelfLon ();
			double tmp = 19349458;
			double dist = 0.0;
			int findServerIndex = 0;
			for (int index : mapKey.keySet ()) {
				if (tempBlackList.contains (mapValue.get (index).get (5))) {
					continue;
				}
				Location source = new Location ("Source");
				source.setLatitude (selfLat);
				source.setLongitude (selfLon);
				List<String> ls = mapValue.get (index);
				Location dest = new Location ("Dest");
				dest.setLatitude (Double.parseDouble (ls.get (0)));
				dest.setLongitude (Double.parseDouble (ls.get (1)));
				distance = source.distanceTo (dest);
				if (tmp > distance) {
					tmp = distance;
					dist = distance;
					findServerIndex = index;
				}
			}
			uploadAddr = mapKey.get (findServerIndex);
			info = mapValue.get (findServerIndex);
			distance = dist;
			if (info != null) {
				if (info.size () > 0) {
					getActivity ().runOnUiThread (() -> {
						tvBlink.clearAnimation ();
						tvBlink.setVisibility (View.VISIBLE);
						tvBlink.setText (String.format ("Hosted by %s (%s) [%s km]", info.get (5), info.get (3), new DecimalFormat ("#.##").format (distance / 1000)));
					});
					getActivity ().runOnUiThread (() -> {
						tvPing.setText ("0");
						tvDownload.setText ("0");
						tvUpload.setText ("0");
						i = 0f;
						j = 0f;
						k = 0f;
						ivPBDownload.setAlpha (0.5f);
						ivPBUpload.setAlpha (0.5f);
					});
					final List<Double> pingRateList = new ArrayList<> ();
					final List<Double> downloadRateList = new ArrayList<> ();
					final List<Double> uploadRateList = new ArrayList<> ();
					Boolean pingTestStarted = false;
					Boolean pingTestFinished = false;
					Boolean downloadTestStarted = false;
					Boolean downloadTestFinished = false;
					Boolean uploadTestStarted = false;
					Boolean uploadTestFinished = false;
					final PingTest pingTest = new PingTest (info.get (6).replace (":8080", ""), 6);
					final HttpDownloadTest downloadTest = new HttpDownloadTest (uploadAddr.replace (uploadAddr.split ("/")[ uploadAddr.split ("/").length - 1 ], ""));
					final HttpUploadTest uploadTest = new HttpUploadTest (uploadAddr);
					while (true) {
						if (!pingTestStarted) {
							pingTest.start ();
							pingTestStarted = true;
						}
						if (pingTestFinished && !downloadTestStarted) {
							downloadTest.start ();
							downloadTestStarted = true;
						}
						if (downloadTestFinished && !uploadTestStarted) {
							uploadTest.start ();
							uploadTestStarted = true;
						}
						if (pingTestFinished) {
							if (pingTest.getAvgRtt () == 0) {
								LOGE ("TAG", "");
							} else {
								if (getActivity () == null)
									return;
								try {
									getActivity ().runOnUiThread (() -> {
										tickProgressMeasure.setmPUnit ("ms");
										tvPing.setText (dec.format (pingTest.getAvgRtt ()) + "");
									});
								} catch (Exception e) {
									e.printStackTrace ();
								}
							}
						} else {
							pingRateList.add (pingTest.getInstantRtt ());
							if (getActivity () == null)
								return;
							try {
								getActivity ().runOnUiThread (() -> {
									LOGI ("TAG", "i = " + i);
									tickProgressMeasure.setmPUnit ("ms");
									tvPing.setText (dec.format (pingTest.getInstantRtt ()) + "");
									LOGE ("PING", "" + pingTest.getInstantRtt ());
									tickProgressMeasure.setProgress ((int) (pingTest.getInstantRtt () * 100));
									if (i == 0) {
										lcMeasure.clear ();
										lineDataSet.clear ();
										lineDataSet.setColor (Color.rgb (255, 207, 223));
										lineData = new LineData (lineDataSet);
										lcMeasure.setData (lineData);
										lcMeasure.invalidate ();
									}
									if (i > 10) {
										LineData data = lcMeasure.getData ();
										LineDataSet set = (LineDataSet) data.getDataSetByIndex (0);
										if (set != null) {
											data.addEntry (new Entry (i, (float) (10 * pingTest.getInstantRtt ())), 0);
											lcMeasure.notifyDataSetChanged ();
											lcMeasure.setVisibleXRange (0, i);
											lcMeasure.invalidate ();
										}
									} else {
										lcMeasure.setVisibleXRange (0, 10);
										LineData data = lcMeasure.getData ();
										LineDataSet set = (LineDataSet) data.getDataSetByIndex (0);
										if (set != null) {
											data.addEntry (new Entry (i, (float) (10 * pingTest.getInstantRtt ())), 0);
											lcMeasure.notifyDataSetChanged ();
											lcMeasure.invalidate ();
										}
									}
									i++;
								});
							} catch (Exception e) {
								e.printStackTrace ();
							}
						}
						if (pingTestFinished) {
							if (downloadTestFinished) {
								if (downloadTest.getFinalDownloadRate () == 0) {
									LOGE ("TAG", "");
								} else {
									if (getActivity () == null)
										return;
									try {
										getActivity ().runOnUiThread (() -> {
											tickProgressMeasure.setmPUnit (sharedPref.getString ("UNIT", "Mbps"));
											switch (sharedPref.getString ("UNIT", "Mbps")) {
												case "MBps":
													tvDownload.setText (dec.format (0.125 * downloadTest.getFinalDownloadRate ()) + "");
													break;
												case "kBps":
													tvDownload.setText (dec.format (125 * downloadTest.getFinalDownloadRate ()) + "");
													break;
												case "Mbps":
													tvDownload.setText (dec.format (downloadTest.getFinalDownloadRate ()) + "");
													break;
												case "kbps":
													tvDownload.setText (dec.format (1000 * downloadTest.getFinalDownloadRate ()) + "");
													break;
												default:
													break;
											}
										});
									} catch (Exception e) {
										e.printStackTrace ();
									}
								}
							} else {
								double downloadRate = downloadTest.getInstantDownloadRate ();
								downloadRateList.add (downloadRate);
								position = getPositionByRate (downloadRate);
								if (getActivity () == null)
									return;
								try {
									getActivity ().runOnUiThread (() -> {
										LOGI ("TAG", "j = " + j);
										tickProgressMeasure.setmPUnit (sharedPref.getString ("UNIT", "Mbps"));
										switch (sharedPref.getString ("UNIT", "Mbps")) {
											case "MBps":
												tvDownload.setText (dec.format (0.125 * downloadTest.getInstantDownloadRate ()) + "");
												tickProgressMeasure.setProgress ((int) (0.125 * downloadTest.getInstantDownloadRate () * 100));
												break;
											case "kBps":
												tvDownload.setText (dec.format (125 * downloadTest.getInstantDownloadRate ()) + "");
												tickProgressMeasure.setProgress ((int) (125 * downloadTest.getInstantDownloadRate () * 100));
												break;
											case "Mbps":
												tvDownload.setText (dec.format (downloadTest.getInstantDownloadRate ()) + "");
												tickProgressMeasure.setProgress ((int) (downloadTest.getInstantDownloadRate () * 100));
												break;
											case "kbps":
												tvDownload.setText (dec.format (1000 * downloadTest.getInstantDownloadRate ()) + "");
												tickProgressMeasure.setProgress ((int) (1000 * downloadTest.getInstantDownloadRate () * 100));
												break;
											default:
												break;
										}
										LOGE ("DOWNLOAD", "" + downloadTest.getInstantDownloadRate ());
										if (j == 0) {
											ivPBDownload.setAlpha (1.0f);
											ivPBUpload.setAlpha (0.5f);
											lcMeasure.clear ();
											lineDataSet.clear ();
											lineDataSet.setColor (Color.rgb (224, 249, 181));
											lineData = new LineData (lineDataSet);
											lcMeasure.setData (lineData);
											lcMeasure.invalidate ();
										}
										if (j > 100) {
											LineData data = lcMeasure.getData ();
											LineDataSet set = (LineDataSet) data.getDataSetByIndex (0);
											if (set != null) {
												data.addEntry (new Entry (j, (float) (1000 * downloadTest.getInstantDownloadRate ())), 0);
												lcMeasure.notifyDataSetChanged ();
												lcMeasure.setVisibleXRange (0, j);
												lcMeasure.invalidate ();
											}
										} else {
											lcMeasure.setVisibleXRange (0, 100);
											LineData data = lcMeasure.getData ();
											LineDataSet set = (LineDataSet) data.getDataSetByIndex (0);
											if (set != null) {
												data.addEntry (new Entry (j, (float) (1000 * downloadTest.getInstantDownloadRate ())), 0);
												lcMeasure.notifyDataSetChanged ();
												lcMeasure.invalidate ();
											}
										}
										j++;
									});
									lastPosition = position;
								} catch (Exception e) {
									e.printStackTrace ();
								}
							}
						}
						if (downloadTestFinished) {
							if (uploadTestFinished) {
								if (uploadTest.getFinalUploadRate () == 0) {
									LOGE ("TAG", "");
								} else {
									if (getActivity () == null)
										return;
									try {
										getActivity ().runOnUiThread (() -> {
											tickProgressMeasure.setmPUnit (sharedPref.getString ("UNIT", "Mbps"));
											switch (sharedPref.getString ("UNIT", "Mbps")) {
												case "MBps":
													tvUpload.setText (String.format ("%.1f", dec.format (0.125 * uploadTest.getFinalUploadRate ())));
													break;
												case "kBps":
													tvUpload.setText (String.format ("%.1f", dec.format (125 * uploadTest.getFinalUploadRate ())));
													break;
												case "Mbps":
													tvUpload.setText (String.format ("%.1f", dec.format (uploadTest.getFinalUploadRate ())));
													break;
												case "kbps":
													tvUpload.setText (String.format ("%.1f", dec.format (1000 * uploadTest.getFinalUploadRate ())));
													break;
												default:
													LOGE ("TAG", "ERROR");
													break;
											}
										});
									} catch (Exception e) {
										e.printStackTrace ();
									}
								}
							} else {
								double uploadRate = uploadTest.getInstantUploadRate ();
								uploadRateList.add (uploadRate);
								position = getPositionByRate (uploadRate);
								if (getActivity () == null)
									return;
								try {
									getActivity ().runOnUiThread (() -> {
										tickProgressMeasure.setmPUnit (sharedPref.getString ("UNIT", "Mbps"));
										switch (sharedPref.getString ("UNIT", "Mbps")) {
											case "MBps":
												tvUpload.setText (dec.format (0.125 * uploadTest.getInstantUploadRate ()) + "");
												tickProgressMeasure.setProgress ((int) (0.125 * uploadTest.getInstantUploadRate () * 100));
												break;
											case "kBps":
												tvUpload.setText (dec.format (125 * uploadTest.getInstantUploadRate ()) + "");
												tickProgressMeasure.setProgress ((int) (125 * uploadTest.getInstantUploadRate () * 100));
												break;
											case "Mbps":
												tvUpload.setText (dec.format (uploadTest.getInstantUploadRate ()) + "");
												tickProgressMeasure.setProgress ((int) (uploadTest.getInstantUploadRate () * 100));
												break;
											case "kbps":
												tvUpload.setText (dec.format (1000 * uploadTest.getInstantUploadRate ()) + "");
												tickProgressMeasure.setProgress ((int) (1000 * uploadTest.getInstantUploadRate () * 100));
												break;
											default:
												LOGE ("TAG", "ERROR");
												break;
										}
										LOGI ("TAG", "k = " + k);
										LOGE ("UPLOAD", "" + uploadTest.getInstantUploadRate ());
										if (k == 0) {
											ivPBDownload.setAlpha (1.0f);
											ivPBUpload.setAlpha (1.0f);
											lcMeasure.clear ();
											lineDataSet.clear ();
											lineDataSet.setColor (Color.rgb (145, 174, 210));
											lineData = new LineData (lineDataSet);
											lcMeasure.setData (lineData);
											lcMeasure.invalidate ();
										}
										if (k > 100) {
											LineData data = lcMeasure.getData ();
											LineDataSet set = (LineDataSet) data.getDataSetByIndex (0);
											if (set != null) {
												data.addEntry (new Entry (k, (float) (1000 * uploadTest.getInstantUploadRate ())), 0);
												lcMeasure.notifyDataSetChanged ();
												lcMeasure.setVisibleXRange (0, k);
												lcMeasure.invalidate ();
											}
										} else {
											lcMeasure.setVisibleXRange (0, 100);
											LineData data = lcMeasure.getData ();
											LineDataSet set = (LineDataSet) data.getDataSetByIndex (0);
											if (set != null) {
												data.addEntry (new Entry (k, (float) (1000 * uploadTest.getInstantUploadRate ())), 0);
												lcMeasure.notifyDataSetChanged ();
												lcMeasure.invalidate ();
											}
										}
										k++;
									});
								} catch (Exception e) {
									e.printStackTrace ();
								}
								lastPosition = position;
							}
						}
						if (pingTestFinished && downloadTestFinished && uploadTest.isFinished ()) {
							break;
						}
						if (pingTest.isFinished ()) {
							pingTestFinished = true;
						}
						if (downloadTest.isFinished ()) {
							downloadTestFinished = true;
						}
						if (uploadTest.isFinished ()) {
							uploadTestFinished = true;
						}
						if (pingTestStarted && !pingTestFinished) {
							try {
								Thread.sleep (300);
							} catch (InterruptedException e) {
								e.printStackTrace ();
								Thread.currentThread ().interrupt ();
							}
						} else {
							try {
								Thread.sleep (100);
							} catch (InterruptedException e) {
								e.printStackTrace ();
								Thread.currentThread ().interrupt ();
							}
						}
					}
					if (getActivity () == null)
						return;
					try {
						getActivity ().runOnUiThread (() -> {
							tvBegin.setImageResource (R.drawable.ic_play);
							LOGE ("TAG", "test1");
							SharedPreferences sharedPrefHistory = getActivity ().getSharedPreferences (
									"historydata", Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = sharedPrefHistory.edit ();
							String _data = sharedPrefHistory.getString ("DATA", "");
							if (!_data.equals ("")) {
								LOGE ("TAG", "1");
								JSONObject jsondata = new JSONObject ();
								try {
									jsondata.put ("date", String.valueOf (System.currentTimeMillis ()));
									jsondata.put ("ping", tvPing.getText ());
									jsondata.put ("download", tvDownload.getText ());
									jsondata.put ("upload", tvUpload.getText ());
									LOGE ("TAG", _data);
									JSONObject js = new JSONObject (_data);
									JSONArray array = js.getJSONArray (getString (R.string.history));
									array.put (jsondata);
									JSONObject new_data = new JSONObject ();
									new_data.put (getString (R.string.history), array);
									editor.remove ("DATA");
									editor.putString ("DATA", new_data.toString ());
									editor.apply ();
								} catch (JSONException e) {
									e.printStackTrace ();
								}
							} else {
								LOGE ("TAG", "2");
								JSONObject jsondata = new JSONObject ();
								try {
									jsondata.put ("date", String.valueOf (System.currentTimeMillis ()));
									jsondata.put ("ping", tvPing.getText ());
									jsondata.put ("download", tvDownload.getText ());
									jsondata.put ("upload", tvUpload.getText ());
									JSONArray array = new JSONArray ();
									array.put (jsondata);
									JSONObject new_data = new JSONObject ();
									new_data.put ("History", array);
									editor.putString ("DATA", new_data.toString ());
									editor.apply ();
									testing = false;
								} catch (JSONException e) {
									e.printStackTrace ();
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace ();
					}
				}
			} else {
				tvBegin.setImageResource (R.drawable.ic_play);
				LOGE ("TAG", "test2");
			}
		}).start ();
	}

	/**
	 * Gets ads app list.
	 */
	public void getAdsAppList () {
		RequestParams params = new RequestParams ();
		AsyncHttpClient client = new AsyncHttpClient ();
		client.setTimeout (60 * 1000);
		client.post ("https://api.flickr.com/services/rest/?method=flickr.photosets.getPhotos&api_key=892bdb8d0e6519769124a90c84643290&photoset_id=" +
				"72157688887276095&extras=description%2C+url_m%2C+url_o%2C+url_l&per_page=20&page=1&format=json&nojsoncallback=1", params, new getTrainResponseHandler ());
	}

	private void defaultValues () {
		tickProgressMeasure.setProgress (0);
		tvPing.setText ("0");
		tvDownload.setText ("0");
		tvUpload.setText ("0");
		tvBlink.setText ("Tap Button to run test");
	}

	/**
	 * Add gradient bitmap.
	 *
	 * @param originalBitmap the original bitmap
	 * @return the bitmap
	 */
	public Bitmap addGradient (Bitmap originalBitmap) {
		int width = originalBitmap.getWidth ();
		int height = originalBitmap.getHeight ();
		Bitmap updatedBitmap = Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas (updatedBitmap);
		canvas.drawBitmap (originalBitmap, 0, 0, null);
		Paint paint = new Paint ();
		LinearGradient shader = new LinearGradient (0, 0, 0, height, 0xFF30E3CA, 0xFFa5dee5, Shader.TileMode.CLAMP);
		paint.setShader (shader);
		paint.setXfermode (new PorterDuffXfermode (PorterDuff.Mode.SRC_IN));
		canvas.drawRect (0, 0, width, height, paint);
		return updatedBitmap;
	}

	@Override
	public void onResume () {
		super.onResume ();
		getSpeedTestHostsHandler = new GetSpeedTestHostsHandler ();
		getSpeedTestHostsHandler.start ();
		if (tvDownloadU != null)
			tvDownloadU.setText (sharedPref.getString ("UNIT", "Mbps"));
		if (tvUploadU != null)
			tvUploadU.setText (sharedPref.getString ("UNIT", "Mbps"));
	}

	/**
	 * The type Get train response handler.
	 */
	public class getTrainResponseHandler extends AsyncHttpResponseHandler {
		@Override
		public void onStart () {
			super.onStart ();
		}

		@Override
		public void onFinish () {
			super.onFinish ();
		}

		@Override
		public void onSuccess (int statusCode, Header[] headers, byte[] responseBody) {
			LOGI ("TAG", "onSuccess");
		}

		@Override
		public void onFailure (int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
			LOGI ("TAG", "onFailure");
		}
	}
}
