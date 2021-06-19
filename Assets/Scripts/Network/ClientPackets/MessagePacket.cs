using System;
using System.Text;

public class MessagePacket : ClientPacket {
    private string _text;
    private string _sender;

    public MessagePacket(string text) : base(0x02) {
        SetText(text);
        BuildPacket(Encoding.ASCII.GetBytes(text));
    }

    public void SetSender(string sender) {
        _sender = sender;
    }

    public void SetText(string text) {
        _text = text;
    }
}