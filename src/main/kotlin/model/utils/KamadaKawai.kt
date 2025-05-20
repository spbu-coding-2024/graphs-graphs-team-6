package model.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import model.Constants.DEFAULT_STRENGTH_CONSTANT
import model.Constants.EPSILON
import model.graph.Graph
import viewmodel.GraphViewModel
import viewmodel.VertexViewModel
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class KamadaKawai<V, K, W: Comparable<W>> (viewModel: GraphViewModel<V, K, W>) {
    private var deltaMap: MutableMap<V, Float> = mutableMapOf()

    init {
        for (vertex in viewModel.graph.vertices) {
            deltaMap[vertex.value] = 0F
        }
    }

    private fun shortestPath(graph: Graph<V, K, W>): Map<Pair<V, V>, Float?> {
        val dist: MutableMap<Pair<V, V>, Float?> = mutableMapOf()
        for (edges in graph.edges) {
            val firstValue = edges.startVertex.value
            val secondValue = edges.endVertex.value
            val value = graph.getEdge(firstValue, secondValue)?.weightToFloat()
            if (value != null && value <= 0) throw IllegalArgumentException("Weights must be positive")
            dist[firstValue to secondValue] = value
        }
        for (startVertex in graph.vertices) {
            for (middleVertex in graph.vertices) {
                for (endVertex in graph.vertices) {
                    val straightPath = dist[startVertex.value to endVertex.value] ?: continue

                    val first = dist[startVertex.value to middleVertex.value] ?: continue
                    val second = dist[middleVertex.value to endVertex.value] ?: continue
                    val sum = first + second
                    if (straightPath > sum) {
                        dist[startVertex.value to endVertex.value] = sum
                        dist[endVertex.value to startVertex.value] = sum
                    }
                }
            }
        }
        return dist
    }

    private fun calculateMaxDelta(
        viewModel: GraphViewModel<V, K, W>,
        strength: Map<Pair<V, V>, Float>, length: Map<Pair<V, V>, Float>
    ): VertexViewModel<V> {

        var particle = viewModel.vertices.first()
        var maxDelta = -1F
        for (mVertex in viewModel.vertices) {
            var derivativeX = 0F
            var derivativeY = 0F
            for (vertex in viewModel.vertices) {
                if (mVertex.model == vertex.model) continue
                val pair = mVertex.model.value to vertex.model.value
                val strength = strength[pair] ?: continue
                val length = length[pair] ?: continue

                val (currentX, currentY) = vertex.returnFloat()
                val (mX, mY) = mVertex.returnFloat()
                val norm = sqrt((mX - currentX).pow(2) + (mY - currentY).pow(2))

                derivativeX += strength * (mX - currentX) * ( 1 - length / norm )
                derivativeY += strength * (mY - currentY) * ( 1 - length / norm )
            }
            val currentDelta = sqrt(derivativeX.pow(2) + derivativeY.pow(2))
            deltaMap[mVertex.model.value] = currentDelta
            if (maxDelta < currentDelta) {
                maxDelta = currentDelta
                particle = mVertex
            }
        }
        return particle
    }

    fun compute(viewModel: GraphViewModel<V, K, W>) {
        val dist = shortestPath(viewModel.graph)
        var maxDist = 0F

        val strength: MutableMap<Pair<V, V>, Float> = mutableMapOf()
        val length: MutableMap<Pair<V, V>, Float> = mutableMapOf()
        for (firstVertex in viewModel.graph.vertices) {
            for (secondVertex in viewModel.graph.vertices) {
                val currentDistance = dist[firstVertex.value to secondVertex.value] ?: continue
                if (maxDist < currentDistance) {
                    maxDist = currentDistance
                }
            }
        }
        val lengthConstant = 500 / maxDist
        for (firstVertex in viewModel.graph.vertices) {
            for (secondVertex in viewModel.graph.vertices) {
                if (firstVertex == secondVertex) continue
                val currentDist = dist[firstVertex.value to secondVertex.value] ?: continue
                val strengthCurrentValue = DEFAULT_STRENGTH_CONSTANT / (currentDist * currentDist)
                val lengthCurrentValue =  lengthConstant * currentDist
                strength[firstVertex.value to secondVertex.value] = strengthCurrentValue
                strength[secondVertex.value to firstVertex.value] = strengthCurrentValue
                length[firstVertex.value to secondVertex.value] = lengthCurrentValue
                length[secondVertex.value to firstVertex.value] = lengthCurrentValue
            }
        }

        var mVertex = calculateMaxDelta(viewModel, strength, length)
        while (deltaMap[mVertex.model.value] != null && deltaMap[mVertex.model.value]!! > EPSILON) {
            while (deltaMap[mVertex.model.value] != null && deltaMap[mVertex.model.value]!! > EPSILON) {
                var derivativeXX = 0F
                var derivativeYY = 0F
                var derivativeXY = 0F
                var derivativeX = 0F
                var derivativeY = 0F
                for (vertex in viewModel.vertices) {
                    if (mVertex.model == vertex.model) continue
                    val pair = mVertex.model.value to vertex.model.value
                    val strength = strength[pair] ?: continue
                    val length = length[pair] ?: continue

                    val (currentX, currentY) = vertex.returnFloat()
                    val (mX, mY) = mVertex.returnFloat()


                    val norm = sqrt((mX - currentX).pow(2) + (mY - currentY).pow(2))

                    derivativeX += strength * (mX - currentX) * (1 - length / norm)
                    derivativeY += strength * (mY - currentY) * (1 - length / norm)

                    derivativeXX += strength * (1 - (length * (mY - currentY).pow(2)) /norm.pow(3))
                    derivativeXY += strength * ((length * (mX - currentX) * (mY - currentY) )/ norm.pow(3))
                    derivativeYY += strength * (1 - (length * (mX - currentX).pow(2))/ norm.pow(3))
                }
                val determinant = derivativeXX * derivativeYY - derivativeXY * derivativeXY
                val deltaX = -(derivativeX * derivativeYY - derivativeY * derivativeXY) / determinant
                val deltaY = -(derivativeXX * derivativeY - derivativeX * derivativeXY) / determinant
                require(deltaX.isFinite() && deltaY.isFinite())
                mVertex.x += deltaX.dp
                mVertex.y += deltaY.dp

                deltaMap[mVertex.model.value] = sqrt(derivativeX.pow(2) + derivativeY.pow(2))
            }
            mVertex = calculateMaxDelta(viewModel, strength, length)
        }
    }
}
