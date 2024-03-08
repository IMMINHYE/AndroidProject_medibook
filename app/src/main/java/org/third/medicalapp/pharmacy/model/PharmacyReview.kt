package org.third.medicalapp.pharmacy.model

data class PharmacyReview(
    var pharmacyId : Long? = null,
    var pharmacyReviewId : String? = null,
    var email : String? = null,
    var nick:String? = null,
    var review : String? = null,
    var date : String? = null,
    var isLiked : Boolean = false
)
