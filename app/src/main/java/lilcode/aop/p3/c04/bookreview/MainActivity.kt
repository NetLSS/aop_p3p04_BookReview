package lilcode.aop.p3.c04.bookreview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import lilcode.aop.p3.c04.bookreview.adapter.BookAdapter
import lilcode.aop.p3.c04.bookreview.adapter.HistoryAdapter
import lilcode.aop.p3.c04.bookreview.api.BookService
import lilcode.aop.p3.c04.bookreview.databinding.ActivityMainBinding
import lilcode.aop.p3.c04.bookreview.model.BestSellerDto
import lilcode.aop.p3.c04.bookreview.model.History
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
    private lateinit var historyAdapter: HistoryAdapter

    private val db: AppDatabase by lazy {
        getAppDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBookRecyclerView()
        initHistoryRecyclerView()
        initSearchEditText()

        initBookService()
        bookServiceLoadBestSellers()
    }

    private fun bookServiceLoadBestSellers() {
        // 베스트 셀러 가져오기;
        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object : Callback<BestSellerDto> {
                // 응답이 온 경우;
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    // 받은 응답이 성공한 응답일 때;
                    if (response.isSuccessful.not()) {
                        Log.e(M_TAG, "NOT!! SUCCESS")
                        return
                    }

                    // 받은 응답의 바디가 채워져 있는 경우만 진행;
                    response.body()?.let {
                        Log.d(M_TAG, it.toString())

                        it.books.forEach { book ->
                            Log.d(M_TAG, book.toString())
                        }

                        // 새 리스트로 갱신;
                        bookRecyclerViewAdapter.submitList(it.books)
                    }
                }

                // 응답에 실패한 경우
                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    Log.e(M_TAG, t.toString())
                }
            })
    }

    private fun initBookService() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com/") // 인터파크 베이스 주소;
            .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 사용;
            .build()

        bookService = retrofit.create(BookService::class.java)
    }

    private fun initBookRecyclerView() {
        bookRecyclerViewAdapter = BookAdapter(itemClickedListener = {
            val intent = Intent(this, DetailActivity::class.java)

            // 직렬화 해서 넘길 것.
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = bookRecyclerViewAdapter
    }

    private fun initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClickListener = {
            deleteSearchKeyword(it)
        }, this)

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter

        initSearchEditText()
    }


    fun bookServiceSearchBook(keyword: String) {

        bookService.getBooksByName(getString(R.string.interparkAPIKey), keyword)
            .enqueue(object : Callback<SearchBookDto> {
                // 성공.

                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    hideHistoryView()
                    saveSearchKeyword(keyword)

                    if (response.isSuccessful.not()) {
                        return
                    }

                    bookRecyclerViewAdapter.submitList(response.body()?.books.orEmpty()) // 새 리스트로 갱신
                }

                // 실패.
                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    hideHistoryView()
                    Log.e(M_TAG, t.toString())
                }
            })
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }

    private fun showHistoryView() {
        Thread {
            val keywords = db.historyDao().getAll().reversed()
            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()

    }

    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchEditText() {
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            // 키보드 입력시 발생

            // 엔터 눌렀을 경우 (눌렀거나, 뗏을 때 -> 눌렀을 때 발생하도록.)
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                bookServiceSearchBook(binding.searchEditText.text.toString())
                return@setOnKeyListener true// 처리 되었음.
            }
            return@setOnKeyListener false // 처리 안됬음 을 나타냄.
        }

        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    companion object {
        private const val M_TAG = "MainActivity"
    }
}