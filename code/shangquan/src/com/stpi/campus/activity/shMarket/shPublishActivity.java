package com.stpi.campus.activity.shMarket;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import com.stpi.campus.R;
import com.stpi.campus.items.user.PostResult;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.ImageFileUtils;
import com.stpi.campus.util.JsonGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 2014/10/26.
 */
public class shPublishActivity extends Activity {

    private static File dir = null;
    private final String filename = "temp.jpg";
    private ImageView imageView;
    private EditText titleView;
    private EditText descriptionView;
    private EditText contactView;
    private ProgressBar progressBar;
    private Boolean pictureLoaded = false;
    private Uri pictureUri = null;
    private Bitmap bitmap = null;
    private File tempFile = null;
    private PublishTask publishTask = null;

    //camera
    private LinearLayout takePhotoBtn;
    private LinearLayout choosePhotoBtn;
    private Button uploadPictureBtn;
    private View cameraOptionLayout = null;
    private AlertDialog cameraDialog = null;

    static {
        String saveDir = Environment.getExternalStorageDirectory() + "/temple";
        dir = new File(saveDir);
        if (!dir.exists())
            dir.mkdir();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sh_publish);

        uploadPictureBtn = (Button) findViewById(R.id.upload_picture);
        imageView = (ImageView) findViewById(R.id.picture);
        titleView = (EditText) findViewById(R.id.title);
        descriptionView = (EditText) findViewById(R.id.description);
        contactView = (EditText) findViewById(R.id.contact);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        initCameraOptions();

        uploadPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraOptionDialog();
            }
        });

    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            pictureUri = data.getData();
            startPhotoZoom(pictureUri, 200);
        } else if (requestCode == 2) {
            startPhotoZoom(pictureUri, 200);
        } else if (requestCode == 3) {
            if (data != null) {
                this.setPictureToPortraitButton(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initCameraOptions() {
        LayoutInflater inflater = getLayoutInflater();
        cameraOptionLayout = inflater.inflate(R.layout.dialog_camera_option,
                (ViewGroup) findViewById(R.id.camera_option_dialog));

        choosePhotoBtn = (LinearLayout) cameraOptionLayout.findViewById(R.id.choosePhoto);
        takePhotoBtn = (LinearLayout) cameraOptionLayout.findViewById(R.id.takePhoto);

        choosePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                cameraDialog.hide();
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempFile = new File(dir, filename);
                tempFile.delete();
                try {
                    tempFile.createNewFile();
                    pictureUri = Uri.fromFile(tempFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    startActivityForResult(intent, 2);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),
                            "拍照打开失败", Toast.LENGTH_SHORT)
                            .show();
                } finally {
                    cameraDialog.hide();
                }
            }
        });

    }

    private void showCameraOptionDialog() {

        if(cameraDialog == null)
            cameraDialog = new AlertDialog.Builder(shPublishActivity.this)
                .setView(cameraOptionLayout)
                .show();
        else
            cameraDialog.show();

    }

    private void setPictureToPortraitButton(Intent pictureData) {
        Bundle bundle = pictureData.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            this.bitmap = photo;
            this.imageView.setImageBitmap(photo);
            pictureLoaded = true;
        }
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");

        // aspectX aspectY
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX outputY
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

//        this.startActivity(intent);
        this.startActivityForResult(intent, 3);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.actionbar_cancel_icon);
        menu.add(0, 1, 0, "完成").setIcon(R.drawable.actionbar_confirm_icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 1:
                this.attemptSubmit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptSubmit() {
        if (publishTask != null)
            return;
        String userId = UserInfo.userId;
        int circleId = UserInfo.circleId == 0 ? 2 : UserInfo.circleId;
        String title = titleView.getText().toString();
        String description = descriptionView.getText().toString();
        String contact = contactView.getText().toString();
        if(title.equals("")){
            Toast.makeText(this, "请完善标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if(description.equals("")){
            Toast.makeText(this, "请完善宝贝描述", Toast.LENGTH_SHORT).show();
            return;
        }
        if(contact.equals("")){
            Toast.makeText(this, "请完善联系方式", Toast.LENGTH_SHORT).show();
            return;
        }
        publishTask = new PublishTask();
        publishTask.execute(userId, String.valueOf(circleId), title, description, "1", contact);
        progressBar.setVisibility(View.VISIBLE);
    }

    public class PublishTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String url = shPublishActivity.this.getString(R.string.add_shlv_info_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", args[0]));
            params.add(new BasicNameValuePair("circleId", args[1]));
            params.add(new BasicNameValuePair("title", args[2]));
            params.add(new BasicNameValuePair("description", args[3]));
            params.add(new BasicNameValuePair("type", args[4]));
            params.add(new BasicNameValuePair("linkWay", args[5]));

            if (pictureLoaded)
                params.add(new BasicNameValuePair("picture", ImageFileUtils.parse(bitmap)));
            PostResult res = new JsonGetter<PostResult>().getFromUrl(url, PostResult.class, params);
            if (res == null)
                return "error";
            if (res.getState().equals("success"))
                return "ok";
            else
                return "error";
        }

        @Override
        protected void onPostExecute(final String retCode) {
            publishTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println(retCode);

            if (retCode.equals("error"))
                Toast.makeText(shPublishActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(shPublishActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                shPublishActivity.this.finish();
            }
        }

        @Override
        protected void onCancelled() {
            publishTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


}