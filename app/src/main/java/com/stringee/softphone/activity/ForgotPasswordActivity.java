package com.stringee.softphone.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

public class ForgotPasswordActivity extends MActivity {

    private EditText etEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initActionBar();

        etEmail = (EditText) findViewById(R.id.et_email);
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(this);
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.forgot_password);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                if (isInputValid()) {
                    showProgressDialog(R.string.executing);
                    resetPassword();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private boolean isInputValid() {
        String email = etEmail.getText().toString().trim();
        if (email.length() == 0 || email.contains(" ")) {
            Utils.reportMessage(this, getString(R.string.email_invalid));
            return false;
        }

        return true;
    }

    private void resetPassword() {
        String url = Constant.URL_BASE + Constant.URL_RESET_PASSWORD;
        RequestQueue queue = Volley.newRequestQueue(this);
        final String email = etEmail.getText().toString().trim();

        Map<String, String> params = new HashMap();
        params.put("email", email);
        JSONObject jsonObject = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url
                , jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Stringee", response.toString());
                dismissProgressDialog();
                try {
                    int status = response.getInt("status");
                    if (status == 200) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        builder.setMessage(getString(R.string.reset_pass_success, email));
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Utils.reportMessage(ForgotPasswordActivity.this, response.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.reportMessage(ForgotPasswordActivity.this, R.string.error_occured);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                Utils.reportMessage(ForgotPasswordActivity.this, R.string.error_occured);
            }
        });
        queue.add(request);
    }
}
