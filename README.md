# WorldEditSlimefun

This plugin only works on Paper and it's forks.

WorldEditSlimefun allows people to paste in Slimefun blocks.
This can especially be helpful for addon creators, server owners and people who test plugins.
This can be helpful with testing how optimized the blocks are or if they would cause any issues in mass usage.

## Commands
- `/wesf wand` 
  - This command gives a player the selection wand
- `/wesf pos1`
  - This command sets position 1 of your selection.
- `/wesf pos2` 
  - This command sets position 2 of your selection.
- `/wesf paste <slimefun_block> <energy> <inputs>` 
  - This command pastes the block specified in the area selected.
  - `<slimefun_block>` (required), a string id, it can be any slimefun or slimefun addon block
  - `<energy>` (optional), a boolean, charges the pasted blocks to max integer so they can process pseudo-infinitely (if possible)
  - `<inputs>` (optional), a string array, places these items in the blocks input slots (if possible)
- `/wesf clear <call_event>`
  - This command clears the blocks you have selected with the position commands. 
  - `<call_event>` (optional), a boolean, if a `BlockBreakEvent` should be used to trigger item handlers. (defaults to `false`)

## Download
You can find the download of this addon in the [releases](https://github.com/Slimefun-Addon-Community/WorldEditSlimefun/releases/tag/latest) tab
