package org.semi.adapter;

import android.content.Context;
import androidx.appcompat.widget.SearchView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;

public class EmptySubmitSearchView extends SearchView {
   public EmptySubmitSearchView(Context context) {
        super(context);
    }

    public EmptySubmitSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptySubmitSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnQueryTextListener(final OnQueryTextListener listener) {
        super.setOnQueryTextListener(listener);
        SearchAutoComplete searchTextView = this.findViewById(androidx.appcompat.R.id.search_src_text);
        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(listener != null) {
                    listener.onQueryTextSubmit(getQuery().toString());
                }
                return true;
            }
        });
    }
}
