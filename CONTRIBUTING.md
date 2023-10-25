# Methane Contribution Guide
The guidelines are pretty simple, there are just a few things you should follow:

## Programmers

- make sure to comment on your code so it's readable.
- try not to rely _too_ heavily on the Fabric API.
- try to seperate your own code into different classes and/or packages for further clarity.
- Instead of flooding the users logs with debug messages via `System.out.println` or `MethaneLogger.info`, please use the methods in the `Debug` class in `util` instead.
- Speaking of which, the `Debug` utilities are quite powerful for adding your own developer-only features to the game, without having to introduce bugs to non-developer users.

## If you don't know how to code, or want to help out in another way...

If you don't know how to code, you can still help out by reporting bugs or helping translate the mod to other languages!

Contributors are listed in GitHub and `fabric.mod.json` (feel free to add yourself to it in your PR).
