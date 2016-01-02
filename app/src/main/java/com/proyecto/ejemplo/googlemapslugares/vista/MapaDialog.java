package com.proyecto.ejemplo.googlemapslugares.vista;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.proyecto.ejemplo.googlemapslugares.R;
import com.proyecto.ejemplo.googlemapslugares.lugar.Place;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

///import com.ngshah.goglemapv2withlazyloading.R;

/**
 * Created by choqu_000 on 01/01/2016.
 */
public class MapaDialog extends FragmentActivity  {

    // Google Map
    private GoogleMap googleMap;
    private static Marker markeres;
    // Latitude & Longitude
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;
    private String name;


    // Stores near by places
    Place[] mPlaces = null;
    private LatLng mClickPos;
    // Links marker id and place object
    HashMap<String, Place> mHMReference = new HashMap<String, Place>();

    // Specifies the drawMarker() to draw the marker with default color
    private static final float UNDEFINED_COLOR = -1;
    private String urlimagen = "http://www.yodot.com/images/jpeg-images-sm.png";
    private String urlimagenes;

    /*
    private GoogleMap googleMap;
    private final LatLng HAMBURG = new LatLng(53.558, 9.927);
    private final LatLng KIEL = new LatLng(53.551, 9.993);*/
    private Marker marker;
    private Hashtable<String, String> markers;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> location = null;
    /*
    static MarkerOptions marker;
    private Hashtable<String, String> markers;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);



        //*** Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String url = "http://informaticaintegral.co/imagenabajar/localizacion.php";
        try {

            JSONArray data = new JSONArray(getHttpGet(url));

            location = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();
                map.put("LocationID", c.getString("LocationID"));
                map.put("Latitude", c.getString("Latitude"));
                map.put("Longitude", c.getString("Longitude"));
                map.put("LocationName", c.getString("LocationName"));
                map.put("ImagePath", c.getString("ImagePath"));
                location.add(map);
                MyArrList.add(map);

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMap();
        initImageLoader();
        markers = new Hashtable<String, String>();
        imageLoader = ImageLoader.getInstance();
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap))
                .getMap();

        initImageLoader();
        markers = new Hashtable<String, String>();
        imageLoader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_launcher)        //    Display Stub Image
                .showImageForEmptyUri(R.drawable.ic_launcher)    //    If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();

        if ( googleMap != null ) {

            googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

            final Marker hamburg = googleMap.addMarker(new MarkerOptions().position(new LatLng(Latitude, Longitude))
                    .title("Hamburg"));
            //markers.put(hamburg.getId(), "http://img.india-forums.com/images/100x100/37525-a-still-image-of-akshay-kumar.jpg");

            /*googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude), 15));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);*/
            //Los Markets
            LatLng coordinate = new LatLng(Latitude, Longitude);
            googleMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 17));

            // *** Marker (Loop)
            for (int i = 0; i < location.size(); i++) {
                Latitude = Double.parseDouble(location.get(i).get("Latitude").toString());
                Longitude = Double.parseDouble(location.get(i).get("Longitude").toString());
                name = location.get(i).get("LocationName").toString();
                urlimagenes = location.get(i).get("ImagePath").toString();
                //MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitude, Longitude)).title(name);
                final Marker kiel = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Latitude, Longitude))
                        .title(name)
                        .snippet(urlimagenes)
                );

                //googleMap.addMarker(marker);
            }





        }
    }

    //
    public static String getHttpGet(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    private class CustomInfoWindowAdapter implements InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_info_window,
                    null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (MapaDialog.this.marker != null
                    && MapaDialog.this.marker.isInfoWindowShown()) {
                MapaDialog.this.marker.hideInfoWindow();
                MapaDialog.this.marker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            MapaDialog.this.marker = marker;

            String url = marker.getSnippet();

            if (marker.getId() != null && markers != null && markers.size() > 0) {
                if ( markers.get(marker.getId()) != null &&
                        markers.get(marker.getId()) != null) {
                    url = markers.get(marker.getId());
                }
            }
            final ImageView image = ((ImageView) view.findViewById(R.id.badge));

            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, image, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);
                                getInfoContents(marker);
                            }
                        });
            } else {
                image.setImageResource(R.drawable.ic_launcher);
            }

            final String title = marker.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            final String snippet = marker.getSnippet();
            final TextView snippetUi = ((TextView) view
                    .findViewById(R.id.snippet));
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }

            return view;
        }
    }

    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
                .build();

        ImageLoader.getInstance().init(config);
    }
}