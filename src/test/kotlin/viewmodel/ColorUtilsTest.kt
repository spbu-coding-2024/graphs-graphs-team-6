package viewmodel

import androidx.compose.ui.graphics.Color
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ColorUtilsTest {

	@Test
	fun `generateColor produces distinct colors and wraps around correctly`() {
		val c0 = ColorUtils.generateColor(0)
		val c1 = ColorUtils.generateColor(1)
		val c360 = ColorUtils.generateColor(360)

		assertNotEquals(c0, c1, "Colors for different indices should not be equal")
		assertEquals(c0, c360, "Color for index 360 should wrap to index 0")
	}

	@Test
	fun `assignColorsAll assigns a unique color based on index`() {
		val values = listOf("a", "b", "c")
		val mapping = ColorUtils.assignColorsAll(values)

		assertEquals(3, mapping.size, "Mapping should contain all values")
		assertEquals(ColorUtils.generateColor(0), mapping["a"], "Color for 'a' should be generated for index 0")
		assertEquals(ColorUtils.generateColor(1), mapping["b"], "Color for 'b' should be generated for index 1")
		assertEquals(ColorUtils.generateColor(2), mapping["c"], "Color for 'c' should be generated for index 2")
	}

	@Test
	fun `assignColorsGrouped assigns same color within groups and distinct across groups`() {
		val group1 = listOf("x", "y")
		val group2 = listOf("z")
		val components = listOf(group1, group2)
		val mapping = ColorUtils.assignColorsGrouped(components)

		val colorGroup1 = ColorUtils.generateColor(0)
		val colorGroup2 = ColorUtils.generateColor(1)

		assertEquals(colorGroup1, mapping["x"], "Items in the first group should share the same color")
		assertEquals(colorGroup1, mapping["y"], "Items in the first group should share the same color")
		assertEquals(colorGroup2, mapping["z"], "Items in the second group should have a different color")
	}

	@Test
	fun `applyColors sets correct colors or default for missing keys`() {
		val itemA = mockk<Colorable<String>>(relaxed = true)
		val itemB = mockk<Colorable<String>>(relaxed = true)
		val itemC = mockk<Colorable<String>>(relaxed = true)

		every { itemA.model } returns "a"
		every { itemB.model } returns "b"
		every { itemC.model } returns "c"

		val colorMap = mapOf(
			"a" to Color.Red,
			"c" to Color.Blue
		)

		ColorUtils.applyColors(colorMap, listOf(itemA, itemB, itemC))

		verify { itemA.color = Color.Red }
		verify { itemB.color = Color.Black }
		verify { itemC.color = Color.Blue }
	}

	@Test
	fun `applyOneColor overrides all items with the same color`() {
		val items = (1..3).map { id ->
			mockk<Colorable<Int>>(relaxed = true).apply { every { model } returns id }
		}
		val overrideColor = Color.Magenta

		ColorUtils.applyOneColor(items, overrideColor)

		items.forEach { item ->
			verify { item.color = overrideColor }
		}
	}
}
