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
</p>

## Packets structure

### Client Packets

```python
' Packet structure '
[ Byte: `opcode` | Byte: `packet size` | ... payload ]

' Payloads '
# 0x00 : Ping
[]
# 0x01 : AuthRequest
[String: `username`]
# 0x02 : SendMessagePacket
[String: `text message`]
# 0x03 : RequestMovePacket
[Float: `move position x` | Float: `move position y` | Float: `move position z`]
# 0x04 : LoadWorldPacket
[]
# 0x05 : RequestRotatePacket
[Float: `new angle`]
# 0x06 : RequestAnimPacket
[Byte: `animation id` | Float: `value`]
# 0x07 : RequestAttack
[Int: `object id` | Byte: `attack type`]
```

### Server Packets

```python
' Packet structure '
[ Byte: `opcode` | Byte: `packet size` | ... payload ]

' Payloads '
# 0x00 : Ping
[]
# 0x01 : AuthResponse
[Byte: `AuthResponseType`]
# 0x02 : MessagePacket
[String: `sender` | String: `message`]
# 0x03 : SystemMessage
[Byte: `Message type` | String: `message`]
# 0x04 : PlayerInfo
[Int: `object id` | String: `player name` | Float: `pos X` | Float: `pos Y` | Float: `pos Z`| Int: `level`| Int: `hp`| Int: `maxhp`| Int: `stamina`| Int: `maxstamina`]
# 0x05 : ObjectPosition
[Int: `object id` | Float: `pos X` | Float: `pos Y` | Float: `pos Z`]
# 0x06 : RemoveObject
[Int: `object id`]
# 0x07 : ObjectRotation
[Int: `object id` | Float: `angle`]
# 0x08 : ObjectAnimation
[Int: `object id` | Byte: `animation id` | Float: `value`]
# 0x09 : ApplyDamage
[Int: `sender id` | Int: `target id` | Byte: `attack type` | Int: `value`]
# 0x0A : NpcInfo
[Int: `id` | Int: `npc id` | Float: `pos X` | Float: `pos Y` | Float: `pos Z`| Int: `level`| Int: `hp`| Int: `maxhp`]
# 0x0B : ObjectMoveTo
[Int: `id` | Float: `pos X` | Float: `pos Y` | Float: `pos Z`]

```
