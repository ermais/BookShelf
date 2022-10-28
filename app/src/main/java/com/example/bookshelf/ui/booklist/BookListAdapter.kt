package com.example.bookshelf.ui.booklist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.model.Book

class BookListAdapter(context : Context,onBtnDownload : (view: View,downloadUri: Uri,bookTitle:String)->Unit)  : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    private val mContext  by lazy {context}
    private val onBtnDownloadBookClicked = onBtnDownload
    var data  = listOf<Book>()
    set(value){
        field = value
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val item = data[position]
        holder.bind(item,mContext,onBtnDownloadBookClicked)

    }

    override fun getItemCount(): Int = data.size
    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivBookCover = itemView.findViewById<ImageView>(R.id.imBookCover)
        val tvBookTitle = itemView.findViewById(R.id.tvBookTitle) as TextView
        val tvCategory = itemView.findViewById<TextView>(R.id.tvCategory)
        val tvAuthor = itemView.findViewById<TextView>(R.id.tvAuthor)
        val rbBook = itemView.findViewById(R.id.rbBook) as RatingBar
        val btnDownloadBook = itemView.findViewById<Button>(R.id.btnDownloadBook)
        @SuppressLint("SetTextI18n")
        fun bind(item: Book, mContext:Context,onBtnDownloadBookClick:(view: View,downloadUri:Uri,bookTitle:String)->Unit){
            val requestOptions : RequestOptions = RequestOptions().placeholder(R.drawable.ic_launcher_background)
            tvBookTitle.text = item.title
            tvCategory.text = item.category
            tvAuthor.text = "#Dr.${item.authorName}"
            rbBook.rating = 0.0f
            if (item.bookCover != null){
             Glide.with(mContext)
                 .load(item.bookCover)
                 .apply(requestOptions)
                 .into(ivBookCover)
            }
            btnDownloadBook.text = item.downloadCount.toString()
            btnDownloadBook.setOnClickListener {
                Log.d("BOOKLISTADAPTER","btnDownload on clicked!")
                onBtnDownloadBookClick(it,Uri.parse(item.bookUri),item.title)
            }

        }
        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.book_list_item,parent,false)
                view.setOnClickListener {
                    findNavController(it).navigate(R.id.bookDetailFragment)
                }
                return ViewHolder(view)
            }
        }
    }
}