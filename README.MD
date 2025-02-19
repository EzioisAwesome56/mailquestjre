# MailQuest: Java Edition
### based on the concepts created for [the original gameboy version](https://github.com/EzioisAwesome56/gameboy-thing)
___
## Why rewrite in java?
The gameboy version was getting kind of unwieldy to manage, with nearly 900 lines of assembly in a single file and
not having proper function calls or methods or even if statements to properly follow the code.<br><br>

Java does not suffer these problems, it makes coding a larger project like this far easier, code is easier  to read and
follow, and as a bonus it has fancier graphics APIs built in compared to trying to do it on the gameboy!
## Is this a custom game engine?
Technically speaking, I guess you could call a lot of this code base a "Game Engine",
(I half-jokingly call it Dank Engine internally), but I'm not sure if this actually qualifies as such.
Most of the code is  handled using Java's Graphics2D apis for rendering, instead of hand-rolling my own rendering like most typical game engines.
It also heavily uses AWT for image loading, and swing for displaying game frames and taking in user input. However, everything else,
like script file syntax and other file layouts, where designed by hand, with the exception of  map block data, which  is the raw .tmx files output by [the wonderful tiled  map editor](http://www.mapeditor.org/),
which is parsed directly using Java's DOM parser and apache commons CSV.
## System Requirements
You need at least Java 8, but it has been tested during development to run on java 21.<br>
The following JVMs have been tested and have been confirmed to, at the bare minimum, work for basic game  play:
* Amazon Corretto 8.392.08.1 (Primary JVM, did most of the testing and compiling)
* Amazon Corretto 21.0.3.9.1
* Void Linux's OpenJDK 11
* IBM Semeru 21.0.4
* GraalVM Community 21.0.2
* ...you get the point, if it's a reasonably standard JVM that ships most of what you'd find on a typical desktop JVM, it'll probably run the game
## Why is there no audio?
I learned while working on the gameboy version that making music is hard, so I opted to not include any audio for that version.
The same reason stands here, no audio because I am awful at making it and it would sound bad
## Enough tech shit, what is this game?
"MailQuest" is a Role Playing Game, with gameplay concepts similar to the likes of various other RPG games that I have played previously.
The battles are turn-based, with a singular character you use to fight all of them. You can find new weapons around the world to increase your stats,
but also leveling up helps in that regard too!
## What is the plot?
<details>
    <summary>Basic Plot Spoilers</summary>
    You play as some character whom had their mail stolen at the start of the game. You eventually come to find out that
the PostMaster has set this evil plan into action, and you have to stop him. But not before you take out the four PostMen that the PostMaster has sent out to try
and stop whoever wants to stop him.
</details>
More plot details to come as the game matures or something<br>

---
## Build from source
- Clone this repo
- make maven  download all the dependencies (idk how to this on the CLI, in intellij you right click the pom and click "sync project")
- run `mvn package`
- check `target` for a jar file with "jar-with-dependencies" in it's name. Run that with a JVM of your choice with `java -jar <jar file>`

## License
TODO: decide. Investigate licensing game code and game assets separately perhaps