package com.stpi.campus.activity.personSetting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.stpi.campus.entity.schoolRegion.SchoolRegionHelper;
import com.stpi.campus.entity.tag.TagHelper;
import com.stpi.campus.items.user.PostResult;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.entity.schoolRegion.RegionDetail;
import com.stpi.campus.entity.schoolRegion.SchoolInfo;
import com.stpi.campus.util.HttpListGetter;
import com.stpi.campus.util.ImageFileUtils;
import com.stpi.campus.util.JsonGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 2014/11/19.
 */
public class ModifyPersonSettingActivity extends Activity {

    private Button editBtn = null;
    private EditText tagname = null;
    private ViewPager viewPager = null;
    private LayoutInflater mInflater = null;
    private Spinner schoolSpinner = null;
    private Spinner regionSpinner = null;
    private TextView tagListView = null;
    private ImageView headImageView = null;
    private ProgressBar progressBar = null;
    private EditText nameView = null;

//    camera
    private LinearLayout takePhotoBtn;
    private LinearLayout choosePhotoBtn;
    private ImageView uploadPictureBtn;
    private View cameraOptionLayout = null;
    private AlertDialog cameraDialog = null;
    private File tempFile = null;
    private static File dir = null;
    private final String filename = "temp.jpg";

    private ModifyInfoTask modifyInfoTask = null;

    private String newTag = "";
    private List<View> views = new ArrayList<View>();
    private List<String> tagList = new ArrayList<String>();

    //    school region
    private List<SchoolInfo> schoolInfoList = new ArrayList<SchoolInfo>();
    private List<RegionDetail> recordedRegionList = new ArrayList<RegionDetail>();
    private String recordedSchoolId;
    private String recordedRegionId;
    private List<String> schoolList = new ArrayList<String>();
    private List<String> regionList = new ArrayList<String>();

    //    head picture
    private Uri pictureUri;
    private Bitmap bitmap;
    private Boolean pictureLoaded = false;

    static {
        String saveDir = Environment.getExternalStorageDirectory() + "/temple";
        dir = new File(saveDir);
        if (!dir.exists())
            dir.mkdir();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_modify);

        initIcons();
        initLoadIcons();
        initEvents();
    }

    private void initIcons() {
        editBtn = (Button) findViewById(R.id.editTag);
        viewPager = (ViewPager) findViewById(R.id.tagPager);
        schoolSpinner = (Spinner) findViewById(R.id.schoolSpinner);
        regionSpinner = (Spinner) findViewById(R.id.regionSpinner);
        tagListView = (TextView) findViewById(R.id.tagList);
        headImageView = (ImageView) findViewById(R.id.headImage);
        nameView = (EditText) findViewById(R.id.nickname);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        uploadPictureBtn = (ImageView) findViewById(R.id.upload_picture);
    }

    private void initLoadIcons() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory().cacheOnDisc()
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return ImageFileUtils.getRoundCornerImage(bitmap,
                                R.dimen.size25);
                    }
                })
                .build();
        imageLoader.displayImage(getString(R.string.person_picture_head) + UserInfo.userPic,
                headImageView, options);
        nameView.setText(UserInfo.userName);

        String tagList = "";
        for(String tag: UserInfo.tagList) {
            tagList += "#" + tag;
        }

        tagListView.setText(tagList);
    }

    private void initEvents() {

        initCameraOptions();

        uploadPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraOptionDialog();
            }
        });

        mInflater = getLayoutInflater();

//        headImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, 1);
//            }
//        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTagDialog();
            }
        });

        new LoadSchoolTask().execute();
        new LoadTagsTask().execute();
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
            cameraDialog = new AlertDialog.Builder(ModifyPersonSettingActivity.this)
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
            this.headImageView.setImageBitmap(photo);
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

    private void attemptAddTag(String tagName) {
        if (tagListView.getText().toString().contains(tagName)) {
            tagListView.setText(tagListView.getText().toString().replace("#" + tagName, ""));
//            Toast.makeText(ModifyPersonSettingActivity.this, "已有该标签",
//                    Toast.LENGTH_SHORT).show();
        }
        else
            tagListView.setText(tagListView.getText() + "#" + tagName);
    }

    public class MyViewAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.actionbar_back_icon);
        menu.add(0, 1, 0, "完成").setIcon(R.drawable.actionbar_confirm_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 1:
                this.attemptModify();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptModify() {

        if (modifyInfoTask != null)
            return;

        progressBar.setVisibility(View.VISIBLE);
        modifyInfoTask = new ModifyInfoTask();
        String tagContents = tagListView.getText().toString();
        String modifiedUserName = nameView.getText().toString();
        modifyInfoTask.execute(UserInfo.userId, modifiedUserName, recordedSchoolId, recordedRegionId,
                tagContents.length() > 0 ? tagContents.substring(1, tagContents.length()) : "");
    }

    private void showEditTagDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View tag_layout = inflater.inflate(R.layout.dialog_tag,
                (ViewGroup) findViewById(R.id.tag_dialog));

        tagname = (EditText) tag_layout.findViewById(R.id.tagname);

        new AlertDialog.Builder(ModifyPersonSettingActivity.this).setTitle("自定义标签").setView
                (tag_layout)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newTag = tagname.getText().toString();
                        attemptAddTag(newTag);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    class LoadSchoolTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            String url = ModifyPersonSettingActivity.this.getString(R.string.get_school_list);
            SchoolRegionHelper schoolInfoListResult = new HttpListGetter<SchoolRegionHelper>()
                    .getFromUrl(url, SchoolRegionHelper.class);

            if (schoolInfoListResult == null || !schoolInfoListResult.getState().equals("success") ||
                    schoolInfoListResult.getResults() == null)
                return false;

            schoolInfoList = schoolInfoListResult.getResults();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean cond) {

            String[] schoolArray = new String[schoolInfoList.size()];
            schoolList = new ArrayList<String>();
            for (int i = 0; i < schoolInfoList.size(); i++) {
                schoolArray[i] = schoolInfoList.get(i).getSchool().getSchoolName();
                schoolList.add(schoolInfoList.get(i).getSchool().getSchoolName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ModifyPersonSettingActivity.this,
                    android.R.layout.simple_spinner_item, schoolArray
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            schoolSpinner.setAdapter(adapter);
            schoolSpinner.setVisibility(View.VISIBLE);

            schoolSpinner.setSelection(schoolList.indexOf(UserInfo.schoolName));
            schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    recordedRegionList = schoolInfoList.get(position).getRegions();
                    recordedSchoolId = schoolInfoList.get(position).getSchool().getSchoolId();
                    updateRegionSpinner();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        private void updateRegionSpinner() {

            String[] regionArray = new String[recordedRegionList.size()];
            regionList = new ArrayList<String>();
            for (int i = 0; i < recordedRegionList.size(); i++) {
                regionArray[i] = recordedRegionList.get(i).getRegionName();
                regionList.add(recordedRegionList.get(i).getRegionName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ModifyPersonSettingActivity.this,
                    android.R.layout.simple_spinner_item, regionArray
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            regionSpinner.setAdapter(adapter);
            regionSpinner.setVisibility(View.VISIBLE);
            regionSpinner.setSelection(regionList.indexOf(UserInfo.regionName) == -1
                    ? 0: regionList.indexOf(UserInfo.regionName));
            regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    recordedRegionId = recordedRegionList.get(position).getRegionId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    class LoadTagsTask extends AsyncTask<String, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {

            String url = ModifyPersonSettingActivity.this.getString(R.string.get_tag_list);
            TagHelper tagListResult = new HttpListGetter<TagHelper>()
                    .getFromUrl(url, TagHelper.class);

            if (tagListResult == null || !tagListResult.getState().equals("success") ||
                    tagListResult.getResults() == null)
                return null;

            tagList = tagListResult.getResults();

            return tagList;
        }

        @Override
        protected void onPostExecute(List<String> data) {
            int tagNum = data.size();
            for (int nowId = 0; nowId < tagNum; ) {
                View tmpView = mInflater.inflate(R.layout.layout_tag_select, null);
                if (nowId < tagNum) {
                    setTextViewListener(tmpView, R.id.tag1, data.get(nowId));
                    nowId++;
                } else {
                    hideTextView(tmpView, R.id.tag1);
                }
                if (nowId < tagNum) {
                    setTextViewListener(tmpView, R.id.tag2, data.get(nowId));
                    nowId++;
                } else {
                    hideTextView(tmpView, R.id.tag2);
                }
                if (nowId < tagNum) {
                    setTextViewListener(tmpView, R.id.tag3, data.get(nowId));
                    nowId++;
                } else {
                    hideTextView(tmpView, R.id.tag3);
                }
                if (nowId < tagNum) {
                    setTextViewListener(tmpView, R.id.tag4, data.get(nowId));
                    nowId++;
                } else {
                    hideTextView(tmpView, R.id.tag4);
                }
                if (nowId < tagNum) {
                    setTextViewListener(tmpView, R.id.tag5, data.get(nowId));
                    nowId++;
                } else {
                    hideTextView(tmpView, R.id.tag5);
                }
                if (nowId < tagNum) {
                    setTextViewListener(tmpView, R.id.tag6, data.get(nowId));
                    nowId++;
                } else {
                    hideTextView(tmpView, R.id.tag6);
                }
                if (nowId < tagNum) {
                    setTextViewListener(tmpView, R.id.tag7, data.get(nowId));
                    nowId++;
                } else {
                    hideTextView(tmpView, R.id.tag7);
                }
                if (nowId < tagNum) {
                    setTextViewListener(tmpView, R.id.tag8, data.get(nowId));
                    nowId++;
                } else {
                    hideTextView(tmpView, R.id.tag8);
                }
                views.add(tmpView);
            }

            MyViewAdapter mPagerAdapter = new MyViewAdapter();
            viewPager.setAdapter(mPagerAdapter);
        }

        private void setTextViewListener(View view, int id, String text) {
            TextView tagView = (TextView) view.findViewById(id);
            tagView.setText(text);
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView nowView = (TextView) v;
                    attemptAddTag(nowView.getText().toString());
                }
            });
        }

        private void hideTextView(View view, int id) {
            TextView tagView = (TextView) view.findViewById(id);
            tagView.setVisibility(View.INVISIBLE);
        }

    }

    class ModifyInfoTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {

            String url = ModifyPersonSettingActivity.this.getString(R.string.modify_userinfo_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", args[0]));
            params.add(new BasicNameValuePair("nickname", args[1]));
            params.add(new BasicNameValuePair("schoolId", args[2]));
            params.add(new BasicNameValuePair("regionId", args[3]));
            params.add(new BasicNameValuePair("tagContent", args[4]));

            if (pictureLoaded)
                params.add(new BasicNameValuePair("picture", ImageFileUtils.parse(bitmap)));
            PostResult result = new JsonGetter<PostResult>().getFromUrl(url, PostResult.class,
                    params);

            if (result == null || !result.getState().equals("success"))
                return false;
            else
                return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            modifyInfoTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            if (result) {
                Toast.makeText(ModifyPersonSettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                ModifyPersonSettingActivity.this.finish();
            } else {
                Toast.makeText(ModifyPersonSettingActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            modifyInfoTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
