package v15project_print_v2

import kotlinx.collections.immutable.persistentHashMapOf

val emptyEnv: Env = persistentHashMapOf()
val initialEnv: Env = persistentHashMapOf(
  "firstChar" to Value.Closure(emptyEnv, "x",
    Expr.Var("#firstChar")
  ),
  "remainingChars" to Value.Closure(emptyEnv, "x",
    Expr.Var("#remainingChars")
  ),
  "charCode" to Value.Closure(emptyEnv, "x",
    Expr.Var("#charCode")
  ),
  "codeChar" to Value.Closure(emptyEnv, "x",
    Expr.Var("#codeChar")
  )
)


fun testInput(lex: Boolean, expr: Boolean, eval: Boolean, infer: Boolean, input: String) {
  if (lex) {
    val lexer = Lexer(input)
    print("\nLEXER: ")
    do {
      print(lexer.next()); print(" ")
    } while (lexer.lookahead() != Token.EOF)
  }
  if (expr) {
      val expression = Parser(Lexer(input)).parseExpression()
      print("\nEXPRESSION: " )
      print(expression)
  }
  if (eval && infer) {
    val expression = Parser(Lexer(input)).parseExpression()
    val ty = infer(initialContext, expression)
    print("\nEVAL: " )
    print("${eval(initialEnv, expression) } : ${prettyPoly(generalize(initialContext, applySolution(ty)))}")
  }
  if (eval && !infer) {
    val expression = Parser(Lexer(input)).parseExpression()
    print("\nEVAL: " )
    print("${eval(initialEnv, expression) }")
  }
  println()
}





fun main() {
  testInput(false,false,true, true, """
    let hello = "Hello" in
    let world = "World" in
    let join = \s1 => \s2 => s1 # " " # s2 in
    let shout = \s => s # "!" in
    let twice = \f => \x => f (f x) in
    twice (twice shout) (join hello world)
  """.trimIndent())


  testInput(false,false,true, true, """
    if 1 != 2 && 3 <= 3 then
    1
    else
    2
  """.trimIndent())

  testInput(false,false,true, true, """
    let x = "Hello" in
    let y = "World" in
    let a = 3 in
    let b = 3 in
    if x != y && a < 4 then
    "richtig"
    else
    "falsch"
  """.trimIndent())

  testInput(false,false,true, true, """
      let a = read(Int) in
      switch (a) {
        case <1: 1
        case 6: 6
        case >2: 2
        case !=3: 3
        default: 4
      }
   """.trimMargin()
  )



  testInput(true,true,true, true, """
      let _ = print("") in
      let _ = print("                                          ____":red) in
      let _ = print("                                      .-/      \-.":red) in
      let _ = print("                                     /            \":red) in
      let _ = print("                                    |              |":red) in
      let _ = print("                                    |,  .-.  .-.  ,|":red) in
      let _ = print("                                    | )(__/  \__)( |":red) in
      let _ = print("                                    |/     /\     \|":red) in
      let _ = print("                                    (_     ^^     _)":red) in
      let _ = print("                                     \__|IIIIII|__/":red) in
      let _ = print("                                      | \IIIIII/ |":red) in
      let _ = print("                                      \          /":red) in
      let _ = print("                                       `--------`":red) in                    
      let _ = print("--------------------------------------------------------------------------------------------":blue) in
      let _ = print("                              💀 THE INSANE FORTUNE TELLER  💀                              ":purple) in
      let _ = print("--------------------------------------------------------------------------------------------":blue) in
      
      let rec readName = \x => let _ = print("Bitte geben Sie Ihren Namen ein: ":blue) in
      let input = read(String) in 
      if input == "" || input == " " then let _ = print("Kein gültiger Name!!!":red) in readName 0 else input in
      
      let rec readAge = \x => let _ = print("Bitte geben Sie Ihr Alter ein: ":blue) in
      let input = read(Int) in
      if input == 0-2147483647 || input <= 0 || input > 120  then let _ = print("Kein gültiges Alter!!!":red) in readAge 0 else input in
      
      let rec readBirthMonth = \x => let _ = print("Bitte geben Sie Ihren Geburtsmonat als Zahl ein: ":blue) in
      let input = read(Int) in
      if input == 0-2147483647 || input <= 0 || input > 12 then let _ = print("Kein gültiger Monat!!!":red) in readBirthMonth 0 else input in
      
      let rec readCigarettes = \x => let _ = print("Bitte geben Sie die Anzahl an Zigaretten ein, die Sie in der Woche rauchen: ":blue) in
      let input = read(Int) in 
      if input == 0-2147483647 || input < 0 then let _ = print("Keine gültige Anzahl!!!":red) in readCigarettes 0 else input in
      
      let rec readBeer = \x =>  let _ = print("Bitte geben Sie die Anzahl an Flaschen Bier ein, die Sie in der Woche trinken: ":blue) in
      let input = read(Int) in
      if input == 0-2147483647 || input < 0 then let _ = print("Keine gültige Anzahl!!!":red) in readBeer 0 else input in
      
      let rec readBooze = \x =>  let _ = print("Trinken Sie regelmäßig Hochprozentigen (ja/nein): ":blue) in
      let input = read(String) in
      if input != "nein" && input != "ja" then let _ = print("Keine gültige Antwort!!!":red) in readBooze 0 else if input=="ja" then true else false in
      
      let rec readHeadache = \x => let _ = print("Wie stark waren Ihre letzten Kopfschmerzen auf einer Skala von 1 bis 10?: ":blue) in 
      let input = read(Int) in
      if input == 0-2147483647 || input < 0 || input > 10 then let _ = print("Keine gültige Angabe!!!":red) in readHeadache 0 else input in
      
      let rec readHeadacheDay = \x => let _ = print("Vor wie vielen Tagen hatten Sie die Kopfschmerzen ca.?: ":blue) in 
      let input = read(Int) in
      if input == 0-2147483647 || input < 0 then let _ = print("Keine gültige Angabe!!!":red) in readHeadacheDay 0 else input in

 
      let name = readName 0 in 
      let age = readAge 0 in 
      let month = readBirthMonth 0 in 
      let cigarettes = readCigarettes 0 in 
      let beer = readBeer 0 in 
      let booze = readBooze 0 in 
      let headache = readHeadache 0 in
      let headacheDay = readHeadacheDay 0 in 
      
      
      let deathTime = switch(age%month) {
        case <=3: age + (age%month)^2 + 2
        case <=6: age + (age%month)^2 + 4
        case <=9: age + (age%month)^2 + 8
        default: age + (age%month)^2 + 16
      } in

      let injury = switch((cigarettes+beer+6)% if booze then 5 else 6) {
        case 0: "einer Infektion am Bein"
        case 1: "einem Genickbruch"
        case 2: "hohem Blutverlust"
        case 3: "einem Herzinfarkt"
        case 4: "einer Vergiftung"
        default: "multiplem Organversagen"
      } in

      let causeOfDeath = switch((headache+headacheDay+6)%6) {
        case 0: "einen Biss eines atomar verseuchten Bieber"
        case 1: "einen Sturz von einem Hochhaus"
        case 2: "einen Ausrutscher in der Dusche"
        case 3: "einen plötzlichen Lachanfall"
        case 4: "einen Flugzeugabsturz"
        default: "einen Mückenstich"
      } in
      
            
      let _ = print("--------------------------------------------------------------------------------------------":blue) in
      let _ = if deathTime >= 80 
              then print("Der Wahrsager prophezeit dir gute Neuigkeiten:":green)
              else print("Der Wahrsager prophezeit dir schlechte Neuigkeiten:":red) 
              in
      let _ = print("Hi §name: du stirbst im Alter von §deathTime Jahren an §injury verursacht durch §causeOfDeath":yellow) in
      print("--------------------------------------------------------------------------------------------":blue)
   """
  )

}