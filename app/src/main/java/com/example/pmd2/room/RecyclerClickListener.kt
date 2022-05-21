package com.example.pmd2.room

interface RecyclerClickListener {
    fun onItemLongRemoveClick(position: Int)
    fun onItemClick(position: Int)
    fun onDoneClick(position: Int)
}

