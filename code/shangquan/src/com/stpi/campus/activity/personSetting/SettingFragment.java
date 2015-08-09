package com.stpi.campus.activity.personSetting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.task.LoadUserInfoTask;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;
import com.stpi.campus.util.ImageFileUtils;
import com.stpi.campus.util.UtilTaskCallBack;

/**
 * Created by cyc on 13-12-11.
 */
public class SettingFragment extends RefreshableFragment {

    private View view = null;
    private Menu menu = null;

    private ImageView pictureView = null;
    private TextView nameView = null;
    private TextView tag1View = null;
    private TextView tag2View = null;
    private TextView tag3View = null;

    //    private String picture = "";
//    private String name = "";
//    private String schoolName = "";
//    private String regionName = "";
    private String tagList = "";

    private ProgressBar progressBar = null;

    private String person_picture_head = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_setting, container, false);
        setHasOptionsMenu(true);

        person_picture_head = this.getString(R.string.person_picture_head);

        pictureView = (ImageView) view.findViewById(R.id.headImage);
        nameView = (TextView) view.findViewById(R.id.name);
        tag1View = (TextView) view.findViewById(R.id.tag1view);
        tag2View = (TextView) view.findViewById(R.id.tag2view);
        tag3View = (TextView) view.findViewById(R.id.tag3view);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    private void setActionBarMenu() {
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_setting));
        menu.clear();
        menu.add(0, R.integer.modify_personal_setting, 0, "编辑").setIcon(R.drawable.actionbar_edit_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.integer.modify_personal_setting:
                this.goToModifyActivity();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToModifyActivity() {
        Intent intent = new Intent(SettingFragment.this.getActivity(),
                ModifyPersonSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void refresh() {
        super.refresh();
        setActionBarMenu();
        progressBar.setVisibility(View.VISIBLE);
        reLoadUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        reLoadUserInfo();
    }

    private void reLoadUserInfo() {
        new LoadUserInfoTask(SettingFragment.this.getActivity(), new UtilTaskCallBack() {
            @Override
            public void taskCallBack() {
                progressBar.setVisibility(View.INVISIBLE);
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
                imageLoader.displayImage(person_picture_head + UserInfo.userPic, pictureView,
                        options);

                nameView.setText(UserInfo.userName + "(" + UserInfo.schoolName + " " +
                        UserInfo.regionName + ")");
                if (UserInfo.tagList.size() > 0) {
                    tag1View.setVisibility(View.VISIBLE);
                    tag1View.setText(UserInfo.tagList.get(0));
                } else {
                    tag1View.setVisibility(View.INVISIBLE);
                }
                if (UserInfo.tagList.size() > 1) {
                    tag2View.setVisibility(View.VISIBLE);
                    tag2View.setText(UserInfo.tagList.get(1));
                } else {
                    tag2View.setVisibility(View.INVISIBLE);
                }
                if (UserInfo.tagList.size() > 2) {
                    tag3View.setVisibility(View.VISIBLE);
                    tag3View.setText(UserInfo.tagList.get(2));
                } else {
                    tag3View.setVisibility(View.INVISIBLE);
                }
            }
        }).execute(UserInfo.userId);
    }

//    class LoadUserInfoTask extends
//            AsyncTask<String, Integer, List<Map<String, Object>>> {
//
//        private String tag_1 = "";
//        private String tag_2 = "";
//        private String tag_3 = "";
//
//        @Override
//        protected List<Map<String, Object>> doInBackground(String... params) {
//
//            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
//            String url = SettingFragment.this.getString(R.string.detail_user_head);
//            url += "?userId=" + params[0];
//
//            DetailUserInfoHelper user_0 = new HttpListGetter<DetailUserInfoHelper>()
//                    .getFromUrl(url, DetailUserInfoHelper.class);
//
//            if (user_0 == null || user_0.getState().equals("fail")) {
//                return res;
//            }
//
//            DetailUserInfo theUserInfo = user_0.getResult();
//
//            if (theUserInfo == null) {
//                return res;
//            }
//            if (theUserInfo.getPicture() != null)
//                picture = person_picture_head + theUserInfo.getPicture();
//            else
//                picture = person_picture_head + "000000.jpg";
//            schoolName = theUserInfo.getSchoolName() != null ? theUserInfo.getSchoolName() : "";
//            regionName = theUserInfo.getRegionName() != null ? theUserInfo.getRegionName() : "";
//            if (theUserInfo.getUserName() != null)
//                name = theUserInfo.getUserName();
//            if (theUserInfo.getTags() != null) {
//                if (theUserInfo.getTags().size() > 0)
//                    tag_1 = theUserInfo.getTags().get(0);
//                if (theUserInfo.getTags().size() > 1)
//                    tag_2 = theUserInfo.getTags().get(1);
//                if (theUserInfo.getTags().size() > 2)
//                    tag_3 = theUserInfo.getTags().get(2);
//                tagList = "";
//                for(String tag: theUserInfo.getTags()) {
//                    tagList += "#" + tag;
//                }
//            }
//
//            return res;
//        }
//
//        @Override
//        protected void onPostExecute(List<Map<String, Object>> data) {
//
//            progressBar.setVisibility(View.INVISIBLE);
//
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            DisplayImageOptions options = new DisplayImageOptions.Builder()
//                    .cacheInMemory().cacheOnDisc()
//                    .preProcessor(new BitmapProcessor() {
//                        @Override
//                        public Bitmap process(Bitmap bitmap) {
//                            return ImageFileUtils.getRoundCornerImage(bitmap,
//                                    R.dimen.size25);
//                        }
//                    })
//                    .build();
//            imageLoader.displayImage(picture, pictureView, options);
//
//            nameView.setText(name + "(" + schoolName + " " + regionName + ")");
//            if (tag_1.equals(""))
//                tag1View.setVisibility(View.INVISIBLE);
//            else {
//                tag1View.setVisibility(View.VISIBLE);
//                tag1View.setText(tag_1);
//            }
//            if (tag_2.equals(""))
//                tag2View.setVisibility(View.INVISIBLE);
//            else {
//                tag2View.setVisibility(View.VISIBLE);
//                tag2View.setText(tag_2);
//            }
//            if (tag_3.equals(""))
//                tag3View.setVisibility(View.INVISIBLE);
//            else {
//                tag3View.setVisibility(View.VISIBLE);
//                tag3View.setText(tag_3);
//            }
//
//        }
//    }

}