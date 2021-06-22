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
            ServerPacketHandler.SetClient(client);
            ClientPacketHandler.SetClient(client);         
            ClientPacketHandler.SendPing();
            ClientPacketHandler.SendAuth(user);                                   
        }
    }

    public int GetPing() {
        return client.GetPing();
    }

    public void SendChatMessage(string message) {
        ClientPacketHandler.SendMessage(message);
    }
 
    public void Disconnect() {
        client.Disconnect();
        Chat.Clear();        
    }
}
