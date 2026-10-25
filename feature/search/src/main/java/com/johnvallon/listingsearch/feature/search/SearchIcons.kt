package com.johnvallon.listingsearch.feature.search

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Small outline icons for the search screen, drawn as vectors so the feature
 * needs no extra icon dependency. Tint them with Icon(tint = ...).
 */
internal object SearchIcons {
    val Search: ImageVector by lazy {
        strokeIcon("Search", "M4 11a7 7 0 1 0 14 0a7 7 0 1 0 -14 0", "M20 20l-3.5 -3.5")
    }
    val SearchOff: ImageVector by lazy {
        strokeIcon(
            "SearchOff",
            "M4 11a7 7 0 1 0 14 0a7 7 0 1 0 -14 0", "M20 20l-3.5 -3.5",
            "M8.5 8.5l5 5", "M13.5 8.5l-5 5",
        )
    }
    val Pin: ImageVector by lazy {
        strokeIcon(
            "Pin",
            "M12 21c0 0 -7 -6.2 -7 -11a7 7 0 0 1 14 0c0 4.8 -7 11 -7 11z",
            "M9.5 10a2.5 2.5 0 1 0 5 0a2.5 2.5 0 1 0 -5 0",
        )
    }
    val Bed: ImageVector by lazy {
        strokeIcon(
            "Bed",
            "M3 18V6", "M3 14h18v4", "M21 14v-2a3 3 0 0 0 -3 -3h-8v5",
            "M5 11a1.5 1.5 0 1 0 3 0a1.5 1.5 0 1 0 -3 0",
        )
    }
    val Bath: ImageVector by lazy {
        strokeIcon("Bath", "M4 12h16v3a4 4 0 0 1 -4 4H8a4 4 0 0 1 -4 -4v-3z", "M6 12V6a2 2 0 0 1 4 0")
    }
    val Area: ImageVector by lazy {
        strokeIcon("Area", "M4 4h16v16H4z", "M4 9h4", "M15 4v4")
    }
    val Home: ImageVector by lazy {
        strokeIcon("Home", "M4 11l8 -7l8 7v9h-5v-6h-6v6H4z")
    }
    val Tune: ImageVector by lazy {
        strokeIcon(
            "Tune",
            "M4 7h10", "M18 7h2", "M4 17h4", "M12 17h8",
            "M14 7a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
            "M8 17a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
        )
    }
    val WifiOff: ImageVector by lazy {
        strokeIcon(
            "WifiOff",
            "M3 3l18 18",
            "M8.5 16.2a5 5 0 0 1 6.2 -0.6",
            "M5 12.8a10 10 0 0 1 4.6 -2.5",
            "M14.4 10.3a10 10 0 0 1 4.6 2.5",
            "M2 8.8a15 15 0 0 1 4 -2.5",
            "M10.6 5.1a15 15 0 0 1 11.4 3.7",
            "M12 20h0.01",
        )
    }
    val ChevronUp: ImageVector by lazy {
        strokeIcon("ChevronUp", "M6 15l6 -6l6 6", strokeWidth = 2.2f)
    }
    val Alert: ImageVector by lazy {
        strokeIcon(
            "Alert",
            "M3 12a9 9 0 1 0 18 0a9 9 0 1 0 -18 0", "M12 7.5v5.5", "M12 16.5v0.01",
            strokeWidth = 2f,
        )
    }
}

private fun strokeIcon(name: String, vararg paths: String, strokeWidth: Float = 1.8f): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        paths.forEach { data ->
            addPath(
                pathData = addPathNodes(data),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = strokeWidth,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()
