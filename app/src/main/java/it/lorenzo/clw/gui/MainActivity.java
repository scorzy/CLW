package it.lorenzo.clw.gui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import it.lorenzo.clw.R;
import it.lorenzo.clw.chooser.FileSelect;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private ViewPager viewPager;
	private DrawerLayout drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.setTabMode(TabLayout.MODE_FIXED);
		tabLayout.addTab(tabLayout.newTab().setText("Settings"));
		tabLayout.addTab(tabLayout.newTab().setText("Calendar"));
		tabLayout.addTab(tabLayout.newTab().setText("Example"));

		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

		viewPager = (ViewPager) findViewById(R.id.pager);
		final PagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				Fragment fragment = null;
				switch (position) {
					case 0:
						fragment = Settings.newInstance();
						break;
					case 1:
						fragment = new CalendarFragment();
						break;
					case 2:
						fragment = new ExampleFragment();
						break;

				}
				return fragment;
			}

			@Override
			public int getCount() {
				return 3;
			}
		};

		viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});

		FileSelect.requirePermission(this, this);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.overview:
				viewPager.setCurrentItem(0);
				break;
			case R.id.action_calendar:
				viewPager.setCurrentItem(1);
				break;
			case R.id.action_example:
				viewPager.setCurrentItem(2);
				break;
		}
		drawer.closeDrawers();
		return false;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[], @NonNull int[] grantResults) {
		if (requestCode == 115) {
			if (grantResults.length > 0
					&& grantResults[1] == PackageManager.PERMISSION_DENIED) {
				Toast.makeText(this, "Example will not work without Write external storage permission !", Toast.LENGTH_LONG).show();

			}
			if (grantResults.length > 1
					&& grantResults[2] == PackageManager.PERMISSION_DENIED) {
				Toast.makeText(this, "Agenda will not work !", Toast.LENGTH_LONG).show();
			}
			if (grantResults.length > 2
					&& grantResults[0] == PackageManager.PERMISSION_DENIED) {
				Toast.makeText(this, "CLW will not work without Read external storage permission !", Toast.LENGTH_LONG).show();
			}
		}
	}
}
