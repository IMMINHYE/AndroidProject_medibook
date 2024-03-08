package org.third.medicalapp.hospital.model

data class Hospital(
    var id: Long?,
    var hname: String,
    var hcode: String,
    var addr:String,
    var city: String,
    var sigun: String,
    var dong: String,
    var x: Double,
    var y: Double,
    var tel: String
)
