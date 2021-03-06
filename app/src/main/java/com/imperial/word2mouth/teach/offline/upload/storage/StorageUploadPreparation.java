package com.imperial.word2mouth.teach.offline.upload.storage;

import android.content.Context;

import com.imperial.word2mouth.learn.main.offline.share.bluetooth.FileZip;
import com.imperial.word2mouth.shared.CourseItem;
import com.imperial.word2mouth.shared.DirectoryConstants;

import net.lingala.zip4j.ZipFile;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StorageUploadPreparation {


    private final String coursePath;
    private final String zipCoursePath;
    private final Context context;


    public StorageUploadPreparation(String coursePath, Context context) {
        this.coursePath = coursePath;
        this.context = context;
        zipCoursePath = context.getExternalFilesDir(null).getPath() + DirectoryConstants.zip + "zipLecture.zip";

        File f = new File(zipCoursePath);

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public byte[] getZippedCourse() {

        if (FileZip.zipFileAtPath(coursePath, zipCoursePath)) {
            byte[] zipArray = FileZip.convertFileToByteArray(zipCoursePath);

            File f = new File(zipCoursePath);
            if (f.exists()) {
                f.delete();
            }
            return zipArray;
        } else {
            return null;
        }
    }


}
