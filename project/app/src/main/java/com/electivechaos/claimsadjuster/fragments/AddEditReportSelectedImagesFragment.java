package com.electivechaos.claimsadjuster.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.electivechaos.claimsadjuster.ImageHelper;
import com.electivechaos.claimsadjuster.PermissionUtilities;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.SingleMediaScanner;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;
import com.electivechaos.claimsadjuster.listeners.OnImageRemovalListener;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.pojo.Image;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.ui.AddEditReportActivity;
import com.electivechaos.claimsadjuster.ui.ImagePickerActivity;
import com.electivechaos.claimsadjuster.ui.ImageSliderActivity;
import com.electivechaos.claimsadjuster.ui.SingleImageDetailsActivity;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by krishna on 11/23/17.
 */

public class AddEditReportSelectedImagesFragment extends Fragment {
    private int REQUEST_CAMERA = 0;
    private int FRONT_IMAGE_REQUEST = 100;
    private int BACK_IMAGE_REQUEST = 200;
    private int LEFT_IMAGE_REQUEST = 300;
    private int RIGHT_IMAGE_REQUEST = 400;

    private int SELECT_FILE = 1;
    private int ADD_IMAGE_DETAILS = 2;
    private static int SET_CLICKED_IMAGE_DETAILS = 3;


    private LinearLayout parentLayout;
    private CoordinatorLayout selectImagesParentLayout;
    private RecyclerView selectedImagesRecyclerView;
    private ProgressBar progressBar;
    private SelectedImagesAdapter selectedImagesAdapter;

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn,fabGoNextBtn, selectPhotoBtn, fabAddLabelBtn, fabGenerateReportBtn;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;



    private ImageView imgViewFront;
    private ImageView imgViewBack;
    private ImageView imgViewLeft;
    private ImageView imgViewRight;



    private ImageView imgViewFrontPreview;
    private ImageView imgViewBackPreview;
    private ImageView  imgViewLeftPreview;
    private ImageView imgViewRightPreview;

    private ImageButton imgRemoveBtnFront;
    private ImageButton imgRemoveBtnBack;
    private ImageButton imgRemoveBtnLeft;
    private ImageButton imgRemoveBtnRight;

    private Uri fileUri;
    private String mCurrentPhotoPath;
    private File photoFile;

    //This is used by image picker
    private ArrayList<Image> selectedImages = null;

    private ArrayList<ImageDetailsPOJO> selectedImageList = null;
    private ArrayList<ImageDetailsPOJO> selectedElevationImagesList = new ArrayList<>();
    private  int labelPosition;


    private OnImageRemovalListener onImageRemovalListener = null;
    static RequestOptions options = null;

    private SelectedImagesDataInterface selectedImagesDataInterface;

    private NextButtonClickListener nextButtonClickListener;

    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;

    public static AddEditReportSelectedImagesFragment initFragment(ArrayList<ImageDetailsPOJO> selectedImageList, ArrayList<ImageDetailsPOJO> selectedElevationImagesList,int position) {
        AddEditReportSelectedImagesFragment fragment = new AddEditReportSelectedImagesFragment();
        Bundle args = new Bundle();

        if(selectedElevationImagesList == null || selectedElevationImagesList.size() == 0){
            selectedElevationImagesList = new ArrayList<>();
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
        }


        args.putSerializable("selectedImagesList", selectedImageList);
        args.putSerializable("selectedElevationImagesList", selectedElevationImagesList);
        args.putInt("position",position);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedElevationImagesList = (ArrayList<ImageDetailsPOJO>) getArguments().get("selectedElevationImagesList");
            selectedImageList = (ArrayList<ImageDetailsPOJO>) getArguments().get("selectedImagesList");
            labelPosition = (int) getArguments().get("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View selectImageView = inflater.inflate(R.layout.fragment_select_photo, container, false);

        showFabBtn =  selectImageView.findViewById(R.id.showFab);
        fabGoNextBtn = selectImageView.findViewById(R.id.fabGoNext);
        fabAddLabelBtn = selectImageView.findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn = selectImageView.findViewById(R.id.fabGenerateReport);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);
        showFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
        onImageRemovalListener = new OnImageRemovalListener() {
            @Override
            public void onImageSelectionChanged(List<ImageDetailsPOJO> selectedImgs) {
                selectedImageList = (ArrayList<ImageDetailsPOJO>) selectedImgs;
            }
        };

        fabAddLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLabelAddClickListener.onLabelAddClick();
            }
        });

        options = new RequestOptions()
                .placeholder(R.drawable.imagepicker_image_placeholder)
                .error(R.drawable.imagepicker_image_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        selectPhotoBtn = selectImageView.findViewById(R.id.btnSelectPhoto);
        parentLayout = selectImageView.findViewById(R.id.parentLinearLayout);
        selectedImagesRecyclerView = selectImageView.findViewById(R.id.selectedImagesRecyclerView);
        selectImagesParentLayout = selectImageView.findViewById(R.id.selectImagesParentLayout);
        progressBar = selectImageView.findViewById(R.id.progressBar);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), CommonUtils.calculateNoOfColumns(getContext()));
        selectedImagesRecyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(selectedImageList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                selectedImagesAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };
        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        ith.attachToRecyclerView(selectedImagesRecyclerView);


        imgViewFront = selectImageView.findViewById(R.id.imgViewFront);
        imgViewBack = selectImageView.findViewById(R.id.imgViewBack);
        imgViewLeft = selectImageView.findViewById(R.id.imgViewLeft);
        imgViewRight = selectImageView.findViewById(R.id.imgViewRight);


        imgViewFrontPreview = selectImageView.findViewById(R.id.frontImagePreview);
        imgViewBackPreview = selectImageView.findViewById(R.id.backImagePreview);
        imgViewLeftPreview = selectImageView.findViewById(R.id.leftImagePreview);
        imgViewRightPreview = selectImageView.findViewById(R.id.rightImagePreview);


        imgViewFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(FRONT_IMAGE_REQUEST);

            }
        });


        imgViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(BACK_IMAGE_REQUEST);
            }
        });


        imgViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(LEFT_IMAGE_REQUEST);
            }
        });

        imgViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(RIGHT_IMAGE_REQUEST);
            }
        });

        imgRemoveBtnFront=selectImageView.findViewById(R.id.imgBtnRemoveFront);
        imgRemoveBtnBack=selectImageView.findViewById(R.id.imgBtnRemoveBack);
        imgRemoveBtnLeft=selectImageView.findViewById(R.id.imgBtnRemoveLeft);
        imgRemoveBtnRight=selectImageView.findViewById(R.id.imgBtnRemoveRight);

        imgRemoveBtnFront.setVisibility(View.INVISIBLE);
        imgRemoveBtnBack.setVisibility(View.INVISIBLE);
        imgRemoveBtnLeft.setVisibility(View.INVISIBLE);
        imgRemoveBtnRight.setVisibility(View.INVISIBLE);

        imgRemoveBtnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0)
                {
                        selectedElevationImagesList.set(0,new ImageDetailsPOJO());
                        imgViewFrontPreview.setImageDrawable(null);
                        imgRemoveBtnFront.setVisibility(View.INVISIBLE);

                }
            }
        });

        imgRemoveBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0)
                {
                    selectedElevationImagesList.set(1,new ImageDetailsPOJO());
                    imgViewBackPreview.setImageDrawable(null);
                    imgRemoveBtnBack.setVisibility(View.INVISIBLE);

                }

            }
        });

        imgRemoveBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0)
                {
                    selectedElevationImagesList.set(2,new ImageDetailsPOJO());
                    imgViewLeftPreview.setImageDrawable(null);
                    imgRemoveBtnLeft.setVisibility(View.INVISIBLE);

                }

            }
        });

        imgRemoveBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0)
                {
                    selectedElevationImagesList.set(3,new ImageDetailsPOJO());
                    imgViewRightPreview.setImageDrawable(null);
                    imgRemoveBtnRight.setVisibility(View.INVISIBLE);

                }

            }
        });
        selectPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        fabGoNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClickListener.onNextButtonClick();
            }
        });
        return selectImageView;



    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (selectedImagesAdapter != null) {
            selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
        } else if (selectedImageList != null && selectedImageList.size() > 0) {
            selectedImagesAdapter = new SelectedImagesAdapter(selectedImageList, getContext(), onImageRemovalListener);
            selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
        }


        if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0) {
            if(selectedElevationImagesList.get(0).getImageUrl() != null && !selectedElevationImagesList.get(0).getImageUrl().isEmpty()){
                Glide.with(getActivity())
                        .load("file://" + selectedElevationImagesList.get(0).getImageUrl())
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(imgViewFrontPreview);
                imgRemoveBtnFront.setVisibility(View.VISIBLE);

            }
            if(selectedElevationImagesList.get(1).getImageUrl() != null && !selectedElevationImagesList.get(1).getImageUrl().isEmpty()){
                Glide.with(getActivity())
                        .load("file://" + selectedElevationImagesList.get(1).getImageUrl())
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(imgViewBackPreview);
                imgRemoveBtnBack.setVisibility(View.VISIBLE);
            }
            if(selectedElevationImagesList.get(2).getImageUrl() != null && !selectedElevationImagesList.get(2).getImageUrl().isEmpty()){
                Glide.with(getActivity())
                        .load("file://" + selectedElevationImagesList.get(2).getImageUrl())
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(imgViewLeftPreview);
                imgRemoveBtnLeft.setVisibility(View.VISIBLE);

            }
            if(selectedElevationImagesList.get(3).getImageUrl() != null && !selectedElevationImagesList.get(3).getImageUrl().isEmpty()){
                Glide.with(getActivity())
                        .load("file://" + selectedElevationImagesList.get(3).getImageUrl())
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(imgViewRightPreview);
                imgRemoveBtnRight.setVisibility(View.VISIBLE);
            }
        }
    }
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS);

                    if (result)
                        cameraIntent(REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS);
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        if (selectedImageList == null) {
            selectedImageList = new ArrayList<>();
        }
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra("already_selected_images", selectedImageList.size());
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_FILE);

    }

    private void cameraIntent(int requestId) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

            photoFile = getOutputMediaFile();
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    fileUri = Uri.fromFile(photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                } else {
                    fileUri = FileProvider.getUriForFile(getContext().getApplicationContext(),
                            getContext().getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                }


                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                ImageHelper.grantAppPermission(getContext(), intent, fileUri);
                startActivityForResult(intent, requestId);
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                if (data != null) {
                    onSelectFromGalleryResult(data);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please try again.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectImage();
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                if (fileUri != null) {
                    try {
                        onCaptureImageResult(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectImage();
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }
            } else if (requestCode == FRONT_IMAGE_REQUEST) {
                if (fileUri != null) {
                    onElevationImageCaptureResult(data, requestCode);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           openCamera(PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS, FRONT_IMAGE_REQUEST);
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }

            } else if (requestCode == BACK_IMAGE_REQUEST) {
                if (fileUri != null) {
                    onElevationImageCaptureResult(data, requestCode);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openCamera(PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS, BACK_IMAGE_REQUEST);
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }

            } else if (requestCode == LEFT_IMAGE_REQUEST) {
                if (fileUri != null) {
                    onElevationImageCaptureResult(data, requestCode);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openCamera(PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS, LEFT_IMAGE_REQUEST);
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }

            } else if (requestCode == RIGHT_IMAGE_REQUEST) {
                if (fileUri != null) {
                    onElevationImageCaptureResult(data, requestCode);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           openCamera(PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS, RIGHT_IMAGE_REQUEST);
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }

            } else if (requestCode == ADD_IMAGE_DETAILS) {
                //Got all selected images here
                ArrayList<ImageDetailsPOJO> selectedImageListReturned = (ArrayList<ImageDetailsPOJO>) data.getExtras().getSerializable("selected_images");
                if (selectedImageList == null) {
                    selectedImageList = new ArrayList<>();
                }
                selectedImageListReturned.addAll(selectedImageList);
                selectedImageList = selectedImageListReturned;

                selectedImagesAdapter = new SelectedImagesAdapter(selectedImageList, getContext(), onImageRemovalListener);
                selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);

                selectedImagesDataInterface.setSelectedImages(selectedImageList,labelPosition);

            } else if (requestCode == SET_CLICKED_IMAGE_DETAILS) {
                ImageDetailsPOJO imageDetails = (ImageDetailsPOJO) data.getExtras().getSerializable("image_entered_details");

                if (data.getExtras().getBoolean("isEdit")) {
                    int position = data.getExtras().getInt("position");
                    selectedImageList.get(position).setTitle(imageDetails.getTitle());
                    selectedImageList.get(position).setImageUrl(imageDetails.getImageUrl());
                    selectedImageList.get(position).setDescription(imageDetails.getDescription());

                } else {
                    if (selectedImageList == null) {
                        selectedImageList = new ArrayList<>();
                    }
                    selectedImageList.add(0, imageDetails);
                }


                if (selectedImagesAdapter == null) {
                    selectedImagesAdapter = new SelectedImagesAdapter(selectedImageList, getContext(), onImageRemovalListener);
                    selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
                } else {
                    selectedImagesAdapter.notifyDataSetChanged();


                }
                selectedImagesDataInterface.setSelectedImages(selectedImageList,labelPosition);
            }

        }
    }

    private void openCamera(int requestId, int cameraIntentReqCode)
    {
        boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, requestId);

        if (result)
            cameraIntent(cameraIntentReqCode);
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ReportClickedImages");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void onElevationImageCaptureResult(Intent dat, final int requestId) {
        //Worked like charm
        new SingleMediaScanner(getContext(), photoFile, new OnMediaScannerListener() {
            @Override
            public void onMediaScanComplete(String path, Uri uri) {
                if (path != null) {
                    path = mCurrentPhotoPath;



                    ImageHelper.revokeAppPermission(getContext(), fileUri);
                    final String finalPath = path;
                    final String finalPath1 = path;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(requestId == FRONT_IMAGE_REQUEST){
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Front View for incidence");
                                imgObj.setTitle("Front View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(0,imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imgViewFrontPreview);
                                imgRemoveBtnFront.setVisibility(View.VISIBLE);

                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList,labelPosition);

                            }else if(requestId == BACK_IMAGE_REQUEST){

                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Back View for incidence");
                                imgObj.setTitle("Back View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(1,imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imgViewBackPreview);
                                imgRemoveBtnBack.setVisibility(View.VISIBLE);
                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList,labelPosition);
                            }else if(requestId == LEFT_IMAGE_REQUEST){
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Left View for incidence");
                                imgObj.setTitle("Left View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(2,imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imgViewLeftPreview);
                                imgRemoveBtnLeft.setVisibility(View.VISIBLE);
                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList,labelPosition);
                            }else if(requestId == RIGHT_IMAGE_REQUEST){
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Right View for incidence");
                                imgObj.setTitle("Right View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(3,imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .apply(options)
                                        .thumbnail(0.1f)
                                        .into(imgViewRightPreview);
                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList,labelPosition);
                                imgRemoveBtnRight.setVisibility(View.VISIBLE);
                            }

                        }

                    });
                }
            }
        });
    }

    private void onCaptureImageResult(Intent data) throws IOException {
        new SingleMediaScanner(getContext(), photoFile, new OnMediaScannerListener() {
            @Override
            public void onMediaScanComplete(String path, Uri uri) {
                if (path != null) {
                    path = mCurrentPhotoPath;
                    ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                    imgObj.setDescription("");
                    imgObj.setTitle("");
                    imgObj.setImageUrl(path);
                    ImageHelper.revokeAppPermission(getContext(), fileUri);
                    Intent intent = new Intent(getContext(), SingleImageDetailsActivity.class);
                    intent.putExtra("image_details", imgObj);
                    startActivityForResult(intent, SET_CLICKED_IMAGE_DETAILS);
                }
            }
        });
    }

    private void onSelectFromGalleryResult(Intent data) {
        selectedImages = data.getParcelableArrayListExtra("ImageUrls");
        Intent intent = new Intent(getActivity(), ImageSliderActivity.class);
        intent.putExtra("ImageList", selectedImages);
        startActivityForResult(intent, ADD_IMAGE_DETAILS);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(REQUEST_CAMERA);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(FRONT_IMAGE_REQUEST);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(BACK_IMAGE_REQUEST);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(LEFT_IMAGE_REQUEST);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(RIGHT_IMAGE_REQUEST);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS);
                }
                return;
            }
            case PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS);
                }
                return;
            }
        }
    }


    public class SelectedImagesAdapter extends RecyclerView.Adapter<AddEditReportSelectedImagesFragment.SelectedImagesAdapter.MyViewHolder> {

        private ArrayList<ImageDetailsPOJO> imageList;
        private Context context;
        private OnImageRemovalListener onImageRemovalListener;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView title;
            public TextView description;
            public ImageButton editBtn;
            public ImageButton deleteBtn;

            public MyViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.selectedImagePreview);
                title = view.findViewById(R.id.title);
                description = view.findViewById(R.id.description);
                editBtn = view.findViewById(R.id.editBtn);
                deleteBtn = view.findViewById(R.id.deleteBtn);

            }
        }


        public SelectedImagesAdapter(ArrayList<ImageDetailsPOJO> imageList, Context context, OnImageRemovalListener onImageRemovalListener) {
            this.imageList = imageList;
            this.context = context;
            this.onImageRemovalListener = onImageRemovalListener;
        }

        @Override
        public AddEditReportSelectedImagesFragment.SelectedImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_image_item, parent, false);

            return new AddEditReportSelectedImagesFragment.SelectedImagesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final AddEditReportSelectedImagesFragment.SelectedImagesAdapter.MyViewHolder holder, final int position) {

            final ImageDetailsPOJO imgDetails = imageList.get(position);
            holder.title.setText(imgDetails.getTitle());
            holder.description.setText(imgDetails.getDescription());

            Glide.with(context)
                    .load("file://" + imgDetails.getImageUrl())
                    .apply(options)
                    .into(holder.imageView);
            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SingleImageDetailsActivity.class);
                    intent.putExtra("image_details", imgDetails);
                    intent.putExtra("isEdit", true);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, SET_CLICKED_IMAGE_DETAILS);
                }
            });

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(getActivity() != null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Remove Image")
                                .setMessage("Are you sure wanna remove this image ?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        imageList.remove(position);
                                        onImageRemovalListener.onImageSelectionChanged(imageList);
                                        notifyDataSetChanged();
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));
                        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }


    @Override
    public void onAttach(Context context)
    {super.onAttach(context);
        try{
            selectedImagesDataInterface = (SelectedImagesDataInterface) getActivity();
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener)getActivity();
        }catch (ClassCastException exception){
            exception.printStackTrace();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable("fileUri");

            if (fileUri != null) {
                photoFile = new File(fileUri.getPath());
            }
            selectedImageList = (ArrayList<ImageDetailsPOJO>) savedInstanceState.getSerializable("selectedImageList");
            selectedElevationImagesList = (ArrayList<ImageDetailsPOJO>) savedInstanceState.getSerializable("selectedElevationImagesList");


            if (selectedImageList != null) {
                selectedImagesAdapter = new AddEditReportSelectedImagesFragment.SelectedImagesAdapter(selectedImageList, getContext(), onImageRemovalListener);
                selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
            }
            if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0) {
                if(selectedElevationImagesList.get(0).getImageUrl() != null && !selectedElevationImagesList.get(0).getImageUrl().isEmpty()){
                    Glide.with(getActivity())
                            .load("file://" + selectedElevationImagesList.get(0).getImageUrl())
                            .apply(options)
                            .into(imgViewFrontPreview);
                }
                if(selectedElevationImagesList.get(1).getImageUrl() != null && !selectedElevationImagesList.get(1).getImageUrl().isEmpty()){
                    Glide.with(getActivity())
                            .load("file://" + selectedElevationImagesList.get(1).getImageUrl())
                            .apply(options)
                            .into(imgViewBackPreview);
                }
                if(selectedElevationImagesList.get(2).getImageUrl() != null && !selectedElevationImagesList.get(2).getImageUrl().isEmpty()){
                    Glide.with(getActivity())
                            .load("file://" + selectedElevationImagesList.get(2).getImageUrl())
                            .apply(options)
                            .into(imgViewLeftPreview);
                }
                if(selectedElevationImagesList.get(3).getImageUrl() != null && !selectedElevationImagesList.get(3).getImageUrl().isEmpty()){
                    Glide.with(getActivity())
                            .load("file://" + selectedElevationImagesList.get(3).getImageUrl())
                            .apply(options)
                            .into(imgViewRightPreview);
                }
            }

        } else {
            if (selectedImageList != null) {
                selectedImagesAdapter = new AddEditReportSelectedImagesFragment.SelectedImagesAdapter(selectedImageList, getContext(), onImageRemovalListener);
                selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
            }
        }
    }
    public void animateFAB() {
        if (isFabOpen) {
            showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
            fabGoNextBtn.startAnimation(fab_close);
            selectPhotoBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            selectPhotoBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            isFabOpen = false;

        } else {
            showFabBtn.setImageResource(R.drawable.ic_close_white);
            fabGoNextBtn.startAnimation(fab_open);
            selectPhotoBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            selectPhotoBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            isFabOpen = true;
        }

    }
}

