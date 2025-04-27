package view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel
import androidx.compose.material.Button
import androidx.compose.material.Text

@Composable
fun <V, E> MainScreenView(viewModel: MainScreenViewModel<V, E>) {
	Column {
		Button (onClick = viewModel::calculateSCC){
			Text("Calculate SCC")
		}
	}
}
