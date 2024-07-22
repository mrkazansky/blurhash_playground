package com.example.blurhash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.blurhash.hash.BlurHash
import com.example.blurhash.ui.theme.BlurHashTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
class MainActivity : ComponentActivity() {

  companion object {
    private const val TAG = "MainActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      BlurHashTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            var hash by remember { mutableStateOf("") }
            var encodeTime by remember { mutableLongStateOf(0L) }
            val context = LocalContext.current
            var componentX by remember { mutableStateOf("4") }
            var componentY by remember { mutableStateOf("3") }
            val coroutineScope = rememberCoroutineScope()

            Spacer(modifier = Modifier.height(40.dp))

            Image(
              modifier = Modifier
                .width(400.dp)
                .height(200.dp)
                .padding(24.dp),
              painter = painterResource(id = R.drawable.image),
              contentDescription = ""
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
              TextField(
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Number,
                  imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.width(120.dp),
                value = componentX,
                onValueChange = {
                  componentX = it
                },
                placeholder = {
                  Text(
                    text = "X counts"
                  )
                })
              Spacer(modifier = Modifier.width(40.dp))
              TextField(
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Number,
                  imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.width(120.dp),
                value = componentY,
                onValueChange = {
                  componentY = it
                },
                placeholder = {
                  Text(
                    text = "Y counts"
                  )
                })
            }

            Spacer(modifier = Modifier.height(16.dp))

            val keyboard = LocalSoftwareKeyboardController.current

            Button(onClick = {
              keyboard?.hide()
              hash = "Calculating..."
              val start = System.currentTimeMillis()
              encodeTime = 0L
              val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image)
              renderHash(
                context,
                coroutineScope,
                bitmap,
                componentX.toIntOrNull() ?: 1,
                componentY.toIntOrNull() ?: 1
              ) {
                hash = it
                encodeTime = (System.currentTimeMillis() - start)
              }
            }) {
              Text("Apply")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              textAlign = TextAlign.Center,
              text = "Blur Hash (${encodeTime}ms):"
            )

            Text(
              textAlign = TextAlign.Center,
              text = hash,
              textDecoration = TextDecoration.Underline
            )

            Spacer(modifier = Modifier.height(20.dp))
            var decodeTime by remember { mutableLongStateOf(0L) }
            var bitmap by remember { mutableStateOf<Bitmap?>(null) }
            LaunchedEffect(key1 = hash) {
              if (hash.isNotEmpty()) {
                val start = System.currentTimeMillis()
                bitmap = BlurHash.decode(hash, 300, 150)
                decodeTime = (System.currentTimeMillis() - start)
              }
            }
            bitmap?.let {
              Image(
                modifier = Modifier
                  .width(400.dp)
                  .height(200.dp)
                  .padding(24.dp),
                bitmap = it.asImageBitmap(),
                contentDescription = ""
              )
              Text(text = "Decoded Time (${decodeTime}ms)")
            }
          }
        }
      }
    }
  }

  private fun renderHash(
    context: Context,
    scope: CoroutineScope,
    bitmap: Bitmap,
    compX: Int,
    compY: Int,
    onSuccess: (String) -> Unit
  ) {
    if (compX !in 1..9 || compY !in 1..9) {
      Toast.makeText(context, "Wrong comp input", LENGTH_SHORT).show()
      return
    }
    scope.launch(Dispatchers.Default) {
      val hash = BlurHash.encode(bitmap, compX, compY)
      onSuccess(hash)
    }
  }
}