package com.example.estublock;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  public static final String EXTRA_MSG = "com.example.estublock.MESSAGE";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ViewPager viewPager = findViewById(R.id.viewPager);

    AuthenticationPagerAdapter pagerAdapter = new
      AuthenticationPagerAdapter(getSupportFragmentManager());
    pagerAdapter.addFragment(new LoginFragment());
    pagerAdapter.addFragment(new RegisterFragment());

    viewPager.setAdapter(pagerAdapter);

  }

  // Called when the user taps the Send button
  // DELETE
  /* public void sendMessage(View view){
    Intent intent = new Intent(this, DisplayMessageActivity.class);
    EditText editText = (EditText) findViewById(R.id.editText);
    String message = editText.getText().toString();
    intent.putExtra(EXTRA_MSG, message);
    startActivity(intent);
  }*/
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