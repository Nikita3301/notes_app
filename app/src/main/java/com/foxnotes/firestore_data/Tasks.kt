package com.foxnotes.firestore_data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


class Tasks {
    @DocumentId
    var taskId: String? = null
    @ServerTimestamp
    var taskDate: Date? = null
    @get: PropertyName("title") @set: PropertyName("title")
    var title: String? = null
    @get: PropertyName("description") @set: PropertyName("description")
    var description: String? = null
    @get: PropertyName("done") @set: PropertyName("done")
    var done: Boolean = false



    constructor(
        taskId: String?,
        taskDate: Date?,
        title: String?,
        description: String?,
        done: Boolean
    ) {
        this.taskId = taskId
        this.taskDate = taskDate
        this.title = title
        this.description = description
        this.done = done
    }

    constructor()


}




