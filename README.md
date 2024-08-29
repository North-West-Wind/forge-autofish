# AutoFish for Forge
Finally! An AFK fishing mod for Forge users!

## Download
1. Go to the CurseForge page [here](https://www.curseforge.com/minecraft/mc-mods/autofish-for-forge)
2. Find the version of the mod you want
3. Find the Minecraft version of the mod you want
4. Download
5. Enjoy!

Thanks for using the mod!

### Currently supported versions
1.21.x  
(Multiple versions were too much for me. I'm sorry.)

### Unsupported, but we have their files
1.20.x  
1.19.x  
1.18.x  
1.17.x  
1.16.x  
1.15.x  
1.14.4  
1.13.2  
1.12.2  
1.11.2  
1.10.2  
1.9.4  
1.8.9

## What does it do?
This mod allows you to AFK fish (as long as the server allows AFK) anywhere. Can I use it in my singleplayer world? Yes! Can I use it on servers? Yes! The mod is completely client-side! You just need a Forge client on your computer, put this mod into the "mods" folder and you finished the setup! How easy it is!

Note: Putting the mod into the "mods" folder of a server will NOT do anything.
There is also NO Fabric version of this mod, as there are other fishing mods for Fabric already.

## Why did I make this?
To answer that, we need to talk about ~~parallel universe~~ the 1.16 update of Minecraft. If you read the changelogs, there is 1 particular part that nerfed the entire AFK fishing farm. Basically, it still allows players to fish with it, but you will not get any treasure (e.g. Enchanted Books, Saddles, etc.). On the other hand, you can still get fish from it. Since the farm is nerfed, players started to create other designs of the fishing farm. However, those are not as good as the old ones. That's what causes me to make this mod, which I think quite a lot of players needed it.

After I made the very first version of the mod, why not make it for more versions of Minecraft? And that's the reason I made the mod for more versions. I know some other Forge fishing mods exist in older versions, but they are not accurate.

## How does it work?
Programmers have probably looked at the source code already, but allow me to explain that for non-coders.

When the mod is enabled, it will look for the bobber of the player. You may think that I look for the state of the bobber but no. The state of the bobber is not public, and there is no public methods that returns the state, so it is impossible to listen for change of state.

However, I found a way simplier method to know if is catch a fish...\*drumroll\* Motion. Since, the bobber is an entity, we can track its motion. As we all know, the bobber sinks into the water when it catches a fish. By tracking the vertical motion of the bobber, we can know when it catches a fish. It is simple as that!
