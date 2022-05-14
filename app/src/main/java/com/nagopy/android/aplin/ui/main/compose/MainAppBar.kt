package com.nagopy.android.aplin.ui.main.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.ui.main.MainUiState
import com.nagopy.android.aplin.ui.main.Screen
import com.nagopy.android.aplin.ui.main.SearchWidgetState
import com.nagopy.android.aplin.ui.theme.AplinTheme

@Composable
fun MainAppBar(
    navController: NavController,
    state: MainUiState,
    currentScreen: Screen,
    sharePackages: (List<PackageModel>) -> Unit,
    onTextChanged: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchTriggered: () -> Unit,
) {
    when (state.searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                navController = navController,
                state = state,
                currentScreen = currentScreen,
                sharePackages = sharePackages,
                onSearchTriggered = onSearchTriggered,
            )
        }
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                navController = navController,
                currentScreen = currentScreen,
                text = state.searchText,
                onTextChanged = onTextChanged,
                onCloseClicked = onCloseClicked,
            )
        }
    }
}

@Composable
fun DefaultAppBar(
    navController: NavController,
    state: MainUiState,
    currentScreen: Screen,
    sharePackages: (List<PackageModel>) -> Unit,
    onSearchTriggered: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = currentScreen.resourceId))
        },
        actions = {
            if (state.isLoading && state.packagesModel != null) {
                CircularProgressIndicator(color = Color.LightGray)
            }

            if (currentScreen is Screen.AppList && state.packagesModel != null) {
                IconButton(onClick = {
                    sharePackages.invoke(
                        currentScreen.getAppList(
                            state.packagesModel,
                            state.searchText,
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(id = R.string.share)
                    )
                }
            }

            IconButton(onClick = {
                onSearchTriggered()
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            }
        },
        navigationIcon = if (currentScreen != Screen.Top) {
            {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        } else {
            null
        },
    )
}

@Composable
fun SearchAppBar(
    navController: NavController,
    currentScreen: Screen,
    text: String,
    onTextChanged: (String) -> Unit,
    onCloseClicked: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    TopAppBar {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = text,
            onValueChange = onTextChanged,
            placeholder = {
                Text(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.search_hint),
                    color = Color.White
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize,
            ),
            singleLine = true,
            maxLines = 1,
            leadingIcon = if (currentScreen != Screen.Top) {
                {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            } else {
                null
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChanged("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // onSearchClicked(text)
                },
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium),
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
fun SearchAppBarPreview() {
    AplinTheme {
        Scaffold(
            topBar = {
                SearchAppBar(
                    navController = rememberNavController(),
                    currentScreen = Screen.Top,
                    text = "text",
                    onTextChanged = {},
                    onCloseClicked = {},
                )
            }
        ) {}
    }
}
