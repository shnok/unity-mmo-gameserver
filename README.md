# unity-mmo-server

<p>This project is firstly a self-learning experience about online games netcode.</p>

It is used to handle multiplayer in my other project [l2-unity](https://gitlab.com/shnok/l2-unity).
## Current features

For now it contains only basic features of an online RPG game. <p>Such as:
- Chat features
- Player sync
	- Position
	- Rotation
	- Animation
- Basic NPC AI
	- Patrol around point
	- Defined patrol path
	- Spawn/respawn
- Pathfinding
- Server Ghosting/Grid system
- Npc list and spawn list persistence

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
[I:`object id`|S:`player name`|F:`pos X`|F:`pos Y`|F:`pos Z`|I:`level`|I:`movespeed`|I:`patkspd`|I:`matkspd`|F:`attack range`|I:`hp`|I:`maxhp`|I:`mp`|I:`maxMp`|I:`cp`|I:`maxCp`]
# 0x05 : ObjectPosition
[I:`object id`|F:`pos X`|F:`pos Y`|F:`pos Z`]
# 0x06 : RemoveObject
[I:`object id`]
# 0x07 : ObjectRotation
[I:`object id`|F:`angle`]
# 0x08 : ObjectAnimation
[I:`object id`|B:`animation id`|F:`value`]
# 0x09 : ApplyDamage
[I:`sender id`|I:`target id`|I:`damage`|I:`new hp`|B:`critical hit`]
# 0x0A : NpcInfo
[I:`object id`|I:`npc id`|S:`npc class`|S:`npc type`|F:`pos X`|F:`pos Y`|F:`pos Z`|F:`collision height`|I:`movespeed`|I:`patkspd`|I:`matkspd`|I:`level`|I:`hp`|I:`maxhp`]
# 0x0B : ObjectMoveTo
[I:`object id`|F:`pos X`|F:`pos Y`|F:`pos Z`|I:`move speed`|B:`walking`]
# 0x0C : UserInfo
[I:`object id`|S:`player name`|F:`pos X`|F:`pos Y`|F:`pos Z`|I:`move speed`|I:`patkspd`|I:`matkspd`|I:`level`|I:`hp`|I:`maxhp`|I:`mp`|I:`maxMp`|I:`cp`|I:`maxCp`]
# 0x0D : ObjectMoveDirection
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
