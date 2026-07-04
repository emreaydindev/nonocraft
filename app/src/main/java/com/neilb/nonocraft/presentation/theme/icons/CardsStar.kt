package com.neilb.nonocraft.presentation.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val cards_star: ImageVector
    get() {
        if (_cards_star != null) {
            return _cards_star!!
        }
        _cards_star =
            ImageVector.Builder(
                name = "cards_star",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(12.23f, 12.5f)
                        lineTo(14.5f, 11.13f)
                        lineToRelative(2.28f, 1.38f)
                        lineTo(16.18f, 9.9f)
                        lineToRelative(2f, -1.72f)
                        lineTo(15.55f, 7.95f)
                        lineTo(14.5f, 5.5f)
                        lineTo(13.45f, 7.95f)
                        lineTo(10.83f, 8.17f)
                        lineToRelative(2f, 1.72f)
                        lineToRelative(-0.6f, 2.6f)
                        close()
                        moveTo(12.7f, 19f)
                        horizontalLineToRelative(5.6f)
                        quadToRelative(-0.18f, 0.65f, -0.6f, 1.05f)
                        quadToRelative(-0.42f, 0.4f, -1.1f, 0.5f)
                        lineTo(5.7f, 21.88f)
                        quadTo(4.88f, 22f, 4.21f, 21.49f)
                        reflectiveQuadTo(3.45f, 20.15f)
                        lineTo(2.13f, 9.23f)
                        quadTo(2.03f, 8.4f, 2.53f, 7.75f)
                        reflectiveQuadTo(3.85f, 7f)
                        lineTo(5f, 6.85f)
                        verticalLineToRelative(2f)
                        lineTo(4.1f, 8.98f)
                        lineTo(5.45f, 19.9f)
                        lineTo(12.7f, 19f)
                        close()
                        moveTo(9f, 17f)
                        quadTo(8.18f, 17f, 7.59f, 16.41f)
                        reflectiveQuadTo(7f, 15f)
                        verticalLineTo(4f)
                        quadTo(7f, 3.17f, 7.59f, 2.59f)
                        reflectiveQuadTo(9f, 2f)
                        horizontalLineTo(20f)
                        quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                        reflectiveQuadTo(22f, 4f)
                        verticalLineTo(15f)
                        quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                        reflectiveQuadTo(20f, 17f)
                        horizontalLineTo(9f)
                        close()
                        moveTo(9f, 15f)
                        horizontalLineTo(20f)
                        verticalLineTo(4f)
                        horizontalLineTo(9f)
                        verticalLineTo(15f)
                        close()
                        moveTo(14.5f, 9.5f)
                        close()
                        moveTo(5.45f, 19.9f)
                        close()
                    }
                }
                .build()
        return _cards_star!!
    }

private var _cards_star: ImageVector? = null
