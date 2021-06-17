using UnityEngine;
using System.Threading.Tasks;

public class DefaultClient : MonoBehaviour
{
    static AsynchronousClient client;

    void Awake() {
        DontDestroyOnLoad(this.gameObject);
    }

    void Start() {
        //GameStateManager.SetState(GameState.MENU);
    }

    public async void Connect() {
        client = new AsynchronousClient("127.0.0.1", 11000);
        GamePacketHandler.SetClient(client);
        bool result = await Task.Run(client.Connect);
        if(result) {           
            GamePacketHandler.SendPing();
            GameStateManager.SetState(GameState.CONNECTED);
        }
    }

    public static int GetPing() {
        return client.GetPing();
    }

    public static void SendMessage() {
        GamePacketHandler.SendMessage("Hello world");
    }

    public static void Disconnect() {
        client.Disconnect();
    }
}
