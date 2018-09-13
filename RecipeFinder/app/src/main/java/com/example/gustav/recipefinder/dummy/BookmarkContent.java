package com.example.gustav.recipefinder.dummy;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class BookmarkContent {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

    public static final List<BookmarkItem> ITEMS = new ArrayList<BookmarkItem>();
    public static final Map<String, BookmarkItem> ITEM_MAP = new HashMap<String, BookmarkItem>();

    private int COUNT = 25;

    static {
        for(int i = 0; i < 25; i++) {
            addItem(createBookmarkItem(i));
        }
    }


    private static void addItem(BookmarkItem item) { // Lägg till ett item
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static BookmarkItem createBookmarkItem(int position) {
        return new BookmarkItem(String.valueOf(position), "Itemss " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for(int i = 0; i < position; i++) {
            builder.append("\nInfo..");
        }
        return builder.toString();
    }

    public static class BookmarkItem {
        public final String id;
        public final String content;
        public final String details;

        public BookmarkItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
