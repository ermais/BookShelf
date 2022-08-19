package com.example.bookshelf.ui.booklist
import com.example.bookshelf.bussiness.FirestoreBookDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.*
import com.google.firebase.ktx.Firebase

class BookListRepository(val firestoreBookDataSource: FirestoreBookDataSource) {
    var fireDb = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser

    suspend fun getBookList() = firestoreBookDataSource.getBooksFromFirestore()
    suspend fun filterByCategory(category: String) = firestoreBookDataSource.filterByCategory(category)
    suspend fun filterByAuthor(author:String) = firestoreBookDataSource.filterByCategory(author)


}