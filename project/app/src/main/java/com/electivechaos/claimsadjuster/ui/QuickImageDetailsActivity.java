package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import com.electivechaos.claimsadjuster.asynctasks.ImageRotationTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.dialog.ImageDetailsFragment;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallbackForNotes;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.electivechaos.claimsadjuster.ui.AddEditCategoryActivity.ADD_COVERAGE_REQUEST_CODE;

public class QuickImageDetailsActivity extends BaseActivity {
    private static final int ADD_CATEGORY_REQUEST = 10;
    private static final int REQUEST_QUICK_CAMERA = 255;
    private static final int QUICK_CAMERA_CAPTURE = 280;
    private ImageDetailsPOJO imageDetails;
    private TextView imageCoverageType, selectCategory;
    private CategoryListDBHelper categoryListDBHelper;
    private String labelDefaultCoverageType = "";
    private ImageButton freqNotes, lastNote;
    private EditText notes;
//    private TextView selectLabel;
    private FrameLayout parentLayoutForMessages;
    private String mCurrentPhotoPath;
    private Uri fileUri;
    private File photoFile;
    private boolean donePressed = false;

    private String labelName;
//    private  String notesString;
    private String reportId;

    private CheckedTextView isDamageTextView;
    private CheckedTextView isOverviewTextView;
    private CheckedTextView isPointOfOriginTextView;
    private FloatingActionButton imageInfoBtn, addImage, rotateImage;
    private ImageView imgView;


    private Animation fabOpen;
    private byte[] capturedImage;

    private int angle = 0;
    private int mCurrRotation = 0;
    private int toRotation =0;
    private int rotateDegree =0;

    private View progressBarLayout;

    public static Activity quickActivityObj;


    static RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.imagepicker_image_placeholder)
            .error(R.drawable.imagepicker_image_placeholder)
            .fitCenter()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE);



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture_quick);
        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        quickActivityObj = this;

        isDamageTextView = findViewById(R.id.isDamageTextView);
        isOverviewTextView = findViewById(R.id.isOverviewTextView);
        isPointOfOriginTextView = findViewById(R.id.isPointOfOrigin);
        imageInfoBtn = findViewById(R.id.imageInfo);
        imgView = findViewById(R.id.imageView);
        rotateImage = findViewById(R.id.rotateImage);
        notes = findViewById(R.id.clickedImageNotes);

        imageCoverageType = findViewById(R.id.imageCoverageType);
        freqNotes = findViewById(R.id.freqNotes);
        lastNote = findViewById(R.id.lastNote);
        addImage = findViewById(R.id.addImage);
//        selectLabel = findViewById(R.id.selectLabel);
        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);

        selectCategory = findViewById(R.id.selectCategory);

        progressBarLayout =findViewById(R.id.progressBarLayout);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        imageInfoBtn.startAnimation(fabOpen);
        addImage.startAnimation(fabOpen);
        rotateImage.startAnimation(fabOpen);


        imageDetails = getIntent().getExtras().getParcelable("image_details");
        reportId = getIntent().getExtras().getString("reportId");
        capturedImage = getIntent().getExtras().getByteArray("capturedImage");


        if (savedInstanceState != null) {
            imageDetails = savedInstanceState.getParcelable("imageDetails");
            fileUri = savedInstanceState.getParcelable("fileUri");
            photoFile = (File) savedInstanceState.getSerializable("photoFile");
            mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");

            labelName = savedInstanceState.getString("labelName");
            rotateDegree = savedInstanceState.getInt("rotateDegree");
            imgView.setRotation(rotateDegree);

            if (imageDetails.isDamage()) {
                isDamageTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                imageDetails.setIsDamage(true);
            } else {
                isDamageTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                imageDetails.setIsDamage(false);
            }


            if (imageDetails.isOverview()) {
                isOverviewTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                imageDetails.setOverview(true);
            } else {
                isOverviewTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                imageDetails.setOverview(false);
            }


            if (imageDetails.isPointOfOrigin()) {
                isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                imageDetails.setPointOfOrigin(true);
            } else {
                isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                imageDetails.setPointOfOrigin(false);
            }

            if (!TextUtils.isEmpty(labelName) && !labelName.equalsIgnoreCase("Select Category")) {
                selectCategory.setText(labelName);
            }

            if (!TextUtils.isEmpty(imageDetails.getCoverageTye())) {
                imageCoverageType.setText(imageDetails.getCoverageTye());
            }

        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (saveImageDetails("yes")) {
                    notes.setText("");
                }
            }
        });


        selectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        selectCategory.setText(categories.get(pos).getCategoryName().toString());
                        labelName = categories.get(pos).getCategoryName().toString();
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
                                        if (!TextUtils.isEmpty(notesList.get(position).toString())) {
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
                                    Object noteObj = notesList.get(position);
                                    if (!TextUtils.isEmpty(noteObj.toString())) {
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

        lastNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                };

                AlertDialog.Builder builder = new AlertDialog.Builder(QuickImageDetailsActivity.this);
                builder.setMessage(notes.getText()).show();
                return true;
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
                }
                if (((CheckedTextView) v).isChecked()) {
                    ((CheckedTextView)
                            v).setChecked(false);
                    imageDetails.setIsDamage(false);
                    v.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                } else {
                    ((CheckedTextView) v).setChecked(true);
                    imageDetails.setIsDamage(true);
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
                }
                if (((CheckedTextView) v).isChecked()) {
                    ((CheckedTextView) v).setChecked(false);
                    imageDetails.setOverview(false);
                    v.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                } else {
                    ((CheckedTextView) v).setChecked(true);
                    imageDetails.setOverview(true);
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
                } else {
                    isPointOfOriginTextView.setChecked(true);
                    isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(QuickImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                    imageDetails.setPointOfOrigin(true);
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



        rotateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();

            }

        });


        FloatingActionButton doneImageDetails = findViewById(R.id.submitImageDetails);
        doneImageDetails.startAnimation(fabOpen);

        doneImageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donePressed = true;
                saveImageDetails("no");
            }
        });
        setImage();
    }


    public void rotateImage(){
        rotateDegree = (int) (imgView.getRotation()-90);
        imgView.setRotation(imgView.getRotation()-90);
        setImage();
    }


    public void setImage() {
        Glide.with(this).load("file://" + imageDetails.getImageUrl())
                .apply(options)
                .into(imgView);
    }

    public boolean saveImageDetails(final String addImage) {
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

        String labelName = selectCategory.getText().toString();
        if (TextUtils.isEmpty(labelName) || labelName.trim().equalsIgnoreCase("Select Category")) {
            CommonUtils.showSnackbarMessage(getString(R.string.please_select_label), true, true, parentLayoutForMessages, QuickImageDetailsActivity.this);
            return false;
        } else {
            String imageId = categoryListDBHelper.addQuickLabel(shareImageDetails, labelName, reportId);
            shareImageDetails.setImageId(imageId);
            if(rotateDegree != 0) {
                File file = new File(imageDetails.getImageUrl());
                Bitmap bitmap = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);

                progressBarLayout.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotateDegree);

                new ImageRotationTsk(QuickImageDetailsActivity.this, imageDetails.getImageUrl(), file, bitmap, matrix, progressBarLayout, false, new AsyncTaskStatusCallback() {

                    @Override
                    public void onPostExecute(Object object, String type) {

                        Toast.makeText(QuickImageDetailsActivity.this, "Image saved.", Toast.LENGTH_SHORT).show();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);

                        if(addImage.equalsIgnoreCase("yes")){
                            cameraIntent(REQUEST_QUICK_CAMERA);
                            rotateDegree = 0;
                            imgView.setRotation(rotateDegree);
                        }else {
                            finish();
                        }
                    }

                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();
            }else {
                if(!donePressed) {
                    cameraIntent(REQUEST_QUICK_CAMERA);
                }
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
            }


            if (donePressed) {
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
        angle = 0;
        if (fileUri != null) {
            imageDetails.setImageUrl(String.valueOf(photoFile));
            imageDetails.setDescription("");
            notes.setText("");
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
                    imageDetails.setImageUrl(path);
                    File file = new File(path);
                    if (file.exists()) {
                        imageDetails.setImageName(file.getName());
                        Date date = new Date(file.lastModified());
                        String dateString = new SimpleDateFormat("dd/MM/yyyy").format(date);
                        String timeString = new SimpleDateFormat("HH:mm:ss a").format(date);
                        imageDetails.setImageDateTime(dateString + " at " + timeString);
                        imageDetails.setImageGeoTag("");
                    }
                    imageDetails.setImageUrl(path);

                }

            }
        });
        imageDetails.setImageUrl(mCurrentPhotoPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) switch (requestCode) {
            case ADD_COVERAGE_REQUEST_CODE:
                Bundle b = data.getExtras().getBundle("coverageDetails");
                imageDetails.setCoverageTye(b.getString("name"));
                imageCoverageType.setText(imageDetails.getCoverageTye());
                break;

            case REQUEST_QUICK_CAMERA:
                capturedImage =  data.getByteArrayExtra("capturedImage");
                onImageCapturedResult(data);
                break;

            case ADD_CATEGORY_REQUEST:
                Bundle dataFromActivity = data.getExtras().getBundle("categoryDetails");
                if (dataFromActivity != null) {
                    String categoryName = dataFromActivity.get("categoryName").toString();
                    selectCategory.setText(categoryName);
//                    selectCategory.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_chip_drawable_active));

                }
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImage();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("imageDetails", imageDetails);
        outState.putString("labelName", labelName);
        outState.putParcelable("fileUri", fileUri);
        outState.putSerializable("photoFile", photoFile);
        outState.putSerializable("mCurrentPhotoPath", mCurrentPhotoPath);
        outState.putString("labelDefaultCoverageType", labelDefaultCoverageType);
        outState.putInt("rotateDegree", rotateDegree);
    }

}
