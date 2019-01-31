package com.electivechaos.claimsadjuster.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.electivechaos.claimsadjuster.BaseActivity;
import com.electivechaos.claimsadjuster.CameraActivity;
import com.electivechaos.claimsadjuster.ImageHelper;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.SingleMediaScanner;
import com.electivechaos.claimsadjuster.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.claimsadjuster.adapters.CustomMenuAdapter;
import com.electivechaos.claimsadjuster.adapters.FrequentlyUsedNotesPopUpAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBFrequentlyUsedNotes;
import com.electivechaos.claimsadjuster.asynctasks.DBPropertyDetailsListTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.dialog.ImageDetailsFragment;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallbackForNotes;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.electivechaos.claimsadjuster.ui.AddEditCategoryActivity.ADD_COVERAGE_REQUEST_CODE;

public class QuickImageDetailsActivity extends BaseActivity {
    private ImageDetailsPOJO imageDetails;
    private int position, labelPosition;
    private boolean isEdit = false;

    private TextView imageCoverageType;
    private CategoryListDBHelper categoryListDBHelper;
    private String labelDefaultCoverageType = "";
    private ImageButton freqNotes, lastNote,addImage;
    private EditText notes;
    private static final int ADD_CATEGORY_REQUEST = 10;
    private TextView selectLabel;
    private FrameLayout parentLayoutForMessages;

    private String mCurrentPhotoPath;
    private Uri fileUri;
    private File photoFile;
    private static final int REQUEST_QUICK_CAMERA = 255;
    private static final int QUICK_CAMERA_CAPTURE = 280;
    private boolean donePressed = false;

    private String labelName,notesString,coverageType;
    private boolean isDamage, isOverview , isPointOfOrigin;
    private String reportId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture_quick);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);
        ImageView imgView = findViewById(R.id.imageView);
        notes = findViewById(R.id.clickedImageNotes);
        final CheckedTextView isDamageTextView = findViewById(R.id.isDamageTextView);
        final CheckedTextView isOverviewTextView = findViewById(R.id.isOverviewTextView);
        final CheckedTextView isPointOfOriginTextView = findViewById(R.id.isPointOfOrigin);
        final ImageButton imageInfoBtn = findViewById(R.id.imageInfo);

        imageCoverageType = findViewById(R.id.imageCoverageType);
        freqNotes = findViewById(R.id.freqNotes);
        lastNote = findViewById(R.id.lastNote);
        addImage = findViewById(R.id.addImage);
        selectLabel = findViewById(R.id.selectLabel);
        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);


        imageDetails = getIntent().getExtras().getParcelable("image_details");
        labelDefaultCoverageType = getIntent().getExtras().getString("labelDefaultCoverageType");
        isEdit = getIntent().getExtras().getBoolean("isEdit", false);
        position = getIntent().getExtras().getInt("position", -1);
        labelPosition = getIntent().getExtras().getInt("labelPosition", -1);

        labelName = getIntent().getExtras().getString("labelName");
        isDamage = getIntent().getExtras().getBoolean("isDamage");
        isOverview = getIntent().getExtras().getBoolean("isOverview");
        isPointOfOrigin = getIntent().getExtras().getBoolean("isPointOfOrigin");
        notesString = getIntent().getExtras().getString("notes");
        coverageType = getIntent().getExtras().getString("coverageType");
        reportId = getIntent().getExtras().getString("reportId");


        if(!TextUtils.isEmpty(labelName) && !labelName.equalsIgnoreCase("Select Label")) {
            selectLabel.setText(labelName);
            selectLabel.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));
        }else {
            selectLabel.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_gray));
        }
        if(isDamage){
            isDamageTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));
            isDamageTextView.setChecked(true);
            imageDetails.setIsDamage(true);
        }else {
            isDamageTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_gray));
            isDamageTextView.setChecked(false);
            imageDetails.setIsDamage(false);
        }
        if(isOverview){
            isOverviewTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));
            isOverviewTextView.setChecked(true);
            imageDetails.setOverview(true);
        }else {
            isOverviewTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_gray));
            isOverviewTextView.setChecked(false);
            imageDetails.setOverview(false);
        }
        if(isPointOfOrigin){
            isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));
            isPointOfOriginTextView.setChecked(true);
            imageDetails.setPointOfOrigin(true);
        }else {
            isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_gray));
            isPointOfOriginTextView.setChecked(false);
            imageDetails.setPointOfOrigin(false);
        }
        if(!TextUtils.isEmpty(notesString)){
            imageDetails.setDescription(notesString);
            notes.setText(notesString);
        }
        if(!TextUtils.isEmpty(coverageType)){
            imageDetails.setCoverageTye(coverageType);
            imageCoverageType.setText(coverageType);
        }
        if (savedInstanceState != null) {
            imageDetails = savedInstanceState.getParcelable("image_details");
            isDamageTextView.setChecked(imageDetails.isDamage());

            if (imageDetails.getCoverageTye() == null || imageDetails.getCoverageTye().isEmpty()) {
                imageCoverageType.setText(R.string.coverage_type);
            } else {
                imageCoverageType.setText(imageDetails.getCoverageTye());
            }


            if (imageDetails.isDamage()) {
                isDamageTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));
            } else {
                isDamageTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_gray));
            }


            isOverviewTextView.setChecked(imageDetails.isOverview());
            if (imageDetails.isOverview()) {
                isOverviewTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));
            } else {
                isOverviewTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_gray));
            }

            isPointOfOriginTextView.setChecked(imageDetails.isPointOfOrigin());
            if (imageDetails.isPointOfOrigin()) {
                isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));
            } else {
                isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_gray));
            }

            labelName = savedInstanceState.getString("labelName");
            if(!TextUtils.isEmpty(labelName) && !labelName.equalsIgnoreCase("Select Label")){
                selectLabel.setText(labelName);
                selectLabel.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));
            }else {
                selectLabel.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_gray));
            }

            if(!TextUtils.isEmpty(coverageType)){
                imageCoverageType.setText(coverageType);
            }

//            fileUri = Uri.parse(savedInstanceState.getString("fileUri"));
        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveImageDetails()) {
                    cameraIntent(REQUEST_QUICK_CAMERA);
                }
            }
        });


        selectLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ArrayList<Category> categories = categoryListDBHelper.getCategoryList();

                final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(QuickImageDetailsActivity.this, categories);

                final android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(QuickImageDetailsActivity.this);
                ad.setCancelable(true);
                ad.setPositiveButton("Add New", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(QuickImageDetailsActivity.this, AddEditCategoryActivity.class);
                        startActivityForResult(intent, ADD_CATEGORY_REQUEST);
                    }
                });
                ad.setTitle("Select Category");

                ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int pos) {
                        selectLabel.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                        selectLabel.setText(categories.get(pos).getCategoryName().toString());
                        labelName  = categories.get(pos).getCategoryName().toString();
                        dialogInterface.dismiss();

                    }
                });

                ad.show();
            }
        });
        freqNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new DBFrequentlyUsedNotes(QuickImageDetailsActivity.this, categoryListDBHelper, "frequent", new AsyncTaskStatusCallbackForNotes() {
                        @Override
                        public void onPostExecute(Object object, String type) {
                            final ArrayList notesList = (ArrayList<String>) object;
                            final AlertDialog.Builder ad = new AlertDialog.Builder(QuickImageDetailsActivity.this);

                            ad.setCancelable(true);

                            ad.setTitle("Frequently Used Notes");
                            if (notesList.size() == 0) {
                                ad.setMessage(R.string.notes_not_found);
                            }
                            final FrequentlyUsedNotesPopUpAdapter adapter = new FrequentlyUsedNotesPopUpAdapter(QuickImageDetailsActivity.this, notesList);

                            ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    if (notesList != null) {
                                        if(!TextUtils.isEmpty(notesList.get(position).toString())){
                                            notes.setText(notesList.get(position).toString());
                                        }
                                    }
                                    dialogInterface.dismiss();

                                }
                            });

                            ad.show();
                        }


                        @Override
                        public void onPreExecute() {

                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    }).execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });


        lastNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new DBFrequentlyUsedNotes(QuickImageDetailsActivity.this, categoryListDBHelper, "lastnote", new AsyncTaskStatusCallbackForNotes() {
                        @Override
                        public void onPostExecute(Object object, String type) {
                            final ArrayList notesList = (ArrayList<String>) object;
                            final AlertDialog.Builder ad = new AlertDialog.Builder(QuickImageDetailsActivity.this);

                            ad.setCancelable(true);

                            ad.setTitle("Last Note");
                            if (notesList.size() == 0) {
                                ad.setMessage(R.string.notes_not_found);
                            }
                            final FrequentlyUsedNotesPopUpAdapter adapter = new FrequentlyUsedNotesPopUpAdapter(QuickImageDetailsActivity.this, notesList);

                            ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    Object noteObj =notesList.get(position);
                                        if(!TextUtils.isEmpty(noteObj.toString())){
                                            notes.setText(noteObj.toString());
                                    }
                                    dialogInterface.dismiss();
                                }
                            });

                            ad.show();
                        }

                        @Override
                        public void onPreExecute() {

                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    }).execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });


        imageInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowPopup(imageDetails);
            }
        });

        isDamageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOverviewTextView.isChecked()) {
                    isOverviewTextView.setChecked(false);
                    isOverviewTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                    imageDetails.setOverview(false);
                    isOverview = false;
                }
                if (((CheckedTextView) v).isChecked()) {
                    ((CheckedTextView) v).setChecked(false);
                    imageDetails.setIsDamage(false);
                    isDamage = false;
                    v.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                } else {
                    ((CheckedTextView) v).setChecked(true);
                    imageDetails.setIsDamage(true);
                    isDamage = true;
                    v.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                }
            }
        });

        isOverviewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDamageTextView.isChecked()) {
                    isDamageTextView.setChecked(false);
                    isDamageTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                    imageDetails.setDamage(false);
                    isDamage = false;
                }
                if (((CheckedTextView) v).isChecked()) {
                    ((CheckedTextView) v).setChecked(false);
                    imageDetails.setOverview(false);
                    isOverview = false;
                    v.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                } else {
                    ((CheckedTextView) v).setChecked(true);
                    imageDetails.setOverview(true);
                    isOverview = true;
                    v.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                }
            }
        });

        isPointOfOriginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPointOfOriginTextView.isChecked()) {
                    isPointOfOriginTextView.setChecked(false);
                    isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                    imageDetails.setPointOfOrigin(false);
                    isPointOfOrigin = false;
                } else {
                    isPointOfOriginTextView.setChecked(true);
                    isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                    imageDetails.setPointOfOrigin(true);
                    isPointOfOrigin = true;
                }

            }
        });


        imageCoverageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBPropertyDetailsListTsk(categoryListDBHelper, "coverage_type", new AsyncTaskStatusCallback() {
                    @Override
                    public void onPostExecute(Object object, String type) {

                        final ArrayList<CoveragePOJO> coveragePOJOS = (ArrayList<CoveragePOJO>) object;

                        final AlertDialog.Builder ad = new AlertDialog.Builder(QuickImageDetailsActivity.this);

                        ad.setCancelable(true);
                        ad.setPositiveButton("Add New", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(QuickImageDetailsActivity.this, AddEditCoverageActivity.class);
                                startActivityForResult(intent, ADD_COVERAGE_REQUEST_CODE);
                            }
                        });
                        ad.setTitle("Coverage Type");
                        if (coveragePOJOS.size() == 0) {
                            ad.setMessage("No coverage types found.");
                        }
                        CustomMenuAdapter adapter = new CustomMenuAdapter(coveragePOJOS, imageDetails.getCoverageTye(), "coverage_type");
                        ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                imageDetails.setCoverageTye(coveragePOJOS.get(position).getName());
                                imageCoverageType.setText(imageDetails.getCoverageTye());
                                coverageType = imageDetails.getCoverageTye();
                                dialogInterface.dismiss();

                            }
                        });

                        ad.show();
                        CommonUtils.unlockOrientation(QuickImageDetailsActivity.this);
                    }

                    @Override
                    public void onPreExecute() {
                        CommonUtils.lockOrientation(QuickImageDetailsActivity.this);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();

            }
        });


        ImageButton imageButton = findViewById(R.id.submitImageDetails);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donePressed = true;
                saveImageDetails();

            }
        });
        Glide.with(this).load("file://" + imageDetails.getImageUrl()).into(imgView);
    }

    public boolean saveImageDetails(){
        ImageDetailsPOJO shareImageDetails = new ImageDetailsPOJO();
        shareImageDetails.setDescription(notes.getText().toString());
        shareImageDetails.setImageId(imageDetails.getImageId());
        shareImageDetails.setImageUrl(imageDetails.getImageUrl());
        shareImageDetails.setIsDamage(imageDetails.isDamage());
        shareImageDetails.setOverview(imageDetails.isOverview());
        shareImageDetails.setPointOfOrigin(imageDetails.isPointOfOrigin());
        shareImageDetails.setCoverageTye(imageDetails.getCoverageTye());
        shareImageDetails.setImageName(imageDetails.getImageName());
        shareImageDetails.setImageDateTime(imageDetails.getImageDateTime());
        shareImageDetails.setImageGeoTag(imageDetails.getImageGeoTag());

        String labelName = selectLabel.getText().toString();
        if(TextUtils.isEmpty(labelName) || labelName.trim().equalsIgnoreCase("Select Label")){
            CommonUtils.showSnackbarMessage(getString(R.string.please_select_label), true, true, parentLayoutForMessages, QuickImageDetailsActivity.this);
            return false;
        }else {
            String imageId = categoryListDBHelper.addQuickLabel(shareImageDetails,labelName,reportId);
            shareImageDetails.setImageId(imageId);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            if(donePressed){
                finish();
            }
            donePressed = false;
            return true;
        }
    }

    public void onShowPopup(ImageDetailsPOJO imageDetails) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
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



    private void cameraIntent(int requestId) {

        Intent intent = new Intent(QuickImageDetailsActivity.this, CameraActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {

            photoFile = getOutputMediaFile();
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    fileUri = Uri.fromFile(photoFile);

                } else {
                    fileUri = FileProvider.getUriForFile(getApplicationContext(),
                            getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoPath);
                ImageHelper.grantAppPermission(this, intent, fileUri);
                startActivityForResult(intent, requestId);
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
                    .make(parentLayoutForMessages, "Something went wrong.Please retry with system camera app.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white_color));
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_error));
            snackbar.show();
        }
    }


    private void onCaptureImageResult(Intent data) {
        new SingleMediaScanner(this, photoFile, new OnMediaScannerListener() {
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
                    ArrayList<ImageDetailsPOJO> returnedImageItem =  capturedImage;
                    Intent intent = new Intent(QuickImageDetailsActivity.this, QuickImageDetailsActivity.class);
                    ////TODO::
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();

                    intent.putExtra("reportId",reportId);

                    if (returnedImageItem != null) {
                        intent.putExtra("image_details", returnedImageItem.get(0));
                    }
                    intent.putExtra("labelDefaultCoverageType", labelDefaultCoverageType);
                    intent.putExtra("labelName",labelName);
                    intent.putExtra("isDamage",isDamage);
                    intent.putExtra("isOverview",isOverview);
                    intent.putExtra("isPointOfOrigin",isPointOfOrigin);
                    intent.putExtra("notes",notes.getText().toString());
                    intent.putExtra("coverageType",coverageType);
                    startActivityForResult(intent, QUICK_CAMERA_CAPTURE);


                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) switch (requestCode) {
            case ADD_COVERAGE_REQUEST_CODE:
                Bundle b = data.getExtras().getBundle("coverageDetails");
                imageDetails.setCoverageTye(b.getString("name"));
                imageCoverageType.setText(imageDetails.getCoverageTye());
                coverageType = imageDetails.getCoverageTye();
                break;

            case REQUEST_QUICK_CAMERA:
                onImageCapturedResult(data);
                break;

            case ADD_CATEGORY_REQUEST:
                Bundle dataFromActivity = data.getExtras().getBundle("categoryDetails");
                if (dataFromActivity != null) {
                    String categoryName = dataFromActivity.get("categoryName").toString();
                    selectLabel.setText(categoryName);
                    selectLabel.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));

                }
                break;

            case QUICK_CAMERA_CAPTURE:
                finish();
                break;
            default:
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image_details", imageDetails);
        outState.putString("labelName",labelName);
        outState.putString("labelDefaultCoverageType", labelDefaultCoverageType);
//        outState.putString("fileUri", fileUri.toString());
    }
}
