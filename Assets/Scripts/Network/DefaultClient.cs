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
        client = new AsynchronousClient("127.0.0.1", 11000);
        GamePacketHandler.SetClient(client);
        bool result = await Task.Run(client.Connect);
        if(result) {           
            GamePacketHandler.SendPing();
            GamePacketHandler.SendAuth(user);   
            username = user;                     
        }
    }

    public static int GetPing() {
        return client.GetPing();
    }

    public static void SendChatMessage(string message) {
        GamePacketHandler.SendMessage(message);
    }

    public static void Disconnect() {
        client.Disconnect();
        Chat.Clear();        
    }
}
