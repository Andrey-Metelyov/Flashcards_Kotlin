package flashcards

import java.io.*

val logText = mutableListOf<String>()

fun main(args: Array<String>) {
    System.err.println("args: ${args.joinToString()}")
    val cards = mutableSetOf<Card>()
    val importIndex = args.indexOf("-import")
    val exportIndex = args.indexOf("-export")
    val importFile = if (importIndex == -1 || importIndex >= args.lastIndex) null else args[importIndex + 1]
    val exportFile = if (exportIndex == -1 || exportIndex >= args.lastIndex) null else args[exportIndex + 1]

    if (importFile != null) {
        importCardsFromFile(cards, importFile)
    }

    while (true) {
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        val action = readLine()!!
        when (action) {
            "add" -> readCard(cards)
            "remove" -> removeCard(cards)
            "import" -> importCards(cards)
            "export" -> exportCards(cards)
            "ask" -> startTest(cards)
            "log" -> log(cards)
            "hardest card" -> hardestCard(cards)
            "reset stats" -> resetStats(cards)
            "exit" -> break
        }
    }
    println("Bye bye!")
    if (exportFile != null) {
        exportCardsToFile(exportFile, cards)
    }
//    val cards = readCards()
//    startTest(cards)
}

fun println(line: String) {
    kotlin.io.println(line)
    logText.add(line)
}

fun readLine(): String? {
    val line = kotlin.io.readLine()
    logText.add(line?:"")
    return line
}

fun resetStats(cards: MutableSet<Card>) {
    for (card in cards) {
        card.mistakes = 0
    }
    println("Card statistics have been reset.")
}

fun hardestCard(cards: MutableSet<Card>) {
    System.err.println(cards.joinToString())
    val card = cards.maxByOrNull { it.mistakes }
    if (card == null || card?.mistakes == 0) {
        println("There are no cards with errors.")
        return
    }
    val maxMistakeCards = cards.filter { it.mistakes == card?.mistakes }
    if (maxMistakeCards.size == 1) {
        println("The hardest card is \"${card?.front}\". You have ${card?.mistakes} errors answering it")
    } else if (maxMistakeCards.size > 1) {
        println("The hardest cards are ${maxMistakeCards.map { "\"${it.front}\"" }.joinToString() }. You have ${card?.mistakes * maxMistakeCards.size} errors answering them")
    }
}

fun log(cards: MutableSet<Card>) {
    println("File name:")
    val filename = readLine()
    File(filename).writeText(logText.joinToString(System.lineSeparator()))
    println("The log has been saved.")
}

fun exportCards(cards: MutableCollection<Card>) {
    println("File name:")
    val fileName = readLine()!!
    exportCardsToFile(fileName, cards)
}

private fun exportCardsToFile(fileName: String, cards: MutableCollection<Card>) {
    ObjectOutputStream(FileOutputStream(fileName)).use { it ->
        it.writeObject(cards)
    }
    println("${cards.size} cards have been saved.")
}

fun importCards(cards: MutableCollection<Card>) {
    println("File name:")
    val fileName = readLine()!!
    importCardsFromFile(cards, fileName)
}

private fun importCardsFromFile(cards: MutableCollection<Card>, fileName: String) {
    val oldSize = cards.size
    try {
        ObjectInputStream(FileInputStream(fileName)).use { it ->
            when (val fileCollection = it.readObject()) {
                is MutableCollection<*> -> {
                    val fileCards: Collection<Card> = fileCollection as Collection<Card>
                    System.err.println("cards: ${cards.joinToString()}")
                    System.err.println("fileCards: ${fileCards.joinToString()}")
                    val filtered = fileCards.filter { card -> cards.find { it.front == card.front } != null }
                    cards.removeIf { card -> filtered.find { it.front == card.front } != null }
                    System.err.println("filtered: ${filtered.joinToString()}")
                    cards.addAll(fileCards)
                    println("${fileCards.size} cards have been loaded.")
                    System.err.println("cards: ${cards.joinToString()}")
                }
                else -> println("Deserialization failed")
            }
        }
    } catch (e: FileNotFoundException) {
        println("File $fileName not found")
    }
}

fun removeCard(cards: MutableCollection<Card>) {
    println("Which card?")
    val term = readLine()!!
    if (cards.removeIf { it.front == term }) {
        println("The card has been removed.")
    } else {
        println("Can't remove \"$term\": there is no such card.")
    }
}

fun startTest(cards: Collection<Card>) {
    println("How many times to ask?")
    val times = readLine()!!.toInt()
    for (i in 1..times) {
        val card = cards.random()
        println("Print the definition of \"${card.front}\":")
        val answer = readLine()!!
        if (answer == card.back) {
            println("Correct!")
        } else {
            card.mistakes++
            print("Wrong. The right answer is \"${card.back}\"")
            val sameDefCard = cards.find { it.back == answer }
            if (sameDefCard != null) {
                print(", but your definition is correct for \"${sameDefCard.front}\"")
            }
            println(".")
        }
    }
}

private fun readCard(cards: MutableCollection<Card>) {
    println("The card:")
    val term = readLine()!!
    if (cards.find { it.front == term } != null) {
        println("The card \"$term\" already exists.")
        return
    }
    println("The definition of the card:")
    val def = readLine()!!
    if (cards.find { it.back == def } != null) {
        println("The definition \"$def\" already exists.")
        return
    }
    val card = Card(term, def)
    cards.add(card)
    println("The pair (\"$term\":\"$def\") has been added.")
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
