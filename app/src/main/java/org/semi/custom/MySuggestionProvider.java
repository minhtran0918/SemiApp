package org.semi.custom;

import android.content.SearchRecentSuggestionsProvider;

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "org.nam.custom.MySuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;
    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
