package lilcode.aop.p3.c04.bookreview.api

import lilcode.aop.p3.c04.bookreview.model.BestSellerDto
import lilcode.aop.p3.c04.bookreview.model.SearchBookDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    // get : 데이터 요청 시 반환 http
    // post : http body에 넣어 전달
    @GET("/api/search.api?output=json")
    fun getBooksByName(
        @Query("key") apiKey: String,
        @Query("query") kweyWord: String
    ): Call<SearchBookDto>

    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(
        @Query("key") apiKey: String,
    ): Call<BestSellerDto>

}