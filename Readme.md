# Realistic Paralyze
Realistic Paralyze is much like normal paralyze apart from instead of completely blocking a players chi, it blocks their actions instead. This ability was based of the Realistic Paralyze thread by Finn_Bueno_. Thanks to him for this amazing idea.

### How to Use
Simply hit another bender. If you are sneaking when you hit them, their sneaking will be disabled. If you aren't sneaking, their click will be disabled. Additionally, they will also be given slowness (which can be configured).

### Config
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
- **Duration** - How long someone is paralyzed for. Measured in milliseconds.3 times.
- **Cooldown** - How long the cooldown applied is. Measured in milliseconds.
- **Slowness.Enabled** - If slowness should be given to hit targets
- **Slowness.Duration** - How long the target should be slowed for. Measured in milliseconds.
- **Slowness.Level** - What level of slowness is given to the user.

### Requirements
* ProjectKorra 1.8.0 or above (compatible with 1.8.3)
* Spigot 1.8 - 1.11.2

(Make sure the ProjectKorra version you have is compatible with your version of spigot)

### Lisense
You can copy, edit and redistribute all the code for JackOSplosion as long as you keep StrangeOne101 as one of the original authors. You may use parts of this code for your own abilities or plugins but don't have to credit me as long as your project isn't based off this one.