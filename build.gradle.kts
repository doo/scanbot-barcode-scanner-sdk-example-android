
plugins {
    id("com.android.application") version "8.9.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id( "org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}
