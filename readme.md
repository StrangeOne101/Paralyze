### Realistic Paralyze

#### Description

Realistic Paralyze is much like normal paralyze apart from instead of completely blocking a players chi, it blocks their actions instead. This ability was based of the Realistic Paralyze thread by Finn_Bueno_. Thanks to him for this amazing idea. :smiley:

#### How to Use

Simply hit another bender. If you are sneaking when you hit them, their sneaking will be disabled. If you aren't sneaking, their click will be disabled. Additionally, they will also be given slowness (which can be configured).

![gif](https://projectkorra.com/forum/proxy.php?image=http%3A%2F%2Fi.imgur.com%2Fofk9ZYI.gif&hash=71175b98c2a687cf371f6276daa49ed4)

#### Config

```
ExtraAbilities:
  StrangeOne101:
    Paralyze:
      Duration: 6500
      Cooldown: 9000
      Slowness:
        Enabled: true
        Duration: 6500
        Level: 2
```

+ **Duration**   : How long someone is paralyzed for. Measured in milliseconds.
+ **Cooldown**   : How long the cooldown applied is. Measured in milliseconds.
+ **Slowness**
  - **Enabled**  : If slowness should be given to hit targets
  - **Duration** : How long the target should be slowed for. Measured in milliseconds.
  - **Level**    : What level of slowness is given to the user.

#### Requirements

+ ProjectKorra 1.8.0 or above (**compatible with 1.8.3**)
+ Spigot 1.8 - 1.11.2

(Make sure the ProjectKorra version you have is compatible with your version of spigot)

#### MUST NOT BE THE 1.8.0 BETAS!

I hope you all enjoy this ability! If you did enjoy this ability, please don't be shy to leave a positive review :smiley:

> "This is not an official ProjectKorra ability, therefore, no official support will be provided in any threads other than this one. Use at your own risk."