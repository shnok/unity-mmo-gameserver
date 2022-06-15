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
```

## Server Packets

```python
' Packet structure '
...
```