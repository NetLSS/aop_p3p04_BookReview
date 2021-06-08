package lilcode.aop.p3.c04.bookreview

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import lilcode.aop.p3.c04.bookreview.adapter.BookAdapter
import lilcode.aop.p3.c04.bookreview.api.BookService
import lilcode.aop.p3.c04.bookreview.databinding.ActivityMainBinding
import lilcode.aop.p3.c04.bookreview.model.BestSellerDto
import lilcode.aop.p3.c04.bookreview.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bookRecyclerViewAdapter: BookAdapter
    private lateinit var bookService: BookService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initBookRecyclerView()
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object : Callback<BestSellerDto> {
                // 성공.
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.e(M_TAG, "NOT!! SUCCESS")
                        return
                    }

                    response.body()?.let {
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

        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            // 키보드 입력시 발생

            // 엔터 눌렀을 경우 (눌렀거나, 뗏을 때 -> 눌렀을 때 발생하도록.)
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true// 처리 되었음.
            }
            return@setOnKeyListener false // 처리 안됬음 을 나타냄.
        }
    }

    private fun search(keyWord: String) {
        bookService.getBooksByName(getString(R.string.interparkAPIKey), keyWord)
            .enqueue(object : Callback<SearchBookDto> {
                // 성공.
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    if (response.isSuccessful.not()) {
                        return
                    }

                    bookRecyclerViewAdapter.submitList(response.body()?.books.orEmpty()) // 새 리스트로 갱신
                }

                // 실패.
                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    Log.e(M_TAG, t.toString())
                }
            })
    }

    private fun initBookRecyclerView() {
        bookRecyclerViewAdapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = bookRecyclerViewAdapter
    }

    companion object {
        private const val M_TAG = "MainActivity"
    }
}