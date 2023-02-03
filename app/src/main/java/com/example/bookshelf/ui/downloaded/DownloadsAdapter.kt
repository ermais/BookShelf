package com.example.bookshelf.ui.downloaded

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.DownloadAndBook
import java.util.*

class DownloadsAdapter(context: Context, onDownloadClicked: (bookUri: String) -> Unit) :
    RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {
    val mContext by lazy { context }
    private val onDownloadCardClicked = onDownloadClicked
    var downloads = listOf<DownloadAndBook>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBookTitle: TextView = view.findViewById(R.id.txtViewDownloadedBookTitle)
        val tvDownloadDate: TextView = view.findViewById(R.id.txtViewDownloadedBookWhen)
        val cvDownload: CardView = view.findViewById(R.id.downloadCardView)

        fun bind(book: DownloadAndBook, onDownloadClicked: (bookUri: String) -> Unit) {
            tvBookTitle.text = book.title
            tvDownloadDate.text = "${Date()}"
            cvDownload.setOnClickListener {
                onDownloadClicked(book.bookUri)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_downloaded, parent, false)
                return ViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(downloads[position], onDownloadCardClicked)
    }

    override fun getItemCount(): Int {
        return downloads.size
    }

}