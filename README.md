# WorldEditSlimefun

This plugin only works on Paper and it's forks.

WorldEditSlimefun allows people to paste in Slimefun blocks.
This can especially be helpful for addon creators, server owners and people who test plugins.
This can be helpful with testing how optimized the blocks are or if they would cause any issues in mass usage.

## Commands
- `/wesf pos1` This command sets the location for position 1 for your paste/clear command.
- `/wesf pos2` This command sets the location for position 2 for your paste/clear command.
- `/wesf paste <Slimefun ID>` This command pastes the block you specified in the world with the position you chose. It has 1 argument that is the slimefun item id. This can be any placeable Slimefun or Slimefun Addon block.
- `/wesf clear <boolean>` This command clears the blocks you have selected with the position commands. When `true` it fires a blockbreakevent coming from a player. This is used to clear all the handlers. When `false` it just removes the block and the blockstorage data.

## Download
You can find the download of this addon in on [Blob Builds](https://blob.build/project/WorldEditSlimefun).
