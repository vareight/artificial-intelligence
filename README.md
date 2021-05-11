# Challenge-Tablut
Project for the tablut challenge, Foundations of Artificial Intelligence AA 2020-2021

# Tablut 

![tablutboard]()
Tablut is an medioeval northern Europe game, almost unknown. Linneo was the first to document the rules of this game, after he had seen some Lapps playing it, but theres no trace of the original rules.
The game is assymetric the players have different chekers and aims. The game board is a grid of 9X9 squares. Two players alternate in moving their checkers, the attacker is the Balck player and defender is the White player.


* White player at the beginning of the game owns 8 «Soldier» checkers and 1 «King» checker
* Black player at the beginning of the game owns 16 «Soldier» checkers.


The checkers can move only orthogonally (like the Tower in chess) any amount of squares without passing over another checkers or obstacles like the camps or the throne.
A checker is captured (and removed from the game) if it is surrounded by opponent’s checkers on 2 opposite sides
The aim of the white player is to make the King flee, reaching the side of the chessboard  to any of the “escape tiles", meanwhile the aim of the black is to capture the King.

# Strategies

Both white and black players have been implemented using  the Iterative Deepening Search  class from the aima-core library combined with MinMax algorithm and AlphaBeta cuts. The heuristic for the black mainly considers the encirclemet of  the pawns, empty rows and columns and the captured pawns. the white strategy considers mainly the...

# Download

Download the zip file from github or clone it from the command line

```bash
git clone https://github.com/vareight/challenge-tablut
cd ....
```

# Run from shell

Check your Java version, it should be higher than 11, JDK >= 11
To play the game you need to run a server and two players, so move in the directory jars and execute in three different shells

```bash
java -jar  Server.jar
java -jar tulbateam.jar WHITE 60 localhost
java -jar tulbateam.jar BLACK 60 localhost
```
The arguments of the clients are the role of the player "BLACK" or "WHITE", the timeout to decide the move in seconds, and the IP address of the server. 

```bash
java -jar <role> <timeout-in-seconds> <server-ip> 
```

# Run from virtual machine 

Download the virtual machine from this link: 
Then move in the directory /home/tablut/tablut and execute the Tablut player

```bash
cd /home/tablut/tablut 
./runmyplayer.sh <role> <timeout-in-seconds> <server-ip> 
```

The arguments of the clients are the role of the player "BLACK" or "WHITE", the timeout to decide the move in seconds, and the IP address of the server. For example:

```bash
./runmyplayer.sh WHITE 60 localhost
./runmyplayer.sh BLACK 60 localhost
```

# Team mates

@vareight           
@ilariacriv         
@sofiamontebugnoli  
