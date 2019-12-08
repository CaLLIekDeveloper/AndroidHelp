package com.CaLLIek.androidhelp.wifiscanner;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.CaLLIek.androidhelp.R;
import com.CaLLIek.androidhelp.wifiscanner.Element;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class WifiScannerActivity extends AppCompatActivity {


    private Element[] nets;
    private WifiManager wifiManager;
    private List<ScanResult> wifili;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wfs_activity_wifi_scanner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DetectWifi();
                Snackbar.make(view, "Скинирование", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
    }


    public void DetectWifi()
    {
        this.wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.wifiManager.startScan();
        this.wifili = this.wifiManager.getScanResults();

        this.nets = new Element[wifili.size()];
        for(int i=0; i<wifili.size(); i++)
        {
            String item = wifili.get(i).toString();
            String[] vector_item = item.split(",");
            String item_essid = vector_item[0];
            String item_capabilities = vector_item[2];
            String item_level = vector_item[3];

            String ssid = item_essid.split(": ")[1];
            String security = item_capabilities.split(": ")[1];// vector_item[2];
            String level = item_level.split(": ")[1];

            nets[i] = new Element(ssid,security,level);
        }

        AdapterElements adapterElements = new AdapterElements(this);
        ListView netList = findViewById(R.id.wfs_list_item);
        netList.setAdapter(adapterElements);
    }

    class AdapterElements extends ArrayAdapter<Object>
    {
        Activity context;
        public AdapterElements(Activity context)
        {
            super(context, R.layout.wfs_items, nets);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.wfs_items, null);

            TextView txtSSID = item.findViewById(R.id.wfs_tvSSID);
            txtSSID.setText(nets[position].getTitle());

            TextView txtSecurity = item.findViewById(R.id.wfs_tvSecurity);
            txtSecurity.setText(nets[position].getSecurity());

            TextView txtLevel = item.findViewById(R.id.wfs_tvLevel);
            txtLevel.setText(nets[position].getLevel());

            return item;
        }
    }
}
