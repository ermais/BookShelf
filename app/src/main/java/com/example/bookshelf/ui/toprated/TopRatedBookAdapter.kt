package com.example.bookshelf.ui.toprated

import android.content.Context
import android.net.Uri
import android.view.View
import com.example.bookshelf.ui.booklist.BookListAdapter

class TopRatedBookAdapter(
    context: Context,
    onBtnDownload: (View, Uri, String, String) -> Unit,) :
    BookListAdapter(
        context,
        onBtnDownload
) {
}