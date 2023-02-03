package com.example.bookshelf.ui.mybooks

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.MyBooksQuery

class MyBooksAdapter() : RecyclerView.Adapter<MyBooksAdapter.ViewHolder>() {

    var myBooks = listOf<MyBooksQuery>()
    @SuppressLint("NotifyDataSetChanged")
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMyBookTitle = itemView.findViewById<TextView>(R.id.tvMyBookTitle)
        val tvMyBookDate = itemView.findViewById<TextView>(R.id.tvMyBookDate)
        val rbMyBookRate = itemView.findViewById<RatingBar>(R.id.rbMyBooks)
        val tvMyBookReview = itemView.findViewById<TextView>(R.id.tvMyBooksReview)

        fun bind(myBooks: MyBooksQuery) {
            tvMyBookTitle.text = myBooks.title
            tvMyBookDate.text = myBooks.boughtDate.toString()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.my_books_item, parent, false)
                return ViewHolder(view)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return myBooks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(myBooks[position])
    }
}