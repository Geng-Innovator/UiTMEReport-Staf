package com.seladanghijau.uitme_reportstaf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

        sharedPreferences = getSharedPreferences("pekerjaPref", Context.MODE_PRIVATE);
    }

    public boolean validatePssword(){
        return edtKataLaluan.getText().toString().trim().equals(edtKataLaluan2.getText().toString().trim());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnDaftar){
            try{
                final ProgressDialog pDialog = new ProgressDialog(Daftar.this);
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = getResources().getString(R.string.url_daftar);
                StringRequest daftarRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //Get the JSON object from the server
                            JSONObject obj = new JSONObject(response);
                            //Get status from the server. 0 - Failed, 1 - Success
                            if (obj.getString("status").equalsIgnoreCase("1")){
                                //Insert into shared preferences
                                if (getIntent().getExtras() != null) {
                                    id = getIntent().getIntExtra("id", 0);
                                }else{
                                    id = Integer.parseInt(sharedPreferences.getString("ID", ""));
                                }

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("ID", ""+id);
                                editor.putString("cur_pass", edtKataLaluan.getText().toString().trim());
                                editor.apply();

                                //Redirect to dashboard
                                AlertDialog alertDialog = new AlertDialog.Builder(Daftar.this)
                                        .setMessage("Penukaran katalaluan berjaya")
                                        .setCancelable(false)
                                        .setPositiveButton("TERUSKAN", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                startActivity(new Intent(Daftar.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            }
                                        })
                                        .create();
                                alertDialog.show();
                            }else{
                                //Redirect to log masuk
                                AlertDialog alertDialog = new AlertDialog.Builder(Daftar.this)
                                        .setMessage("Penukaran katalaluan gagal")
                                        .create();
                                alertDialog.show();
                                //startActivity(new Intent(Daftar.this, LogMasuk.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }

                        }catch (Exception e){ e.printStackTrace(); }

                        if(pDialog.isShowing())
                            pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Daftar.this)
                                .setMessage("Penukaran katalaluan ralat")
                                .create();
                        alertDialog.show();

                        if(pDialog.isShowing())
                            pDialog.dismiss();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params;
                        if (validatePssword()) {
                            //Get intent data
                            String cur_pass;
                            String staf_id;

                            if (getIntent().getExtras() != null) {
                                id = getIntent().getIntExtra("id", 0);
                                staf_id = String.valueOf(id);
                                cur_pass = getIntent().getStringExtra("cur_pass");
                            }else{
                                SharedPreferences sharedPreferences = getSharedPreferences("pekerjaPref", Context.MODE_PRIVATE);
                                staf_id = sharedPreferences.getString("ID", "");
                                cur_pass = sharedPreferences.getString("cur_pass", "");
                            }

                            params = new HashMap<>();
                            params.put("staf_id", staf_id); //User id for query
                            params.put("cur_pass", cur_pass); //default password
                            params.put("new_pass", edtKataLaluan.getText().toString().trim()); //user input password
                            return params;
                        }else{
                            AlertDialog alertDialog = new AlertDialog.Builder(Daftar.this)
                                    .setMessage("Katalaluan anda tidak sama")
                                    .create();
                            alertDialog.show();
                            return null;
                        }
                    }
                };

                requestQueue.add(daftarRequest);
                pDialog.setMessage("Sedang menukar katalaluan...");
                pDialog.setCancelable(false);
                pDialog.show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Terdapat masalah dengan rangkaian internet anda", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
