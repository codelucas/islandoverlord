islandoverlord
==============

One of my bigger and more complicated `java applet` games. This is a Sims-eque game where the user plays as a "god" or "ruler" of a self sustaining island. The villagers interact with the natural elements of the island. 

This was built because I wanted a platform to implement large scale polymorphism. Also, the code organizaiton was inspired by many of [`Notch's`](https://mojang.com/notch/) games. This game was built to be easy to maintain, but it slightly got out of hand. Every object in the game extends the `Entity` class, which has draw() and tick() methods. Every X real seconds, the game ticks the island, which in turn ticks all of the island's entities. 

As god you can decide the fate of these villagers with god powers. Graphics were hand drawn via GIMP. No external libraries or frameworks were used aside from the Java SDK.

Try the game out for yourself [HERE](http://codelucas.com/pages/island-overlord.html).


![My image](http://i1145.photobucket.com/albums/o516/lukepop/da522032.png)
