package com.example.compressfile;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZipper {
    private static final int BUFFER_SIZE = 8192; //2048
    private static String TAG = FileZipper.class.getName().toString();
    private static String parentPath = "";

    public static boolean zip(String sourcePath, String destinationPath, String destinationFileName, Boolean includeParentFolder) {
        new File(destinationPath).mkdirs();
        FileOutputStream fileOutputStream;
        ZipOutputStream zipOutputStream = null;
        try {
            if (!destinationPath.endsWith("/")) destinationPath += "/";
            String destination = destinationPath + destinationFileName;
            File file = new File(destination);

            if (!file.exists()) file.createNewFile();

            fileOutputStream = new FileOutputStream(file);
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

            if (includeParentFolder)
                parentPath = new File(sourcePath).getParent() + "/";
            else
                parentPath = sourcePath;
            zipFile(zipOutputStream, sourcePath);
        }catch(IOException ioe){
                Log.d(TAG, ioe.getMessage());
                return false;
            }finally{
                if (zipOutputStream != null)
                    try{
                        zipOutputStream.close();
                    }catch (IOException e){

                    }
            }
            return true;
        }
        private static void zipFile(ZipOutputStream zipOutputStream, String sourcePath) throws IOException{
        java.io.File files = new java.io.File(sourcePath);
        java.io.File[] fileList = files.listFiles();

        String entryPath = "";
        BufferedInputStream input;
        for (java.io.File file : fileList){
            if (file.isDirectory()){
                zipFile(zipOutputStream, file.getPath());
            }else {
                byte data[] = new byte[BUFFER_SIZE];
                FileInputStream fileInputStream = new FileInputStream(file.getPath());
                input = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
                entryPath = file.getAbsolutePath().replace(parentPath,"");

                String name;
                ZipEntry entry = new ZipEntry(entryPath);
                zipOutputStream.putNextEntry(entry);

                int count;
                while ((count = input.read(data, 0, BUFFER_SIZE)) != -1){
                    zipOutputStream.write(data, 0, count);
                }
                input.close();
            }
        }
    }
    private void zipSubFolder (ZipOutputStream out, File folder, int basePathLength) throws IOException{
        File[] folderList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : folderList){
            if (file.isDirectory()){
                zipSubFolder(out, file, basePathLength);
            }else{
                byte data[] = new byte[BUFFER_SIZE];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath.substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data,0,BUFFER_SIZE)) != -1 ){
                    out.write(data,0,count);
                }
            }origin.close();
        }
    }
}

