package org.third.medicalapp.medicalInfo.model

import java.io.Serializable

data class MediInfo(
    var id : Long,
    var siteName : String,
    var siteUrl : String,
    var siteIntro : String,
):Serializable
