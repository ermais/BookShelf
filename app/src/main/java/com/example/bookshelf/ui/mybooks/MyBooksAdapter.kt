package com.example.bookshelf.ui.mybooks

import android.annotation.SuppressLint
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.MyBooksQuery
import java.util.*

@Suppress("DEPRECATION")
class MyBooksAdapter(val activity: FragmentActivity) :
    RecyclerView.Adapter<MyBooksAdapter.ViewHolder>() {

    var myBooks = listOf<MyBooksQuery>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val myBooksLayout: ConstraintLayout = itemView.findViewById(R.id.myBooksLayout)
        private val tvMyBookTitle: TextView = itemView.findViewById(R.id.tvMyBookTitle)
//        val tvMyBookDate = itemView.findViewById<TextView>(R.id.tvMyBookDate)
//        val rbMyBookRate = itemView.findViewById<RatingBar>(R.id.rbMyBooks)

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(myBooks: MyBooksQuery, activity: FragmentActivity) {
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val dVH = dm.heightPixels
            val dVW = dm.widthPixels
            val myBooksVH: Int = (dVH.div(3)).dec() - 20
            val myBooksVW: Int = (dVW.div(2)).dec() - 20
            myBooksLayout.minHeight = myBooksVH
            myBooksLayout.maxWidth = myBooksVW
            tvMyBookTitle.text = myBooks.title
//            tvMyBookDate.text = bookShelfDateFormatter(Date(myBooks.boughtDate))
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(myBooks[position], activity)
    }
}