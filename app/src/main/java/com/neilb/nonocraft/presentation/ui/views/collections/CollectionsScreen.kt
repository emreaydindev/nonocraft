package com.neilb.nonocraft.presentation.ui.views.collections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.neilb.nonocraft.R
import com.neilb.nonocraft.common.NavDestinations
import com.neilb.nonocraft.data.model.request.GameItem
import com.neilb.nonocraft.presentation.ui.lib.custom_game_dialog.CustomGameDialog
import com.neilb.nonocraft.presentation.ui.views.collections.components.PageSelector
import com.neilb.nonocraft.presentation.ui.views.collections.components.PuzzleItem
import com.neilb.nonocraft.presentation.ui.views.error.ErrorPage
import com.neilb.nonocraft.presentation.ui.views.main.MainViewModel
import com.neilb.nonocraft.presentation.util.isScrollingUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    isPublic: Boolean
) {

    val viewModel = hiltViewModel<CollectionsViewModel>()
    val state by viewModel.state
    val context = LocalContext.current

    val listState = rememberLazyStaggeredGridState()

    LaunchedEffect(state.data) {
        if (!state.data.isNullOrEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    val lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val onStart = {
        viewModel.getCollections(context, isPublic)
        viewModel.getDocumentSize(context, isPublic)
    }

    val currentOnStart by rememberUpdatedState(onStart)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                currentOnStart()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var createYourGameDialogVisibility by remember { mutableStateOf(false) }

    CustomGameDialog(
        visibility = createYourGameDialogVisibility,
        dismissDialog = { },
        title = stringResource(id = R.string.create_your_nonogram),
        context = LocalContext.current,
        onSubmit = {
            mainViewModel.createGame(it, saveLocally = false)
            navController.navigate(NavDestinations.CREATE_NONOGRAM_SCREEN)
        },
        emptyTable = true,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = if (isPublic) R.string.public_collections else R.string.your_collections))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null)
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(id = R.string.create))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                    )
                },
                onClick = { createYourGameDialogVisibility = true },
                expanded = listState.isScrollingUp(),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (!state.error.isNullOrBlank()) {
                ErrorPage(errorMessage = state.error!!)
            } else if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (!state.data.isNullOrEmpty()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                ) {
                    val puzzles = state.data
                    items(puzzles?.size ?: 0) {
                        PuzzleItem(game = puzzles?.get(it) ?: GameItem(), mainViewModel, navController, viewModel)
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        PageSelector(viewModel, isPublic)
                    }
                }
            } else {
                Text(
                    text = stringResource(id = R.string.no_puzzle_to_display),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

}

