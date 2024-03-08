package org.third.medicalapp.hospital.model

data class HospitalReview(
    var hospitalId : Long? = null,
    var hospitalReviewId : String? = null,
    var email : String? = null,
    var nick:String? = null,
    var review : String? = null,
    var date : String? = null,
    var isLiked : Boolean = false
)
