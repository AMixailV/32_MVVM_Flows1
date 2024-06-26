package ru.mixail_akulov.a32_mvvm_flows1.simplemvvm.model.colors

import kotlinx.coroutines.flow.Flow
import ru.mixail_akulov.a32_mvvm_flows1.foundation.model.Repository

typealias ColorListener = (NamedColor) -> Unit

/**
 * Пример интерфейса репозитория.
 *
 * Предоставляет доступ к доступным цветам и текущему выбранному цвету.
 */
interface ColorsRepository : Repository {

    /**
     * Получить список всех доступных цветов, которые может выбрать пользователь.
     */
    suspend fun getAvailableColors(): List<NamedColor>

    /**
     * Get the color content by its ID
     */
    suspend fun getById(id: Long): NamedColor

    /**
     * Get the current selected color.
     */
    suspend fun getCurrentColor(): NamedColor

    /**
     * Set the specified color as current.
     */
    fun setCurrentColor(color: NamedColor): Flow<Int>

    /**
     * Слушайте текущие изменения цвета.
     * Слушатель запускается немедленно с текущим значением при вызове этого метода.
     */
    fun listenCurrentColor(): Flow<NamedColor>
}