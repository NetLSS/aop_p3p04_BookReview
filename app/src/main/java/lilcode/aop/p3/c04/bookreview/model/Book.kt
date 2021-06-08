package lilcode.aop.p3.c04.bookreview.model

import com.google.gson.annotations.SerializedName

// SerializedName 서버에서 받아오는 명칭을 적용해줄 수 있음
// DEA DTO
data class Book(
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String,
)