package org.houxg.leamonax.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.houxg.leamonax.R;
import org.houxg.leamonax.model.Note;
import org.houxg.leamonax.utils.ScreenUtils;
import org.houxg.leamonax.utils.SkinCompatUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommandDialogFragment extends DialogFragment {
    public static final String TAG = "CommandDialogFragment";
    public static final String ARG_NOTE = "ARG_NOTE";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private int[] resIdList = new int[]{R.drawable.ic_share_black, R.drawable.ic_folder_open_black, R.drawable.ic_delete_black};
    private int[] resIdWhiteList = new int[]{R.drawable.ic_share_white, R.drawable.ic_folder_open_white, R.drawable.ic_delete_white};
    private String[] titleList = new String[]{"分享", "移动", "删除"};
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_note, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new Adapter();
        mAdapter.setData(generateData());
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, position);
                }
                dismiss();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(ScreenUtils.getScreenWidth(getContext()) * 4 / 5,
                        WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.customDialogFragment);
    }

    private List<CommandItem> generateData() {
        List<CommandItem> itemList = new ArrayList<>();

        for (int index = 0; index < resIdList.length; index++) {
            CommandItem item = new CommandItem();
            item.resId = SkinCompatUtils.isThemeNight() ? resIdWhiteList[index] : resIdList[index];
            item.title = titleList[index];
            itemList.add(item);
        }
        return itemList;
    }

    class CommandItem {
        public int resId;
        public String title;
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<CommandItem> itemList = new ArrayList<>();
        OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void setData(List<CommandItem> dataList) {
            itemList = dataList;
        }

        @NonNull
        @Override

        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dialog_note_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CommandItem item = itemList.get(position);
            viewHolder.iconView.setImageResource(item.resId);
            viewHolder.titleView.setText(item.title);
            if (onItemClickListener != null) {
                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v, holder.getAdapterPosition());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView titleView;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.icon);
            titleView = itemView.findViewById(R.id.title);
            rootView = itemView.findViewById(R.id.root_view);
        }
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static CommandDialogFragment newInstance(Note note) {
        CommandDialogFragment fragment = new CommandDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE, note);
        fragment.setArguments(args);
        return fragment;
    }
}
