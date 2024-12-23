package hk.com.nmg.casestudyreactiveprogramming

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ContentView(modifier: Modifier, viewModel: MainViewModel = viewModel() ) {
    val articles: List<Article> by viewModel.articles.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(viewModel, 0) {
        viewModel.fetchArticles()
    }

    Column(modifier = modifier.padding(16.dp)) {
        TextField(
            value = searchText,
            onValueChange = { viewModel.onSearchTextChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search articles...", modifier = Modifier) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!error.isNullOrEmpty()) {
            Text("Error: $error", color = Color.Red)
        }

        LazyColumn {
            items(articles) { article ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(article.title, style = MaterialTheme.typography.bodyLarge)
                    Text("By ${article.author}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContentViewPreview() {
    MaterialTheme {
        ContentView(Modifier.fillMaxSize())
    }
}