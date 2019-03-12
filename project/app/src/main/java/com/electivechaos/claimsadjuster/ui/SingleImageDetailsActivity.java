package com.electivechaos.claimsadjuster.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.electivechaos.claimsadjuster.BaseActivity;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.CustomMenuAdapter;
import com.electivechaos.claimsadjuster.adapters.FrequentlyUsedNotesPopUpAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBFrequentlyUsedNotes;
import com.electivechaos.claimsadjuster.asynctasks.DBPropertyDetailsListTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.dialog.ImageDetailsFragment;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallbackForNotes;
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.electivechaos.claimsadjuster.ui.AddEditCategoryActivity.ADD_COVERAGE_REQUEST_CODE;

/**
 * Created by krishna on 11/10/17.
 */

public class SingleImageDetailsActivity extends BaseActivity {
    private ImageDetailsPOJO imageDetails;
    private int position, labelPosition;
    private boolean isEdit = false;

    private TextView imageCoverageType;
    private CategoryListDBHelper categoryListDBHelper;
    private String labelDefaultCoverageType = "";
    private ImageButton freqNotes, lastNote;


    static RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.imagepicker_image_placeholder)
            .error(R.drawable.imagepicker_image_placeholder)
            .fitCenter()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image_details_layout);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);
        ImageView imgView = findViewById(R.id.imageView);
        final EditText description = findViewById(R.id.clickedImageNotes);
        final CheckedTextView isDamageTextView = findViewById(R.id.isDamageTextView);
        final CheckedTextView isOverviewTextView = findViewById(R.id.isOverviewTextView);
        final CheckedTextView isPointOfOriginTextView = findViewById(R.id.isPointOfOrigin);
        final ImageButton imageInfoBtn = findViewById(R.id.imageInfo);

        imageCoverageType = findViewById(R.id.imageCoverageType);
        freqNotes = findViewById(R.id.freqNotes);
        lastNote = findViewById(R.id.lastNote);


        imageDetails = getIntent().getExtras().getParcelable("image_details");
        labelDefaultCoverageType = getIntent().getExtras().getString("labelDefaultCoverageType");
        isEdit = getIntent().getExtras().getBoolean("isEdit", false);
        position = getIntent().getExtras().getInt("position", -1);
        labelPosition = getIntent().getExtras().getInt("labelPosition", -1);

        Log.d("FUCK:UrL",imageDetails.getImageUrl());


        if (imageDetails != null && isEdit) {
            if (imageDetails.getCoverageTye() == null || imageDetails.getCoverageTye().isEmpty()) {
                imageCoverageType.setText(R.string.coverage_type);
            } else {
                imageCoverageType.setText(imageDetails.getCoverageTye());
            }
            description.setText(imageDetails.getDescription());

            isDamageTextView.setChecked(imageDetails.isDamage());
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
        }


        //Most frequently used notes


        freqNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new DBFrequentlyUsedNotes(SingleImageDetailsActivity.this, categoryListDBHelper, "frequent", new AsyncTaskStatusCallbackForNotes() {
                        @Override
                        public void onPostExecute(Object object, String type) {
                            final ArrayList notesList = (ArrayList<String>) object;
                            final AlertDialog.Builder ad = new AlertDialog.Builder(SingleImageDetailsActivity.this);

                            ad.setCancelable(true);

                            ad.setTitle("Frequently Used Notes");
                            if (notesList.size() == 0) {
                                ad.setMessage(R.string.notes_not_found);
                            }
                            final FrequentlyUsedNotesPopUpAdapter adapter = new FrequentlyUsedNotesPopUpAdapter(SingleImageDetailsActivity.this, notesList);

                            ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    if (notesList != null) {
                                        if(!TextUtils.isEmpty(notesList.get(position).toString())){
                                            description.setText(notesList.get(position).toString());
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
                    new DBFrequentlyUsedNotes(SingleImageDetailsActivity.this, categoryListDBHelper, "lastnote", new AsyncTaskStatusCallbackForNotes() {
                        @Override
                        public void onPostExecute(Object object, String type) {
                            final ArrayList notesList = (ArrayList<String>) object;
                            final AlertDialog.Builder ad = new AlertDialog.Builder(SingleImageDetailsActivity.this);

                            ad.setCancelable(true);

                            ad.setTitle("Last Note");
                            if (notesList.size() == 0) {
                                ad.setMessage(R.string.notes_not_found);
                            }
                            final FrequentlyUsedNotesPopUpAdapter adapter = new FrequentlyUsedNotesPopUpAdapter(SingleImageDetailsActivity.this, notesList);

                            ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    Object noteObj =notesList.get(position);
                                        if(!TextUtils.isEmpty(noteObj.toString())){
                                            description.setText(noteObj.toString());
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

                AlertDialog.Builder builder = new AlertDialog.Builder(SingleImageDetailsActivity.this);
                builder.setMessage(description.getText()).show();
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
                    isOverviewTextView.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                    imageDetails.setOverview(false);
                }
                if (((CheckedTextView) v).isChecked()) {
                    ((CheckedTextView) v).setChecked(false);
                    imageDetails.setIsDamage(false);
                    v.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                } else {
                    ((CheckedTextView) v).setChecked(true);
                    imageDetails.setIsDamage(true);
                    v.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                }
            }
        });

        isOverviewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDamageTextView.isChecked()) {
                    isDamageTextView.setChecked(false);
                    isDamageTextView.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                    imageDetails.setDamage(false);
                }
                if (((CheckedTextView) v).isChecked()) {
                    ((CheckedTextView) v).setChecked(false);
                    imageDetails.setOverview(false);
                    v.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                } else {
                    ((CheckedTextView) v).setChecked(true);
                    imageDetails.setOverview(true);
                    v.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
                }
            }
        });

        isPointOfOriginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPointOfOriginTextView.isChecked()) {
                    isPointOfOriginTextView.setChecked(false);
                    isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this, R.drawable.shape_chip_drawable_gray));
                    imageDetails.setPointOfOrigin(false);
                } else {
                    isPointOfOriginTextView.setChecked(true);
                    isPointOfOriginTextView.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this, R.drawable.shape_chip_drawable_active));
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

                        final AlertDialog.Builder ad = new AlertDialog.Builder(SingleImageDetailsActivity.this);

                        ad.setCancelable(true);
                        ad.setPositiveButton("Add New", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SingleImageDetailsActivity.this, AddEditCoverageActivity.class);
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
                        CommonUtils.unlockOrientation(SingleImageDetailsActivity.this);
                    }

                    @Override
                    public void onPreExecute() {
                        CommonUtils.lockOrientation(SingleImageDetailsActivity.this);
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
                ImageDetailsPOJO shareImageDetails = new ImageDetailsPOJO();
                shareImageDetails.setDescription(description.getText().toString());
                shareImageDetails.setImageId(imageDetails.getImageId());
                shareImageDetails.setImageUrl(imageDetails.getImageUrl());
                Log.d("FUCK:",imageDetails.getImageUrl());
                shareImageDetails.setIsDamage(imageDetails.isDamage());
                shareImageDetails.setOverview(imageDetails.isOverview());
                shareImageDetails.setPointOfOrigin(imageDetails.isPointOfOrigin());
                shareImageDetails.setCoverageTye(imageDetails.getCoverageTye());
                shareImageDetails.setImageName(imageDetails.getImageName());
                shareImageDetails.setImageDateTime(imageDetails.getImageDateTime());
                shareImageDetails.setImageGeoTag(imageDetails.getImageGeoTag());


                Intent intent = new Intent();
                intent.putExtra("selected_images", shareImageDetails);
                intent.putExtra("isEdit", isEdit);
                intent.putExtra("position", position);
                intent.putExtra("labelPosition", labelPosition);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Glide.with(this).load("file://" + imageDetails.getImageUrl()). apply(options).into(imgView);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image_details", imageDetails);
        outState.putString("labelDefaultCoverageType", labelDefaultCoverageType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_COVERAGE_REQUEST_CODE) {
                Bundle b = data.getExtras().getBundle("coverageDetails");
                imageDetails.setCoverageTye(b.getString("name"));
                imageCoverageType.setText(imageDetails.getCoverageTye());
            }
        }
    }
}
