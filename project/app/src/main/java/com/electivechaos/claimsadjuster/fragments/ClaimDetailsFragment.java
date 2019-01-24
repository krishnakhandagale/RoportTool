package com.electivechaos.claimsadjuster.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.electivechaos.claimsadjuster.CameraActivity;
import com.electivechaos.claimsadjuster.ImageHelper;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.SingleMediaScanner;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBSelectedImagesListTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.BackButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.ui.QuickImageDetailsActivity;
import com.electivechaos.claimsadjuster.ui.SingleImageDetailsActivity;
import com.electivechaos.claimsadjuster.utils.ReportConstants;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClaimDetailsFragment extends Fragment {


    ViewPager viewPager;
    TabLayout tabLayout;
    View view;


    String reportTitle = "";
    String reportDescription = "";
    String clientName = "";
    String claimNumber = "";
    String reportBy = "";
    String address = "";


    String locationLat = "";
    String locationLong = "";
    String addressLine = "";

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn, fabGoNextBtn, fabGoBackBtn, fabAddLabelBtn, fabGenerateReportBtn, fabSaveReportBtn;
    private Animation fab_open;
    private Animation fab_close;

    private NextButtonClickListener nextButtonClickListener;
    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;
    private OnSaveReportClickListener onSaveReportClickListener;
    private OnGenerateReportClickListener onGenerateReportClickListener;
    private BackButtonClickListener backButtonClickListener;

    private FloatingActionButton selectPhoto;
    private String mCurrentPhotoPath;
    private Uri fileUri;
    private File photoFile;
    private CoordinatorLayout parentLayout;
    private CategoryListDBHelper categoryListDBHelper;
    private SelectedImagesDataInterface selectedImagesDataInterface;
    private int labelPosition;
    private String labelDefaultCoverageType;
    private Label label;




    @Override
    public void onStart() {
        super.onStart();
        Bundle passedArgs = getArguments();

        if (passedArgs != null) {
            reportTitle = passedArgs.get("reportTitle").toString();
            reportDescription = passedArgs.get("reportDescription").toString();
            clientName = passedArgs.get("clientName").toString();
            claimNumber = passedArgs.get("claimNumber").toString();
            reportBy = passedArgs.get("reportBy").toString();

            locationLat = passedArgs.get("locationLat").toString();
            locationLong = passedArgs.get("locationLong").toString();
            addressLine = passedArgs.get("addressLine").toString();

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            labelPosition = (int) getArguments().get("position");
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.claim_details_layout, container, false);
        showFabBtn = view.findViewById(R.id.showFab);
        fabGoNextBtn = view.findViewById(R.id.fabGoNext);
        fabGoBackBtn = view.findViewById(R.id.fabGoBack);

        fabAddLabelBtn = view.findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn = view.findViewById(R.id.fabGenerateReport);
        fabSaveReportBtn = view.findViewById(R.id.fabSaveReport);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        showFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
        viewPager = view.findViewById(R.id.claim_details_view_pager);
        tabLayout = view.findViewById(R.id.claim_details_tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        selectPhoto = view.findViewById(R.id.btnSelectPhoto);
        parentLayout = view.findViewById(R.id.parentLayout);
        categoryListDBHelper = CategoryListDBHelper.getInstance(getContext());


        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent(250);
            }
        });


        fabAddLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLabelAddClickListener.onLabelAddClick();
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

        ClaimDetailsTabsPagerAdapter adapter = new ClaimDetailsTabsPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fabGoNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClickListener.onNextButtonClick();
                animateFAB();
            }
        });

        //Add code
        fabGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonClickListener.onBackButtonClick();
                animateFAB();
            }
        });

        fabGenerateReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                onGenerateReportClickListener.onReportGenerateClicked();

            }
        });
        return view;


    }

    public void animateFAB() {
        if (isFabOpen) {

            fabGoNextBtn.startAnimation(fab_close);
            fabGoBackBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabSaveReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            fabGoBackBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            fabSaveReportBtn.setClickable(false);
            showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
            isFabOpen = false;

        } else {


            fabGoNextBtn.startAnimation(fab_open);
            fabGoBackBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabSaveReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            fabGoBackBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            fabSaveReportBtn.setClickable(true);
            showFabBtn.setImageResource(R.drawable.ic_close_white);
            isFabOpen = true;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            backButtonClickListener = (BackButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener) getActivity();
            onSaveReportClickListener = (OnSaveReportClickListener) getActivity();
            onGenerateReportClickListener = (OnGenerateReportClickListener) getActivity();
            selectedImagesDataInterface = (SelectedImagesDataInterface) getActivity();


        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    public class ClaimDetailsTabsPagerAdapter extends FragmentStatePagerAdapter {

        public ClaimDetailsTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            if (i == 0) {
                Fragment fragment = new ClaimDetailsTabsFragment();
                Bundle claimDetailsArgs = new Bundle();
                claimDetailsArgs.putString("reportTitle", reportTitle);
                claimDetailsArgs.putString("reportDescription", reportDescription);
                claimDetailsArgs.putString("clientName", clientName);
                claimDetailsArgs.putString("claimNumber", claimNumber);
                claimDetailsArgs.putString("reportBy", reportBy);
                claimDetailsArgs.putString("address", address);
                fragment.setArguments(claimDetailsArgs);
                return fragment;
            } else {
                Fragment fragment = new LossLocationFragment();

                Bundle claimDetailsLocationArgs = new Bundle();
                claimDetailsLocationArgs.putString("locationLat", locationLat);
                claimDetailsLocationArgs.putString("locationLong", locationLong);
                claimDetailsLocationArgs.putString("addressLine", addressLine);

                fragment.setArguments(claimDetailsLocationArgs);
                return fragment;
            }

        }

        @Override
        public int getCount() {

            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Claim Details";
            } else {
                return "Loss Location";
            }
        }
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
//                onSetImageFileUriListener.onSetImageFileUri(mCurrentPhotoPath);
                getActivity().startActivityForResult(intent, requestId);
            }
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

    public void onImageCapturedResult(Intent data) {
        if (fileUri != null) {
            onCaptureImageResult(data);
        } else {
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    selectImage(ReportConstants.REQUEST_CAMERA, ReportConstants.SELECT_FILE);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
        }
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

                   // selectedImagesDataInterface.setCapturedImage(capturedImage, 1);

                    ArrayList<ImageDetailsPOJO> returnedImageItem = (ArrayList<ImageDetailsPOJO>) capturedImage;
                    Intent intent = new Intent(getActivity(), QuickImageDetailsActivity.class);
                    if (returnedImageItem != null) {
                        intent.putExtra("image_details", returnedImageItem.get(0));
                    }
                    intent.putExtra("labelPosition", labelPosition);
                    intent.putExtra("labelDefaultCoverageType", labelDefaultCoverageType);
                    getActivity().startActivityForResult(intent, ReportConstants.SET_QUICK_CLICKED_CAPTURED_DETAILS);

//                    new DBSelectedImagesListTsk(categoryListDBHelper, "quick_captured_image", label, capturedImage, new AsyncTaskStatusCallback() {
//
//                        @Override
//                        public void onPostExecute(Object object, String type) {
//
//                            ImageHelper.revokeAppPermission(getActivity(), fileUri);
//
//                            ArrayList<ImageDetailsPOJO> returnedImageItem = (ArrayList<ImageDetailsPOJO>) object;
//                            Intent intent = new Intent(getActivity(), QuickImageDetailsActivity.class);
//                            if (returnedImageItem != null) {
//                                intent.putExtra("image_details", returnedImageItem.get(0));
//                            }
//                            intent.putExtra("labelPosition", labelPosition);
//                            intent.putExtra("labelDefaultCoverageType", labelDefaultCoverageType);
//                            getActivity().startActivityForResult(intent, ReportConstants.SET_CLICKED_CAPTURED_DETAILS);
//
//                        }
//
//                        @Override
//                        public void onPreExecute() {
//
//                        }
//
//                        @Override
//                        public void onProgress(int progress) {
//
//                        }
//                    }).execute();


                }
            }
        });
    }


}


