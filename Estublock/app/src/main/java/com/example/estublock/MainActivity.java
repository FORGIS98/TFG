package com.example.estublock;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  GlobalState gs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ViewPager viewPager = findViewById(R.id.viewPager);

    // IMPORTANTE: Aquí y solo aquí se crea el objeto GlobalState();
    gs = new GlobalState();

    AuthenticationPagerAdapter pagerAdapter = new
        AuthenticationPagerAdapter(getSupportFragmentManager());
    pagerAdapter.addFragment(new LoginFragment());
    pagerAdapter.addFragment(new RegisterFragment());

    viewPager.setAdapter(pagerAdapter);

  }
}

class AuthenticationPagerAdapter extends FragmentPagerAdapter {
  private final ArrayList<Fragment> fragmentList = new ArrayList<>();

  public AuthenticationPagerAdapter(FragmentManager fm){
    super(fm);
  }

  @Override
  public Fragment getItem(int i){
    return fragmentList.get(i);
  }

  @Override
  public int getCount(){
    return fragmentList.size();
  }

  void addFragment(Fragment fragment){
    fragmentList.add(fragment);
  }
}
