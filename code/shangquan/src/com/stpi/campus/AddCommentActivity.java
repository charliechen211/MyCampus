package com.stpi.campus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.stpi.campus.R;

/**
 * Created by cyc on 13-11-22.
 */
public class AddCommentActivity extends Activity {

    final Context context = this;
    Button addTagButton;
    Button confirmButton;
    EditText tagText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        tagText = (EditText) this.findViewById(R.id.tagsWrite);
        addTagButton = (Button) this.findViewById(R.id.addTagButton);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTagDialog();
            }
        });
        confirmButton = (Button) this.findViewById(R.id.addCommentConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddCommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showTagDialog() {
        //定义复选框选项
        final String[] multiChoiceItems = {"很好吃", "性价比高", "速度快", "服务好", "重口味", "有点辣", "餐馆妹纸多", "口味较轻", "烧烤", "西餐", "不太好吃"};
        //复选框默认值：false=未选;true=选中 ,各自对应items[i]
        final boolean[] defaultSelectedStatus = {false, false, false, false, false, false, false, false, false, false, false};
        final StringBuilder sb = new StringBuilder();
        new AlertDialog.Builder(context).setTitle("热门标签").setMultiChoiceItems(multiChoiceItems, defaultSelectedStatus, new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which,
                                boolean isChecked) {
                //来回重复选择取消，得相应去改变item对应的bool值，点击确定时，根据这个bool[],得到选择的内容
                defaultSelectedStatus[which] = isChecked;
            }
        })  //设置对话框[肯定]按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < defaultSelectedStatus.length; i++) {
                            if (defaultSelectedStatus[i]) {
//                                sb.append(multiChoiceItems[i]);
//                                tagText.setText(multiChoiceItems[i]);
//                                tagText.setText(strText.toCharArray(), 0, strText.length()-1);
                                if (tagText.getText().length() > 0 && tagText.getText().charAt(tagText.getText().length() - 1) != ';')
                                    tagText.setText(tagText.getText() + ";");
                                tagText.setText(tagText.getText() + multiChoiceItems[i] + ";");
                            }
                        }
// TODO Auto-generated method stub
//                        Toast.makeText(context,sb.toString(), Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("取消", null).show();
    }
}