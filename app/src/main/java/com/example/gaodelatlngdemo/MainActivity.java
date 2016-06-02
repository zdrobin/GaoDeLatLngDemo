package com.example.gaodelatlngdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.CoordinateConverter.CoordType;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    MapView mMapView = null;
    AMap aMap;
    ListView mListView;
    OffsetAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        mListView = (ListView) findViewById(R.id.offset_listview);
        mAdapter = new OffsetAdapter();
        mListView.setAdapter(mAdapter);

        aMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.setFlat(true);
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp));
                markerOptions.position(new LatLng(arg0.latitude, arg0.longitude));
                aMap.addMarker(markerOptions);

                JZLocationConverter.LatLng latLng = new JZLocationConverter.LatLng();
                latLng.latitude = arg0.latitude;
                latLng.longitude = arg0.longitude;
                latLng = JZLocationConverter.gcj02ToWgs84(latLng);
                CoordinateConverter converter = new CoordinateConverter(MainActivity.this);
                converter.from(CoordType.GPS);
                converter.coord(new LatLng(latLng.latitude, latLng.longitude));
                LatLng wgsLatLng = converter.convert();

                MarkerOptions markerOptions2 = new MarkerOptions();
                markerOptions2.setFlat(true);
                markerOptions2.anchor(0.5f, 0.5f);
                markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_marker_1));
                markerOptions2.position(wgsLatLng);
                aMap.addMarker(markerOptions2);

                LatLng gaodeLatLng = converter.coord(arg0).convert();
                MarkerOptions markerOptions3 = new MarkerOptions();
                markerOptions3.setFlat(true);
                markerOptions3.anchor(0.5f, 0.5f);
                markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue1));
                markerOptions3.position(gaodeLatLng);
                aMap.addMarker(markerOptions3);

                mAdapter.latLngList.add(new Position(AMapUtils.calculateLineDistance(wgsLatLng, arg0),
                        AMapUtils.calculateLineDistance(gaodeLatLng, arg0)));
                Log.i("distance", "" + AMapUtils.calculateLineDistance(wgsLatLng, arg0));
                mAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(mAdapter.getCount() - 1);

//				 PolylineOptions options = new PolylineOptions();
                // if (mMakerCount >= 1) {
                // options.add(mMakers[0].getPosition());
                // options.add(mMakers[1].getPosition());
                // }
                // if (mMakerCount >= 2) {
                // options.add(mMakers[2].getPosition());
                // options.add(mMakers[0].getPosition());
                // }
//				 aMap.addPolyline(options.color(Color.parseColor("#ff0000")).width(10));
                // PolylineOptions options2 = new PolylineOptions();
                // if (mMakerCount >= 1) {
                // options2.add(mBlueLatLngs[0]);
                // options2.add(mBlueLatLngs[1]);
                // }
                // if (mMakerCount >= 2) {
                // options2.add(mBlueLatLngs[2]);
                // options2.add(mBlueLatLngs[0]);
                // }
                // aMap.addPolyline(options2.color(Color.parseColor("#0000ff")).width(10));
            }
        });

        findViewById(R.id.clear_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                aMap.clear();
                mAdapter.latLngList.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    class OffsetAdapter extends BaseAdapter {
        List<Position> latLngList = new ArrayList<>();
        ViewHolder holder;

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return latLngList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return latLngList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.offset_item, parent, false);
                holder.latView = (TextView) convertView.findViewById(R.id.lat_offset_view);
                holder.lngView = (TextView) convertView.findViewById(R.id.lng_offset_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.latView.setText("" + latLngList.get(position).latitude);
            holder.lngView.setText("" + latLngList.get(position).longitude);
            return convertView;
        }

        class ViewHolder {
            TextView latView;
            TextView lngView;
        }

    }

    class Position {
        float latitude;
        float longitude;

        public Position(float lat, float lng) {
            latitude = lat;
            longitude = lng;
        }
    }
}
