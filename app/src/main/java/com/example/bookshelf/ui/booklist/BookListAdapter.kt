package com.example.bookshelf.ui.booklist

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.ui.home.HomeFragmentDirections

open class BookListAdapter(
    context: Context,
    onBtnDownload: (view: View, downloadUri: Uri, bookTitle: String, bookId: String) -> Unit
) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    private val mContext by lazy { context }
    private val onBtnDownloadBookClicked = onBtnDownload
    var data = listOf<Book>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, mContext, onBtnDownloadBookClicked)

    }

    override fun getItemCount(): Int = data.size
    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivBookCover: ImageView = itemView.findViewById(R.id.imBookCover)
        private val tvBookTitle = itemView.findViewById(R.id.tvBookTitle) as TextView
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        private val rbBook = itemView.findViewById(R.id.rbBook) as RatingBar
        private val btnDownloadBook: Button = itemView.findViewById(R.id.btnDownloadBook)
        private val cvBookItem: CardView = itemView.findViewById(R.id.cvBookItem)

        @SuppressLint("SetTextI18n")
        fun bind(
            item: Book,
            mContext: Context,
            onBtnDownloadBookClick: (view: View, downloadUri: Uri, bookTitle: String, bookId: String) -> Unit
        ) {
            val requestOptions: RequestOptions =
                RequestOptions().placeholder(R.drawable.ic_launcher_background)
            tvBookTitle.text = item.title
            tvCategory.text = item.category
            tvAuthor.text = "#Dr.${item.authorName}"
            rbBook.rating = 0.0f
            if (item.bookCover != null) {
                Glide.with(mContext)
                    .load(item.bookCover)
                    .apply(requestOptions)
                    .into(ivBookCover)
            }
            btnDownloadBook.text = item.downloadCount.toString()
            btnDownloadBook.setOnClickListener {
                Log.d("BOOK_LIST_ADAPTER", "btnDownload on clicked!")
                onBtnDownloadBookClick(it, Uri.parse(item.bookUri), item.title, item.bookId)
            }
            cvBookItem.setOnClickListener {
                val action = HomeFragmentDirections.actionNavHomeToBookDetailFragment(item.title)
                findNavController(it).navigate(action)
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.book_list_item, parent, false)
                return ViewHolder(view)
            }
        }
    }
}