package com.stringee.softphone.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.stringee.softphone.R;
import com.stringee.softphone.common.Constant;
import com.stringee.softphone.common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends MActivity {

    private EditText etEmail;
    private EditText etPhoneNo;
    private EditText etPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);

        etEmail = (EditText) findViewById(R.id.et_email);
        etPhoneNo = (EditText) findViewById(R.id.et_phone);
        etPassword = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (isInputValid()) {
                    showProgressDialog(R.string.executing);
                    register();
                }
                break;
        }
    }

    private boolean isInputValid() {
        String email = etEmail.getText().toString().trim();
        if (email.length() == 0 || email.contains(" ")) {
            Utils.reportMessage(this, getString(R.string.email_invalid));
            return false;
        }

        String phone = etPhoneNo.getText().toString().trim();
        if (phone.length() == 0) {
            Utils.reportMessage(this, getString(R.string.input_invalid));
            return false;
        }

        if (phone.contains(" ")) {
            Utils.reportMessage(this, getString(R.string.username_invalid));
            return false;
        }

        String password = etPassword.getText().toString().trim();
        if (password.length() == 0) {
            Utils.reportMessage(this, getString(R.string.password_invalid));
            return false;
        }
        return true;
    }

    private void register() {
        String url = Constant.URL_BASE + Constant.URL_REGISTER;
        RequestQueue queue = Volley.newRequestQueue(this);
        final String email = etEmail.getText().toString().trim();
        final String phone = etPhoneNo.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        Map<String, String> params = new HashMap();
        params.put("email", email);
        params.put("phone", phone);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage(getString(R.string.register_success, email));
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Utils.reportMessage(RegisterActivity.this, response.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.reportMessage(RegisterActivity.this, R.string.error_occured);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                Utils.reportMessage(RegisterActivity.this, R.string.error_occured);
            }
        });
        queue.add(request);
    }
}
