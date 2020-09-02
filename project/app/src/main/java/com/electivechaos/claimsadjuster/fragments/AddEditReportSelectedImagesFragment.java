package com.electivechaos.claimsadjuster.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.electivechaos.claimsadjuster.CameraActivity;
import com.electivechaos.claimsadjuster.ImageHelper;
import com.electivechaos.claimsadjuster.PermissionUtilities;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.SingleMediaScanner;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBSelectedImagesListTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.dialog.ImageDetailsFragment;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.BackButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSetImageFileUriListener;
import com.electivechaos.claimsadjuster.interfaces.QuickCaptureListener;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.pojo.Image;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.ui.ImagePickerActivity;
import com.electivechaos.claimsadjuster.ui.ImageSliderActivity;
import com.electivechaos.claimsadjuster.ui.SingleImageDetailsActivity;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.electivechaos.claimsadjuster.utils.ReportConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class AddEditReportSelectedImagesFragment extends Fragment {
    RequestOptions options = null;
    CategoryListDBHelper categoryListDBHelper;
    private LinearLayout parentLayout;
    private RecyclerView selectedImagesRecyclerView;
    private SelectedImagesAdapter selectedImagesAdapter;
    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn;
    private FloatingActionButton fabGoNextBtn;
    private FloatingActionButton fabGoBackBtn;
    private FloatingActionButton fabGalleryBtn;

//    private FloatingActionButton fabAddLabelBtn;
    private FloatingActionButton fabGenerateReportBtn;
    private FloatingActionButton fabSaveReportBtn;
    private FloatingActionButton fabQuickCapture;
    private Animation fab_open, fab_close;
    private Uri fileUri;
    private String mCurrentPhotoPath;
    private File photoFile;
    //This is used by image picker
    private ArrayList<Image> selectedImages = null;

    private ArrayList<ImageDetailsPOJO> selectedImageList = null;
    private int labelPosition;
    private String labelDefaultCoverageType;
    private Label label;
    //    private OnImageRemovalListener onImageRemovalListener = null;
    private SelectedImagesDataInterface selectedImagesDataInterface;

    private NextButtonClickListener nextButtonClickListener;

    private BackButtonClickListener backButtonClickListener;

    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;

    private OnSaveReportClickListener onSaveReportClickListener;

    private OnGenerateReportClickListener onGenerateReportClickListener;
    private OnSetImageFileUriListener onSetImageFileUriListener;

    private QuickCaptureListener quickCaptureListener;
    private String reportId;


    public static AddEditReportSelectedImagesFragment initFragment(ArrayList<ImageDetailsPOJO> selectedImageList, int position, Label label, String fileUri, String labelDefaultCoverageType) {
        AddEditReportSelectedImagesFragment fragment = new AddEditReportSelectedImagesFragment();
        Bundle args = new Bundle();

        args.putSerializable("selectedImagesList", selectedImageList);
        args.putInt("position", position);
        args.putParcelable("label", label);
        args.putString("fileUri", fileUri);
        args.putString("labelDefaultCoverageType", labelDefaultCoverageType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryListDBHelper = CategoryListDBHelper.getInstance(getActivity());
        if (getArguments() != null) {
            selectedImageList = (ArrayList<ImageDetailsPOJO>) getArguments().get("selectedImagesList");
            labelPosition = (int) getArguments().get("position");
            label = getArguments().getParcelable("label");
            labelDefaultCoverageType = (String) getArguments().get("labelDefaultCoverageType");

            mCurrentPhotoPath = getArguments().getString("fileUri");
            if (mCurrentPhotoPath != null) {
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
        View selectImageView = inflater.inflate(R.layout.fragment_select_photo, container, false);

        showFabBtn = selectImageView.findViewById(R.id.showFab);
        fabGoNextBtn = selectImageView.findViewById(R.id.fabGoNext);
        fabGoBackBtn = selectImageView.findViewById(R.id.fabGoBack);
        fabGalleryBtn = selectImageView.findViewById(R.id.fabGallery);

//        fabAddLabelBtn = selectImageView.findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn = selectImageView.findViewById(R.id.fabGenerateReport);
        fabSaveReportBtn = selectImageView.findViewById(R.id.fabSaveReport);
        fabQuickCapture = selectImageView.findViewById(R.id.btnQuickSelectPhoto);
        FloatingActionButton selectPhotoBtn = selectImageView.findViewById(R.id.btnSelectPhoto);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        showFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        fabGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS);
                if (result)
                    galleryIntent(ReportConstants.SELECT_FILE);
                animateFAB();
            }
        });

//        fabAddLabelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onLabelAddClickListener.onLabelAddClick();
//                animateFAB();
//            }
//        });


        fabGenerateReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenerateReportClickListener.onReportGenerateClicked();
                animateFAB();
            }
        });

        fabQuickCapture.startAnimation(fab_open);
        fabQuickCapture.setClickable(true);

        fabQuickCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickCaptureListener.onClickCapture();
            }
        });


        options = new RequestOptions()
                .placeholder(R.drawable.imagepicker_image_placeholder)
                .error(R.drawable.imagepicker_image_placeholder)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        parentLayout = selectImageView.findViewById(R.id.parentLinearLayout);
        selectedImagesRecyclerView = selectImageView.findViewById(R.id.selectedImagesRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), CommonUtils.calculateNoOfColumns(getContext()));
        selectedImagesRecyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(selectedImageList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

                selectedImagesAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                new DBSelectedImagesListTsk(categoryListDBHelper, "insert_rearranged_image", label, selectedImageList, new AsyncTaskStatusCallback() {

                    @Override
                    public void onPostExecute(Object object, String type) {
                        ArrayList<ImageDetailsPOJO> imageDetailsList = (ArrayList<ImageDetailsPOJO>) object;
                        selectedImagesDataInterface.setSwapedSelectedImages(imageDetailsList, labelPosition);

                    }

                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();
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

        selectPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // selectImage(ReportConstants.REQUEST_CAMERA, ReportConstants.SELECT_FILE);
                boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS);
                if (result)
                    galleryIntent(ReportConstants.SELECT_FILE);
                if (isFabOpen) {
                    showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
                    fabGoNextBtn.startAnimation(fab_close);
                    fabGalleryBtn.startAnimation(fab_close);

//                    fabAddLabelBtn.startAnimation(fab_close);
                    fabGenerateReportBtn.startAnimation(fab_close);
                    fabSaveReportBtn.startAnimation(fab_close);
                    fabGoNextBtn.setClickable(false);
                    fabGalleryBtn.setClickable(false);

//                    fabAddLabelBtn.setClickable(false);
                    fabGenerateReportBtn.setClickable(false);
                    fabSaveReportBtn.setClickable(false);
                    isFabOpen = false;

                }
            }
        });

        fabGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonClickListener.onBackButtonClick();
                animateFAB();
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


        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable("fileUri");
        }
        return selectImageView;


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (selectedImagesAdapter != null) {
            selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
        } else if (selectedImageList != null && selectedImageList.size() > 0) {
            selectedImagesAdapter = new SelectedImagesAdapter(selectedImageList, getContext());
            selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
        }
    }

    private void selectImage(final int CameraRequestCode, final int galleryRequestCode) {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS);

                    if (result)
                        cameraIntent(CameraRequestCode);
                } else if (items[item].equals("Choose from Gallery")) {
                    boolean result = PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS);
                    if (result)
                        galleryIntent(galleryRequestCode);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent(int galleryRequestCode) {
        if (selectedImageList == null) {
            selectedImageList = new ArrayList<>();
        }

        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra("already_selected_images", selectedImageList.size());

        if (galleryRequestCode != 1) {
            intent.putExtra("already_selected_images", 0);
            intent.putExtra("number_of_images_allowed", 1);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(intent, galleryRequestCode);


    }

    private void cameraIntent(int requestId) {

        Intent intent = new Intent(getContext(), CameraActivity.class);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

            photoFile = getOutputMediaFile();
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    fileUri = Uri.fromFile(photoFile);

                } else {
                    fileUri = FileProvider.getUriForFile(getContext().getApplicationContext(),
                            getContext().getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoPath);
                ImageHelper.grantAppPermission(getContext(), intent, fileUri);
                onSetImageFileUriListener.onSetImageFileUri(mCurrentPhotoPath);
                getActivity().startActivityForResult(intent, requestId);
            }
        }

    }

    public void onImageCapturedResult(Intent data) {
        if (fileUri != null) {
            onCaptureImageResult(data);
        } else {
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage(ReportConstants.REQUEST_CAMERA, ReportConstants.SELECT_FILE);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
        }
    }

    public void onSelectImagesFromGallery(Intent data, final int requestCode, String reportIdd) {

        reportId = reportIdd;
        if (data != null) {
            onSelectFromGalleryResult(data, requestCode);
        } else {
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Something went wrong.Please try again.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage(requestCode, requestCode);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
        }

    }

    public void setDataAndAdapter(ArrayList<ImageDetailsPOJO> selectedImageListToSet) {
        selectedImageList.clear();
        selectedImageList.addAll(selectedImageListToSet);
        if (selectedImagesAdapter == null) {
            selectedImagesAdapter = new SelectedImagesAdapter(selectedImageList, getContext());
            selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
        } else {

            selectedImagesAdapter.notifyDataSetChanged();
        }
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
                    imgObj.setPointOfOrigin(false);
                    imgObj.setImageUrl(path);
                    imgObj.setCoverageTye(labelDefaultCoverageType);

                    File file = new File(path);
                    if (file.exists()) {
                        imgObj.setImageName(file.getName());
                        Date date = new Date(file.lastModified());
                        String dateString = new SimpleDateFormat("dd/MM/yyyy").format(date);
                        String timeString = new SimpleDateFormat("HH:mm:ss a").format(date);
                        imgObj.setImageDateTime(dateString + " at " + timeString);
                        imgObj.setImageGeoTag("");
                    }
                    final ArrayList<ImageDetailsPOJO> capturedImage = new ArrayList<ImageDetailsPOJO>();
                    capturedImage.add(imgObj);

                    selectedImagesDataInterface.setSelectedImages(capturedImage, labelPosition);

                    new DBSelectedImagesListTsk(categoryListDBHelper, "insert_captured_image", label, capturedImage, new AsyncTaskStatusCallback() {

                        @Override
                        public void onPostExecute(Object object, String type) {

                            ImageHelper.revokeAppPermission(getActivity(), fileUri);

                            ArrayList<ImageDetailsPOJO> returnedImageItem = (ArrayList<ImageDetailsPOJO>) object;
                            Intent intent = new Intent(getActivity(), SingleImageDetailsActivity.class);
                            if (returnedImageItem != null) {
                                intent.putExtra("image_details", returnedImageItem.get(0));
                            }
                            intent.putExtra("labelPosition", labelPosition);
                            intent.putExtra("labelDefaultCoverageType", labelDefaultCoverageType);
                            getActivity().startActivityForResult(intent, ReportConstants.SET_CLICKED_CAPTURED_DETAILS);

                        }

                        @Override
                        public void onPreExecute() {

                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    }).execute();


                }
            }
        });
    }

    private void onSelectFromGalleryResult(Intent data, int requestId) {
        selectedImages = data.getParcelableArrayListExtra("ImageUrls");
        if (requestId == ReportConstants.SELECT_FILE) {
            ArrayList<ImageDetailsPOJO> imagesInformation = new ArrayList<>();
            for (int i = 0; i < selectedImages.size(); i++) {
                ImageDetailsPOJO imgObj = new ImageDetailsPOJO();
                imgObj.setImageUrl(selectedImages.get(i).getPath());
                imgObj.setTitle("");
                imgObj.setDescription("");
                imgObj.setCoverageTye(labelDefaultCoverageType);
                File file = new File(selectedImages.get(i).getPath());
                if (file.exists()) {
                    imgObj.setImageName(file.getName());
                    Date date = new Date(file.lastModified());
                    String dateString = new SimpleDateFormat("dd/MM/yyyy").format(date);
                    String timeString = new SimpleDateFormat("HH:mm:ss a").format(date);
                    imgObj.setImageDateTime(dateString + " at " + timeString);
                }
                imgObj.setImageGeoTag("");
         //       imgObj.setLabel(label);
                imagesInformation.add(imgObj);


            }

            selectedImagesDataInterface.setSelectedImages(imagesInformation, labelPosition);

            new DBSelectedImagesListTsk(categoryListDBHelper, "insert_selected_images", label, imagesInformation, new AsyncTaskStatusCallback() {

                @Override
                public void onPostExecute(Object object, String type) {
                    ArrayList<ImageDetailsPOJO> returnedImagesList = (ArrayList<ImageDetailsPOJO>) object;
                    for(int i =0; i<returnedImagesList.size() ;i++){
                        returnedImagesList.get(i).setLabelName(label.getName());
                    }
                    Intent intent = new Intent(getActivity(), ImageSliderActivity.class);
                    intent.putExtra("ImageList", returnedImagesList);
                    intent.putExtra("labelPosition", labelPosition);
                    intent.putExtra("label",label);
                    intent.putExtra("reportId",reportId);
                    intent.putExtra("labelDefaultCoverageType", labelDefaultCoverageType);
                    getActivity().startActivityForResult(intent, ReportConstants.ADD_IMAGE_DETAILS);
                }

                @Override
                public void onPreExecute() {

                }

                @Override
                public void onProgress(int progress) {

                }
            }).execute();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(ReportConstants.REQUEST_CAMERA);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS);
                }
                break;
            }
            case PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent(ReportConstants.SELECT_FILE);
                } else {
                    PermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS);
                }
                break;
            }
            default:
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            selectedImagesDataInterface = (SelectedImagesDataInterface) getActivity();
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            backButtonClickListener = (BackButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener) getActivity();
            onSaveReportClickListener = (OnSaveReportClickListener) getActivity();
            onGenerateReportClickListener = (OnGenerateReportClickListener) getActivity();
            onSetImageFileUriListener = (OnSetImageFileUriListener) getActivity();
            quickCaptureListener = (QuickCaptureListener) getActivity();
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void onShowPopup(ImageDetailsPOJO imageDetails) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ImageDetailsFragment imageDetailsFragment = new ImageDetailsFragment();
        Bundle imageDetailsData = new Bundle();
        imageDetailsData.putString("imgName", imageDetails.getImageName());
        imageDetailsData.putString("imgDateTime", imageDetails.getImageDateTime());
        imageDetailsData.putString("imgGeoTag", imageDetails.getImageGeoTag());
        imageDetailsFragment.setArguments(imageDetailsData);
        imageDetailsFragment.show(ft, "dialog");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void animateFAB() {
        if (isFabOpen) {
            showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
            fabGoNextBtn.startAnimation(fab_close);
            fabGoBackBtn.startAnimation(fab_close);
            fabGalleryBtn.startAnimation(fab_close);

//            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabSaveReportBtn.startAnimation(fab_close);

            fabQuickCapture.startAnimation(fab_open);
            fabQuickCapture.setClickable(true);

            fabGoNextBtn.setClickable(false);
            fabGoBackBtn.setClickable(false);
            fabGalleryBtn.setClickable(false);

//            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            fabSaveReportBtn.setClickable(false);
            isFabOpen = false;

        } else {
            showFabBtn.setImageResource(R.drawable.ic_close_white);
            fabGoNextBtn.startAnimation(fab_open);
            fabGoBackBtn.startAnimation(fab_open);
            fabGalleryBtn.startAnimation(fab_open);

//            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabSaveReportBtn.startAnimation(fab_open);

            fabQuickCapture.startAnimation(fab_close);
            fabQuickCapture.setClickable(false);

            fabGoNextBtn.setClickable(true);
            fabGoBackBtn.setClickable(true);
            fabGalleryBtn.setClickable(true);

//            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            fabSaveReportBtn.setClickable(true);
            isFabOpen = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        CategoryListDBHelper categoryListDBHelper = CategoryListDBHelper.getInstance(getActivity());
        ArrayList<ImageDetailsPOJO> imageList = categoryListDBHelper.getLabelImages(label.getId());
        setDataAndAdapter(imageList);
    }

    public class SelectedImagesAdapter extends RecyclerView.Adapter<AddEditReportSelectedImagesFragment.SelectedImagesAdapter.MyViewHolder> {

        private ArrayList<ImageDetailsPOJO> imageList;
        private Context context;

        public SelectedImagesAdapter(ArrayList<ImageDetailsPOJO> imageList, Context context) {
            this.imageList = imageList;
            this.context = context;
        }

        @Override
        public AddEditReportSelectedImagesFragment.SelectedImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_image_item, parent, false);
            return new AddEditReportSelectedImagesFragment.SelectedImagesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final AddEditReportSelectedImagesFragment.SelectedImagesAdapter.MyViewHolder holder, final int position) {
            final ImageDetailsPOJO imgDetails = imageList.get(holder.getAdapterPosition());

            File file = new File(imgDetails.getImageUrl());
            if (!file.exists()) {
                Glide.with(context)
                        .load("file:///android_asset/NoImageFound.jpg")
                        .apply(options)
                        .into(holder.imageView);
                holder.imageType.setVisibility(View.GONE);
                holder.isPointOfOrigin.setVisibility(View.GONE);


            } else {
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

                if (imgDetails.isPointOfOrigin()) {
                    holder.isPointOfOrigin.setVisibility(View.VISIBLE);
                } else {
                    holder.isPointOfOrigin.setVisibility(View.GONE);
                }

                Glide.with(context)
                        .load("file://" + imgDetails.getImageUrl())
                        .apply(options)
                        .into(holder.imageView);
            }


            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SingleImageDetailsActivity.class);
                    intent.putExtra("image_details", imgDetails);
                    intent.putExtra("isEdit", true);
                    intent.putExtra("position", holder.getAdapterPosition());
                    intent.putExtra("labelPosition", labelPosition);
                    getActivity().startActivityForResult(intent, ReportConstants.SET_CLICKED_IMAGE_DETAILS);
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
                                        categoryListDBHelper.deleteImage(imageList.get(holder.getAdapterPosition()).getImageId());
                                        imageList.remove(holder.getAdapterPosition());
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

            holder.imageInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShowPopup(imgDetails);
                }
            });


        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView title;
            public TextView description;
            public TextView imageType;
            public TextView isPointOfOrigin;
            public ImageButton editBtn;
            public ImageButton deleteBtn;
            public ImageButton imageInfoBtn;

            public MyViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.selectedImagePreview);
                title = view.findViewById(R.id.title);
                imageType = view.findViewById(R.id.imageType);
                description = view.findViewById(R.id.description);
                editBtn = view.findViewById(R.id.editBtn);
                deleteBtn = view.findViewById(R.id.deleteBtn);
                isPointOfOrigin = view.findViewById(R.id.isPointOfOriginImage);
                imageInfoBtn = view.findViewById(R.id.imageInfo);

            }
        }
    }

}

