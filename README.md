# unity-mmo-server

<p>I made this project to learn about online games netcode.</>

<p>It is heavily inspired of L2J project as I am mostly following the same structure.</p>

<p>The objective is to make it generic and not only focused on the old mmorpg Lineage2</p>

<p>I use it to handle multiplayer in my other project [l2-unity](https://gitlab.com/shnok/l2-unity).</p>

## Current features

For now it contains only basic features of an online RPG game. <p>Such as:
- Chat features
- Player sync
	- Position
	- Rotation
	- Animation
- Basic NPC AI
	- Patrol around point
	- Spawn/respawn
- Pathfinding
- Server Ghosting/Grid system
- Npc list and spawn list persistence
- Basic combat
- Itemization (gear/inventory)

</p>

## Packets structure

### Client Packets

```python
' Packet structure '
[ B:`opcode`|B:`packet size`|... payload ]

' Payloads '
# 0x00 : Ping
[]
# 0x01 : AuthRequest
[S:`username`]
# 0x02 : SendMessagePacket
[S:`text message`]
# 0x03 : RequestMovePacket
[F:`move position x`|F:`move position y`|F:`move position z`]
# 0x04 : LoadWorldPacket
[]
# 0x05 : RequestRotatePacket
[F:`new angle`]
# 0x06 : RequestAnimPacket
[B:`animation id`|F:`value`]
# 0x07 : RequestAttack
[I:`object id`|B:`attack type`]
# 0x08 : RequestMoveDirection
[F:`dir X`|F:`dir Y`|F:`dir Z`]
# 0x09 :`RequestSetTarget`
[I:`target id`]
# 0x0A :`RequestAutoAttack`
[]
```

### Server Packets

```python
' Packet structure '
[ B:`opcode`|B:`packet size`|... payload ]

' Payloads '
# 0x00 : Ping
[]
# 0x01 : AuthResponse
[B:`AuthResponseType`]
# 0x02 : MessagePacket
[S:`sender`|S:`message`]
# 0x03 : SystemMessage
[B:`Message type`|S:`message`]
# 0x04 : PlayerInfo
[I:`object id`|S:`player name`|B:`class`|B:`isMage`|F:`heading`|F:`posX`|F:`posY`|F:`posZ`|I:`level`|I:`hp`|I:`maxhp`|I:`mp`|I:`maxMp`|I:`cp`|I:`maxCp`|I:`movespd`|I:`patkspd`|I:`matkspd`|F:`atkrange`|B:`con`|B:`dex`|B:`str`|B:`men`|B:`wit`|B:`int`|F:`colh`|F:`colr`|B:`race`|B:`sex`|B:`face`|B:`hair`|B:`haircolor`|I:`lefthand `|I:`righthand`|I:`chest`|I:`gloves`|I:`feet`]
# 0x05 : ObjectPosition
[I:`object id`|F:`posX`|F:`posY`|F:`posZ`]
# 0x06 : RemoveObject
[I:`object id`]
# 0x07 : ObjectRotation
[I:`object id`|F:`angle`]
# 0x08 : ObjectAnimation
[I:`object id`|B:`animation id`|F:`value`]
# 0x09 : ApplyDamage
[I:`sender id`|I:`target id`|I:`damage`|I:`new hp`|B:`critical hit`]
# 0x0A : NpcInfo
[I:`object id`|I:`npc id`|S:`npc class`|S:`npc type`|S:`npc name`|S:`npc title`|F:`heading`|F:`posX`|F:`posY`|F:`posZ`|F:`colh`|F:`colr`|I:`lefthand`|I:`righthand`|I:`movespeed`|I:`patkspd`|I:`matkspd`|I:`level`|I:`hp`|I:`maxhp`]
# 0x0B : ObjectMoveTo
[I:`object id`|F:`pos X`|F:`pos Y`|F:`pos Z`|I:`move speed`|B:`walking`]
# 0x0C : UserInfo
[I:`object id`|S:`player name`|B:`class`|B:`isMage`|F:`heading`|F:`posX`|F:`posY`|F:`posZ`|I:`level`|I:`hp`|I:`maxhp`|I:`movespd`|I:`patkspd`|I:`matkspd`|F:`colh`|F:`colr`|B:`race`|B:`sex`|B:`face`|B:`hair`|B:`haircolor`|I:`lefthand`|I:`righthand`|I:`chest`|I:`gloves`|I:`feet`]
# 0x0D : ObjectMoveDirecion
[I:`object id`|I:`move speed`|F:`dir X`|F:`dir Y`|F:`dir Z`]
# 0x0E : GameTime
[Long:`current server ticks`|I:`tick duration in Ms`|I:`day duration in minutes`]
# 0x0F : EntitySetTarget
[I:`object id`|I:`target id`]
# 0x010 : AutoAttackStart
[I:`object id`]
# 0x11 : AutoAttackStop
[I:`object id`]
# 0x12 : ActionFailed
[B:`action`]
```
