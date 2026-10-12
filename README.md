# Bird Style Client — first prototype

This is a fresh Fabric 1.21.11 project scaffold for the GUI shown in your reference video.

## Important status
- This is a **GUI prototype**, not yet a transfer of the original Prestige modules.
- The visible module switches currently only toggle prototype entries; they do not activate the original client modules.
- The existing client JAR is not bundled or modified.
- The original client source is obfuscated and tightly coupled to its own event, setting, and module systems. Reusing those modules requires further dependency analysis and integration.

## Build on GitHub from a phone
1. Create a new GitHub repository.
2. Upload the contents of this folder (not the folder itself).
3. Open **Actions** and run **Build Bird Style Client** (or push to main).
4. Open the completed workflow run and download the `bird-style-client` artifact.

## Open the GUI
In Minecraft, press **Right Shift**.

## Next step
Once this skeleton builds, the next phase is integrating original modules where their dependencies can be resolved. If a module cannot be reused safely, recreate it one at a time.
