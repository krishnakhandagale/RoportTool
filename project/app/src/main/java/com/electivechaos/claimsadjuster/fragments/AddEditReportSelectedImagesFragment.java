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
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;
import com.electivechaos.claimsadjuster.listeners.OnImageRemovalListener;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.pojo.Image;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.ui.ImagePickerActivity;
import com.electivechaos.claimsadjuster.ui.ImageSliderActivity;
import com.electivechaos.claimsadjuster.ui.SingleImageDetailsActivity;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by krishna on 11/23/17.
 */

public class AddEditReportSelectedImagesFragment extends Fragment {

    private  static final int REQUEST_CAMERA = 0;
    private  static final int IMAGE_ONE_REQUEST = 100;
    private  static final int IMAGE_TWO_REQUEST = 200;
    private  static final int IMAGE_THREE_REQUEST = 300;
    private  static final int IMAGE_FOUR_REQUEST = 400;

    private  static final int SELECT_FILE = 1;
    private  static final int ADD_IMAGE_DETAILS = 2;
    private  static final int SET_CLICKED_IMAGE_DETAILS = 3;


    private LinearLayout parentLayout;
    private RecyclerView selectedImagesRecyclerView;
    private SelectedImagesAdapter selectedImagesAdapter;

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn, fabGoNextBtn, selectPhotoBtn, fabAddLabelBtn, fabGenerateReportBtn, fabSaveReportBtn;
    private Animation fab_open, fab_close;


    private ImageView imageViewOne;
    private ImageView imageViewTwo;
    private ImageView imageViewThree;
    private ImageView imageViewFour;


    private ImageView imageOnePreview;
    private ImageView imageTwoPreview;
    private ImageView imageThreePreview;
    private ImageView imageFourPreview;

    private ImageButton imgBtnRemoveOne;
    private ImageButton imgBtnRemoveTwo;
    private ImageButton imgBtnRemoveThree;
    private ImageButton imgBtnRemoveFour;

    private Uri fileUri;
    private String mCurrentPhotoPath;
    private File photoFile;

    //This is used by image picker
    private ArrayList<Image> selectedImages = null;

    private ArrayList<ImageDetailsPOJO> selectedImageList = null;
    private ArrayList<ImageDetailsPOJO> selectedElevationImagesList = new ArrayList<>();
    private int labelPosition;


    private OnImageRemovalListener onImageRemovalListener = null;
    static RequestOptions options = null;

    private SelectedImagesDataInterface selectedImagesDataInterface;

    private NextButtonClickListener nextButtonClickListener;

    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;

    private OnSaveReportClickListener onSaveReportClickListener;

    private OnGenerateReportClickListener onGenerateReportClickListener;

    public static AddEditReportSelectedImagesFragment initFragment(ArrayList<ImageDetailsPOJO> selectedImageList, ArrayList<ImageDetailsPOJO> selectedElevationImagesList, int position) {
        AddEditReportSelectedImagesFragment fragment = new AddEditReportSelectedImagesFragment();
        Bundle args = new Bundle();

        if (selectedElevationImagesList == null || selectedElevationImagesList.size() == 0) {
            selectedElevationImagesList = new ArrayList<>();
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
        }


        args.putSerializable("selectedImagesList", selectedImageList);
        args.putSerializable("selectedElevationImagesList", selectedElevationImagesList);
        args.putInt("position", position);


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

        showFabBtn = selectImageView.findViewById(R.id.showFab);
        fabGoNextBtn = selectImageView.findViewById(R.id.fabGoNext);
        fabAddLabelBtn = selectImageView.findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn = selectImageView.findViewById(R.id.fabGenerateReport);
        fabSaveReportBtn = selectImageView.findViewById(R.id.fabSaveReport);
        selectPhotoBtn = selectImageView.findViewById(R.id.btnSelectPhoto);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

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
                selectedImagesDataInterface.setSelectedImages(selectedImageList, labelPosition);
            }
        };

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
        selectedImagesRecyclerView = selectImageView.findViewById(R.id.selectedImagesRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), CommonUtils.calculateNoOfColumns(getContext()));
        selectedImagesRecyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(selectedImageList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                selectedImagesDataInterface.setSelectedImages(selectedImageList, labelPosition);
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


        imageViewOne = selectImageView.findViewById(R.id.imageViewOne);
        imageViewTwo = selectImageView.findViewById(R.id.imageViewTwo);
        imageViewThree = selectImageView.findViewById(R.id.imageViewThree);
        imageViewFour = selectImageView.findViewById(R.id.imageViewFour);


        imageOnePreview = selectImageView.findViewById(R.id.imageOnePreview);
        imageTwoPreview = selectImageView.findViewById(R.id.imageTwoPreview);
        imageThreePreview = selectImageView.findViewById(R.id.imageThreePreview);
        imageFourPreview = selectImageView.findViewById(R.id.imageFourPreview);


        imageViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(IMAGE_ONE_REQUEST);

            }
        });


        imageViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(IMAGE_TWO_REQUEST);
            }
        });


        imageViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(IMAGE_THREE_REQUEST);
            }
        });

        imageViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(IMAGE_FOUR_REQUEST);
            }
        });

        imgBtnRemoveOne = selectImageView.findViewById(R.id.imgBtnRemoveOne);
        imgBtnRemoveTwo = selectImageView.findViewById(R.id.imgBtnRemoveTwo);
        imgBtnRemoveThree = selectImageView.findViewById(R.id.imgBtnRemoveThree);
        imgBtnRemoveFour = selectImageView.findViewById(R.id.imgBtnRemoveFour);

        imgBtnRemoveOne.setVisibility(View.INVISIBLE);
        imgBtnRemoveTwo.setVisibility(View.INVISIBLE);
        imgBtnRemoveThree.setVisibility(View.INVISIBLE);
        imgBtnRemoveFour.setVisibility(View.INVISIBLE);

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
        selectPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
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
        AddEditReportSelectedImagesFragment.this.startActivityForResult(intent, SELECT_FILE);

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
                AddEditReportSelectedImagesFragment.this.startActivityForResult(intent, requestId);
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
                        onCaptureImageResult(data);
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
            } else if (requestCode == IMAGE_ONE_REQUEST) {
                if (fileUri != null) {
                    onElevationImageCaptureResult(data, requestCode);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openCamera(PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS, IMAGE_ONE_REQUEST);
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }

            } else if (requestCode == IMAGE_TWO_REQUEST) {
                if (fileUri != null) {
                    onElevationImageCaptureResult(data, requestCode);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openCamera(PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS, IMAGE_TWO_REQUEST);
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }

            } else if (requestCode == IMAGE_THREE_REQUEST) {
                if (fileUri != null) {
                    onElevationImageCaptureResult(data, requestCode);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openCamera(PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS, IMAGE_THREE_REQUEST);
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }

            } else if (requestCode == IMAGE_FOUR_REQUEST) {
                if (fileUri != null) {
                    onElevationImageCaptureResult(data, requestCode);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openCamera(PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS, IMAGE_FOUR_REQUEST);
                            v.setVisibility(View.GONE);
                        }
                    });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
                    snackbar.show();
                }

            } else if (requestCode == ADD_IMAGE_DETAILS) {
                ArrayList<ImageDetailsPOJO> selectedImageListReturned = (ArrayList<ImageDetailsPOJO>) data.getExtras().getSerializable("selected_images");
                if (selectedImageList == null) {
                    selectedImageList = new ArrayList<>();
                }
                selectedImageListReturned.addAll(selectedImageList);
                selectedImageList = selectedImageListReturned;

                selectedImagesAdapter = new SelectedImagesAdapter(selectedImageList, getContext(), onImageRemovalListener);
                selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);

                selectedImagesDataInterface.setSelectedImages(selectedImageList, labelPosition);

            }

        }
    }
    public void setDataAndAdapter(ArrayList<ImageDetailsPOJO> selectedImageListToSet){
        selectedImageList = selectedImageListToSet;
        if (selectedImagesAdapter == null) {
            selectedImagesAdapter = new SelectedImagesAdapter(selectedImageList, getContext(), onImageRemovalListener);
            selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
        } else {
            selectedImagesAdapter.notifyDataSetChanged();
        }
    }

    private void openCamera(int requestId, int cameraIntentReqCode) {
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
                            if (requestId == IMAGE_ONE_REQUEST) {
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Image one view for incidence");
                                imgObj.setTitle("Image One View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(0, imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imageOnePreview);
                                imgBtnRemoveOne.setVisibility(View.VISIBLE);

                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);

                            } else if (requestId == IMAGE_TWO_REQUEST) {

                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Image two view for incidence");
                                imgObj.setTitle("Image Two View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(1, imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imageTwoPreview);
                                imgBtnRemoveTwo.setVisibility(View.VISIBLE);
                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                            } else if (requestId == IMAGE_THREE_REQUEST) {
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Image three view for incidence");
                                imgObj.setTitle("Image Three View");
                                imgObj.setImageUrl(finalPath1);
                                selectedElevationImagesList.set(2, imgObj);
                                Glide.with(getActivity())
                                        .load("file://" + finalPath)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(imageThreePreview);
                                imgBtnRemoveThree.setVisibility(View.VISIBLE);
                                selectedImagesDataInterface.setSelectedElevationImages(selectedElevationImagesList, labelPosition);
                            } else if (requestId == IMAGE_FOUR_REQUEST) {
                                final ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                                imgObj.setDescription("Image four view for incidence");
                                imgObj.setTitle("Image Four View");
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

                        }

                    });
                }
            }
        });
    }

    private void onCaptureImageResult(Intent data) {
        new SingleMediaScanner(getContext(), photoFile, new OnMediaScannerListener() {
            @Override
            public void onMediaScanComplete(String path, Uri uri) {
                if (path != null) {
                    path = mCurrentPhotoPath;
                    ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                    imgObj.setDescription("");
                    imgObj.setTitle("");
                    imgObj.setIsDamage(false);
                    imgObj.setOverview(false);
                    imgObj.setImageUrl(path);
                    ImageHelper.revokeAppPermission(getContext(), fileUri);
                    Intent intent = new Intent(getContext(), SingleImageDetailsActivity.class);
                    intent.putExtra("image_details", imgObj);
                    AddEditReportSelectedImagesFragment.this.startActivityForResult(intent, SET_CLICKED_IMAGE_DETAILS);
                }
            }
        });
    }

    private void onSelectFromGalleryResult(Intent data) {
        selectedImages = data.getParcelableArrayListExtra("ImageUrls");
        Intent intent = new Intent(getActivity(), ImageSliderActivity.class);
        intent.putExtra("ImageList", selectedImages);
        AddEditReportSelectedImagesFragment.this.startActivityForResult(intent, ADD_IMAGE_DETAILS);
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
                    cameraIntent(IMAGE_ONE_REQUEST);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(IMAGE_TWO_REQUEST);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(IMAGE_THREE_REQUEST);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(IMAGE_FOUR_REQUEST);
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
            public TextView imageType;
            public ImageButton editBtn;
            public ImageButton deleteBtn;

            public MyViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.selectedImagePreview);
                title = view.findViewById(R.id.title);
                imageType = view.findViewById(R.id.imageType);
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

            if (imgDetails.isOverview()) {
                holder.imageType.setText("Overview");
                holder.imageType.setVisibility(View.VISIBLE);
            } else if (imgDetails.isDamage()) {
                holder.imageType.setText("Damage");
                holder.imageType.setVisibility(View.VISIBLE);
            } else {
                holder.imageType.setVisibility(View.GONE);
            }
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
                    intent.putExtra("labelPosition",labelPosition);
                    getActivity().startActivityForResult(intent, SET_CLICKED_IMAGE_DETAILS);
                }
            });

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getActivity() != null) {
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
                        negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
                        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            selectedImagesDataInterface = (SelectedImagesDataInterface) getActivity();
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener) getActivity();
            onSaveReportClickListener = (OnSaveReportClickListener) getActivity();
            onGenerateReportClickListener = (OnGenerateReportClickListener) getActivity();
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
                if (selectedElevationImagesList.get(0).getImageUrl() != null && !selectedElevationImagesList.get(0).getImageUrl().isEmpty()) {
                    Glide.with(getActivity())
                            .load("file://" + selectedElevationImagesList.get(0).getImageUrl())
                            .apply(options)
                            .into(imageOnePreview);
                }
                if (selectedElevationImagesList.get(1).getImageUrl() != null && !selectedElevationImagesList.get(1).getImageUrl().isEmpty()) {
                    Glide.with(getActivity())
                            .load("file://" + selectedElevationImagesList.get(1).getImageUrl())
                            .apply(options)
                            .into(imageTwoPreview);
                }
                if (selectedElevationImagesList.get(2).getImageUrl() != null && !selectedElevationImagesList.get(2).getImageUrl().isEmpty()) {
                    Glide.with(getActivity())
                            .load("file://" + selectedElevationImagesList.get(2).getImageUrl())
                            .apply(options)
                            .into(imageThreePreview);
                }
                if (selectedElevationImagesList.get(3).getImageUrl() != null && !selectedElevationImagesList.get(3).getImageUrl().isEmpty()) {
                    Glide.with(getActivity())
                            .load("file://" + selectedElevationImagesList.get(3).getImageUrl())
                            .apply(options)
                            .into(imageFourPreview);
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

