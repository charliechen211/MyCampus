package com.stpi.campus.task;

import android.content.Context;
import android.os.AsyncTask;
import com.stpi.campus.entity.user.DetailUserInfoHelper;
import com.stpi.campus.R;
import com.stpi.campus.entity.user.DetailUserInfo;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.util.HttpListGetter;
import com.stpi.campus.util.UtilTaskCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cyc on 2014/11/30.
 */
public class LoadUserInfoTask extends
        AsyncTask<String, Integer, Boolean> {

        private Context activity = null;
        private UtilTaskCallBack callBack = null;

        public LoadUserInfoTask(Context theActivity,
                                UtilTaskCallBack theCallBack) {
            activity = theActivity;
            callBack = theCallBack;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
            String url = activity.getString(R.string.detail_user_head);
            url += "?userId=" + params[0];

            DetailUserInfoHelper user_0 = new HttpListGetter<DetailUserInfoHelper>()
                    .getFromUrl(url, DetailUserInfoHelper.class);

            if (user_0 == null || user_0.getState().equals("fail") || user_0.getResult() == null) {
                return false;
            }

            DetailUserInfo theUserInfo = user_0.getResult();

            if (theUserInfo.getPicture() != null)
                UserInfo.userPic = theUserInfo.getPicture();
            else
                UserInfo.userPic = "000000.jpg";
            UserInfo.schoolName = theUserInfo.getSchoolName() != null ? theUserInfo.getSchoolName
                    () : "";
            UserInfo.regionName = theUserInfo.getRegionName() != null ? theUserInfo.getRegionName
                    () : "";
            if (theUserInfo.getUserName() != null)
                UserInfo.userName = theUserInfo.getUserName();
            UserInfo.tagList.clear();
            for (String tag: theUserInfo.getTags()) {
                UserInfo.tagList.add(tag);
//                if (theUserInfo.getTags().size() > 0)
//                    UserInfo.tag_1 = theUserInfo.getTags().get(0);
//                if (theUserInfo.getTags().size() > 1)
//                    tag_2 = theUserInfo.getTags().get(1);
//                if (theUserInfo.getTags().size() > 2)
//                    tag_3 = theUserInfo.getTags().get(2);
//                tagList = "";
//                for(String tag: theUserInfo.getTags()) {
//                    tagList += "#" + tag;
//                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean cond) {

            if(cond)
                callBack.taskCallBack();

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

        }

}
