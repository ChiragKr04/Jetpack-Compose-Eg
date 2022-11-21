package com.chirag.jetpackpractice1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import com.chirag.jetpackpractice1.ui.theme.JetpackPractice1Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackPractice1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val scaffoldState = rememberScaffoldState()
                    val coroutineScope = rememberCoroutineScope()
                    val names = remember {
                        mutableStateListOf("Abc", "Def", "Ghi", "Jkl")
                    }
                    var counter by remember {
                        mutableStateOf(0)
                    }
                    Scaffold(
                        scaffoldState = scaffoldState,
                        floatingActionButtonPosition = FabPosition.End,
                        topBar = {
                            TopAppBar(
                                elevation = 10.dp,
                                title = {
                                    Text(
                                        "Quiz App",
                                        style = TextStyle(
                                        fontSize = MaterialTheme.typography.body1.fontSize
                                    ))
                                }
                            )},
                        floatingActionButton = {
                            Row {
                                ExtendedFloatingActionButton(
                                    text = {
                                        Text("Add Item",
                                            style= TextStyle(fontSize = 17.sp)
                                        ) },
                                    icon = {Icon(
                                        modifier = Modifier.size(30.dp),
                                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                                        contentDescription = "add_icon")},
                                    onClick = {
                                        counter++
                                        names.add(counter.toString())
                                        Log.d("floatingbtn","Add PRESSED $counter - $names")
                                    })
                                Spacer(modifier = Modifier.padding(start = 10.dp))
                                ExtendedFloatingActionButton(
                                    text = {
                                        Text("Remove Item",
                                            style= TextStyle(fontSize = 17.sp)
                                        ) },
                                    icon = {Icon(
                                        modifier = Modifier.size(30.dp),
                                        painter = painterResource(id = R.drawable.ic_baseline_remove_24),
                                        contentDescription = "remove_icon")},
                                    onClick = {
                                        if(counter > 0) {
                                            counter--
                                            names.removeLast()
                                            Log.d("floatingbtn", "Remove PRESSED $counter - $names")
                                        }
                                    })
                            }
                        }
                    ) {
                        Column(
                            Modifier.verticalScroll(rememberScrollState())
                        ) {
//                        Greeting("Android")
                            CardView(names = names, callBack = { value ->
                                val message = if(value==names.first()) "Correct Answer!" else "Wrong Answer!"
                                coroutineScope.launch {
                                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = "Ok"
                                    )
                                    delay(2000)
                                    when (snackbarResult) {
                                        SnackbarResult.Dismissed ->
                                            Log.d("SnackbarDemo", "Dismissed")
                                        SnackbarResult.ActionPerformed ->
                                            Log.d("SnackbarDemo", "Snackbar's button clicked")
                                        else -> {}
                                    }

                                }
                            })
                            ToggleCard()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackPractice1Theme {
        Greeting("Android")
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardView(names:List<String>, callBack:(String)-> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFFFFFF))
            .padding(all = 5.dp)
    ) {
        var selectedItem by remember {
            mutableStateOf<String?>(null)
        }
        for(name in names){
            Row(
                modifier = Modifier
                .padding(PaddingValues(vertical = 6.dp, horizontal = 5.dp))
            ) {
//                var isClicked by remember {
//                    mutableStateOf(false)
//                }
                val currText = if(selectedItem == name) "Yes this is $name!" else "Hello $name!"
                Card(modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(50.dp)
                    .align(Alignment.CenterVertically)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jetpack),
                        contentDescription = "row_icon")
                }
                Spacer(modifier = Modifier.padding(start = 10.dp))
                Card(
                    border= BorderStroke(
                        2.5.dp,
                        if(selectedItem == name) Color.Green else Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        Log.d("name","current name: $name")
//                        isClicked = !isClicked
                        selectedItem = name
                    }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            currText,
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 21.sp,
                            ),
                            modifier = Modifier
                                .padding(PaddingValues(vertical = 15.dp, horizontal = 10.dp))
                        )
                        RadioButton(
                            selected = if(selectedItem==null) false else selectedItem==name,
                            onClick = {
                            selectedItem = name
                        })
                    }
                }
            }
        }
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
            callBack(selectedItem.toString())
        }) {
            Text("Check Answer")
        }
    }
}

@Composable
fun ToggleCard() {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val cardHeight = screenHeight.value * 0.5
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .height(cardHeight.dp)
        .clickable {
            isExpanded = !isExpanded
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

    ) {
        Image(
            painter = painterResource(id = R.drawable.jetpack),
            contentDescription = "icon"
        )
        AnimatedVisibility(visible = isExpanded) {
            Text(text="What's Up", style = MaterialTheme.typography.h3)
        }
    }
}

@Preview
@Composable
fun CardPreview() {
//    CardView(names = listOf("Abc","Xy"))
}
