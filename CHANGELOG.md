# v0.0.2 The button textures update
### Features:
- Now the (formerly) "P" and "R" buttons have proper textures ot them
- Change `panoramas_button` texture to `load_panoramas_button`
- Add `remove_panoramas_button` texture
### Other Changes:
- Simplified / resolved positioning of buttons
- Removed unused code (unused panoramas options screen)
- The changelog loaded to Modrinth is now named `CHANGELOG_NEWEST.md` and contains only the newest version without the version header (the main `CHANGELOG.md` remains)
- Added links to the mod's GitHub page

# v0.0.1 The initial version
### Features:
- Added a keybind for creating a panorama (default `H`)
- Added 3 config options
  - `Resolution` dictates what size would the panorama be. Its calculated from window height times this multiplier
  - `Hide panoramas resourcepacks` determines whether the panoramas resourcepacks should be displayed or not
  - `Automatically set new panoramas` is used to automatically load the panorama you just created as the current panorama
- Added 2 buttons to the world select screen
  - The `P` button which is enabled only when a world with a panorama is selected will set the world's panorama as the title screen panorama
  - The `R` button is always enabled and will reset the panorama to Minecraft's default
