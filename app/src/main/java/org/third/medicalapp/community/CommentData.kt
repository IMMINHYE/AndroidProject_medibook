package org.third.medicalapp.community

data class CommentData(
    var docId : String? = null,
    var commentId : String? = null,
    var nick : String? = null,
    var email : String? = null,
    var comment : String? = null,
    var date : String? = null,
    var isLiked : Boolean = false
)
