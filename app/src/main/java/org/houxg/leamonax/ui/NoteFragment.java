package org.houxg.leamonax.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.houxg.leamonax.Leamonax;
import org.houxg.leamonax.R;
import org.houxg.leamonax.adapter.NoteAdapter;
import org.houxg.leamonax.database.NoteDataStore;
import org.houxg.leamonax.model.Account;
import org.houxg.leamonax.model.Note;
import org.houxg.leamonax.service.NoteService;
import org.houxg.leamonax.utils.NetworkUtils;
import org.houxg.leamonax.utils.SharedPreferenceUtils;
import org.houxg.leamonax.utils.SkinCompatUtils;
import org.houxg.leamonax.utils.ToastUtils;
import org.houxg.leamonax.widget.NoteList;
import org.houxg.leamonax.widget.SelectPopupWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NoteFragment extends Fragment implements NoteAdapter.NoteAdapterListener {

    private static final String EXT_SCROLL_POSITION = "ext_scroll_position";
    private static final String SP_VIEW_TYPE = "sp_viewType";

    @BindView(R.id.recycler_view)
    RecyclerView mNoteListView;

    List<Note> mNotes;
    NoteList mNoteList;
    Mode mCurrentMode;
    OnSearchFinishListener mOnSearchFinishListener;
    private int mSortType = -1;

    public void setOnSearchFinishListener(OnSearchFinishListener onSearchFinishListener) {
        this.mOnSearchFinishListener = onSearchFinishListener;
    }

    public NoteFragment() {
    }

    public static NoteFragment newInstance() {
        return new NoteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortType = SharedPreferenceUtils.read(SharedPreferenceUtils.CONFIG, SelectPopupWindow.SP_SORT_TYPE, -1);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(SkinCompatUtils.isThemeNight() ? R.menu.note_night : R.menu.note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_view_type) {
            mNoteList.toggleType();
            SharedPreferenceUtils.write(SharedPreferenceUtils.CONFIG, SP_VIEW_TYPE, mNoteList.getType());
        } else if (item.getItemId() == R.id.action_view_more) {
            final SelectPopupWindow popupWindow = new SelectPopupWindow(getContext());
            if (getActivity() instanceof BaseActivity) {
                Toolbar toolbar = ((BaseActivity) getActivity()).getToolbar();
                popupWindow.setOnItemClickListener(new SelectPopupWindow.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int value) {
                        mSortType = value;
                        renderNotes();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showPopWindow(toolbar);
            }
        }
        return super.onOptionsItemSelected(item);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, view);
        mNoteList = new NoteList(container.getContext(), view, this);
        mNoteList.setType(SharedPreferenceUtils.read(SharedPreferenceUtils.CONFIG, SP_VIEW_TYPE, NoteList.DEFAULT_TYPE));
        return view;
    }

    public void setMode(Mode mode) {
        mCurrentMode = mode;
        List<Note> notes;
        mNoteList.setHighlight("");
        switch (mode) {
            case RECENT_NOTES:
                notes = NoteDataStore.getAllNotes(Account.getCurrent().getUserId());
                break;
            case NOTEBOOK:
                notes = NoteDataStore.getNotesFromNotebook(Account.getCurrent().getUserId(), mode.notebookId);
                break;
            case TAG:
                notes = NoteDataStore.getByTagText(mode.tagText, Account.getCurrent().getUserId());
                break;
            case SEARCH:
                notes = NoteDataStore.searchByKeyword(mode.keywords);
                mNoteList.setHighlight(mode.keywords);
                break;
            default:
                notes = new ArrayList<>();
        }
        mNotes = notes;
        renderNotes();
    }

    private void renderNotes() {
        Collections.sort(mNotes, getComparatorBySortType(mSortType));
        mNoteList.render(mNotes);
        if (mNotes.size() == 0 && mOnSearchFinishListener != null) {
            mOnSearchFinishListener.doSearchFinish();
        }
    }

    private Comparator<Note> getComparatorBySortType(int sortType) {
        switch (sortType) {
            case 1:
                return new Note.CreateTimeAscComparetor();
            case 2:
                return new Note.CreateTimeDescComparetor();
            case 3:
                return new Note.UpdateTimeAscComparetor();
            case 4:
                return new Note.UpdateTimeDescComparetor();
            case 5:
                return new Note.TitleAscComparetor();
            case 6:
                return new Note.TitleDescComparetor();
            default:
                return new Note.UpdateTimeDescComparetor();
        }

    }


    @Override
    public void onClickNote(Note note) {
        startActivity(NotePreviewActivity.getOpenIntent(getActivity(), note.getId()));
    }

    @Override
    public void onLongClickNote(final Note note) {
        CommandDialogFragment dialogFragment = CommandDialogFragment.newInstance(note);
        dialogFragment.setOnItemClickListener(new CommandDialogFragment.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        
                        break;
                    case 2:
                        showAlertDialog(note);
                        break;
                }
            }
        });
        dialogFragment.show(getChildFragmentManager(), CommandDialogFragment.TAG);
    }

    private void deleteNote(Note note) {
        Observable.just(note)
                .flatMap(new Func1<Note, rx.Observable<Note>>() {
                    @Override
                    public rx.Observable<Note> call(final Note note) {
                        return Observable.create(new Observable.OnSubscribe<Note>() {
                            @Override
                            public void call(Subscriber<? super Note> subscriber) {
                                if (!subscriber.isUnsubscribed()) {
                                    NoteService.trashNotesOnLocal(note);
                                    subscriber.onNext(note);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                })
                .flatMap(new Func1<Note, Observable<Note>>() {
                    @Override
                    public Observable<Note> call(Note note) {
                        NetworkUtils.checkNetwork();
                        return Observable.just(note);
                    }
                })
                .flatMap(new Func1<Note, Observable<Note>>() {
                    @Override
                    public Observable<Note> call(final Note note) {
                        return Observable.create(new Observable.OnSubscribe<Note>() {
                            @Override
                            public void call(Subscriber<? super Note> subscriber) {
                                if (!subscriber.isUnsubscribed()) {
                                    NoteService.saveNote(note.getId());
                                    subscriber.onNext(note);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof NetworkUtils.NetworkUnavailableException) {
                            ToastUtils.show(Leamonax.getContext(), R.string.delete_network_error);
                        } else {
                            ToastUtils.show(Leamonax.getContext(), R.string.delete_note_failed);
                        }
                        refresh();
                    }

                    @Override
                    public void onNext(Note note) {
                        mNoteList.remove(note);
                    }
                });
    }

    private void refresh() {
        setMode(mCurrentMode);
    }

    private void showAlertDialog(final Note note) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_note)
                .setMessage(R.string.are_you_sure_to_delete_note)
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteNote(note);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public enum Mode {
        RECENT_NOTES,
        NOTEBOOK,
        TAG,
        SEARCH;

        long notebookId;
        String tagText;
        String keywords;

        public void setNotebookId(long notebookId) {
            this.notebookId = notebookId;
        }

        public void setTagText(String tagText) {
            this.tagText = tagText;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        @Override
        public String toString() {
            return name() + "{" +
                    "notebookId=" + notebookId +
                    ", tagText='" + tagText + '\'' +
                    '}';
        }
    }

    public interface OnSearchFinishListener {
        void doSearchFinish();
    }

}
