package com.example.arch.core.common.extensions

fun String?.isNotNullOrBlank(): Boolean = !isNullOrBlank()

fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercaseChar() }
    }

fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword(): Boolean = length >= 8 && any { it.isDigit() } && any { it.isLetter() }

fun String.truncate(maxLength: Int, ellipsis: String = "…"): String =
    if (length <= maxLength) this else take(maxLength) + ellipsis
