package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.graph.Vertex

/**
 * ViewModel for a vertex
 *
 * @param x x coordinate
 * @param y y coordinate
 * @param color Main color
 * @param borderColor Second color for a border
 * @param radius radius of a vertex
 * @param borderWidth the thickness of a border
 * @param model Vertex model
 */
class VertexViewModel<V>(
	x: Dp = 0.dp,
	y: Dp = 0.dp,
	color: Color,
	borderColor: Color,
	var radius: Dp,
	var borderWidth: Dp,
	override val model: Vertex<V>
): Colorable<Vertex<V>> {
	private val _x = mutableStateOf(x)
	private val _y = mutableStateOf(y)
	private val _color = mutableStateOf(color)
	private val _borderColor = mutableStateOf(borderColor)

	override var color: Color
		get() = _color.value
		set(value) { _color.value = value }

	var borderColor: Color
		get() = _borderColor.value
		set(value) { _borderColor.value = value }

	var x: Dp
		get() = _x.value
		set(value) { _x.value = value }

	var y: Dp
		get() = _y.value
		set(value) { _y.value = value }

	/**
	 * Moves x and y coordinates using offset
	 * @param offset offset to move
	 */
	fun move(offset: Offset) {
		_x.value += offset.x.dp
		_y.value += offset.y.dp
	}
	/**
	 * Converts dp to float type
	 * @return Float coordinates
	 */
	fun toFloat(): Pair<Float, Float> {
		return x.value to y.value
	}

}
