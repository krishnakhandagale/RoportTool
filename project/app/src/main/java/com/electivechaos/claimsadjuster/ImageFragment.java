package com.electivechaos.claimsadjuster;

/**
 * Created by krishna on 11/7/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.electivechaos.claimsadjuster.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.claimsadjuster.adapters.CustomMenuAdapter;
import com.electivechaos.claimsadjuster.adapters.FrequentlyUsedNotesPopUpAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBFrequentlyUsedNotes;
import com.electivechaos.claimsadjuster.asynctasks.DBPropertyDetailsListTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.dialog.ImageDetailsFragment;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallbackForNotes;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.ui.AddEditCategoryActivity;
import com.electivechaos.claimsadjuster.ui.AddEditCoverageActivity;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.electivechaos.claimsadjuster.ui.AddEditCategoryActivity.ADD_COVERAGE_REQUEST_CODE;

public class ImageFragment extends Fragment {
    private static final int RESULT_OK = -1;
    static ViewPager mPagerInstance;
    private static CategoryListDBHelper categoryListDBHelper;
    TextView imageCoverageType, selectCategory;
    ImageButton imageInfo;
    private String imageUrl;
    private String imgDescription;
    private String coverageType, categoryType;
    private String imgName, imgDateTime, imgGeoTag;
    private boolean imgIsDamage;
    private boolean imgIsOverview;
    private boolean imgIsPointOfOrigin;
    private int position;
    private MonitorImageDetailsChange monitorImageDetailsChange;
    private ImageButton freqNotes, lastNote;

    private Label label;
    private String quickLabel;
    private ImageDetailsPOJO imageDetailsPOJO;
    private String reportId;

    private static final int ADD_CATEGORY_REQUEST = 10;


    public static ImageFragment init(ImageDetailsPOJO imageDetails, int position, ViewPager mPager, Label label, String reportIdd) {
        ImageFragment imageFragment = new ImageFragment();
        mPagerInstance = mPager;

        categoryListDBHelper = CategoryListDBHelper.getInstance(mPager.getContext());
        Bundle args = new Bundle();

        args.putString("imageUrl", imageDetails.getImageUrl());
        args.putString("description", imageDetails.getDescription());
        args.putString("coverageType", imageDetails.getCoverageTye());
        args.putBoolean("imgIsDamage", imageDetails.isDamage());
        args.putBoolean("imgIsOverview", imageDetails.isOverview());
        args.putBoolean("imgIsPointPofOrigin", imageDetails.isPointOfOrigin());
        args.putString("imgName", imageDetails.getImageName());
        args.putString("imgDateTime", imageDetails.getImageDateTime());
        args.putString("imgGeoTag", imageDetails.getImageGeoTag());
        args.putInt("position", position);

            args.putParcelable("label", label);
            args.putString("quickLabel", imageDetails.getLabelName());

        args.putParcelable("imageDetailsPojo", imageDetails);
        args.putString("reportId", reportIdd);

        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getArguments() != null ? getArguments().getString("imageUrl") : "";
        position = getArguments() != null ? getArguments().getInt("position") : 0;
        imgDescription = getArguments() != null ? getArguments().getString("description") : "";
        coverageType = getArguments() != null ? getArguments().getString("coverageType") : "";
        imgIsDamage = getArguments() != null && getArguments().getBoolean("imgIsDamage");
        imgIsOverview = getArguments() != null && getArguments().getBoolean("imgIsOverview");
        imgIsPointOfOrigin = getArguments() != null && getArguments().getBoolean("imgIsPointOfOrigin");
        imgName = getArguments() != null ? getArguments().getString("imgName") : "";
        imgDateTime = getArguments() != null ? getArguments().getString("imgDateTime") : "";
        imgGeoTag = getArguments() != null ? getArguments().getString("imgGeoTag") : "";
        label = getArguments() != null ? (Label) getArguments().getParcelable("label") : new Label();
        quickLabel = getArguments() != null ? getArguments().getString("quickLabel") : "";
        reportId = getArguments() != null ? getArguments().getString("reportId") : null;

        imageDetailsPOJO = getArguments() != null ? (ImageDetailsPOJO) getArguments().getParcelable("imageDetailsPojo") : new ImageDetailsPOJO();


        if (savedInstanceState != null) {
            imgIsDamage = savedInstanceState.getBoolean("imgIsDamage");
            imgIsOverview = savedInstanceState.getBoolean("imgIsOverview");
            imgIsPointOfOrigin = savedInstanceState.getBoolean("imgIsPointOfOrigin");
            coverageType = savedInstanceState.getString("coverageType");
            imgName = savedInstanceState.getString("imgName");
            imgDateTime = savedInstanceState.getString("imgDateTime");
            imgGeoTag = savedInstanceState.getString("imgGeoTag");
            label = savedInstanceState.getParcelable("label");
            quickLabel = savedInstanceState.getString("quickLabel");
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layoutView = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView iv = layoutView.findViewById(R.id.imageView1);
        final EditText description = layoutView.findViewById(R.id.image_description);

        final CheckedTextView damageTextView = layoutView.findViewById(R.id.damageTextView);
        final CheckedTextView overviewTextView = layoutView.findViewById(R.id.overviewTextView);
        final CheckedTextView pointOfOriginTextView = layoutView.findViewById(R.id.isPointOfOrigin);
        imageCoverageType = layoutView.findViewById(R.id.imageCoverageType);

        imageInfo = layoutView.findViewById(R.id.imageInfo);
        freqNotes = layoutView.findViewById(R.id.freqNotes);
        lastNote = layoutView.findViewById(R.id.lastNote);

        selectCategory = layoutView.findViewById(R.id.selectCategory);


        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowPopup();
            }
        });

        Log.d("FUCK: CREATE VIEW", quickLabel+"-----"+position);


        if(label != null && !TextUtils.isEmpty(label.getName())) {
            selectCategory.setText(label.getName());
        }else if(quickLabel != null && !TextUtils.isEmpty(quickLabel)){
            selectCategory.setText(quickLabel);
        }


        if (coverageType == null || coverageType.isEmpty()) {
            imageCoverageType.setText("Coverage Type");
        } else {
            imageCoverageType.setText(coverageType);
        }
        pointOfOriginTextView.setChecked(imgIsPointOfOrigin);

        if (imgIsPointOfOrigin) {
            pointOfOriginTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_active));
        } else {
            pointOfOriginTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_gray));
        }

        damageTextView.setChecked(imgIsDamage);
        if (imgIsDamage) {
            imgIsDamage = true;
            imgIsOverview = false;
            damageTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_active));
        } else {
            imgIsDamage = false;
            damageTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_gray));
        }

        overviewTextView.setChecked(imgIsOverview);
        if (imgIsOverview) {
            imgIsOverview = true;
            imgIsDamage = false;
            overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_active));
        } else {
            imgIsOverview = false;
            overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_gray));
        }


        description.setText(imgDescription);

        imageCoverageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBPropertyDetailsListTsk(categoryListDBHelper, "coverage_type", new AsyncTaskStatusCallback() {
                    @Override
                    public void onPostExecute(Object object, String type) {

                        final ArrayList<CoveragePOJO> coveragePOJOS = (ArrayList<CoveragePOJO>) object;

                        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                        ad.setCancelable(true);
                        ad.setPositiveButton("Add New", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), AddEditCoverageActivity.class);
                                getActivity().startActivityForResult(intent, ADD_COVERAGE_REQUEST_CODE);
                            }
                        });
                        ad.setTitle("Coverage Type");
                        if (coveragePOJOS.size() == 0) {
                            ad.setMessage("No coverage types found.");
                        }
                        CustomMenuAdapter adapter = new CustomMenuAdapter(coveragePOJOS, coverageType, "coverage_type");
                        ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                setCoverageType(coveragePOJOS.get(position).getName());
                                dialogInterface.dismiss();

                            }
                        });

                        ad.show();
                        CommonUtils.unlockOrientation(getActivity());
                    }

                    @Override
                    public void onPreExecute() {
                        CommonUtils.lockOrientation(getActivity());
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();
            }
        });


        //Most frequently used notes


        freqNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new DBFrequentlyUsedNotes(getActivity(), categoryListDBHelper, "frequent", new AsyncTaskStatusCallbackForNotes() {
                        @Override
                        public void onPostExecute(Object object, String type) {
                            final ArrayList notesList = (ArrayList<String>) object;
                            final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                            ad.setCancelable(true);

                            ad.setTitle("Frequently Used Notes");
                            if (notesList.size() == 0) {
                                ad.setMessage(R.string.notes_not_found);
                            }
                            final FrequentlyUsedNotesPopUpAdapter adapter = new FrequentlyUsedNotesPopUpAdapter(getActivity(), notesList);

                            ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    if (notesList != null) {
                                        if (!TextUtils.isEmpty(notesList.get(position).toString())) {
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
                    new DBFrequentlyUsedNotes(getActivity(), categoryListDBHelper, "lastnote", new AsyncTaskStatusCallbackForNotes() {
                        @Override
                        public void onPostExecute(Object object, String type) {
                            final ArrayList notesList = (ArrayList<String>) object;
                            final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                            ad.setCancelable(true);

                            ad.setTitle("Last Note");
                            if (notesList.size() == 0) {
                                ad.setMessage(R.string.notes_not_found);
                            }
                            final FrequentlyUsedNotesPopUpAdapter adapter = new FrequentlyUsedNotesPopUpAdapter(getActivity(), notesList);

                            ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    if (notesList != null) {
                                        if (!TextUtils.isEmpty(notesList.get(position).toString())) {
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


        selectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Category> categories = categoryListDBHelper.getCategoryList();

                final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(getActivity(), categories);

                final android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(getActivity());
                ad.setCancelable(true);
                ad.setPositiveButton("Add New", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
//                        startActivity(intent);
                        startActivityForResult(intent, ADD_CATEGORY_REQUEST);

                    }
                });
                ad.setTitle("Select Category");

                ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int pos) {
                        selectCategory.setText(categories.get(pos).getCategoryName().toString());
                        if(label != null) {
                            label = categoryListDBHelper.updateImageLabel(imageDetailsPOJO, categories.get(pos).getCategoryName(), reportId);
                            if(label != null) {
                                imageDetailsPOJO.setLabelName(label.getName());
                                monitorImageDetailsChange.setLabelName(label.getName(), position);
                            }

                        } else if(quickLabel != null) {
                            imageDetailsPOJO.setLabelName(categories.get(pos).getCategoryName().toString());
                            monitorImageDetailsChange.setLabelName(categories.get(pos).getCategoryName().toString(), position);
                            quickLabel = categories.get(pos).getCategoryName().toString();
                        }
                        dialogInterface.dismiss();

                    }
                });

                ad.show();

            }
        });


        //Here image information
        monitorImageDetailsChange.setImageName(imgName, position);
        monitorImageDetailsChange.setImageDateTime(imgDateTime, position);
        monitorImageDetailsChange.setGeoTag(imgGeoTag, position);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                monitorImageDetailsChange.updateImageDescription(s.toString(), position);
            }
        });


        damageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overviewTextView.isChecked()) {
                    overviewTextView.setChecked(false);
                    overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_gray));
                    imgIsOverview = false;
                    monitorImageDetailsChange.setUnsetOverview(false, position);
                }

                if (((CheckedTextView) v).isChecked()) {
                    ((CheckedTextView) v).setChecked(false);
                    imgIsDamage = false;
                    v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetDamage(false, position);
                } else {
                    ((CheckedTextView) v).setChecked(true);
                    imgIsDamage = true;
                    v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_active));
                    monitorImageDetailsChange.setUnsetDamage(true, position);
                }

            }
        });

        overviewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (damageTextView.isChecked()) {
                    damageTextView.setChecked(false);
                    imgIsDamage = false;
                    damageTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetDamage(false, position);
                }
                if (((CheckedTextView) v).isChecked()) {
                    ((CheckedTextView) v).setChecked(false);
                    imgIsOverview = false;
                    v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetOverview(false, position);
                } else {

                    ((CheckedTextView) v).setChecked(true);
                    imgIsOverview = true;
                    v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_active));
                    monitorImageDetailsChange.setUnsetOverview(true, position);
                }
            }
        });

        pointOfOriginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointOfOriginTextView.isChecked()) {
                    imgIsPointOfOrigin = false;
                    pointOfOriginTextView.setChecked(false);
                    pointOfOriginTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetPointOfOrigin(false, position);
                } else {
                    imgIsPointOfOrigin = true;
                    pointOfOriginTextView.setChecked(true);
                    pointOfOriginTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_chip_drawable_active));
                    monitorImageDetailsChange.setUnsetPointOfOrigin(true, position);
                }
            }
        });


        description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mPagerInstance.setCurrentItem(position + 1);
                    handled = true;
                }
                return handled;
            }
        });

        Glide.with(this).load("file://" + imageUrl).into(iv);



        return layoutView;
    }

    public void setCoverageType(String name) {
        coverageType = name;
        imageCoverageType.setText(coverageType);
        monitorImageDetailsChange.setUnsetCoverageType(coverageType, position);
    }

    public void onShowPopup() {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ImageDetailsFragment imageDetailsFragment = new ImageDetailsFragment();
        Bundle imageDetailsData = new Bundle();
        imageDetailsData.putString("imgName", imgName);
        imageDetailsData.putString("imgDateTime", imgDateTime);
        imageDetailsData.putString("imgGeoTag", imgGeoTag);

        imageDetailsFragment.setArguments(imageDetailsData);
        imageDetailsFragment.show(ft, "dialog");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            monitorImageDetailsChange = (ImageFragment.MonitorImageDetailsChange) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("imgIsDamage", imgIsDamage);
        outState.putBoolean("imgIsOverview", imgIsOverview);
        outState.putBoolean("imgIsPointOfOrigin", imgIsPointOfOrigin);
        outState.putString("coverageType", coverageType);
        outState.putString("imgName", imgName);
        outState.putString("imgDateTime", imgDateTime);
        outState.putString("imgGeoTag", imgGeoTag);
        outState.putParcelable("label", label);
        outState.putString("quickLabel", quickLabel);
    }

    public interface MonitorImageDetailsChange {
        // void updateImageTitle(String title, int position);
        void updateImageDescription(String description, int position);

        void setUnsetDamage(boolean isDamage, int position);

        void setUnsetOverview(boolean isOverview, int position);

        void setUnsetPointOfOrigin(boolean isPointOfOrigin, int position);

        void setUnsetCoverageType(String coverageType, int position);

        void setImageName(String name, int position);

        void setImageDateTime(String dateTime, int position);

        void setGeoTag(String geoTag, int position);

        void setLabelName(String labelName, int position);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_CATEGORY_REQUEST) {
                Bundle dataFromActivity = data.getExtras().getBundle("categoryDetails");
                if (dataFromActivity != null) {
                    String categoryName = dataFromActivity.get("categoryName").toString();
                    selectCategory.setText(categoryName);
                    label = categoryListDBHelper.updateImageLabel(imageDetailsPOJO, categoryName, reportId);
                    imageDetailsPOJO.setLabelName(label.getName());
                    monitorImageDetailsChange.setLabelName(label.getName(), position);
                 //   quickLabel = label.getName();
                }
            }
        }
    }
}
