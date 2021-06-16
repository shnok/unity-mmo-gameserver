using UnityEngine;
using System.Threading.Tasks;

public class DefaultClient : MonoBehaviour
{
    AsynchronousClient client;

    void Start() {
        GameStateManager.SetState(GameState.MENU);
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

    public void SendData() {
        GamePacketHandler.SendString("Hello world");
    }

    public void Disconnect() {
        client.Disconnect();
    }
}
