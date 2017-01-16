package com.example.android.imgurtestapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImgurFragment extends Fragment {

    private String image_title;
    private int image_number;
    private String image_url;
    private int arr_size;

    public static ImgurFragment newInstance(String im_t, int im_num, String im_url, int arrSize) {
        ImgurFragment imgurFragment = new ImgurFragment();
        Bundle args = new Bundle();
        args.putString("title", im_t);
        args.putInt("number", im_num);
        args.putInt("array_size", arrSize);
        args.putString("url", im_url);
        imgurFragment.setArguments(args);
        return imgurFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image_title = getArguments().getString("title");
        image_number = getArguments().getInt("number");
        image_url = getArguments().getString("url");
        arr_size = getArguments().getInt("array_size");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_fragment, container, false);
        TextView titleView = (TextView) view.findViewById(R.id.image_title);
        if (image_title.equals("null")) {
            titleView.setText("Untitled");
        } else {
            titleView.setText(image_title);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.image_pic);
        Glide.with(this).load(image_url).into(imageView);
        TextView numberView = (TextView) view.findViewById(R.id.image_number);
        numberView.setText(String.valueOf(image_number+1 + " / " + arr_size));
        return view;
    }
}
