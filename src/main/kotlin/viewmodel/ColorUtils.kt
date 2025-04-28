package viewmodel

import androidx.compose.ui.graphics.Color
import model.Vertex

private const val CIRCLE_GRADUS = 360
private const val SATURATION = 0.7f
private const val BRIGHTNESS = 0.9f
private const val COLOR_OFFSET = 137

class ColorUtils {
	companion object {
		fun <T> assignColorsGrouped(components: Collection<Collection<T>>): Map<T, Color> {
			val mapping = mutableMapOf<T, Color>()
			components.forEachIndexed { idx, comp ->
				val color = generateColor(idx)
				comp.forEach { node -> mapping[node] = color }
			}
			return mapping
		}

		fun <T>	assignColorsAll(elements: Collection<T>): Map<T, Color> {
			val mapping = mutableMapOf<T, Color>()
			elements.forEachIndexed { idx, elem ->
				mapping[elem]= generateColor(idx)
			}
			return mapping
		}

		fun generateColor(index: Int): Color {
			val hue = ((index * COLOR_OFFSET) % CIRCLE_GRADUS) / (CIRCLE_GRADUS).toFloat()
			val rgbInt = java.awt.Color.HSBtoRGB(hue, SATURATION, BRIGHTNESS)
			return Color(rgbInt)
		}

		internal fun <T, C: Colorable> applyColors(colorMap: Map<T, Color>, colorable: Collection<C>) {
			colorMap.keys.zip(colorable).forEach { (model, viewmodel) ->
				viewmodel.color = colorMap[model]
					?: throw error("Missing color for $model")
			}
		}
	}
}