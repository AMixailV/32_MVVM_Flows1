package ru.mixail_akulov.a32_mvvm_flows1.foundation

/**
 * Implement this interface in your Application class.
 * Do not forget to add the application class into the AndroidManifest.xml file.
 */
interface BaseApplication {

    /**
     * The list of singleton scope dependencies that can be added to the fragment
     * view-model constructors.
     */
    val singletonScopeDependencies: List<Any>
}