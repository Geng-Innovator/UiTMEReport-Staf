package com.seladanghijau.uitme_reportstaf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity implements View.OnClickListener{

    public static final String id = "ID";
    public static final String pekerjaPrefs = "pekerjaPref";
    private String pekerja_id;
    private LaporanAdapter laporanAdapter;
    private RecyclerView recyclerView;
    private List<Laporan> laporanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences sharedPreferences = getSharedPreferences(Dashboard.pekerjaPrefs, Context.MODE_PRIVATE);
        pekerja_id = sharedPreferences.getString(id, "");

        recyclerView = findViewById(R.id.rcyLaporan);
        laporanAdapter = new LaporanAdapter(laporanList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(laporanAdapter);
        prepareLaporan();
    }

    private void prepareLaporan(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest laporanRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.dashboard), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    //Get the JSON object from the server
                    JSONObject obj = new JSONObject(response);

                    //Get status from the server. 0 - Failed, 1 - Success
                    if (obj.getString("status").equalsIgnoreCase("1")){
                        //Redirect to dashboard after laporan success
                        JSONObject data = obj.getJSONObject("data");
                        JSONArray laporan_list = data.getJSONArray("laporan_list");
                        for (int i=0; i<laporan_list.length(); i++){
                            JSONObject o = laporan_list.getJSONObject(i);
                            Laporan laporan = new Laporan();
                            laporan.setId(o.getString("id"));
                            laporan.setLaporan_imej(o.getString("laporan_imej"));
                            laporan.setLaporan_tempat(o.getString("laporan_tempat"));
                            laporan.setLaporan_tarikh(o.getString("laporan_tarikh"));
                            laporan.setLaporan_masa(o.getString("laporan_masa"));
                            laporan.setLaporan_status(o.getString("laporan_status"));
                            laporanList.add(laporan);
                            laporanAdapter.notifyDataSetChanged();
                        }
                    }else{
                        //Try again
                        Toast.makeText(Dashboard.this, "Gagal mengakses internet anda", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Dashboard.this, "Gagal mengakses internet anda", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("staf_id", pekerja_id);
                return params;
            }
        };
        requestQueue.add(laporanRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProfil:
                startActivity(new Intent(this, Profil.class));
                break;
            case R.id.btnLaporan:
                startActivity(new Intent(this, Laporan_Baru.class));
                break;
        }
    }
}
