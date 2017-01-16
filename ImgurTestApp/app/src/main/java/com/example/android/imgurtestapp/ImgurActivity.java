package com.example.android.imgurtestapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import java.util.ArrayList;

public class ImgurActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<ImgurImage>> {

    private static final String AUTHORIZATION_URL = "https://api.imgur.com/oauth2/authorize";
    private static final String CLIENT_ID = "5b5f6b057860d86";
    private static final String QUERY = "https://api.imgur.com/3/account/me/images";
    public static final String LOG_TAG = ImgurActivity.class.getName();
    private String accessToken;
    private String refreshToken;
    private static ArrayList<ImgurImage> imgurImages;
    private FragmentPagerAdapter adapterViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgur);

        String action = getIntent().getAction(); // return the intent that started this activity + return action

        // вызывается в самом начале, до авторизации в браузере, которая после аутентификации пользователя вызовет через интент данный компонент
        // вызывается, чтобы получить стринг, содержащий access_token
        if (action == null || !action.equals(Intent.ACTION_VIEW)) {

            Uri uri = Uri.parse(AUTHORIZATION_URL).buildUpon()
                    .appendQueryParameter("client_id",CLIENT_ID)
                    .appendQueryParameter("response_type","token")
                    .build();
            //Intent intent = new Intent();
            //intent.setData(uri);
            //startActivity(intent);
            //finish();
            // Решил сделать все-таки через WebView.
            WebView webview = new WebView(this);
            setContentView(webview);
            webview.loadUrl(uri.toString());

        } else { // после авторизации пользователя, с интентом приходи стринг с токеном

            Uri uri = getIntent().getData();
            String uriString = uri.toString();
            String paramsString = "http://callback?" + uriString.substring(uriString.indexOf("#") + 1);
            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
            sanitizer.setAllowUnregisteredParamaters(true);
            sanitizer.parseUrl(paramsString);
            accessToken = sanitizer.getValue("access_token");
            refreshToken = sanitizer.getValue("refresh_token");

            Log.d("tag", "access_token = " + accessToken);
            Log.d("tag", "refresh_token = " + refreshToken);

            getLoaderManager().initLoader(1,null,this);

        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter{
        private static int NUM_ITEMS = imgurImages.size();

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return ImgurFragment.newInstance(imgurImages.get(position).getTitle(), position, imgurImages.get(position).getImage(), NUM_ITEMS);
        }
    }



    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new ImgurLoader(ImgurActivity.this, QUERY, accessToken);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ImgurImage>> loader, ArrayList<ImgurImage> data) {
        if (data != null && !data.isEmpty()) {
            imgurImages = data;
            ViewPager vPager = (ViewPager) findViewById(R.id.vpPager);
            adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
            vPager.setAdapter(adapterViewPager);
        }


    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ImgurImage>> loader) {
        imgurImages.clear();

    }

    private static class ImgurLoader extends AsyncTaskLoader<ArrayList<ImgurImage>> {
        private String queryUrl;
        private String aToken;

        public ImgurLoader(Context context, String query, String token) {
            super(context);
            queryUrl = query;
            aToken = token;
        }

        @Override
        public ArrayList<ImgurImage> loadInBackground() {
            ArrayList<ImgurImage> result = QueryUtils.fetchImages(queryUrl, aToken);
            return result;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
