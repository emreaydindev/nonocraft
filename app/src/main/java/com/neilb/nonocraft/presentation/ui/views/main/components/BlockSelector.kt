package com.neilb.nonocraft.presentation.ui.views.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.neilb.nonocraft.R
import com.neilb.nonocraft.domain.model.Block
import com.neilb.nonocraft.domain.model.Game
import com.neilb.nonocraft.presentation.ui.lib.color_selector_dialog.ColorSelectorDialog
import com.neilb.nonocraft.presentation.ui.theme.goldColor
import com.neilb.nonocraft.presentation.ui.theme.greenColor
import com.neilb.nonocraft.presentation.ui.views.main.MainViewModel

@Composable
fun BlockSelector(
    modifier: Modifier = Modifier,
    game: Game,
    selectedBlock: Block,
    updateSelectedBlock: (Block) -> Unit,
    deleteColorTraces: ((Color) -> Unit) = {},
    isDesigner: Boolean = false
) {

    val designerColors = remember { mutableStateListOf<Color>() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row {
            BlockSelectorItem(
                selected = selectedBlock == Block(Block.empty),
                backgroundColor = Color.Black,
                isEmpty = true,
                onClickEvent = {
                    updateSelectedBlock(Block(Block.empty))
                },
                onLongClickEvent = {}
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (game.type == MainViewModel.blackAndWhite) {
                BlockSelectorItem(
                    selected = selectedBlock == Block(Block.black),
                    backgroundColor = Color.Black,
                    isEmpty = false,
                    onClickEvent = {
                        updateSelectedBlock(Block(Block.black))
                    },
                    onLongClickEvent = {}
                )
            } else {
                var colors =
                    designerColors.ifEmpty { game.solvedTable.flatten().mapNotNull { it.color }.toSet().toList() }
                repeat(colors.size) {
                    BlockSelectorItem(
                        selected = selectedBlock == Block(Block.coloured, colors[it]),
                        backgroundColor = colors[it],
                        isEmpty = false,
                        onClickEvent = {
                            updateSelectedBlock(Block(Block.coloured, colors[it]))
                        },
                        onLongClickEvent = {
                            if (isDesigner) {
                                if (selectedBlock == Block(Block.coloured, colors[it])) {
                                    updateSelectedBlock(Block(Block.empty))
                                }
                                deleteColorTraces(colors[it])
                                designerColors.remove(colors[it])
                                colors =
                                    designerColors.ifEmpty { game.solvedTable.flatten().mapNotNull { it.color }.toSet().toList() }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        if (game.type == MainViewModel.coloured && isDesigner) {

            var colorDialogVisibility by remember { mutableStateOf(false) }

            ColorSelectorDialog(
                visibility = colorDialogVisibility,
                dismissDialog = { colorDialogVisibility = false },
                onSubmit = {
                    if (!designerColors.contains(it))
                        designerColors.add(it)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))
            Button(onClick = { colorDialogVisibility = true }, colors = ButtonDefaults.buttonColors(
                containerColor = greenColor
            )) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(id = R.string.add_color))
            }

        }
    }
}

@Composable
fun BlockSelectorItem(
    selected: Boolean,
    backgroundColor: Color,
    isEmpty: Boolean,
    onClickEvent: () -> Unit,
    onLongClickEvent: () -> Unit = {}
) {
    var modifier: Modifier = Modifier
        .padding(8.dp)
        .size(36.dp)
        .background(backgroundColor, CircleShape)
        .clip(CircleShape)

    val borderColor = if (selected) goldColor else if (isSystemInDarkTheme()) Color.White else Color.Black
    val borderWidth = if (selected) 4.dp else 2.dp

    modifier = modifier.border(borderWidth, borderColor, CircleShape)

    Box(
        modifier = modifier.combinedClickable(
            onClick = onClickEvent,
            onLongClick = onLongClickEvent
        ),
        contentAlignment = Alignment.Center
    ) {
        if (isEmpty) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}