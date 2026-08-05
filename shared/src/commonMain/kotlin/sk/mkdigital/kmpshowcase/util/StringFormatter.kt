package sk.mkdigital.kmpshowcase.util

expect object StringFormatter {
    fun formatDouble(value: Double, decimals: Int): String
}
