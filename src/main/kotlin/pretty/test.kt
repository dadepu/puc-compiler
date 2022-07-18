package pretty


fun main() {
    testInput("""      
      let rec readName = (\x => let _ = print("Bitte geben Sie Ihren Namen ein: ":blue) in
      let input = read(String) in 
      if input == "" || input == " " then let _ = print("Kein gültiger Name!!!":red) in readName 0 else input) in
      let rec readAge = (\x => let _ = print("Bitte geben Sie Ihr Alter ein: ":blue) in
      let input = read(Int) in
      if input == 0-2147483647 || input <= 0 || input > 120  then let _ = print("Kein gültiges Alter!!!":red) in readAge 0 else input) in
      let rec readBirthMonth = (\x => let _ = print("Bitte geben Sie Ihren Geburtsmonat als Zahl ein: ":blue) in
      let input = read(Int) in
      if input == 0-2147483647 || input <= 0 || input > 12 then let _ = print("Kein gültiger Monat!!!":red) in readBirthMonth 0 else input) in
      let name = readName 0 in 
      let age = readAge 0 in
      let month = readBirthMonth 0 in
      
      let deathTime = switch(age%month) {
        case <=3: age + (age%month)^2 + 2
        case <=6: age + (age%month)^2 + 4
        case <=9: age + (age%month)^2 + 8
        default: age + (age%month)^2 + 16
      } in
      
      let _ = if deathTime >= 80 
              then print("Der Wahrsager prophezeit dir gute Neuigkeiten:":green)
              else print("Der Wahrsager prophezeit dir schlechte Neuigkeiten:":red) 
              in
      let _ = print("§name, du stirbst im Alter von §deathTime Jahren an §injury verursacht durch §causeOfDeath":yellow) in
      3
      
    """.trimIndent())
}

private fun testInput(input: String) {
    val printer = PrettyPrinter()
    printer.format({ input }, { s -> println(s) })
}
