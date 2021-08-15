package flashcards

data class Card(val front: String, val back: String) {
    fun print() {
        println("Card:")
        println(front)
        println("Definition:")
        println(back)
    }
}