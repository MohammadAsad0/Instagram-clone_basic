/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener{

  EditText username,password;
  Button loginButton,signUpButton;
  TextView textView;

  boolean isLogin=true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Instagram");

    loginButton=(Button) findViewById(R.id.loginButton);
    signUpButton=(Button) findViewById(R.id.signUpButton);
    username=(EditText) findViewById(R.id.usernameEditText);
    password=(EditText) findViewById(R.id.passwordEditText);

    RelativeLayout layout=(RelativeLayout) findViewById(R.id.layout);
    ImageView instagram=(ImageView) findViewById(R.id.imageView);

    textView=(TextView) findViewById(R.id.textView);

    layout.setOnClickListener(this);
    instagram.setOnClickListener(this);
    password.setOnKeyListener(this);

    if(ParseUser.getCurrentUser() != null) {
      showUserList();
    }
    
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  public void button(View view) {

    if(username.getText().toString().matches("") || password.getText().toString().matches("")) {
      Toast.makeText(this, "Username or Password field is empty.", Toast.LENGTH_SHORT).show();
    } else {

      if (isLogin) {
        loginFunction();
      } else {
        signUpFunction();
      }

    }
  }

  public void switchButton(View view) {
    if (isLogin) {
      loginButton.setText("Sign Up");
      signUpButton.setText("Log In");
      textView.setText("Already have an account?");
      isLogin=false;

    } else {
      loginButton.setText("Log In");
      signUpButton.setText("Sign Up");
      textView.setText("Don't have an account?");
      isLogin=true;
    }
  }

  public void loginFunction() {
    ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if (user!=null) {
          Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
          showUserList();
        } else {
          e.printStackTrace();
          Toast.makeText(MainActivity.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
        }
      }
    });

  }

  public void signUpFunction() {
    ParseUser user=new ParseUser();

    user.setUsername(username.getText().toString());
    user.setPassword(password.getText().toString());

    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {
        if (e==null) {
          Toast.makeText(MainActivity.this, "Signed Up", Toast.LENGTH_SHORT).show();
          showUserList();
        } else {
          e.printStackTrace();
          Toast.makeText(MainActivity.this, "Username already exist!", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  @Override
  public void onClick(View view) {
    if(view.getId()==R.id.imageView || view.getId()==R.id.layout) {
      InputMethodManager manager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
  }

  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {
    if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN) {
      button(view);
    }

    return false;
  }
  public void showUserList() {
    Intent intent=new Intent(getApplicationContext(),userListActivity.class);
    startActivity(intent);
  }
}