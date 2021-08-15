package flashcards

fun main() {
    val cards = readCards()
    startTest(cards)
}

fun startTest(cards: Collection<Card>) {
    for (card in cards) {
        println("Print the definition of \"${card.front}\":")
        val answer = readLine()!!
        if (answer == card.back) {
            println("Correct!")
        } else {
            print("Wrong. The right answer is \"${card.back}\"")
            val sameDefCard = cards.find { it.back == answer }
            if (sameDefCard != null) {
                print(", but your definition is correct for \"${sameDefCard.front}\"")
            }
            println(".")
        }
    }
}

private fun readCards(): Collection<Card> {
    println("Input the number of cards:")
    val number = readLine()!!.toInt()
    val cards = mutableListOf<Card>()
    for (i in 1..number) {
        println("Card #$i:")
        val term = getUniqueTerm(cards)

        println("The definition for card #$i:")
        val def = getUniqueDef(cards)

        val card = Card(term, def)
        cards.add(card)
    }
    return cards
}

private fun getUniqueTerm(cards: MutableList<Card>): String {
    while (true) {
        val term = readLine()!!
        if (cards.find { it.front == term } != null) {
            println("The term \"$term\" already exists. Try again:")
        } else {
            return term
        }
    }
}

private fun getUniqueDef(cards: MutableList<Card>): String {
    while (true) {
        val def = readLine()!!
        if (cards.find { it.back == def } != null) {
            println("The definition \"$def\" already exists. Try again:")
        } else {
            return def
        }
    }
}
