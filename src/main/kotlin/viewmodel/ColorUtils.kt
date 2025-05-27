package viewmodel

import androidx.compose.ui.graphics.Color

private const val CIRCLE_GRADUS = 360
private const val SATURATION = 0.7f
private const val BRIGHTNESS = 0.9f
private const val COLOR_OFFSET = 137

/**
 * Utility object for generating and assigning colors to nodes or components.
 */
object ColorUtils {
	/**
	 * Assigns a distinct color to each group of values in [components].
	 * All values within the same sub-collection receive the same color.
	 * Colors are generated based on the group's index in the outer collection.
	 *
	 * @param components A collection of collections; each inner collection represents a group.
	 * @return A map from each value to its assigned [Color].
	 */
	fun <T> assignColorsGrouped(components: Collection<Collection<T>>): Map<T, Color> {
		val mapping = mutableMapOf<T, Color>()
		components.forEachIndexed { idx, comp ->
			val color = generateColor(idx)
			comp.forEach { node -> mapping[node] = color }
		}
		return mapping
	}

	/**
	 * Assigns a unique color to each value in [values], independent of grouping.
	 * Colors are generated based on the value's index in the collection.
	 *
	 * @param values A collection of items to color.
	 * @return A map from each value to its assigned [Color].
	 */
	fun <T> assignColorsAll(values: Collection<T>): Map<T, Color> {
		val mapping = mutableMapOf<T, Color>()
		values.forEachIndexed { idx, elem ->
			mapping[elem] = generateColor(idx)
		}
		return mapping
	}

	/**
	 * Generates a visually distinct color for a given [index].
	 * Uses HSB color space distribution with fixed saturation and brightness,
	 * and varies hue by multiplying the index by [COLOR_OFFSET].
	 *
	 * @param index The index used to compute hue.
	 * @return A [Color] instance corresponding to the computed HSB value.
	 */
	fun generateColor(index: Int): Color {
		val hue = ((index * COLOR_OFFSET) % CIRCLE_GRADUS) / CIRCLE_GRADUS.toFloat()
		val rgbInt = java.awt.Color.HSBtoRGB(hue, SATURATION, BRIGHTNESS)
		return Color(rgbInt)
	}

	/**
	 * Applies a precomputed [colorMap] to a set of [Colorable] view models.
	 * Each view model's [Colorable.color] is set to the color corresponding to its model key,
	 * or to [defaultColor] if no mapping exists for that key.
	 *
	 * @param colorMap Map from model values to [Color]s.
	 * @param colorable Collection of [Colorable] objects to color.
	 * @param defaultColor Color to assign when a model key is missing in [colorMap].
	 */
	internal fun <T, C : Colorable<T>> applyColors(
		colorMap: Map<T, Color>,
		colorable: Collection<C>,
		defaultColor: Color = Color.Black
	) {
		for (viewModel in colorable) {
			viewModel.color = colorMap[viewModel.model] ?: defaultColor
		}
	}

	/**
	 * Applies a [color] to a collection of [Colorable] objects.
	 * @param colorable Collection of [Colorable] objects to color.
	 * @param color The color to apply to all objects.
	 */
	internal fun <T, C : Colorable<T>> applyOneColor(colorable: Collection<C>, color: Color) {
		colorable.forEach { it.color = color }
	}
}
