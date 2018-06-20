package com.electivechaos.claimsadjuster.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.electivechaos.claimsadjuster.ImageHelper;
import com.electivechaos.claimsadjuster.PermissionUtilities;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.SingleMediaScanner;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSetImageFileUriListener;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.listeners.OnStarterFragmentDataChangeListener;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by krishna on 11/23/17.
 */

public class StarterPhotosFragment extends Fragment {

    private  static final int IMAGE_ONE_REQUEST_STARTER = 500;
    private  static final int IMAGE_TWO_REQUEST_STARTER = 600;
    private  static final int IMAGE_THREE_REQUEST_STARTER = 700;
    private  static final int IMAGE_FOUR_REQUEST_STARTER = 800;
    private  static final int HOUSE_NUMBER_REQUEST_STARTER = 900;

    private LinearLayout parentLayout;

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn;
    private FloatingActionButton fabGoNextBtn;
    private FloatingActionButton fabAddLabelBtn;
    private FloatingActionButton fabGenerateReportBtn;
    private FloatingActionButton fabSaveReportBtn;
    private Animation fab_open, fab_close;


    private ImageView imageOnePreview;
    private ImageView imageTwoPreview;
    private ImageView imageThreePreview;
    private ImageView imageFourPreview;
    private ImageView imageHouseNumberPreview;

    private ImageButton imgBtnRemoveOne;
    private ImageButton imgBtnRemoveTwo;
    private ImageButton imgBtnRemoveThree;
    private ImageButton imgBtnRemoveFour;
    private ImageButton imgBtnRemoveHouseNumber;

    private Uri fileUri;
    private String mCurrentPhotoPath;
    private File photoFile;


    private ArrayList<ImageDetailsPOJO> selectedElevationImagesList = new ArrayList<>();
    private int labelPosition;


    static RequestOptions options = null;

    private SelectedImagesDataInterface selectedImagesDataInterface;

    private NextButtonClickListener nextButtonClickListener;

    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;

    private OnSaveReportClickListener onSaveReportClickListener;

    private OnGenerateReportClickListener onGenerateReportClickListener;
    private OnSetImageFileUriListener onSetImageFileUriListener;
    private OnStarterFragmentDataChangeListener onStarterFragmentDataChangeListener;

    private  String houseNumber = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedElevationImagesList = (ArrayList<ImageDetailsPOJO>) getArguments().get("selectedElevationImagesList");

            if (selectedElevationImagesList == null || selectedElevationImagesList.size() == 0) {
                selectedElevationImagesList = new ArrayList<>();
                selectedElevationImagesList.add(new ImageDetailsPOJO());
                selectedElevationImagesList.add(new ImageDetailsPOJO());
                selectedElevationImagesList.add(new ImageDetailsPOJO());
                selectedElevationImagesList.add(new ImageDetailsPOJO());
            }

            labelPosition = (int) getArguments().get("position");
            houseNumber = (String) getArguments().get("houseNumber");
            mCurrentPhotoPath = getArguments().getString("fileUri");
            if(mCurrentPhotoPath != null){
                photoFile = new File(mCurrentPhotoPath);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    fileUri = Uri.fromFile(photoFile);

                } else {
                    fileUri = FileProvider.getUriForFile(getContext().getApplicationContext(),
                            getContext().getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                }

            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View selectImageView = inflater.inflate(R.layout.starter_fragment_layout, container, false);
        showFabBtn = selectImageView.findViewById(R.id.showFab);
        fabGoNextBtn = selectImageView.findViewById(R.id.fabGoNext);
        fabAddLabelBtn = selectImageView.findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn = selectImageView.findViewById(R.id.fabGenerateReport);
        fabSaveReportBtn = selectImageView.findViewById(R.id.fabSaveReport);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        showFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        fabAddLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLabelAddClickListener.onLabelAddClick();
                animateFAB();
            }
        });


        fabGenerateReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenerateReportClickListener.onReportGenerateClicked();
                animateFAB();
            }
        });


        options = new RequestOptions()
                .placeholder(R.drawable.imagepicker_image_placeholder)
                .error(R.drawable.imagepicker_image_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        parentLayout = selectImageView.findViewById(R.id.parentLinearLayout);


        ImageView imageViewOne = selectImageView.findViewById(R.id.imageViewOne);
        ImageView imageViewTwo = selectImageView.findViewById(R.id.imageViewTwo);
        ImageView imageViewThree = selectImageView.findViewById(R.id.imageViewThree);
        ImageView imageViewFour = selectImageView.findViewById(R.id.imageViewFour);
        ImageView imageViewHouseNumber = selectImageView.findViewById(R.id.houseNumberImage);


        imageOnePreview = selectImageView.findViewById(R.id.imageOnePreview);
        imageTwoPreview = selectImageView.findViewById(R.id.imageTwoPreview);
        imageThreePreview = selectImageView.findViewById(R.id.imageThreePreview);
        imageFourPreview = selectImageView.findViewById(R.id.imageFourPreview);
        imageHouseNumberPreview = selectImageView.findViewById(R.id.houseNumberPreview);

        imageViewHouseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(HOUSE_NUMBER_REQUEST_STARTER);
            }
        });
        imageViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(IMAGE_ONE_REQUEST_STARTER);

            }
        });


        imageViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(IMAGE_TWO_REQUEST_STARTER);
            }
        });


        imageViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(IMAGE_THREE_REQUEST_STARTER);
            }
        });

        imageViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(IMAGE_FOUR_REQUEST_STARTER);
            }
        });

        imgBtnRemoveOne = selectImageView.findViewById(R.id.imgBtnRemoveOne);
        imgBtnRemoveTwo = selectImageView.findViewById(R.id.imgBtnRemoveTwo);
        imgBtnRemoveThree = selectImageView.findViewById(R.id.imgBtnRemoveThree);
        imgBtnRemoveFour = selectImageView.findViewById(R.id.imgBtnRemoveFour);
        imgBtnRemoveHouseNumber =  selectImageView.findViewById(R.id.imgBtnRemoveHouseNumber);

        imgBtnRemoveOne.setVisibility(View.INVISIBLE);
        imgBtnRemoveTwo.setVisibility(View.INVISIBLE);
        imgBtnRemoveThree.setVisibility(View.INVISIBLE);
        imgBtnRemoveFour.setVisibility(View.INVISIBLE);
        imgBtnRemoveHouseNumber.setVisibility(View.INVISIBLE);

        imgBtnRemoveOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0) {
                    selectedElevationImagesList.set(0, new ImageDetailsPOJO());
                    selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                    imageOnePreview.setImageDrawable(null);
                    imgBtnRemoveOne.setVisibility(View.INVISIBLE);

                }
            }
        });

        imgBtnRemoveTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0) {
                    selectedElevationImagesList.set(1, new ImageDetailsPOJO());
                    selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                    imageTwoPreview.setImageDrawable(null);
                    imgBtnRemoveTwo.setVisibility(View.INVISIBLE);

                }

            }
        });

        imgBtnRemoveThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0) {
                    selectedElevationImagesList.set(2, new ImageDetailsPOJO());
                    selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                    imageThreePreview.setImageDrawable(null);
                    imgBtnRemoveThree.setVisibility(View.INVISIBLE);

                }

            }
        });

        imgBtnRemoveFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0) {
                    selectedElevationImagesList.set(3, new ImageDetailsPOJO());
                    selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                    imageFourPreview.setImageDrawable(null);
                    imgBtnRemoveFour.setVisibility(View.INVISIBLE);

                }

            }
        });

        imgBtnRemoveHouseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(houseNumber)) {
                    onStarterFragmentDataChangeListener.onHouseNumberChange("", labelPosition);
                    imageHouseNumberPreview.setImageDrawable(null);
                    imgBtnRemoveHouseNumber.setVisibility(View.INVISIBLE);

                }

            }
        });

        fabGoNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClickListener.onNextButtonClick();
                animateFAB();
            }
        });


        fabSaveReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveReportClickListener.onReportSave(true);
                animateFAB();
            }
        });


        if(savedInstanceState != null){
            fileUri = savedInstanceState.getParcelable("fileUri");
        }
        return selectImageView;


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (selectedElevationImagesList != null && selectedElevationImagesList.size() > 0) {
            if (selectedElevationImagesList.get(0).getImageUrl() != null && !selectedElevationImagesList.get(0).getImageUrl().isEmpty()) {
                Glide.with(getActivity())
                        .load("file://" + selectedElevationImagesList.get(0).getImageUrl())
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(imageOnePreview);
                imgBtnRemoveOne.setVisibility(View.VISIBLE);

            }
            if (selectedElevationImagesList.get(1).getImageUrl() != null && !selectedElevationImagesList.get(1).getImageUrl().isEmpty()) {
                Glide.with(getActivity())
                        .load("file://" + selectedElevationImagesList.get(1).getImageUrl())
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(imageTwoPreview);
                imgBtnRemoveTwo.setVisibility(View.VISIBLE);
            }
            if (selectedElevationImagesList.get(2).getImageUrl() != null && !selectedElevationImagesList.get(2).getImageUrl().isEmpty()) {
                Glide.with(getActivity())
                        .load("file://" + selectedElevationImagesList.get(2).getImageUrl())
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(imageThreePreview);
                imgBtnRemoveThree.setVisibility(View.VISIBLE);

            }
            if (selectedElevationImagesList.get(3).getImageUrl() != null && !selectedElevationImagesList.get(3).getImageUrl().isEmpty()) {
                Glide.with(getActivity())
                        .load("file://" + selectedElevationImagesList.get(3).getImageUrl())
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(imageFourPreview);
                imgBtnRemoveFour.setVisibility(View.VISIBLE);
            }
        }

        if (!TextUtils.isEmpty(houseNumber)) {
            Glide.with(getActivity())
                    .load("file://" + houseNumber)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(imageHouseNumberPreview);
            imgBtnRemoveHouseNumber.setVisibility(View.VISIBLE);
        }
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
                onSetImageFileUriListener.onSetImageFileUri(mCurrentPhotoPath);
                getActivity().startActivityForResult(intent, requestId);
            }
        }
    }

    public void onElevationImageFourCapture(Intent data, int requestCode){
        if (fileUri != null) {
            onElevationImageCaptureResult(data, requestCode);
        } else {
            Snackbar snackbar = Snackbar.make(parentLayout, R.string.camera_error_msg, Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCamera(PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS, IMAGE_FOUR_REQUEST_STARTER);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
        }
    }

    public void onElevationImageThreeCapture(Intent data, int requestCode) {
        if (fileUri != null) {
            onElevationImageCaptureResult(data, requestCode);
        } else {
            Snackbar snackbar = Snackbar.make(parentLayout, R.string.camera_error_msg, Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCamera(PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS, IMAGE_THREE_REQUEST_STARTER);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
        }

    }

    public void onElevationImageOneCapture(Intent data, int requestCode) {
        if (fileUri != null) {
            onElevationImageCaptureResult(data, requestCode);
        } else {
            Snackbar snackbar = Snackbar.make(parentLayout, R.string.camera_error_msg, Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCamera(PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS, IMAGE_ONE_REQUEST_STARTER);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
        }
    }

    public void onElevationImageTwoCapture(Intent data, int requestCode) {
        if (fileUri != null) {
            onElevationImageCaptureResult(data, requestCode);
        } else {
            Snackbar snackbar = Snackbar.make(parentLayout, R.string.camera_error_msg, Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCamera(PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS, IMAGE_TWO_REQUEST_STARTER);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
        }
    }

    public void onHouseNumberImageCapture(Intent data, int requestCode) {
        if (fileUri != null) {
            onElevationImageCaptureResult(data, requestCode);
        } else {
            Snackbar snackbar = Snackbar.make(parentLayout, R.string.camera_error_msg, Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCamera(PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS, HOUSE_NUMBER_REQUEST_STARTER);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
        }
    }


    private void openCamera(int requestId, int cameraIntentReqCode) {
        boolean result = PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, requestId);

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

    private void onElevationImageCaptureResult(Intent data, final int requestId) {
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
                            if (requestId == IMAGE_ONE_REQUEST_STARTER) {
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Front view for incidence");
                                imgObj.setTitle("Front View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(0, imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imageOnePreview);
                                imgBtnRemoveOne.setVisibility(View.VISIBLE);

                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);

                            } else if (requestId == IMAGE_TWO_REQUEST_STARTER) {

                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Back view for incidence");
                                imgObj.setTitle("Back View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(1, imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imageTwoPreview);
                                imgBtnRemoveTwo.setVisibility(View.VISIBLE);
                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                            } else if (requestId == IMAGE_THREE_REQUEST_STARTER) {
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Left view for incidence");
                                imgObj.setTitle("Left View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(2, imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imageThreePreview);
                                imgBtnRemoveThree.setVisibility(View.VISIBLE);
                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                            } else if (requestId == IMAGE_FOUR_REQUEST_STARTER) {
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Right view for incidence");
                                imgObj.setTitle("Right View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(3, imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .apply(options)
                                        .thumbnail(0.1f)
                                        .into(imageFourPreview);
                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                                imgBtnRemoveFour.setVisibility(View.VISIBLE);
                            }
                            else if (requestId == HOUSE_NUMBER_REQUEST_STARTER) {

                                houseNumber = finalPath1;

                                Glide.with(getActivity())
                                        .load("file://" + houseNumber)
                                        .apply(options)
                                        .thumbnail(0.1f)
                                        .into(imageHouseNumberPreview);
                                onStarterFragmentDataChangeListener.onHouseNumberChange(houseNumber, labelPosition);
                                imgBtnRemoveHouseNumber.setVisibility(View.VISIBLE);
                            }

                        }

                    });
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(IMAGE_ONE_REQUEST_STARTER);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);
                }
                break;
            }

            case PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(IMAGE_TWO_REQUEST_STARTER);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS);
                }
                break;
            }

            case PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(IMAGE_THREE_REQUEST_STARTER);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS);
                }
                break;
            }

            case PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(IMAGE_FOUR_REQUEST_STARTER);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS);
                }
                break;
            }
            case PermissionUtilities.MY_APP_TAKE_HOUSE_NUMBER_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(HOUSE_NUMBER_REQUEST_STARTER);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), StarterPhotosFragment.this, PermissionUtilities.MY_APP_TAKE_HOUSE_NUMBER_PHOTO_PERMISSIONS);
                }
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            selectedImagesDataInterface = (SelectedImagesDataInterface) getActivity();
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener) getActivity();
            onSaveReportClickListener = (OnSaveReportClickListener) getActivity();
            onGenerateReportClickListener = (OnGenerateReportClickListener) getActivity();
            onSetImageFileUriListener = (OnSetImageFileUriListener) getActivity();
            onStarterFragmentDataChangeListener = (OnStarterFragmentDataChangeListener) getActivity();
            
        } catch (ClassCastException exception) {
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
    }

    public void animateFAB() {
        if (isFabOpen) {
            showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
            fabGoNextBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabSaveReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            fabSaveReportBtn.setClickable(false);
            isFabOpen = false;

        } else {
            showFabBtn.setImageResource(R.drawable.ic_close_white);
            fabGoNextBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabSaveReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            fabSaveReportBtn.setClickable(true);
            isFabOpen = true;
        }

    }


}

