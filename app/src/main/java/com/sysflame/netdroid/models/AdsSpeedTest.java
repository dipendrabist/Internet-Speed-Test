package com.sysflame.netdroid.models;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import com.sysflame.netdroid.R;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.sysflame.netdroid.utils.LogUtils.LOGE;
import static com.sysflame.netdroid.utils.LogUtils.LOGI;
import static com.sysflame.netdroid.utils.LogUtils.makeLogTag;

/**
 * The type Ads speed test.
 */
public class AdsSpeedTest {
	private static final String TAG = makeLogTag (AdsSpeedTest.class);
	private Activity activity;
	private FrameLayout adContainerView;
	private AdView adView;
	private InterstitialAd interstitialAd;
	private List<String> testDevices = new ArrayList<> ();
	private ConsentForm form;

	/**
	 * Instantiates a new Ads speed test.
	 */
	public AdsSpeedTest () {
	}

	/**
	 * Instantiates a new Ads speed test.
	 *
	 * @param activity the activity
	 */
	public AdsSpeedTest (Activity activity) {
		this.activity = activity;
		testDevices.add (AdRequest.DEVICE_ID_EMULATOR);
		MobileAds.initialize (activity.getApplicationContext (), initializationStatus -> {
		});
		RequestConfiguration requestConfiguration = new RequestConfiguration.Builder ()
				.setTestDeviceIds (testDevices)
				.build ();
		MobileAds.setRequestConfiguration (requestConfiguration);
	}

	/**
	 * Init consent.
	 */
	public void initConsent () {
		getConsent ();
	}

	private void getConsent () {
		ConsentInformation consentInformation = ConsentInformation.getInstance (activity.getApplicationContext ());
		consentInformation.addTestDevice (AdRequest.DEVICE_ID_EMULATOR);
		consentInformation.addTestDevice ("298B12906876C04EAC9C8FDD066FFF1A");
		consentInformation.setDebugGeography (DebugGeography.DEBUG_GEOGRAPHY_EEA);
		String[] publisherIds = {"pub-7009790987498270"};
		consentInformation.requestConsentInfoUpdate (publisherIds, new ConsentInfoUpdateListener () {
			@Override
			public void onConsentInfoUpdated (ConsentStatus consentStatus) {
				LOGE (TAG, "ConsentStatus = " + consentStatus);
				dialogConsent ();
			}

			@Override
			public void onFailedToUpdateConsentInfo (String errorDescription) {
				LOGE (TAG, "ConsentStatus = " + errorDescription);
				dialogConsent ();
			}
		});
	}

	private void dialogConsent () {
		URL privacyUrl = null;
		try {
			privacyUrl = new URL ("https://raw.githubusercontent.com/N1001/envato/master/speedtest/privacy_policy.md");
		} catch (MalformedURLException e) {
			e.printStackTrace ();
		}
		form = new ConsentForm.Builder (activity, privacyUrl)
				.withListener (new ConsentFormListener () {
					@Override
					public void onConsentFormLoaded () {
						LOGE (TAG, "Consent form loaded successfully.");
						showConsentForm ();
					}

					@Override
					public void onConsentFormOpened () {
						LOGE (TAG, "Consent form was displayed.");
					}

					@Override
					public void onConsentFormClosed (
							ConsentStatus consentStatus, Boolean userPrefersAdFree) {
						LOGE (TAG, "Consent form was closed.");
					}

					@Override
					public void onConsentFormError (String errorDescription) {
						LOGE (TAG, "Consent form error." + errorDescription);
					}
				})
				.withPersonalizedAdsOption ()
				.withNonPersonalizedAdsOption ()
				.withAdFreeOption ()
				.build ();
		form.load ();
	}

	private void showConsentForm () {
		form.show ();
	}

	/**
	 * Init banner.
	 */
	public void initBanner () {
		adContainerView = activity.findViewById (R.id.ad_view_container);
		adContainerView.post (this::loadBanner);
	}

	/**
	 * Init interstitial.
	 */
	public void initInterstitial () {
		interstitialAd = new InterstitialAd (activity.getApplicationContext ());
		interstitialAd.setAdUnitId (activity.getResources ().getString (R.string.admob_app_interstitial));
		interstitialAd.loadAd (new AdRequest.Builder ().build ());
		interstitialAd.setAdListener (new AdListener () {
			@Override
			public void onAdLoaded () {
				LOGI (TAG, "onAdLoaded()");
			}

			@Override
			public void onAdFailedToLoad (int errorCode) {
				LOGI (TAG, "onAdFailedToLoad() with error code: " + errorCode);
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
				if (!interstitialAd.isLoading () && !interstitialAd.isLoaded ()) {
					interstitialAd.loadAd (new AdRequest.Builder ().build ());
				}
			}
		});
	}

	/**
	 * Show interstitial.
	 */
	public void showInterstitial () {
		if (interstitialAd != null && interstitialAd.isLoaded ()) {
			interstitialAd.show ();
		} else {
			LOGI (TAG, "d did not load");
		}
	}

	/**
	 * Pause banner.
	 */
	public void pauseBanner () {
		if (adView != null) {
			adView.pause ();
		}
	}

	/**
	 * Resume banner.
	 */
	public void resumeBanner () {
		if (adView != null) {
			adView.resume ();
		}
	}

	/**
	 * Destroy banner.
	 */
	public void destroyBanner () {
		if (adView != null) {
			adView.destroy ();
		}
	}

	private void loadBanner () {
		adView = new AdView (activity.getApplicationContext ());
		adView.setAdUnitId (activity.getResources ().getString (R.string.admob_app_banner));
		adContainerView.removeAllViews ();
		adContainerView.addView (adView);
		AdSize adSize = getAdSize ();
		adView.setAdSize (adSize);
		AdRequest adRequest = new AdRequest.Builder ()
				.addTestDevice (AdRequest.DEVICE_ID_EMULATOR)
				.build ();
		adView.loadAd (adRequest);
	}

	private AdSize getAdSize () {
		Display display = activity.getWindowManager ().getDefaultDisplay ();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics (outMetrics);
		float density = outMetrics.density;
		float adWidthPixels = adContainerView.getWidth ();
		if (adWidthPixels == 0) {
			adWidthPixels = outMetrics.widthPixels;
		}
		int adWidth = (int) (adWidthPixels / density);
		return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize (activity.getApplicationContext (), adWidth);
	}

	/**
	 * Gets interstitial ad.
	 *
	 * @return the interstitial ad
	 */
	public InterstitialAd getInterstitialAd () {
		return interstitialAd;
	}

	/**
	 * Sets interstitial ad.
	 *
	 * @param interstitialAd the interstitial ad
	 */
	public void setInterstitialAd (InterstitialAd interstitialAd) {
		this.interstitialAd = interstitialAd;
	}
}
