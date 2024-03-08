package org.third.medicalapp.community

data class CommunityData(
    var docId : String? = null,
    var email : String? = null,
    var nick : String? = null,
    var title : String? = null,
    var content : String? = null,
    var date : String? = null,
    var likeCount : Int = 0
)
