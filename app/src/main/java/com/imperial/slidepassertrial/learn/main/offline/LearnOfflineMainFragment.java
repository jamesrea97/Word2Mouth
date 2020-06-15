package com.imperial.slidepassertrial.learn.main.offline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.imperial.slidepassertrial.R;
import com.imperial.slidepassertrial.learn.main.LearnActivityMain;
import com.imperial.slidepassertrial.learn.main.offline.p2p.WifiDirectCourseSharingActivity;
import com.imperial.slidepassertrial.shared.ArrayAdapterCourseItems;
import com.imperial.slidepassertrial.shared.CourseItem;
import com.imperial.slidepassertrial.shared.FileHandler;
import com.imperial.slidepassertrial.shared.FileReader;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LearnOfflineMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LearnOfflineMainFragment extends Fragment {

    // Permission
    private int READ_WRITE_PERMISSION = 1;

    private boolean hasReadWriteStorageAccess = false;

    // Button View
    private ImageButton learn;
    private ImageButton share;
    private ImageButton receive;
    private ImageButton delete;

    // ListView
    private ListView courseList;
    private ArrayAdapterCourseItems adapter;

    // Model
    private ArrayList<CourseItem> localCourses = null;
    private CourseItem courseItem = null;

    private boolean selectedCourse = false;
    private String courseName = null;

    public LearnOfflineMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LearnOfflineMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LearnOfflineMainFragment newInstance() {
        LearnOfflineMainFragment fragment = new LearnOfflineMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_learn_offline_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configureLearnButton();
        configureShareButton();
        configureReceiveButton();
        configureDeleteButton();
        configureListView();

        getPermissions();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Permissions

    private void getPermissions() {
        if (!(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(getView().getContext(), "Please allow access to Storage", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, READ_WRITE_PERMISSION);
        } else{
            hasReadWriteStorageAccess = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_WRITE_PERMISSION) {
            if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[1].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasReadWriteStorageAccess = true;
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    // UI
    private void configureListView() {
        courseList = (ListView) getView().findViewById(R.id.list_view_course_offline);
        getPermissions();
        if (hasReadWriteStorageAccess) {
            localCourses = retrieveLocalCourses();

            if (localCourses.size() > 0) {
                adapter = new ArrayAdapterCourseItems(getContext(), R.layout.list_item, localCourses);

                courseList.setAdapter(adapter);
                courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 0; i < adapter.getCount(); i++) {
                            View item = courseList.getChildAt(i);
                            if (item != null) {
                                item.setBackgroundColor(Color.WHITE);
                            }
                        }

                        if (selectedCourse) {
                            view.setBackgroundColor(Color.WHITE);
                            selectedCourse = false;
                            if (learn != null) {
                                learn.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                            }
                            if (share != null) {
                                share.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                            }
                            if (delete != null) {
                                delete.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                            }
                            if(receive != null) {
                                receive.setColorFilter(null);
                            }
                            courseName = null;

                        } else {
                            selectedCourse = true;
                            view.setBackgroundColor(Color.LTGRAY);
                            if (learn != null) {
                                learn.setColorFilter(null);
                            }
                            if (share != null) {
                                share.setColorFilter(null);
                            }
                            if (delete != null) {
                                delete.setColorFilter(null);
                            }
                            if(receive != null) {
                                receive.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                            }

                            courseItem = (CourseItem) parent.getAdapter().getItem(position);
                            courseName = courseItem.getCourseName();
                        }
                    }
                });
            }

        }

    }

    private ArrayList<CourseItem> retrieveLocalCourses() {
        ArrayList<CourseItem> courseItems = new ArrayList<>();

        File directory = getView().getContext().getExternalFilesDir(null);

        File[] courses = directory.listFiles();

        for (File f : courses) {
            String courseName = FileReader.readTextFromFile(f.getPath()+ "/meta/title.txt");

            CourseItem courseItem= new CourseItem(courseName, f.getPath());
            courseItems.add(courseItem);
        }
        return courseItems;
    }

    // Receive
    private void configureReceiveButton() {
        receive = getView().findViewById(R.id.receive_button);

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasReadWriteStorageAccess) {
                    if (!selectedCourse) {
                        Toast.makeText(getContext(), "Receive Button", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getView().getContext(), "Need Storage Permission For Receive Button", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    // Share
    private void configureShareButton() {
        share = getView().findViewById(R.id.share_button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasReadWriteStorageAccess) {
                        if (selectedCourse) {
                            Intent sharingIntent = new Intent(getView().getContext(), WifiDirectCourseSharingActivity.class);
                            sharingIntent.putExtra("coursePath", getView().getContext().getExternalFilesDir(null) + "/" + courseName);
                            sharingIntent.putExtra("courseName", courseName);
                            startActivity(sharingIntent);
                        }
                } else {
                    Toast.makeText(getView().getContext(), "Need Storage Permission For Share Button", Toast.LENGTH_SHORT).show();
                }

            }
        });


        if (share != null) {
            share.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
        }
    }


    // Learn
    private void configureLearnButton() {
        learn = getView().findViewById(R.id.learn_button);
        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasReadWriteStorageAccess) {
                    if (selectedCourse) {
                        if (courseName != null) {
                            Intent learnIntent = new Intent(getView().getContext(), SlideLearningActivity.class);
                            learnIntent.putExtra("course directory path",  getView().getContext().getExternalFilesDir(null) + "/" + courseName);
                            startActivity(learnIntent);
                            startActivity(learnIntent);
                        }
                    }
                } else {
                    Toast.makeText(getView().getContext(), "Need Storage Permission For Learn Button", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (learn != null) {
            learn.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
        }
    }

    // Delete
    private void configureDeleteButton() {
        delete = getView().findViewById(R.id.delete_button);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasReadWriteStorageAccess) {
                    if (selectedCourse) {
                        if (courseName != null) {
                            File courseFile = new File(getView().getContext().getExternalFilesDir(null) + "/" + courseName);
                            if (courseFile.exists()) {
                                Toast.makeText(getView().getContext(), "Deleting Course: " + courseName, Toast.LENGTH_SHORT).show();
                                FileHandler.deleteRecursive(courseFile);
                                adapter.remove(courseItem);
                                adapter.notifyDataSetChanged();
                                adapter.notifyDataSetInvalidated();
                            }
                        }
                    }
                    selectedCourse = false;
                    learn.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                    share.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                    delete.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                } else {
                    Toast.makeText(getView().getContext(), "Need Storage Permission For Learn Button", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (delete != null) {
            delete.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
