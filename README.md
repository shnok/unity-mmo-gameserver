# unity-mmo

An honest try at making an online action rpg demo with java (server) and unity3D (client). 

## Client Packets

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

## Server Packets

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