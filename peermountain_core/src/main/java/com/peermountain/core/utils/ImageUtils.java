package com.peermountain.core.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Galeen on 10/19/2017.
 */

public class ImageUtils {

    public static Bitmap decodeSampledBitmapFromFile(String path,
                                                     int reqWidth, int reqHeight) { // BEST QUALITY MATCH
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }

        int expectedWidth = width / inSampleSize;
        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }
        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }


    public Bitmap getBitmapFromURI(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            File f_image = new File(cursor.getString(columnIndex));
            cursor.close();
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            return BitmapFactory.decodeFile(f_image.getAbsolutePath(), o2);
        }
        return null;
    }

    public static int getImageRotation(String imagePath) {
        return getImageRotation(new File(imagePath));
    }

    public static int getImageRotation(File imageFile) {
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static boolean saveBitmap(String filename, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String path = getPathForV19AndUp(context, contentUri);
                if (path == null) {
                    path = getPathForPreV19(context, contentUri);
                }
                return path;
            } else {
                return getPathForPreV19(context, contentUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles pre V19 uri's
     */
    public static String getPathForPreV19(Context context, Uri contentUri) {
        String res = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Handles V19 and up uri's
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathForV19AndUp(Context context, Uri contentUri) {
        String filePath = null;
        try {
            String wholeID = DocumentsContract.getDocumentId(contentUri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static void saveImageAsync(File file, Bitmap bitmap, SaveImageEvents callback){
        new SaveBitmapToFile(file, bitmap, callback).execute();
    }
    public static void saveImageAsyncParallel(File file, Bitmap bitmap, SaveImageEvents callback){
        new SaveBitmapToFile(file, bitmap, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private static class SaveBitmapToFile extends AsyncTask<Void,Void,Boolean>{
        private File file;
        private Bitmap bitmap;
        private SaveImageEvents callback;

        public SaveBitmapToFile(File file, Bitmap bitmap, SaveImageEvents callback) {
            this.file = file;
            this.bitmap = bitmap;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return saveBitmap(file.getPath(),bitmap);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(callback!=null) callback.onFinish(aBoolean);
        }
    }

    public interface SaveImageEvents{
        void onFinish(boolean isSuccess);
    }

    public static void rotateAndResizeImageAsync(File fileSrc, File fileDest, int reqWidth, int reqHeight, boolean shouldDeleteSource, ConvertImageTask.ImageCompressorListener imageCompressorListener) {
        rotateAndResizeImageAsync(fileSrc, fileDest, reqWidth, reqHeight, shouldDeleteSource, true, imageCompressorListener);
//        new ConvertImageTask(fileSrc,fileDest,reqWidth,reqHeight,shouldDeleteSource,imageCompressorListener).execute();
    }

    public static void rotateAndResizeImageAsync(File fileSrc, File fileDest, int reqWidth, int reqHeight, boolean shouldDeleteSource, boolean shouldRotate, ConvertImageTask.ImageCompressorListener imageCompressorListener) {
        ConvertImageTask task = new ConvertImageTask(fileSrc, fileDest, reqWidth, reqHeight, shouldDeleteSource, imageCompressorListener);
        task.setRotation(shouldRotate);
        task.execute();
    }



    public static class ConvertImageTask extends AsyncTask<Void, ConvertImageTask.Response, ConvertImageTask.Response> {

        private ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private File file, newFile;
        private int reqWidth = 1280, reqHeight = 700;

        private int CompressionRatio = 80; //You can change it by what ever ratio you want. in 0 to 100.

        private boolean shouldRotate = true;
        private boolean shouldDeleteSource = false;

        private ImageCompressorListener imageCompressorListener;

        public ConvertImageTask(File file, File newFile, ImageCompressorListener imageCompressorListener) {
            this.file = file;
            this.newFile = newFile;
            this.imageCompressorListener = imageCompressorListener;
        }

        public ConvertImageTask(File file, File newFile, int reqWidth, int reqHeight, boolean shouldDeleteSource, ImageCompressorListener imageCompressorListener) {
            this.file = file;
            this.newFile = newFile;
            this.reqWidth = reqWidth;
            this.reqHeight = reqHeight;
            this.shouldDeleteSource = shouldDeleteSource;
            this.imageCompressorListener = imageCompressorListener;
        }

        public void setRotation(boolean isRotate) {
            shouldRotate = isRotate;
        }


        public void setCompressionRatio(int Ratio) {
            CompressionRatio = Ratio;
        }


        public void setImageCompressorListener(ImageCompressorListener imageCompressorListener) {
            this.imageCompressorListener = imageCompressorListener;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Response doInBackground(Void... params) {
            try {
                //*****Code for Orientation
                Matrix matrix = new Matrix();
                if (shouldRotate) {
                    setRotation(matrix);
                } else {
                    matrix.postRotate(0);
                }

                try {
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), option);

                    int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                    Log.e("ImageSize", "" + file_size);

                    int scale = getScale(file_size);
                    Log.e("Scale", "Finaly Scaling with " + scale);

                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    Bitmap pickImg = BitmapFactory.decodeFile(file.getAbsolutePath(), o2);
                    pickImg = resizeBitmap(matrix, pickImg);
                    pickImg.compress(Bitmap.CompressFormat.JPEG, CompressionRatio, baos);

                    File fileToSaveImage = newFile == null ? file : newFile;
                    saveBitmap(fileToSaveImage.getPath(), pickImg);
                    if (shouldDeleteSource && newFile != null) file.delete();
                    return new Response(pickImg, Uri.fromFile(fileToSaveImage));
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }

        }

        private Bitmap resizeBitmap(Matrix matrix, Bitmap pickImg) {
            if (pickImg.getWidth() > reqWidth || pickImg.getHeight() > reqHeight) {
                int width = pickImg.getWidth();
                int height = pickImg.getHeight();

                while (width > reqWidth || height > reqHeight) {
                    width = (width * 90) / 100;
                    height = (height * 90) / 100;
                }

                pickImg = Bitmap.createScaledBitmap(pickImg, width, height, true);
            }
//            else {
            pickImg = Bitmap.createBitmap(pickImg, 0, 0, pickImg.getWidth(), pickImg.getHeight(), matrix, true); // rotating bitmap
//            }
            return pickImg;
        }

        private int getScale(int file_size) {
            int scale = 1;
            if (file_size < 512) {
                Log.e("image size is good", "image size is less");
            } else if (file_size < 1024) {
                Log.e("image size is 1 mb", "image size is heavy");
                scale = 2;
            } else if (file_size < 1536) {
                Log.e("image size is 1.5 mb", "image size is heavy");
                scale = 2;
            } else if (file_size < 2048) {
                Log.e("image size is 2 mb", "image size is heavy");
                scale = 4;
            } else {
                Log.e("image size > 2 mb", "image size is heavy");
                scale = 4;
            }
            return scale;
        }

        @Override
        protected void onPostExecute(Response result) {
            super.onPostExecute(result);
            if (result != null) {
                if (imageCompressorListener != null) {
                    imageCompressorListener.onImageCompressed(result.bitmap, result.uri);
                }
            } else {
                if (imageCompressorListener != null) {
                    imageCompressorListener.onError();
                }
            }
        }

        private void setRotation(Matrix matrix) throws IOException {
            ExifInterface exif1 = new ExifInterface(file.getAbsolutePath());
            int orientation = exif1.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            } else {
                matrix.postRotate(0);
            }
        }

        class Response {
            Bitmap bitmap;
            Uri uri;

            Response(Bitmap bitmap, Uri uri) {
                this.bitmap = bitmap;
                this.uri = uri;
            }
        }

        public interface ImageCompressorListener {
            void onImageCompressed(Bitmap bitmap, Uri uri);

            void onError();
        }
    }
}
