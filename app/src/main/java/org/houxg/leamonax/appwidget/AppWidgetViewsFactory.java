package org.houxg.leamonax.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.houxg.leamonax.R;
import org.houxg.leamonax.database.NoteDataStore;
import org.houxg.leamonax.model.Account;
import org.houxg.leamonax.model.Note;
import org.houxg.leamonax.ui.NotePreviewActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context = null;
    private int appWidgetId;
    private List<Note> allNotes;

    public AppWidgetViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        allNotes = NoteDataStore.getAllNotes(Account.getCurrent().getUserId());
        Collections.sort(allNotes, new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                String temp1 = o1.getUpdatedTimeVal() + "";
                String temp2 = o2.getUpdatedTimeVal() + "";
                return temp2.compareTo(temp1);
            }
        });
        if (allNotes.size() > 30) {
            allNotes = allNotes.subList(0, 30);
        }
    }

    @Override
    public int getCount() {
        return allNotes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(context.getPackageName(),
                R.layout.note_app_widget_row);
        Note note = allNotes.get(position);
        row.setTextViewText(R.id.appwidget_row_title, note.getTitle());
        String content;
        if (note.isMarkDown()) {
            content = note.getNoteAbstract();
        } else {
            Spanned spannedContent = Html.fromHtml(note.getNoteAbstract());
            content = spannedContent.toString().replaceAll("\\n\\n+", "\n");
        }
        if (content.length() >= 200) {
            content = content.substring(0, 200) + "...";
        }
        row.setTextViewText(R.id.appwidget_row_text, content);

        Intent intent = new Intent();
        Bundle extras = new Bundle();
        extras.putLong(NotePreviewActivity.EXT_NOTE_LOCAL_ID, note.getId());
        intent.putExtras(extras);
        row.setOnClickFillInIntent(R.id.widget_note, intent);
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }
}