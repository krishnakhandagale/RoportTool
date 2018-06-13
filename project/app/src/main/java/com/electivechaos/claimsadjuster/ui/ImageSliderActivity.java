package com.electivechaos.claimsadjuster.ui;


/**
 * Created by krishna on 11/7/17.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.electivechaos.claimsadjuster.BaseActivity;
import com.electivechaos.claimsadjuster.DepthPageTransformer;
import com.electivechaos.claimsadjuster.ImageFragment;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.listeners.OnLastSelectionChangeListener;
import com.electivechaos.claimsadjuster.pojo.Image;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;

import java.util.ArrayList;

public class ImageSliderActivity extends BaseActivity implements ImageFragment.MonitorImageDetailsChange {
    static  int ITEMS;
    ImagePagerAdapter mAdapter;
    ImagePreviewListAdapter mImagePreviewListAdapter;
    static ViewPager mPager;
    RecyclerView mImagePreviewList;
    ImageButton selectImagesBtn;

    private ArrayList<ImageDetailsPOJO> imagesInformation;
    private ArrayList<Image> imageList;
    private int lastSelectedPosition = -1;
    private int labelPosition = -1;

    private OnLastSelectionChangeListener onLastSelectionChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_slider_layout);




        imageList = (ArrayList<Image>) getIntent().getExtras().get("ImageList");
        labelPosition = getIntent().getExtras().getInt("labelPosition");
        imagesInformation = new ArrayList<>();
        for(int i =0; i< imageList.size();i++){
            ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
            imgObj.setImageUrl(imageList.get(i).getPath());
            imgObj.setTitle("");
            imgObj.setDescription("");
            imagesInformation.add(imgObj);
        }

        if(savedInstanceState != null){
            lastSelectedPosition = savedInstanceState.getInt("lastSelectedPosition",-1);
            imagesInformation = (ArrayList<ImageDetailsPOJO>) savedInstanceState.getSerializable("imagesInformation");
            labelPosition = savedInstanceState.getInt("labelPosition");
        }

        ITEMS = imageList.size();
        selectImagesBtn = findViewById(R.id.selectImages);
        mImagePreviewList = findViewById(R.id.imagePreviewList);
        mPager = findViewById(R.id.pager);

        mAdapter = new ImagePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mImagePreviewList.setLayoutManager(mLayoutManager);



        onLastSelectionChangeListener = new OnLastSelectionChangeListener() {
            @Override
            public void onLastSelectionChanged(final int lastSelectedPos) {
                mLayoutManager.scrollToPositionWithOffset(lastSelectedPos,20);
                lastSelectedPosition = lastSelectedPos;
            }
        };
        mImagePreviewListAdapter = new ImagePreviewListAdapter(imagesInformation,getBaseContext(),mPager,lastSelectedPosition, onLastSelectionChangeListener);
        mImagePreviewList.setAdapter(mImagePreviewListAdapter);

        selectImagesBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle imagesObj = new Bundle();

                imagesObj.putSerializable("selected_images",imagesInformation);
                imagesObj.putInt("labelPosition",labelPosition);
                intent.putExtras(imagesObj);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    @Override
    public void updateImageTitle(String title, int position) {
        imagesInformation.get(position).setTitle(title);
    }

    @Override
    public void updateImageDescription(String description, int position) {
        imagesInformation.get(position).setDescription(description);
    }

    @Override
    public void setUnsetDamage(boolean isDamage, int position) {
        imagesInformation.get(position).setIsDamage(isDamage);
    }

    @Override
    public void setUnsetOverview(boolean isDamage, int position) {
        imagesInformation.get(position).setOverview(isDamage);
    }

    @Override
    public void setUnsetPointOfOrigin(boolean isPointOfOrigin, int position) {
        imagesInformation.get(position).setPointOfOrigin(isPointOfOrigin);
    }

    public class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ImagePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.init(imagesInformation.get(position),position,mPager);
        }
    }

    public class ImagePreviewListAdapter extends RecyclerView.Adapter<ImagePreviewListAdapter.MyViewHolder> {

        private ArrayList<ImageDetailsPOJO> imageList;
        private Context context;
        private ViewPager mPager;
        private int lastSelectedPos = -1;
        private OnLastSelectionChangeListener onLastSelectionChangeListener;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView hiddenText;
            public MyViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.imagePreview);
                hiddenText = view.findViewById(R.id.hiddenText);
            }
        }
        public ImagePreviewListAdapter(ArrayList<ImageDetailsPOJO> imageList, Context context, ViewPager mPager, int lastSelectedPos, OnLastSelectionChangeListener onLastSelectionChangeListener) {
            this.imageList = imageList;
            this.context = context;
            this.mPager = mPager;
            this.lastSelectedPos =  lastSelectedPos;
            this.onLastSelectionChangeListener = onLastSelectionChangeListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_preview_list, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.hiddenText.setTag(position);
            if(lastSelectedPos == position){
                lastSelectedPos = position;
                onLastSelectionChangeListener.onLastSelectionChanged(position);
            }
            else if(position == 0 &&   lastSelectedPos == -1){
                lastSelectedPos = 0 ;
                onLastSelectionChangeListener.onLastSelectionChanged(position);
            }


            ImageDetailsPOJO imgDetails = imageList.get(position);
            Glide.with(context).load("file://"+imgDetails.getImageUrl()).thumbnail(0.1f).into(holder.imageView);
            holder.imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lastSelectedPos != position){
                        lastSelectedPos = position;
                        onLastSelectionChangeListener.onLastSelectionChanged(position);
                        holder.imageView.setBackgroundResource(R.drawable.image_selection_border);
                        mPager.setCurrentItem(position);
                        notifyItemChanged(position);
                    }


                }
            });

            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if((int)holder.hiddenText.getTag() == position){
                        lastSelectedPos = position;
                        onLastSelectionChangeListener.onLastSelectionChanged(position);
                        holder.imageView.setBackgroundResource(R.drawable.image_selection_border);
                        notifyItemChanged(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastSelectedPosition",lastSelectedPosition);
        outState.putSerializable("imagesInformation",imagesInformation);
        outState.putInt("labelPosition",labelPosition);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}