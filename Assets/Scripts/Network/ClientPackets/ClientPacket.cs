public abstract class ClientPacket {
    protected byte _packetType;
    protected byte _packetLength;
    protected byte[] _packetData;

    public ClientPacket(byte type) {
        _packetType = type;
    }

    public byte[] getData() {
        return _packetData;
    }

    public byte getType() {
        return _packetType;
    }

    public void setData(byte[] data) {
        _packetType = (byte)(data.Length);
        _packetLength = (byte)(data.Length);
        _packetData = data;
    }

    public void buildPacket(byte[] data) {
        _packetLength = (byte)(data.Length + 2);
        _packetData = new byte[_packetLength];
        _packetData[0] = _packetType;
        _packetData[1] = _packetLength;
        for (int i = 2; i < data.Length + 2; i++) {
            _packetData[i] = data[i - 2];
        }
    }
       // System.out.println("Sent: " + Arrays.toString(_packetData));

}