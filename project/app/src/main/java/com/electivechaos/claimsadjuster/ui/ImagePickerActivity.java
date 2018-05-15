package com.electivechaos.claimsadjuster.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.electivechaos.claimsadjuster.BaseActivity;
import com.electivechaos.claimsadjuster.Folder;
import com.electivechaos.claimsadjuster.GridSpacingItemDecoration;
import com.electivechaos.claimsadjuster.ImageHelper;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.listeners.FolderClickListener;
import com.electivechaos.claimsadjuster.listeners.ImageClickListener;
import com.electivechaos.claimsadjuster.pojo.Image;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by krishna on 11/15/17.
 */

public class ImagePickerActivity extends BaseActivity {
    private final String[] projection = new String[]{MediaStore.Images.Media._ID
            , MediaStore.Images.Media.DISPLAY_NAME
            , MediaStore.Images.Media.DATA
            , MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
    static RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.imagepicker_image_placeholder)
            .error(R.drawable.imagepicker_image_placeholder)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


    RecyclerView gridView;
    AppCompatImageView backToFolder;
    TextView imageToolbarTitle;
    TextView doneSelection;
    Boolean isFolderView;

    List<Image> selectedImages = null;
    int numberOfAlreadySelectedImages = 0;
    private FolderClickListener folderClickListener = new FolderClickListener() {

        @Override
        public void onFolderClicked(Folder folder) {
            setImageAdapter(folder.getImages(), folder.getFolderName());
        }
    };
    private ImageClickListener imageClickListener = new ImageClickListener() {


        @Override
        public void onImageSelectionChanged(List<Image> selectedImgs) {
            selectedImages = selectedImgs;
            if(selectedImgs!= null && selectedImgs.size() > 0){
                doneSelection.setVisibility(View.VISIBLE);
            }else{
                doneSelection.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        numberOfAlreadySelectedImages =  getIntent().getIntExtra("already_selected_images",0);
        if(selectedImages == null){
            selectedImages = new ArrayList<>();
        }


        setContentView(R.layout.image_picker_layout);
        isFolderView = true;
        backToFolder = findViewById(R.id.image_toolbar_back);
        doneSelection = findViewById(R.id.text_toolbar_done);
        backToFolder.setVisibility(View.INVISIBLE);
        doneSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("ImageUrls", (ArrayList<? extends Parcelable>) selectedImages);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        imageToolbarTitle = findViewById(R.id.text_toolbar_title);
        imageToolbarTitle.setText(getResources().getString(R.string.imagepicker_title_folder));
        Cursor cursor = getBaseContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Images.Media.DATE_ADDED);

        if (cursor == null) {
            return;
        }

        List<Image> images = new ArrayList<>(cursor.getCount());
        Map<String, Folder> folderMap =  new LinkedHashMap<>() ;

        if (cursor.moveToLast()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                String bucket = cursor.getString(cursor.getColumnIndex(projection[3]));

                File file = makeSafeFile(path);
                if (file != null && file.exists()) {
                    Image image = new Image(id, name, path);
                    images.add(image);

                    if (folderMap != null) {
                        Folder folder = folderMap.get(bucket);
                        if (folder == null) {
                            folder = new Folder(bucket);
                            folderMap.put(bucket, folder);
                        }
                        folder.getImages().add(image);
                    }
                }

            } while (cursor.moveToPrevious());
        }
        cursor.close();
        /* Convert HashMap to ArrayList if not null */
        List<Folder> folders = null;
        if (folderMap != null) {
            folders = new ArrayList<>(folderMap.values());
        }
        FolderPickerAdapter imageAdapter = new ImagePickerActivity.FolderPickerAdapter(this,folders, folderClickListener);
        gridView = findViewById(R.id.gridview);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getBaseContext(),2);
        gridView.setLayoutManager(mLayoutManager);
        gridView.setHasFixedSize(true);
        GridSpacingItemDecoration itemOffsetDecoration = new GridSpacingItemDecoration(2,
                getBaseContext().getResources().getDimensionPixelSize(R.dimen.imagepicker_item_padding),
                false
        );
        gridView.addItemDecoration(itemOffsetDecoration);
        mLayoutManager.setSpanCount(CommonUtils.calculateNoOfColumns(getBaseContext()));
        gridView.setAdapter(imageAdapter);

        final List<Folder> finalFolders = folders;
        backToFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFolderView = true;
                backToFolder.setVisibility(View.INVISIBLE);
                imageToolbarTitle.setText(getResources().getString(R.string.imagepicker_title_folder));
                FolderPickerAdapter imageAdapter = new ImagePickerActivity.FolderPickerAdapter(getBaseContext(), finalFolders, folderClickListener);
                gridView.setAdapter(imageAdapter);
            }
        });

    }
    private static File makeSafeFile(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        try {
            return new File(path);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static class FolderPickerAdapter extends RecyclerView.Adapter<ImagePickerActivity.FolderPickerAdapter.MyViewHolder>{
        Context context;
        List<Folder> folders;
        FolderClickListener folderClickListener;
        LayoutInflater mInflater;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.imagepicker_item_folder, parent, false);

            return new ImagePickerActivity.FolderPickerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final  Folder folder = folders.get(position);
            Glide.with(context)
                    .load("file://"+folder.getImages().get(0).getPath())
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageView);
            holder.folderName.setText(folder.getFolderName());
            holder.photoCount.setText(String.valueOf(folder.getImages().size()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    folderClickListener.onFolderClicked(folder);
                }
            });
        }

        @Override
        public int getItemCount() {
            return folders.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView photoCount;
            public TextView folderName;
            public MyViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.image_folder_thumbnail);
                photoCount = view.findViewById(R.id.text_photo_count);
                folderName = view.findViewById(R.id.text_folder_name);

            }
        }


        FolderPickerAdapter (Context context, List<Folder> folders, FolderClickListener folderClickListener){
            mInflater = LayoutInflater.from(context);
            this.context = context;
            this.folders = folders;
            this.folderClickListener = folderClickListener;
        }


    }


    public static class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerActivity.ImagePickerAdapter.MyViewHolder>{
        Context context;
        List<Image> images;
        List<Image> selectedImages;
        int numberOfAlreadySelectedImages = 0;
        LayoutInflater mInflater;
        ImageClickListener imageClickListener;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.imagepicker_item_image, parent, false);

            return new ImagePickerActivity.ImagePickerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            final Image image = images.get(position);
            final boolean isSelected = isSelected(image);
            Glide.with(context)
                    .load("file://"+image.getPath())
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageView);
            holder.gifIndicator.setVisibility(ImageHelper.isGifFormat(image) ? View.VISIBLE : View.GONE);

            holder.alphaView.setAlpha(isSelected ? 0.5f : 0.0f);
            holder.container.setForeground(isSelected
                    ? ContextCompat.getDrawable(context, R.drawable.imagepicker_ic_selected)
                    : null);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSelected){
                        selectedImages.remove(image);
                        notifyItemChanged(position);
                        numberOfAlreadySelectedImages = numberOfAlreadySelectedImages -1;
                        imageClickListener.onImageSelectionChanged(getSelectedImages());
                    }else{
                        if(numberOfAlreadySelectedImages < 300){
                            selectedImages.add(image);
                            notifyItemChanged(position);
                            numberOfAlreadySelectedImages = numberOfAlreadySelectedImages + 1;
                            imageClickListener.onImageSelectionChanged(getSelectedImages());
                        }else{
                            Toast.makeText(context,"You can not select more than 30 images.",Toast.LENGTH_LONG).show();
                        }

                    }

                }
            });
        }

        private boolean isSelected(Image image) {
            for (Image selectedImage : selectedImages) {
                if (selectedImage.getPath().equals(image.getPath())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView gifIndicator;
            public FrameLayout container;
            public View alphaView;
            public MyViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.image_thumbnail);
                gifIndicator = view.findViewById(R.id.gif_indicator);
                container = (FrameLayout) view;
                alphaView = view.findViewById(R.id.view_alpha);

            }
        }


        ImagePickerAdapter (Context context, List<Image> images,  List<Image> selectedImages,ImageClickListener imageClickListener, int numberOfAlreadySelectedImages){
            mInflater = LayoutInflater.from(context);
            this.context = context;
            this.images = images;
            this.selectedImages = selectedImages;
            this.imageClickListener = imageClickListener;
            this.numberOfAlreadySelectedImages = numberOfAlreadySelectedImages;
            if(this.selectedImages == null){
                this.selectedImages = new ArrayList<>();
            }
        }

        public List<Image> getSelectedImages() {
            return selectedImages;
        }


    }

    public void  setImageAdapter (List<Image> images, String folderName){
        isFolderView = false;
        backToFolder.setVisibility(View.VISIBLE);
        imageToolbarTitle.setText(folderName);
        ImagePickerAdapter adapter = new ImagePickerAdapter(getBaseContext(),images,selectedImages, imageClickListener,numberOfAlreadySelectedImages);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}

