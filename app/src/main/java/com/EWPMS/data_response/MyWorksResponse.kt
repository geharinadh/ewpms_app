package com.EWPMS.data_response

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

data class MyWorksResponse(
    var categoryName: String,
    var completedPercentage: String,
    var currentProjectsID: String,
    var deadline: String,
    var noOfMileStones: String,
    var projectName: String
)




