package ru.mixail_akulov.a32_mvvm_flows1.simplemvvm.views.changecolor

import ru.mixail_akulov.a32_mvvm_flows1.simplemvvm.model.colors.NamedColor

/**
 * Представляет элемент списка для цвета; его можно выбрать или нет
 */
data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)