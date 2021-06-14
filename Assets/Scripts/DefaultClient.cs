using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using System.Threading.Tasks;

public class DefaultClient : MonoBehaviour
{
    void Start() {
        AsynchronousClient.setIp("127.0.0.1");
        AsynchronousClient.setPort(11000);
        Task.Run(AsynchronousClient.StartReceiving);
        Task.Run(AsynchronousClient.StartSending);
        GameStateManager.SetState(GameState.MENU);
    }

    public async void Connect() {
        bool result = await Task.Run(AsynchronousClient.Connect);
        AsynchronousClient.SendPing();
    }

    public void SendData() {
        AsynchronousClient.SendString("Hello world");
    }

    public void Disconnect() {
        AsynchronousClient.Disconnect();
    }
}
