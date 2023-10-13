# Methane Contribution Guide
The guidelines are pretty simple, there are just a few things you should follow:

- make sure to comment on your code so it's readable.
- try not to rely _too_ heavily on the Fabric API.
- try to seperate your own code into different classes and/or packages for further clarity.
- Instead of flooding the users logs with debug messages via `System.out.println` or `MethaneLogger.info`, please use the methods in the `Debug` class in `util` instead.

Happy programming!
