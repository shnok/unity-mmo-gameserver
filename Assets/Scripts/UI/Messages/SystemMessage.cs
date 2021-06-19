public enum MessageType {
    USER_LOGGED_IN,
    USER_LOGGED_OFF
}

public abstract class SystemMessage {
    private byte[] _data;

    public SystemMessage() {}
    public SystemMessage(byte[] data) {
        _data = data;
    }
    
    public abstract override string ToString(); 
}