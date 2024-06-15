package ru.mixail_akulov.a32_mvvm_flows1.simplemvvm.views.changecolor

import androidx.lifecycle.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mixail_akulov.a32_mvvm_flows1.R
import ru.mixail_akulov.a32_mvvm_flows1.foundation.model.PendingResult
import ru.mixail_akulov.a32_mvvm_flows1.foundation.model.Result
import ru.mixail_akulov.a32_mvvm_flows1.foundation.model.SuccessResult
import ru.mixail_akulov.a32_mvvm_flows1.foundation.sideeffects.navigator.Navigator
import ru.mixail_akulov.a32_mvvm_flows1.foundation.sideeffects.resources.Resources
import ru.mixail_akulov.a32_mvvm_flows1.foundation.sideeffects.toasts.Toasts
import ru.mixail_akulov.a32_mvvm_flows1.foundation.views.BaseViewModel
import ru.mixail_akulov.a32_mvvm_flows1.foundation.views.ResultFlow
import ru.mixail_akulov.a32_mvvm_flows1.foundation.views.ResultMutableStateFlow
import ru.mixail_akulov.a32_mvvm_flows1.simplemvvm.model.colors.ColorsRepository
import ru.mixail_akulov.a32_mvvm_flows1.simplemvvm.model.colors.NamedColor
import ru.mixail_akulov.a32_mvvm_flows1.simplemvvm.views.changecolor.ChangeColorFragment.Screen

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors: ResultMutableStateFlow<List<NamedColor>> = MutableStateFlow(PendingResult())
    private val _currentColorId = savedStateHandle.getsStateFlow  ("currentColorId", screen.currentColorId)
    private val _saveInProgress = MutableStateFlow(false)

    // основной пункт назначения (содержит объединенные значения из _availableColors, _currentColorId и _saveInProgress)
    val viewState: ResultFlow<ViewState> = combine(
        _availableColors,
        _currentColorId,
        _saveInProgress,
        ::mergeSources
    )

    // example of converting Flow into LiveData
    // - входящий поток Flow<Result<ViewState>>
    // - Flow<Result<ViewState>> сопоставляется с Flow<String> by using .map() operator
    // - then Flow<String> is converted to LiveData<String> by using .asLiveData()
    val screenTitle: LiveData<String> = viewState
        .map { result ->
            return@map if (result is SuccessResult) {
                val currentColor = result.data.colorsList.first { it.selected }
                resources.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
            } else {
                resources.getString(R.string.change_color_screen_title_simple)
            }
        }.asLiveData()


    init {
        load()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _saveInProgress.value = true

            // this code is launched asynchronously in other thread
            val currentColorId = _currentColorId.value ?: throw IllegalStateException("Color ID should not be NULL")
            val currentColor = colorsRepository.getById(currentColorId)
            // здесь мы не хотим прослушивать прогресс, а только ждем завершения операции,
            // поэтому мы используем collect() без какого-либо внутреннего блока:
            colorsRepository.setCurrentColor(currentColor).collect()

            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e is CancellationException) toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _saveInProgress.value = false
        }
    }


    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }
    /**
    * Чистый метод преобразования для объединения данных из нескольких входных потоков:
     * - результат выборки списка цветов (Result<List<NamedColor>>)
     * - текущий выбранный цвет в RecyclerView (Long)
     * - флаг, выполняется ли операция сохранения или нет (Boolean)
     * Все приведенные выше значения объединены в один экземпляр [ViewState]:
    * ```
    * Flow<Result<List<NamedColor>>> ---+
    * Flow<Long> -----------------------|--> Flow<Result<ViewState>>
    * Flow<Boolean> --------------------+
    * ```
    */
    private fun mergeSources(colors: Result<List<NamedColor>>, currentColorId: Long, saveInProgress: Boolean): Result<ViewState> {

        // map Result<List<NamedColor>> to Result<ViewState>
        return colors.map { colorsList ->
            ViewState(
                // map List<NamedColor> to List<NamedColorListItem>
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showSaveProgressBar = saveInProgress
            )
        }
    }

    private fun load() = into(_availableColors) {
        colorsRepository.getAvailableColors()
    }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean
    )
}