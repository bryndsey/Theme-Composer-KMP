package dev.bryanlindsey.staccato

import java.io.File

fun deleteRecursive(fileOrDirectory: File) {
    if (fileOrDirectory.isDirectory) {
        fileOrDirectory.listFiles().forEach { child ->
            deleteRecursive(child)
        }
    }
    fileOrDirectory.delete()
}
