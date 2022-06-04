package com.foxnotes.firestore_data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Notes {
    @DocumentId
    var noteId: String? = null
    @ServerTimestamp
    var noteDate: Date? = null
    @get: PropertyName("title") @set: PropertyName("title")
    var title: String? = null
    @get: PropertyName("noteText") @set: PropertyName("noteText")
    var noteText: String? = null

    constructor(noteId: String?, noteDate: Date?, title: String?, noteText: String?) {
        this.noteId = noteId
        this.noteDate = noteDate
        this.title = title
        this.noteText = noteText
    }

    constructor()

}
