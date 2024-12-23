package hk.com.nmg.casestudyreactiveprogramming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MainViewModel : ViewModel() {
    private val repository = ArticleRepository()

    // State for search input
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    // State for filtered articles
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    init {
        // Combine search text and articles to filter results reactively
        scope.launch {
            _searchText
                .debounce(300) // Reduce frequent updates
                .combine(repository.fetchArticles()) { searchText, articles ->
                    articles.filter { article ->
                        searchText.isEmpty() || article.title.contains(searchText, ignoreCase = true)
                    }
                }
                .catch { e ->
                    _error.emit("Error fetching articles: ${e.message}")
                }
                .collect {
                    _articles.emit(it)
                }
        }
    }

    fun fetchArticles(): Job = viewModelScope.launch {
        repository.fetchArticles().collect {
            _articles.emit(it)
        }

    }

    fun onSearchTextChanged(newText: String) {
        _searchText.value = newText
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
