using UnityEngine;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Collections;

public class DefaultClient : MonoBehaviour {
    static AsynchronousClient client;
    public Player currentPlayer;
    public string username; 

    private static DefaultClient _instance;
    public static DefaultClient GetInstance() {
        return _instance;
    }

    void Awake(){                
        if (_instance == null) {
            DontDestroyOnLoad (this); 
            _instance = this;
        } else {
            Object.Destroy(gameObject);
        }
    }

    public async void Connect(string user) {
        username = user; 
        client = new AsynchronousClient("127.0.0.1", 11000);
        bool connected = await Task.Run(client.Connect);
        if(connected) {  
            ServerPacketHandler.GetInstance().SetClient(client);
            ClientPacketHandler.GetInstance().SetClient(client);         
            ClientPacketHandler.GetInstance().SendPing();
            ClientPacketHandler.GetInstance().SendAuth(user);                                   
        }
    }

    public void ClientReady() {
        ClientPacketHandler.GetInstance().SendLoadWorld();
    }

    public int GetPing() {
        return client.GetPing();
    }

    public void SendChatMessage(string message) {
        ClientPacketHandler.GetInstance().SendMessage(message);
    }
 
    public void Disconnect() {
        Debug.Log("Disconnect");
        GameStateManager.SetState(GameState.MENU);
    }

    void OnApplicationQuit() {
        if(client != null) {
            client.Disconnect();
        }   
    }

    public void OnDisconnectReady() {
        if(client != null) {
            client.Disconnect();
            client = null;
        }

        World.GetInstance().objects.Clear();
        World.GetInstance().players.Clear();
        Chat.Clear();     
    }
}
