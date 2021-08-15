package flashcards

import java.io.Serializable

data class Card(val front: String, val back: String, var mistakes: Int = 0): Serializable {
    fun print() {
        println("Card:")
        println(front)
        println("Definition:")
        println(back)
    }
}