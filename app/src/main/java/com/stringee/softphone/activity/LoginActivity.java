package com.stringee.softphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.stringee.softphone.R;
import com.stringee.softphone.common.Constant;
import com.stringee.softphone.common.PrefUtils;
import com.stringee.softphone.common.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luannguyen on 7/10/2017.
 */

public class LoginActivity extends MActivity {

    private EditText etEmail;
    private EditText etPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        TextView tvRegister = (TextView) findViewById(R.id.tv_register);
        tvRegister.setOnClickListener(this);

        TextView tvForgot = (TextView) findViewById(R.id.tv_forgot);
        tvForgot.setOnClickListener(this);

        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (isInputValid()) {
                    showProgressDialog(R.string.executing);
                    login();
                }
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.tv_forgot:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    private boolean isInputValid() {
        String email = etEmail.getText().toString().trim();
        if (email.length() == 0 || email.contains(" ")) {
            Utils.reportMessage(this, getString(R.string.email_invalid));
            return false;
        }

        String password = etPassword.getText().toString().trim();
        if (password.length() == 0) {
            Utils.reportMessage(this, getString(R.string.password_invalid));
            return false;
        }

        return true;
    }

    private void login() {
        String url = Constant.URL_BASE + Constant.URL_LOGIN;
        RequestQueue queue = Volley.newRequestQueue(this);
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        Map<String, String> params = new HashMap();
        params.put("email", email);
        params.put("password", password);
        JSONObject jsonObject = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url
                , jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialog();
                try {
                    int status = response.getInt("status");
                    if (status == 200) {
                        PrefUtils.getInstance(LoginActivity.this).putBoolean(Constant.PREF_LOGINED, true);
                        PrefUtils.getInstance(LoginActivity.this).putString(Constant.PREF_USER_ID, email);
                        JSONObject dataObject = response.getJSONObject("data");
                        JSONObject tokenObject = dataObject.getJSONObject("token");
                        String token = tokenObject.getString("token");
                        String accessToken = tokenObject.getString("access_token");
                        long expiredTime = tokenObject.getLong("expire_time");
                        JSONArray numbers = dataObject.getJSONArray("call_out_number");
                        String defaultNumber = dataObject.getString("call_out_number_default");
                        PrefUtils.getInstance(LoginActivity.this).putString(Constant.PREF_TOKEN, token);
                        PrefUtils.getInstance(LoginActivity.this).putString(Constant.PREF_ACCESS_TOKEN, accessToken);
                        PrefUtils.getInstance(LoginActivity.this).putLong(Constant.PREF_EXPIRED_TIME, expiredTime);
                        PrefUtils.getInstance(LoginActivity.this).putString(Constant.PREF_SIP_NUMBERS, numbers.toString());
                        PrefUtils.getInstance(LoginActivity.this).putString(Constant.PREF_SELECTED_NUMBER, defaultNumber);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        startActivity(intent);
                        finish();
                    } else {
                        Utils.reportMessage(LoginActivity.this, response.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.reportMessage(LoginActivity.this, R.string.error_occured);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                Utils.reportMessage(LoginActivity.this, R.string.error_occured);
            }
        });
        queue.add(request);
    }
}
