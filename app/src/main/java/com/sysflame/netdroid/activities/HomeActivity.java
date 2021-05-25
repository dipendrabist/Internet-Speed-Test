package com.sysflame.netdroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sysflame.netdroid.R;
import com.sysflame.netdroid.fragments.DailyDataFragment;
import com.sysflame.netdroid.fragments.SettingFragment;
import com.sysflame.netdroid.fragments.SpeedTestFragment;
import com.sysflame.netdroid.models.AdsSpeedTest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import static com.sysflame.netdroid.utils.LogUtils.LOGE;

/**
 * The type Home activity.
 */
public class HomeActivity extends AppCompatActivity {
	private Toolbar toolbar;
	private TextView tvSubTitle;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private BottomNavigationView bottomNavigationView;
	private AdsSpeedTest adsSpeedTest;

	@Override
	protected void onCreate (@Nullable Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_home);
		find_views_by_id ();
		init_variables ();
	}

	private void find_views_by_id () {
		toolbar = findViewById (R.id.toolbar);
		tvSubTitle = toolbar.findViewById (R.id.toolbar_subtitle);
		viewPager = findViewById (R.id.viewpager);
		bottomNavigationView = findViewById (R.id.bottom_navigation);
	}

	private void init_variables () {
		FirebaseAnalytics.getInstance (this);
		adsSpeedTest = new AdsSpeedTest (this);
		adsSpeedTest.initConsent ();
		Intent intentBC = new Intent ();
		intentBC.setAction ("com.sysflame.netdroid");
		sendBroadcast (intentBC);
		adapter = new ViewPagerAdapter (getSupportFragmentManager ());
		if (viewPager != null)
			viewPager.setAdapter (adapter);
		if (viewPager != null)
			viewPager.setCurrentItem (1);
		tvSubTitle.setText (getString (R.string.speed));
		bottomNavigationView.setSelectedItemId (R.id.action_speed);
		bottomNavigationView.setOnNavigationItemSelectedListener (item -> {
			switch (item.getItemId ()) {
				case R.id.action_history:
					viewPager.setCurrentItem (0);
					tvSubTitle.setText (getString (R.string.history));
					break;
				case R.id.action_speed:
					viewPager.setCurrentItem (1);
					tvSubTitle.setText (getString (R.string.speed));
					break;
				case R.id.action_setting:
					viewPager.setCurrentItem (2);
					tvSubTitle.setText (getString (R.string.settings));
					break;
				default:
					break;
			}
			return true;
		});
		if (viewPager != null)
			viewPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
				@Override
				public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
					LOGE ("TAG", "onPageScrolled");
				}

				@Override
				public void onPageSelected (int position) {
					switch (position) {
						case 0:
							bottomNavigationView.getMenu ().findItem (R.id.action_history).setChecked (true);
							tvSubTitle.setText (getString (R.string.history));
							break;
						case 1:
							bottomNavigationView.getMenu ().findItem (R.id.action_speed).setChecked (true);
							tvSubTitle.setText (getString (R.string.speed));
							break;
						case 2:
							bottomNavigationView.getMenu ().findItem (R.id.action_setting).setChecked (true);
							tvSubTitle.setText (getString (R.string.settings));
							break;
						default:
							break;
					}
				}

				@Override
				public void onPageScrollStateChanged (int state) {
					LOGE ("TAG", "onPageScrollStateChanged");
				}
			});
	}

	@Override
	protected void onResume () {
		super.onResume ();
	}

	@Override
	protected void onPause () {
		super.onPause ();
	}

	@Override
	protected void onDestroy () {
		super.onDestroy ();
	}

	private class ViewPagerAdapter extends FragmentStatePagerAdapter {
		/**
		 * The Page count.
		 */
		final int PAGE_COUNT = 3;
		private final String[] mTabsTitle = {getString (R.string.history), getString (R.string.speed), getString (R.string.settings)};

		/**
		 * Instantiates a new View pager adapter.
		 *
		 * @param fm the fm
		 */
		ViewPagerAdapter (FragmentManager fm) {
			super (fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		}

		/**
		 * Gets tab view.
		 *
		 * @param position the position
		 * @return the tab view
		 */
		public View getTabView (int position) {
			View view = LayoutInflater.from (getApplicationContext ()).inflate (R.layout.custom_tab, null);
			TextView title = view.findViewById (R.id.title);
			title.setText (mTabsTitle[ position ]);
			return view;
		}

		@NonNull
		@Override
		public Fragment getItem (int pos) {
			switch (pos) {
				case 0:
					return new DailyDataFragment ();
				case 1:
					return new SpeedTestFragment ();
				case 2:
					return new SettingFragment ();
				default:
					break;
			}
			return null;
		}

		@Override
		public void destroyItem (ViewGroup viewPager, int position, @NonNull Object object) {
			LOGE ("TAG", "destroyItem");
		}

		@Override
		public int getCount () {
			return PAGE_COUNT;
		}

		@Override
		public CharSequence getPageTitle (int position) {
			return null;
		}
	}
}
