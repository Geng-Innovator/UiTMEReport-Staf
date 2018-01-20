package com.seladanghijau.uitme_reportstaf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Daftar extends AppCompatActivity implements View.OnClickListener{

    private EditText edtKataLaluan, edtKataLaluan2;
    private int id;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        edtKataLaluan = findViewById(R.id.edtKataLaluanBaru);
        edtKataLaluan2 = findViewById(R.id.edtKataLaluanBaru2);

        sharedPreferences = getSharedPreferences(LogMasuk.pekerjaPrefs, Context.MODE_PRIVATE);
    }

    public boolean validatePssword(){
        return edtKataLaluan.getText().toString().trim().equals(edtKataLaluan2.getText().toString().trim());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnDaftar){
            try{
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = "http://beta.seladanghijau.com/uitm_e_laporan/public/staf/reset-password";
                StringRequest daftarRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //Get the JSON object from the server
                            JSONObject obj = new JSONObject(response);
                            //Get status from the server. 0 - Failed, 1 - Success
                            if (obj.getString("status").equalsIgnoreCase("1")){
                                //Insert into shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(LogMasuk.id, ""+id);
                                editor.commit();

                                //Redirect to dashboard
                                startActivity(new Intent(Daftar.this, Dashboard.class));
                                finish();
                            }else{
                                //Redirect to log masuk
                                Toast.makeText(Daftar.this, "Tukar katalaluan gagal", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Daftar.this, LogMasuk.class));
                                finish();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Pendaftaran anda ralat", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params;
                        if (validatePssword()) {
                            //Get intent data
                            id = getIntent().getIntExtra("id", 0);
                            String cur_pass = getIntent().getStringExtra("cur_pass");

                            params = new HashMap<>();
                            params.put("staf_id", ""+id); //User id for query
                            params.put("cur_pass", cur_pass); //default password
                            params.put("new_pass", edtKataLaluan.getText().toString().trim()); //user input password
                            return params;
                        }else{
                            Toast.makeText(Daftar.this, "Katalaluan anda tidak sama", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    }
                };

                requestQueue.add(daftarRequest);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Terdapat masalah dengan rangkaian internet anda", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
