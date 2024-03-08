package org.third.medicalapp.community

data class LikeData(
    var likeId : String?= null,
    var like_docId : String? = null,
    var like_email : String? = null,
    var isLiked : Boolean = false
)
