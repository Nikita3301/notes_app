package com.example.pmd2.firestore

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


class Tasks {
    @DocumentId
    var taskId: String? = null
    @ServerTimestamp
    var taskDate: Date? = null
//    var date: Date? = null
    @get: PropertyName("title") @set: PropertyName("title")
    var title: String? = null
    @get: PropertyName("description") @set: PropertyName("description")
    var description: String? = null
    @get: PropertyName("done") @set: PropertyName("done")
    var done: Boolean = false


    constructor()
    constructor(taskId: String?, title: String?, description: String?, done: Boolean) {
        this.taskId = taskId
        this.title = title
        this.description = description
        this.done = done
    }


}




