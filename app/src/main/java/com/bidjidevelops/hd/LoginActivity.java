package com.bidjidevelops.hd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import com.bidjidevelops.hd.gambar.Upload;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseApp {

    private MaterialEditText logtxtEmail, logtxtPassword;
    private TextView loglblRegister;
    private Button logbtnLogin;
    SessionManager sessionManager;
    String iduser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupView();
        sessionManager = new SessionManager(getApplicationContext());
        loglblRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(BtnAnimasi);
//                sessionManager = new SessionManager(getApplicationContext());
//                HashMap<String, String> user = sessionManager.getUserDetails();
//                String Semail = user.get(SessionManager.idusers);
//                Toast.makeText(context, Semail, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

        logbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        logtxtEmail.setError(null);
        logtxtPassword.setError(null);

        /*check kebaradan teks*/
        if (Helper.isEmpty(logtxtEmail)) {
            logtxtEmail.setError("email masih kosong");
            logtxtEmail.requestFocus();
        } else if (Helper.isEmpty(logtxtPassword)) {
            logtxtPassword.setError("Password masih kosong");
            logtxtPassword.requestFocus();
        } else {
            /*kirim data ke server*/
            String URL = Helper.BASE_URL + "login.php";
            Map<String, String> param = new HashMap<>();
            param.put("email", logtxtEmail.getText().toString());
            param.put("password", logtxtPassword.getText().toString());

            /*menampilkan progressbar saat mengirim data*/
            ProgressDialog pd = new ProgressDialog(context);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setInverseBackgroundForced(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setTitle("Info");
            pd.setMessage("Login");
            pd.show();

            try {
                /*format ambil data*/
                aQuery.progress(pd).ajax(URL, param, String.class, new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String object, AjaxStatus status) {
                        /*jika objek tidak kosong*/
                        if (object != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(object);
                                String result = jsonObject.getString("result");
                                String msg = jsonObject.getString("msg");
                                if (result.equalsIgnoreCase("true")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("user");
                                    for (int a = 0; a < jsonArray.length(); a++) {
                                        JSONObject o = jsonArray.getJSONObject(a);
                                        iduser = o.getString("iduser");
                                    }
                                    startActivity(new Intent(context, MainActivity.class));
                                    Helper.pesan(context, msg);
                                    sessionManager.createSession(logtxtEmail.getText().toString(),logtxtPassword.getText().toString());
                                    sessionManager.createSessionid(iduser);
                                    finish();
                                } else {
                                    Helper.pesan(context, msg);
                                }
                            } catch (JSONException e) {
                                Helper.pesan(context, "Tidak ada internet");
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Helper.pesan(context, "Gagal mengambil data");
            }
        }
    }

    private void setupView() {
        logtxtEmail = (MaterialEditText) findViewById(R.id.logtxtEmail);
        logtxtPassword = (MaterialEditText) findViewById(R.id.logtxtPassword);
        logbtnLogin = (Button) findViewById(R.id.logbtnLogin);
        loglblRegister = (TextView) findViewById(R.id.loglblRegister);
    }
    @Override
    public void onBackPressed() {

            finish();
    }

}