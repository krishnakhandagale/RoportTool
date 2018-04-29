package com.electivechaos.checklistapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.electivechaos.checklistapp.pojo.Image;
import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.pojo.ReportItemPOJO;
import com.electivechaos.checklistapp.database.ReportsListDBHelper;
import com.electivechaos.checklistapp.listeners.OnImageRemovalListener;
import com.electivechaos.checklistapp.listeners.OnMediaScannerListener;
import com.electivechaos.checklistapp.utils.CommonUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class AddEditReportSelectedImagesFragment extends Fragment {

    private int REQUEST_CAMERA = 0;
    private int FRONT_IMAGE_REQUEST = 100;
    private int BACK_IMAGE_REQUEST = 200;
    private int LEFT_IMAGE_REQUEST = 300;
    private int RIGHT_IMAGE_REQUEST = 400;

    private int SELECT_FILE = 1;
    private int ADD_IMAGE_DETAILS = 2;
    private static int SET_CLICKED_IMAGE_DETAILS = 3;

    private static final int IMAGEMAXWIDTH = 150;
    private static final int IMAGEMAXHEIGHT = 150;

    private static final int IMAGEMAXHEIGHTBIG = 320;
    private static final int IMAGEMAXWIDTHBIG = 320;

    private static Font TIMESNEWROMAN18 = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
    private LinearLayout parentLayout;
    private PermissionUtilities permissionPermissionUtilities;
    private CoordinatorLayout selectImagesParentLayout;
    private RecyclerView selectedImagesRecyclerView;
    private ReportsListDBHelper reportsListDBHelper;
    private ProgressBar progressBar;
    private SendReportDBChangeSignal sendReportDBchangeSignal;
    private SelectedImagesAdapter selectedImagesAdapter;
    Button btnUpload = null;


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
    private String reportTitle;
    private String reportDescription;
    private String clientName;
    private String claimNumber;
    private String address;
    private String reportId;
    private String reportPath;
    private File photoFile;
    private ArrayList<? extends Image> selectedImages = null;
    private ArrayList<ImageDetailsPOJO> selectedImageList =null;
    private ArrayList<ImageDetailsPOJO> selectedElevationImagesList = new ArrayList<>();


    private OnImageRemovalListener onImageRemovalListener = null;
    static RequestOptions options = null;

    private static String TAG = "AddEditReportSelectedImagesFragment";

    public static AddEditReportSelectedImagesFragment initFragment(ArrayList<ImageDetailsPOJO> selectedImageList, String reportId, String reportPath, ArrayList<ImageDetailsPOJO> selectedElevationImagesList) {
        AddEditReportSelectedImagesFragment fragment = new AddEditReportSelectedImagesFragment();
        Bundle args = new Bundle();
        args.putSerializable("selectedImagesList", selectedImageList);
        args.putSerializable("selectedElevationImagesList", selectedElevationImagesList);
        args.putString("reportId", reportId);
        args.putString("reportPath", reportPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (getArguments() != null) {

            selectedImageList = (ArrayList<ImageDetailsPOJO>) getArguments().getSerializable("selectedImagesList");
            selectedElevationImagesList = (ArrayList<ImageDetailsPOJO>) getArguments().getSerializable("selectedElevationImagesList");


            reportId = getArguments().getString("reportId");
            reportPath = getArguments().getString("reportPath");
        }
        if(selectedElevationImagesList==null || selectedElevationImagesList.size()==0)
        {
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
            selectedElevationImagesList.add(new ImageDetailsPOJO());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View selectImageView = inflater.inflate(R.layout.fragment_category_details, container, false);

        onImageRemovalListener = new OnImageRemovalListener() {
            @Override
            public void onImageSelectionChanged(List<ImageDetailsPOJO> selectedImgs) {
                selectedImageList = (ArrayList<ImageDetailsPOJO>) selectedImgs;
            }
        };

        options = new RequestOptions()
                .placeholder(R.drawable.imagepicker_image_placeholder)
                .error(R.drawable.imagepicker_image_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        btnUpload = selectImageView.findViewById(R.id.btnUploadDetails);
        FloatingActionButton selectPhotoBtn = selectImageView.findViewById(R.id.btnSelectPhoto);
        parentLayout = selectImageView.findViewById(R.id.parentLinearLayout);
        selectedImagesRecyclerView = selectImageView.findViewById(R.id.selectedImagesRecyclerview);
        selectImagesParentLayout = selectImageView.findViewById(R.id.selectImagesParentLayout);
        progressBar = selectImageView.findViewById(R.id.progressBar);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), CommonUtils.calculateNoOfColumns(getContext()));
        selectedImagesRecyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // get the viewHolder's and target's positions in your adapter data, swap them
                Collections.swap(selectedImageList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // and notify the adapter that its dataset has changed
                selectedImagesAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
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
                boolean result = permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(FRONT_IMAGE_REQUEST);
            }
        });


        imgViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(BACK_IMAGE_REQUEST);
            }
        });


        imgViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(LEFT_IMAGE_REQUEST);
            }
        });

        imgViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS);

                if (result)
                    cameraIntent(RIGHT_IMAGE_REQUEST);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    generatePdf();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
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
        if (reportId != null) {
            btnUpload.setText("Edit Report");
        } else {
            btnUpload.setText("Add Report");
        }
    }

    private void generatePdf() throws IOException, DocumentException {
        if (reportTitle == null) {
            showSnackbarMessage(getResources().getString(R.string.title_error), false, true);
            return;
        }
        if (reportTitle.trim().isEmpty()) {
            showSnackbarMessage(getResources().getString(R.string.title_error), false, true);
            return;
        } else if (reportDescription == null) {
            showSnackbarMessage(getResources().getString(R.string.description_error), false, true);
            return;
        } else if (reportDescription.trim().isEmpty()) {
            showSnackbarMessage(getResources().getString(R.string.description_error), false, true);
            return;
        } else if (clientName == null) {
            showSnackbarMessage(getResources().getString(R.string.client_name_error), false, true);
            return;
        } else if (clientName.trim().isEmpty()) {
            showSnackbarMessage(getResources().getString(R.string.client_name_error), false, true);
            return;
        } else if (claimNumber == null) {
            showSnackbarMessage(getResources().getString(R.string.claim_number_error), false, true);
            return;
        } else if (claimNumber.trim().isEmpty()) {
            showSnackbarMessage(getResources().getString(R.string.claim_number_error), false, true);
            return;
        } else if (address == null) {
            showSnackbarMessage(getResources().getString(R.string.address_error), false, true);
            return;
        } else if (address.trim().isEmpty()) {
            showSnackbarMessage(getResources().getString(R.string.address_error), false, true);
            return;
        } else if (selectedImageList == null) {
            showSnackbarMessage(getResources().getString(R.string.add_images_error), false, true);
            return;
        } else if (selectedImageList.size() == 0) {
            showSnackbarMessage(getResources().getString(R.string.add_images_error), false, true);
            return;
        }

        boolean result = permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS);
        if (result) {
            showReportPreferenceDialog();
        }
    }

    private static void addMetaData(Document document) {
        document.addTitle("PDF Report");
        document.addSubject("Created By ElectiveChaos");
        document.addKeywords("ElectiveChaos");
        document.addAuthor("ElectiveChaos");
        document.addCreator("ElectiveChaos");
    }

    private void showSnackbarMessage(String message, boolean isAutoHide, boolean isError) {
        if (isError == false) {
            Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorSuccess));
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_error));
            snackbar.show();
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
                    boolean result = permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS);

                    if (result)
                        cameraIntent(REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    boolean result = permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS);
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

        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go

            photoFile = getOutputMediaFile();
            // Continue only if the File was successfully created
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
            } else if (requestCode == SET_CLICKED_IMAGE_DETAILS) {
                //Got all selected images here
                ImageDetailsPOJO imageDetails = (ImageDetailsPOJO) data.getExtras().getSerializable("image_entered_details");

                if (data.getExtras().getBoolean("isEdit")) {
                    int position = data.getExtras().getInt("position");
                    selectedImageList.get(position).setTitle(imageDetails.getTitle());
                    selectedImageList.get(position).setImageUrl(imageDetails.getImageUrl());
                    selectedImageList.get(position).setDescription(imageDetails.getDescription());
                    selectedImageList.get(position).setCategory(imageDetails.getCategory());

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

            }
        }
    }

    private void openCamera(int requestId, int cameraIntentReqCode)
    {
        boolean result = permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, requestId);

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
                                imgRemoveBtnRight.setVisibility(View.VISIBLE);
                            }

                        }

                    });
                }
            }
        });
    }

    private void onCaptureImageResult(Intent data) throws IOException {
        //Worked like charm
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


    private byte[] resizeImage(String imagePath, int maxWidth, int maxHeight) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, maxWidth, maxHeight, orientation);

    }

    private byte[] resizeImage(Bitmap image, int maxWidth, int maxHeight, int orientation) {

        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxHeight / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            Bitmap rotatedBitmap;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(image, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(image, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(image, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = image;
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            return outStream.toByteArray();
        } else {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            return outStream.toByteArray();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    ;

    public PdfPCell getCell(String category, String title, String description, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell();
        // Font font=new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

        cell.addElement(new Phrase(category,TIMESNEWROMAN18));
        cell.addElement(new Phrase(title));
        cell.addElement(new Phrase(description));
        cell.setPadding(0);
        cell.setBorder(Rectangle.NO_BORDER);
        //cell.setBorderWidth(1);
        //BaseColor baseColor = new BaseColor(99,100,99);
        //cell.setBorderColor(baseColor);
        //cell.setBorder(Rectangle.NO_BORDER);

        if (perPage == 2) {
            cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);

        } else {
            cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);
        }
        return cell;

    }

    public PdfPCell getCellImagCell(com.itextpdf.text.Image img, int alignment, Document document, int perPage) {

        PdfPCell cell = new PdfPCell(img);
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        //cell.setBorderWidth(1);
        //BaseColor baseColor = new BaseColor(99,100,99);
        //cell.setBorderColor(baseColor);
        if (perPage == 2) {
            cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);

        } else {
            cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);
        }
        return cell;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(REQUEST_CAMERA);
                } else {
                    permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(FRONT_IMAGE_REQUEST);
                } else {
                    permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(BACK_IMAGE_REQUEST);
                } else {
                    permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_BACK_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(LEFT_IMAGE_REQUEST);
                } else {
                    permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS);
                }
                return;
            }

            case PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS: {
                if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent(RIGHT_IMAGE_REQUEST);
                } else {
                    permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS);
                }
                return;
            }
            case PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                } else {
                    permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_BROWSE_PHOTO_PERMISSIONS);
                }
                return;
            }
            case PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        generatePdf();
                    } catch (DocumentException | IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    permissionPermissionUtilities.checkPermission(getActivity(), AddEditReportSelectedImagesFragment.this, PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS);

                }
            }

        }
    }

    public void setReceivedImageDetailsData(JSONObject message) {
        try {
            reportTitle = message.getString("reportTitle");
            reportDescription = message.getString("reportDescription");
            clientName = message.getString("clientName");
            claimNumber = message.getString("claimNumber");
            address = message.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
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

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.image_remove_alert_layout, null);
                    dialogBuilder.setView(dialogView);

                    TextView positiveBtn = dialogView.findViewById(R.id.positive_button);
                    TextView negativeBtn = dialogView.findViewById(R.id.negative_button);


                    final AlertDialog alertDialog = dialogBuilder.create();

                    positiveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            imageList.remove(position);
                            onImageRemovalListener.onImageSelectionChanged(imageList);
                            notifyDataSetChanged();
                        }
                    });

                    negativeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
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
//        reportsListDBHelper = ReportsListDBHelper.getInstance(context);
//        permissionPermissionUtilities = new PermissionUtilities();
//        try {
//            sendReportDBchangeSignal = (SendReportDBChangeSignal) getActivity();
//        } catch (ClassCastException e) {
//            throw new ClassCastException("Error in retrieving data. Please try again");
//        }
    }

    public interface SendReportDBChangeSignal {
        void notifyReportDBChanged();
    }


    private class GenerateReportOperator extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            int numberOfImagesPerPage = params[0];
            if (reportId != null) {
                //Delete existing record and below is the code crete new pdf and also delete existing pdf file
                reportsListDBHelper.deleteReportEntry(reportId);
                File file = new File(reportPath);
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
            long currentTimeMillisecond = System.currentTimeMillis();
            File destination = new File(Environment.getExternalStorageDirectory(),
                    String.valueOf(currentTimeMillisecond) + "report.pdf");
            FileOutputStream fo;
            ReportItemPOJO reportItem = new ReportItemPOJO();
            reportItem.setFilePath(destination.getAbsolutePath());
            reportItem.setId(String.valueOf(currentTimeMillisecond).concat("report"));
            reportItem.setCreatedDate(String.valueOf(currentTimeMillisecond));
            try {
                Font fontTitles = new Font(Font.FontFamily.UNDEFINED, 16, Font.BOLD);
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                final Document document = new Document(PageSize.A4, 50, 50, 50, 50);
                addMetaData(document);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, fo);
                document.open();
                MyDocHeader footer = new MyDocHeader(reportTitle);
                pdfWriter.setPageEvent(footer);
                reportItem.setReportTitle(reportTitle);
                document.add(new Paragraph("Report Description", fontTitles));
                document.add(new Paragraph(reportDescription));
                reportItem.setReportDescription(reportDescription);
                document.add(new Paragraph(""));
                document.add(new Paragraph("Client Name", fontTitles));
                document.add(new Paragraph(clientName));
                document.add(new Paragraph(""));

                reportItem.setClientName(clientName);
                document.add(new Paragraph("Claim Number", fontTitles));
                document.add(new Paragraph(claimNumber));
                document.add(new Paragraph(""));

                reportItem.setClaimNumber(claimNumber);

                document.add(new Paragraph("Address", fontTitles));
                document.add(new Paragraph(address));
                reportItem.setAddress(address);
                document.newPage();

                reportItem.setSelectedImagesList(selectedImageList);
                reportItem.setSelectedElevationImagesList(selectedElevationImagesList);

                int j =0, k= 0;
                while( j< selectedElevationImagesList.size()){
                    if(!selectedElevationImagesList.get(j).getImageUrl().isEmpty()){
                        try {
                            PdfPTable table = new PdfPTable(3);
                            byte[] imageBytesResized;
                            table.setWidths(new float[]{1, 5, 4});
                            imageBytesResized = resizeImage(selectedElevationImagesList.get(j).getImageUrl(), (int) ((document.getPageSize().getWidth() / 2) - 100), (int) ((document.getPageSize().getHeight() / numberOfImagesPerPage) - 100));
                            com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                            table.setHorizontalAlignment(Element.ALIGN_LEFT);
                            table.setWidthPercentage(100);
                            table.addCell(getImageNumberPdfPCell("", PdfPCell.ALIGN_LEFT));
                            table.addCell(getCellImagCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
                            table.addCell(getCell(selectedElevationImagesList.get(j).getCategory(),selectedElevationImagesList.get(j).getTitle(), selectedElevationImagesList.get(j).getDescription(), PdfPCell.LEFT, document, numberOfImagesPerPage));

                            document.add(table);
                            document.add(new Paragraph(" "));

                            if ((k + 1) % numberOfImagesPerPage == 0) {
                                document.newPage();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        k++;
                    }
                    j++;
                }
                if(k +1 < selectedElevationImagesList.size()){
                    document.newPage();
                }

                for (int i = 0; i < selectedImageList.size(); i++) {
                    try {
                        PdfPTable table = new PdfPTable(3);
                        byte[] imageBytesResized;
                        table.setWidths(new float[]{1, 5, 4});
                        imageBytesResized = resizeImage(selectedImageList.get(i).getImageUrl(), (int) ((document.getPageSize().getWidth() / 2) - 100), (int) ((document.getPageSize().getHeight() / numberOfImagesPerPage) - 100));
                        com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                        table.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.setWidthPercentage(100);
                        table.addCell(getImageNumberPdfPCell((i + 1) + ".", PdfPCell.ALIGN_LEFT));
                        table.addCell(getCellImagCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
                        table.addCell(getCell(!selectedImageList.get(i).getCategory().isEmpty()?"Category: "+selectedImageList.get(i).getCategory(): "",selectedImageList.get(i).getTitle(), selectedImageList.get(i).getDescription(), PdfPCell.LEFT, document, numberOfImagesPerPage));
                        document.add(table);
                        document.add(new Paragraph(" "));

                        if ((i + 1) % numberOfImagesPerPage == 0) {
                            document.newPage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                reportsListDBHelper.addReportEntry(reportItem);
                document.close();
                return "done";
            } catch (FileNotFoundException e) {
                return "error";
            } catch (IOException e) {
                return "error";
            } catch (DocumentException e) {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            selectImagesParentLayout.setClickable(true);
            progressBar.setVisibility(View.GONE);
            parentLayout.setVisibility(View.VISIBLE);
            if (result.trim().equals("done")) {
                sendReportDBchangeSignal.notifyReportDBChanged();
                showSnackbarMessage(getResources().getString(R.string.rep_gen_success), false, false);
            }
            if (result.trim().equals("error")) {
                showSnackbarMessage(getResources().getString(R.string.rep_gen_failure), false, true);
            }
            CommonUtils.unlockOrientation(getActivity());
        }

        @Override
        protected void onPreExecute() {
            //Before process execution begins
            parentLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            selectImagesParentLayout.setClickable(false);
            CommonUtils.lockOrientation(getActivity());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fileUri != null) {
            outState.putParcelable("fileUri", fileUri);
        }
        if (reportTitle != null) {
            outState.putString("reportTitle", reportTitle);
        }

        if (reportDescription != null) {
            outState.putString("reportDescription", reportDescription);
        }
        if (clientName != null) {
            outState.putString("clientName", clientName);
        }
        if (claimNumber != null) {
            outState.putString("claimNumber", claimNumber);
        }
        if (address != null) {
            outState.putString("claimNumber", address);
        }
        if (selectedImageList != null) {
            outState.putSerializable("selectedImageList", selectedImageList);
        }
        if (reportId != null) {
            outState.putString("reportId", reportId);
        }
        if (reportPath != null) {
            outState.putString("reportPath", reportPath);
        }
        if (selectedElevationImagesList != null) {
            outState.putSerializable("selectedElevationImagesList", selectedElevationImagesList);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable("fileUri");
            reportTitle = savedInstanceState.getString("reportTitle");
            reportDescription = savedInstanceState.getString("reportDescription");
            clientName = savedInstanceState.getString("clientName");
            claimNumber = savedInstanceState.getString("claimNumber");
            address = savedInstanceState.getString("address");
            reportId = savedInstanceState.getString("reportId");
            reportPath = savedInstanceState.getString("reportPath");
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    class MyDocHeader extends PdfPageEventHelper {
        Font font = new Font(Font.FontFamily.UNDEFINED, 18, Font.ITALIC);
        String reportTitle;

        MyDocHeader(String reportTitle) {
            this.reportTitle = reportTitle;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase(reportTitle, font);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    header,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + 10, 0);
            Phrase footer = new Phrase("Page " + writer.getPageNumber() + "", font);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footer,
                    (document.right()),
                    document.bottom() - 10, 0);

        }
    }

    public void showReportPreferenceDialog() {
        final CharSequence[] items = {"2", "4"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Number of images per page ?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("2")) {
                    new GenerateReportOperator().execute(2);
                } else if (items[item].equals("4")) {
                    new GenerateReportOperator().execute(4);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public PdfPCell getImageNumberPdfPCell(String number, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(new Phrase(number));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
}

