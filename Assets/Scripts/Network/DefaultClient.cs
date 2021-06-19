using UnityEngine;
using System.Threading.Tasks;

public class DefaultClient : MonoBehaviour
{
    static AsynchronousClient client;
    public static string username;

    void Awake() {
        DontDestroyOnLoad(this.gameObject);
    }

    public static async void Connect(string user) {
        username = user; 
        client = new AsynchronousClient("127.0.0.1", 11000);
        bool connected = await Task.Run(client.Connect);
        if(connected) {  
            ServerPacketHandler.SetClient(client);
            ClientPacketHandler.SetClient(client);         
            ClientPacketHandler.SendPing();
            ClientPacketHandler.SendAuth(user);                                   
        }
    }

    public static int GetPing() {
        return client.GetPing();
    }

    public static void SendChatMessage(string message) {
        ClientPacketHandler.SendMessage(message);
    }

    public static void Disconnect() {
        client.Disconnect();
        Chat.Clear();        
    }
}
