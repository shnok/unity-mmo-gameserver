using System;
using System.Text;

public class MessagePacket : ClientPacket {
    private string _text;
    private string _sender;

    public MessagePacket(string text) : base(0x02) {
        setText(text);
        buildPacket(Encoding.ASCII.GetBytes(text));
    }

    public void setSender(string sender) {
        _sender = sender;
    }

    public void setText(string text) {
        _text = text;
    }
}