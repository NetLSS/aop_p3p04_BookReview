package lilcode.aop.p3.c04.bookreview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import lilcode.aop.p3.c04.bookreview.adapter.BookAdapter
import lilcode.aop.p3.c04.bookreview.api.BookService
import lilcode.aop.p3.c04.bookreview.databinding.ActivityMainBinding
import lilcode.aop.p3.c04.bookreview.model.BestSellerDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bookRecyclerViewAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initBookRecyclerView()
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val bookService: BookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(M_API_KEY)
            .enqueue(object: Callback<BestSellerDto>{
                // 성공.
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    if(response.isSuccessful.not()){
                        Log.e(M_TAG, "NOT!! SUCCESS")
                        return
                    }

                    response.body()?.let{
                        Log.d(M_TAG, it.toString())

                        it.books.forEach { book ->
                            Log.d(M_TAG, book.toString())
                        }

                        bookRecyclerViewAdapter.submitList(it.books) // 새 리스트로 갱신
                    }
                }

                // 실패.
                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    Log.e(M_TAG, t.toString())
                }

            })
    }

    private fun initBookRecyclerView(){
        bookRecyclerViewAdapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = bookRecyclerViewAdapter
    }

    companion object{
        private const val M_TAG = "MainActivity"
        private const val M_API_KEY = "B37540CD55F6A52183F9AA6EACBC2767918F2070D1A3015C87F24AADD094279E"
    }
}