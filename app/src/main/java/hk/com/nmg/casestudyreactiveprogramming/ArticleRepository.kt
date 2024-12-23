package hk.com.nmg.casestudyreactiveprogramming

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ArticleRepository {
    private val articles = listOf(
        Article(1, "Kotlin Flow and MVVM", "Author A"),
        Article(2, "Jetpack Compose Basics", "Author B"),
        Article(3, "Reactive Programming in Android", "Author C")
    )

    fun fetchArticles(): Flow<List<Article>> {
        return flow {
            delay(1000) // Simulate network delay
            emit(articles)
        }
    }
}
