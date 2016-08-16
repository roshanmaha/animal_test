package roshan.com.animals_test;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Dialog;

import java.io.File;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.os.Handler;
import android.os.Message;


import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<Species>> expandableListDetail;
    private Uri fileUri;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static MainActivity ActivityContext = null;
    public int animalPosition = 0;
    public int speciesPosition = 0;
    TextView txtSpecies = null;
    View hideChildView = null;
    View hideParentView = null;
    int imgId=0;


    //For uploading recorded video file
    private String SERVER_URL = "http://webavenue.com.au/extras/uploadToServer.php";
    String speciesName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = AnimalListDataMap.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new AnimalListAdapter(this, expandableListTitle, expandableListDetail) {
        };
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                /*Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();
*/
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        final int groupPosition, final int childPosition, long id) {
                final String name = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).getName();
                final String desc = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).getDesc();
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + name
                                + " -> "
                                + desc, Toast.LENGTH_SHORT
                ).show();
                animalPosition = groupPosition;
                speciesPosition = childPosition;
                speciesName = expandableListDetail.get(expandableListTitle.get(animalPosition)).get(speciesPosition).getName();
                txtSpecies = (TextView) v.findViewById(R.id.expandedListItem);
                hideChildView = v;
                hideParentView = parent.findViewById(R.id.listTitle);
                imgId = expandableListDetail.get(expandableListTitle.get(animalPosition)).get(speciesPosition).getImageId();



                // Create dialog describing the animal species
                final Dialog dialog = new Dialog(MainActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.dialog);
                // Set animal name
                dialog.setTitle(name);
                animalPosition = groupPosition;
                speciesPosition = childPosition;

                // set values for custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.txtDialog);
                text.setText(desc);
                ImageView image = (ImageView) dialog.findViewById(R.id.imgDialog);
                image.setImageResource(imgId);
                dialog.show();

                //Cancel button
                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //Record button
                Button btnRecord = (Button) dialog.findViewById(R.id.btnRecord);
                btnRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create new Intentwith with Standard Intent action that can be
                        // sent to have the camera application capture an video and return it.
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                        // create a file to save the video
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO, groupPosition, childPosition);

                        // set the video file name
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                        // set the video image quality to high
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

                        // start the Video Capture Inten
                        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                        dialog.dismiss();


                    }
                });


                return true;
            }
        });


    }

//Handler to update upload progress for species
    Handler handlerUploadProgress = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String uploadProgress = "";
            uploadProgress = msg.getData().getString("uploadMsg");
            txtSpecies.setText(speciesName + "    " + uploadProgress);
        }
    };

    //Handler to update 100% upload success and hide animal type if videos of all the species are uploaded

    Handler handlerHideView = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int no_of_species = 0;
            String uploadProgress = "";
            uploadProgress = msg.getData().getString("uploadMsg");
            txtSpecies.setText(speciesName + "    " + uploadProgress);
            for (int i = 0; i < expandableListAdapter.getChildrenCount(animalPosition); i++) {

                if (expandableListDetail.get(expandableListTitle.get(animalPosition)).get(i).getVideoUploadFlag() == true) {
                    no_of_species++;
                }
            }
            if (no_of_species == expandableListAdapter.getChildrenCount(animalPosition)) {
                // Create dialog describing the animal species
                final Dialog dialog = new Dialog(MainActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.application_close_dialog);

                TextView text = (TextView) dialog.findViewById(R.id.txtCongrats);
                text.setText("Congratulations, you have uploaded all videos for " + expandableListTitle.get(animalPosition).toString());
                dialog.show();
                //Cancel button
                expandableListTitle.remove(animalPosition);
                ((BaseExpandableListAdapter) expandableListAdapter).notifyDataSetChanged();

                Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       finish();
                    }
                });

                //Record button
                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();


                    }
                });

            }


        }
    };




    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        // After camera screen this code will executed
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {


                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:" + data.getData(), Toast.LENGTH_LONG).show();
                final Dialog dialogCongrats = new Dialog(MainActivity.this);
                // Include dialog.xml file
                dialogCongrats.setContentView(R.layout.congratulations_dialog);
                // Set dialog title
                dialogCongrats.setTitle("Success");

                // set values for custom dialog components - text, image and button
                TextView text = (TextView) dialogCongrats.findViewById(R.id.txtCongratulations);
                text.setText("Congratulations!! Video Saved Successfully");
                dialogCongrats.show();

                //Go back button
                Button btnGoBack = (Button) dialogCongrats.findViewById(R.id.btnGoBack);
                btnGoBack.setText("Go Back");
                btnGoBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri selectedFileUri = data.getData();
                        final String selectedFilePath = selectedFileUri.getPath();

                        new Thread(new Runnable() {
                            public void run() {
                                uploadFile(dialogCongrats, selectedFilePath, animalPosition, speciesPosition);
                            }
                        }).start();


                    }
                });

            } else if (resultCode == RESULT_CANCELED) {


                // User cancelled the video record
                Toast.makeText(this, "You cancelled the video recording.",
                        Toast.LENGTH_LONG).show();

            } else {

                // Video record failed, inform user
                Toast.makeText(this, "Video record failed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    //Upload Video to server
    public int uploadFile(Dialog dialogCongrats, final String selectedFilePath, final int groupPosition, final int childPosition) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);
        long totalSize = selectedFile.length();


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialogCongrats.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Source File Doesn't Exist", Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {
                dialogCongrats.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = handlerUploadProgress.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("uploadMsg", "Uploading Video,please wait");
                        msg.setData(bundle);
                        handlerUploadProgress.sendMessage(msg);
                    }
                });
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                int totalByteRead = 0;
                final String strUploadProgress = "";


                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    totalByteRead += bytesRead;
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    final int uploadProgress = (int) ((totalByteRead / (float) totalSize) * 100);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();

                //response code of 200 indicates the server status OK

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = handlerHideView.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putInt("groupPosition", groupPosition);
                            bundle.putInt("childPosition", childPosition);
                            bundle.putString("speciesName", expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).getName());
                            bundle.putString("uploadMsg", "100% Upload Complete");
                            expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).setVideoUploadFlag();
                            msg.setData(bundle);
                            handlerHideView.sendMessage(msg);
                            Toast.makeText(MainActivity.this, "Video Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://webavenue.com.au/extras/uploads/" + fileName, Toast.LENGTH_LONG).show();

                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            return serverResponseCode;
        }

    }

    /**
     * Create a file Uri for saving video
     */
    private static Uri getOutputMediaFileUri(int type, int groupPosition, int childPosition) {

        return Uri.fromFile(getOutputMediaFile(type, groupPosition, childPosition));
    }

    /**
     * Create a File for saving video
     */
    private static File getOutputMediaFile(int type, int groupPosition, int childPosition) {

        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "AnimalVideos");


        // Create the storage directory(AnimalVideos) if it does not exist
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {

                Toast.makeText(ActivityContext, "Failed to create directory AnimalVideos.", Toast.LENGTH_LONG).show();

                Log.d("AnimalVideos", "Failed to create directory AnimalVideos.");
                return null;
            }
        }


        // Create a video file name

       java.util.Date date = new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;

        if (type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "vid_" + groupPosition + "_" + childPosition + "_" + timeStamp + ".mp4");


        } else {
            return null;
        }

        return mediaFile;
    }




}