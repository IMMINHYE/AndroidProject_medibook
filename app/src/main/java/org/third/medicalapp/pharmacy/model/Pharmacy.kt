package org.third.medicalapp.pharmacy.model

data class Pharmacy(
    var id: Long?,
    var pharmacy: String,
    var citycode: String,
    var siguncode: String,
    var dong:String,
    var addr: String,
    var tel: String,
    var x:Double,
    var y:Double
)
