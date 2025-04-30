package viewmodel

import androidx.compose.ui.graphics.Color

/**
 * Interface for objects that can be colored with [Color]
 */
interface Colorable<T> {
	var color: Color
	val model: T
}
