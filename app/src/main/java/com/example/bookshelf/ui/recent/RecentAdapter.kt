package com.example.bookshelf.ui.recent

import android.content.Context
import android.net.Uri
import android.view.View
import com.example.bookshelf.ui.booklist.BookListAdapter

class RecentAdapter(
    context: Context,
    onBtnDownload: (view: View, downloadUri: Uri, bookTitle: String, bookId: String) -> Unit
) : BookListAdapter(context, onBtnDownload) {

}