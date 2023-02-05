## What is this?
**Methane** is a _small_ mod, that aims to reduce lag by **completely removing _fog (without sodium)_ and lighting.** By doing this, the CPU and GPU no longer has to do lighting and fog calculations for everything, speeding up rendering performance. The client also no longer cares about getting lighting from the server, partially improving latency and further benefiting CPU usage.

## Benefits
- better framerates: ![some stats](https://github.com/AnOpenSauceDev/Methane-mod/blob/master/Propaganda%202.png?raw=true)
_*recorded with Methane 1.4.5, everything low, 5 sim and render distance_
- the same effect as fullbright, but with actual performance gains.
- infinitely faster lighting: ![img](https://github.com/AnOpenSauceDev/Methane-mod/blob/master/(MEME)%20vs%20starlight.png?raw=true)
_infinitely more performance in lighting due to never actually calculating it._

## Compared to fullbright
while fullbright makes the world look lit, it still performs all those lighting calculations. Methane instead skips those, resulting in the same end product but with much better performance.

## Can i use this with starlight?
Yes! As long as Starlight is only on the server. The main reason is that starlight will try to calculate lighting anyway, defeating the main purpose of Methane.

## Tested compatible _performance_ mods (as of 1.4.6)
- Vivecraft
- Sodium
- Starlight (on server-side)
- Iris Shaders (some lighting issues on non-raytraced shaders)
- Exordium
- DashLoader
- Lithium
- Smooth Boot
- FerriteCore
- LazyDFU
- Concurrent Chunk Management Engine
- Carpet
- Indium
- MemoryLeakFix
- Krypton
- A more complete list can be found [here](https://github.com/AnOpenSauceDev/Methane-mod/blob/master/Compatability.md)

## known bugs 🐞
~~Sodium can cause the world to become incredibly dark.~~ **Fixed in 1.3** <br>
~~The sky below the horizon is black, this is due to the fog not being able to cover it.~~ **Fixed in 1.3**<br>
Shaders can experience lighting issues when dealing with methane. <br>
Feel free to add any bugs you find to the [issue tracker](https://github.com/AnOpenSauceDev/Methane-mod/issues).
