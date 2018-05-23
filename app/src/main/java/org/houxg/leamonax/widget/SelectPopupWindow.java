package org.houxg.leamonax.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.houxg.leamonax.R;
import org.houxg.leamonax.utils.DisplayUtils;
import org.houxg.leamonax.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectPopupWindow extends ListPopupWindow {
    public static final String SP_SORT_TYPE = "sp_sort_type";
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private Adapter mAdapter;
    private int mChecked = -1;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public SelectPopupWindow(Context context) {
        super(context);
        this.mContext = context;
    }

    @SuppressLint("RestrictedApi")
    public void showPopWindow(View anchorView) {
        String[] selectKey = mContext.getResources().getStringArray(R.array.select_key);
        final String[] selectValue = mContext.getResources().getStringArray(R.array.select_value);
        mAdapter = new Adapter();
        mAdapter.setDatas(new ArrayList<>(Arrays.asList(selectKey)));
        mChecked = SharedPreferenceUtils.read(SharedPreferenceUtils.CONFIG, SP_SORT_TYPE, mChecked);
        mAdapter.setChecked(mChecked);
        setAdapter(mAdapter);
        setWidth(DisplayUtils.dp2px(180));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setDropDownGravity(Gravity.END);
        setAnchorView(anchorView);
        setOverlapAnchor(true);
        setModal(true);
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int value = Integer.valueOf(selectValue[position]);
                SharedPreferenceUtils.write(SharedPreferenceUtils.CONFIG, SP_SORT_TYPE, value);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(parent, view, value);
                }
            }
        });
        show();
    }

    class Adapter extends BaseAdapter {
        List<String> datas = new ArrayList<>();
        private int checked = -1;

        void setDatas(List<String> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public String getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item_1, parent, false);
            }
            ImageView checkView = convertView.findViewById(R.id.check);
            TextView textView = convertView.findViewById(android.R.id.text1);
            if (checked == (position + 1)) {
                checkView.setVisibility(View.VISIBLE);
            } else {
                checkView.setVisibility(View.INVISIBLE);
            }
            textView.setText(getItem(position));
            return convertView;
        }

        public void setChecked(int checked) {
            this.checked = checked;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> parent, View view, int value);
    }

}
