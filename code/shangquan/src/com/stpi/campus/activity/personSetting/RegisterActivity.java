package com.stpi.campus.activity.personSetting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.stpi.campus.components.TagPool;
import com.stpi.campus.R;
import com.stpi.campus.components.TagBar;
import com.stpi.campus.entity.schoolRegion.RegionDetail;
import com.stpi.campus.entity.schoolRegion.SchoolInfo;
import com.stpi.campus.entity.schoolRegion.SchoolRegionHelper;
import com.stpi.campus.entity.tag.TagInfoHelper;
import com.stpi.campus.items.user.DetailInfo;
import com.stpi.campus.items.user.RegisterResult;
import com.stpi.campus.util.HttpListGetter;
import com.stpi.campus.util.ImageFileUtils;
import com.stpi.campus.util.JsonGetter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private static File dir = null;
    static {
        String saveDir = Environment.getExternalStorageDirectory() + "/temple";
        dir = new File(saveDir);
        if (!dir.exists())
            dir.mkdir();
    }
    private final String filename = "temp.jpg";
    private RadioButton boyCheck = null;
    private TagPool tagPool = null;
    private EditText tagContent = null;
    private EditText phoneView = null;
    private EditText passwordView = null;
    private EditText confirmPasswordView = null;
    private EditText nicknameView = null;
    private ImageView imageView = null;
    private TabHost tabs = null;
    private Spinner ageSpinner = null;
    private Spinner schoolSpinner = null;
    private Spinner regionSpinner = null;
    private RegisterTask registerTask = null;
    private RecommendTask recommendTask = null;
    private ProgressBar progressBar = null;
    private Uri pictureUri = null;
    private Bitmap bitmap = null;
    private boolean pictureLoaded = false;
    private File tempFile = null;
    private GestureDetector gestureDetector = null;

    //    school region
    private List<SchoolInfo> schoolInfoList = new ArrayList<SchoolInfo>();
    private List<RegionDetail> recordedRegionList = new ArrayList<RegionDetail>();
    private String recordedSchoolId;
    private String recordedRegionId;
    private List<String> schoolList = new ArrayList<String>();
    private List<String> regionList = new ArrayList<String>();

    // user passwd global
    private String userId;
    private String passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initIcons();
        initEvents();
        new LoadSchoolTask().execute();
    }

    private void initIcons() {

        // 设置桌面标签
        tabs = (TabHost) this.findViewById(R.id.tabHost);
        tabs.setup();
        tabs.addTab(tabs.newTabSpec("tab1").setContent(R.id.tab1).setIndicator("注册资料"));
        tabs.addTab(tabs.newTabSpec("tab2").setContent(R.id.tab2).setIndicator("详细信息"));
        tabs.addTab(tabs.newTabSpec("tab3").setContent(R.id.tab3).setIndicator("我的头像"));

        // 设置性别选项
        boyCheck = (RadioButton) this.findViewById(R.id.check_boy);
        boyCheck.setChecked(true);

//        设置年龄列表
        ageSpinner = (Spinner) this.findViewById(R.id.age);
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DetailInfo.age);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);

        schoolSpinner = (Spinner) this.findViewById(R.id.school);
        regionSpinner = (Spinner) this.findViewById(R.id.region);

        tagContent = (EditText) this.findViewById(R.id.tag_content);
        phoneView = (EditText) this.findViewById(R.id.username);
        passwordView = (EditText) this.findViewById(R.id.password);
        confirmPasswordView = (EditText) this.findViewById(R.id.confirm_password);
        nicknameView = (EditText) this.findViewById(R.id.nickname);
        progressBar = (ProgressBar) this.findViewById(R.id.progress);

        tagPool = (TagPool) this.findViewById(R.id.tag_container);
        imageView = (ImageView) this.findViewById(R.id.picture);
    }

    private void initEvents() {
        attemptRecommend(boyCheck.isChecked() ? "1" : "2", ageSpinner.getSelectedItemPosition());

        Button addButton = (Button) this.findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagContent.getText().toString().length() <= 0)
                    return;
                TagBar tagBar = new TagBar(RegisterActivity.this);
                tagBar.setParent(tagPool);
                tagBar.setText(tagContent.getText().toString());
                tagBar.setSelected(true);
                tagPool.addView(tagBar);
                tagContent.setText("");
            }
        });

        Button changeButton = (Button) this.findViewById(R.id.change);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRecommend(boyCheck.isChecked() ? "1" : "2", ageSpinner.getSelectedItemPosition());
            }
        });
        changeButton.performClick();

        Button chooseButton = (Button) this.findViewById(R.id.upload_picture);
        chooseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        Button cameraButton = (Button) this.findViewById(R.id.upload_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {

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
                }
            }
        });
        gestureDetector = new GestureDetector(this, this);
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.register_activity_layout);
        layout.setOnTouchListener(this);
        layout.setLongClickable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem submit = menu.add(0, 1, 0, "提交");
        MenuItem exit = menu.add(0, 2, 0, "退出");
        submit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        exit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            attemptRegister();
        } else if (item.getItemId() == 2) {
            finishWithoutUserId();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finishWithoutUserId();
        }
        return false;
    }

    private void attemptRegister() {
        if (registerTask != null)
            return;

        phoneView.setError(null);
        passwordView.setError(null);
        confirmPasswordView.setError(null);

        String phone = phoneView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();
        String sex = boyCheck.isChecked() ? "1" : "2";
        String nickname = nicknameView.getText().toString();
        String age = String.valueOf(ageSpinner.getSelectedItemPosition());

        String tags = tagPool.toString();

        boolean cancel = false;
        View focusView = null;

        if (phone.length() != 11) {
            phoneView.setError("手机号位数错误");
            focusView = phoneView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordView.setError("密码不能为空");
            focusView = passwordView;
            cancel = true;
        } else if (password.length() < 4) {
            passwordView.setError("密码长度不能小于四位");
            focusView = passwordView;
            cancel = true;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordView.setError("密码不一致");
            focusView = confirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            tabs.setCurrentTab(0);
            focusView.requestFocus();
        } else {
            registerTask = new RegisterTask();
            registerTask.execute(phone, password, sex, age, nickname, recordedSchoolId,
                    recordedRegionId, tags.length() > 0 ? tags.substring(1, tags.length()) : "");
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void attemptRecommend(String sex, int age) {
        if (registerTask != null)
            return;

        recommendTask = new RecommendTask();
        recommendTask.execute(sex, String.valueOf(age));
        progressBar.setVisibility(View.VISIBLE);
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

        this.startActivityForResult(intent, 3);
    }

    private void finishWithUserId() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userId);
        bundle.putString("passwd", passwd);
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        this.finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
    }

    private void finishWithoutUserId() {
        this.setResult(RESULT_CANCELED, null);
        this.finish();
        overridePendingTransition(R.anim.in_from_left_direct, R.anim.out_of_right_direct);
    }

    // 滑动改变屏幕
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        if (motionEvent2.getX() - motionEvent.getX() > 50 && Math.abs(v) > 0) {
            int next = (tabs.getCurrentTab() + 2) % 3;
            if (tabs.getCurrentTab() == 1)
                attemptRecommend(boyCheck.isChecked() ? "1" : "2", ageSpinner.getSelectedItemPosition());
            tabs.setCurrentTab(next);
        } else if (motionEvent.getX() - motionEvent2.getX() > 50 && Math.abs(v) > 0) {
            int next = (tabs.getCurrentTab() + 1) % 3;
            if (tabs.getCurrentTab() == 1)
                attemptRecommend(boyCheck.isChecked() ? "1" : "2", ageSpinner.getSelectedItemPosition());
            tabs.setCurrentTab(next);
        }
        return false;
    }

    public class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String url = RegisterActivity.this.getString(R.string.register_head);
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("mobilePhone", args[0]));
            params.add(new BasicNameValuePair("password", args[1]));
            params.add(new BasicNameValuePair("sex", args[2]));
            params.add(new BasicNameValuePair("age", args[3]));
            if (args[4].length() > 0)
                params.add(new BasicNameValuePair("nickname", args[4]));
            if (!args[5].equals("0"))
                params.add(new BasicNameValuePair("schoolId", args[5]));
            if (!args[6].equals("0"))
                params.add(new BasicNameValuePair("regionId", args[6]));
            if (args[7].length() > 0)
                params.add(new BasicNameValuePair("tagContent", args[7]));
            else
                params.add(new BasicNameValuePair("tagContent", ""));
            if (pictureLoaded)
                params.add(new BasicNameValuePair("picture", ImageFileUtils.parse(bitmap)));
            RegisterResult res = new JsonGetter<RegisterResult>().getFromUrl(url, RegisterResult.class, params);
            if (res == null)
                return "error";
            if (res.getState().equals("success")) {
                userId = args[0];
                passwd = args[1];
                return "success";
            }
            else
                return "duplicate";
        }

        @Override
        protected void onPostExecute(final String retCode) {
            registerTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println(retCode);

            if (retCode.equals("error"))
                Toast.makeText(RegisterActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            else if (retCode.equals("duplicate")) {
                phoneView.setError("用户名已被注册");
                phoneView.requestFocus();
            } else {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                finishWithUserId();
            }
        }

        @Override
        protected void onCancelled() {
            registerTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public class RecommendTask extends AsyncTask<String, Void, List<TagBar>> {

        @Override
        protected List<TagBar> doInBackground(String... args) {

            List<TagBar> res = new ArrayList<TagBar>();

            String url = RegisterActivity.this.getString(R.string.tags_rec_head);
            url += "?sex=" + args[0];
            url += "&age=" + args[1];

            TagInfoHelper tags = new HttpListGetter<TagInfoHelper>().getFromUrl(url, TagInfoHelper.class);
            if (tags == null || tags.getState().equals("fail")) {
                return res;
            }

            for (String bar : tags.getRecResult()) {
                TagBar bar1 = new TagBar(RegisterActivity.this);
                bar1.setParent(tagPool);
                bar1.setText(bar);
                bar1.setSelected(false);
                res.add(bar1);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(List<TagBar> tagBars) {
            recommendTask = null;
            progressBar.setVisibility(View.INVISIBLE);

            tagPool.removeUnselected();
            for (TagBar bar : tagBars)
                tagPool.addView(bar);
        }

        @Override
        protected void onCancelled() {
            recommendTask = null;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    class LoadSchoolTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            String url = RegisterActivity.this.getString(R.string.get_school_list);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,
                    android.R.layout.simple_spinner_item, schoolArray
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            schoolSpinner.setAdapter(adapter);
            schoolSpinner.setVisibility(View.VISIBLE);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,
                    android.R.layout.simple_spinner_item, regionArray
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            regionSpinner.setAdapter(adapter);
            regionSpinner.setVisibility(View.VISIBLE);
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
}