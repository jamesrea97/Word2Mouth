package com.imperial.word2mouth.shared.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.imperial.word2mouth.R;
import com.imperial.word2mouth.shared.CourseItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ArrayAdapterCourseOnline extends ArrayAdapter<CourseItem> {

    private static ArrayList<CourseItem> courseItems = new ArrayList<>();
    private final Context context;

    private int layout;

    private ViewHolder holder = new ViewHolder();
    private Categories categories = new Categories();
    private Languages languages =new Languages();

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ArrayMap<String, Uri> soundThumbnails = new ArrayMap<>();
    private ArrayMap<String, Uri> photoThumbnails = new ArrayMap<>();


    private ArrayList<CourseItem> queryCourses;




    public ArrayAdapterCourseOnline(@NonNull Context context, int resource, @NonNull ArrayList<CourseItem> objects) {
        super(context, resource, objects);

        layout = resource;
        this.context = context;
        queryCourses = objects;

        courseItems.clear();
        courseItems.addAll(queryCourses);
    }


    public void loadThumbnails() {

        StorageReference teacherRef = FirebaseStorage.getInstance().getReference("/content/");

        soundThumbnails.clear();
        photoThumbnails.clear();

        for (CourseItem course : courseItems) {
            StorageReference imageRef = teacherRef.child(course.getCourseOnlineIdentification() + "/Photo Thumbnail");
            StorageReference soundRef = teacherRef.child(course.getCourseOnlineIdentification()  + "/Sound Thumbnail");

            soundRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    soundThumbnails.put(course.getCourseOnlineIdentification(), uri);
                    notifyDataSetInvalidated();
                    notifyDataSetChanged();
                }
            });

            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    photoThumbnails.put(course.getCourseOnlineIdentification(), uri);
                    notifyDataSetInvalidated();
                    notifyDataSetChanged();
                }
            });

        }
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layout, parent, false);
            holder = new ViewHolder();

            holder.audio = convertView.findViewById(R.id.list_audio_button);
            holder.thumbnail = convertView.findViewById(R.id.list_item_thumbnail);
            holder.title = convertView.findViewById(R.id.list_item_text);
            holder.language = convertView.findViewById(R.id.flag);
            holder.category = convertView.findViewById(R.id.category);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (courseItems.get(position).getCategory() != null) {
            holder.category.setImageResource(categories.categoryIconMap.get(courseItems.get(position).getCategory()));
        }

        if (courseItems.get(position).getLanguage() != null) {
            holder.language.setImageResource(languages.languageIconMap.get(courseItems.get(position).getLanguage()));
        }

        // Retrieve image

        if (photoThumbnails.get(queryCourses.get(position).getCourseOnlineIdentification()) == null){
            Glide.with(getContext()).load(R.drawable.ic_picture).into(holder.thumbnail);

        } else {
            Glide.with(getContext()).load(photoThumbnails.get(queryCourses.get(position).getCourseOnlineIdentification())).into(holder.thumbnail);

        }


        holder.audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer player;
                Uri audioUri = soundThumbnails.get(queryCourses.get(position).getCourseOnlineIdentification());
                if (audioUri != null) {
                    player = MediaPlayer.create(getContext(), audioUri);

                    if (player != null) {
                        player.start();
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                holder.audio.setColorFilter(null);
                            }
                        });
                    }

                } else {
                    Toast.makeText(getContext(), R.string.noAudioFile, Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Title Course
        holder.title.setText(queryCourses.get(position).getCourseName());


        return convertView;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public void filter(String query) {
        query = query.toLowerCase(Locale.getDefault());
        queryCourses.clear();

        if (query.length() == 0) {
            queryCourses.addAll(courseItems);
        } else {
            for (CourseItem wp : courseItems) {
                if (wp.getCourseName().toLowerCase(Locale.getDefault()).contains(query)) {
                    queryCourses.add(wp);
                }
            }
        }
        notifyDataSetChanged();

    }


    public class ViewHolder {
        ImageView thumbnail;
        TextView title;

        ImageView language;
        ImageView category;

        ImageButton audio;
        public String getCourseName() {
            return title.getText().toString();
        }
    }



    public class Categories {

        public final ArrayList<String> categories = new ArrayList<>(Arrays.asList("Health", "Mechanical", "Agriculture", "Academic"));




        public final HashMap<String, Integer> categoryIconMap= new HashMap<String, Integer>(){{
            put("Health", R.drawable.category_health);
            put("Mechanical", R.drawable.category_mechanical);
            put("Agriculture", R.drawable.category_agriculture);
            put("Academic", R.drawable.category_academic);
        }};

        public String get(int position) {
            return categories.get(position);
        }

    }



    public class Languages {


        public  ArrayList<String> languages = new ArrayList<>(Arrays.asList("English", "Francais", "Maures", "Swahili"));


        public  HashMap<String, Integer> languageIconMap = new HashMap<String, Integer>(){{
            put("English", R.drawable.flag_uk);
            put("Francais", R.drawable.flag_france);
            put("Maures", R.drawable.flag_burkina);
            put("Swahili", R.drawable.flag_kenya);
        }};

        public  String get(int position) {
            return languages.get(position);
        }

    }

}
