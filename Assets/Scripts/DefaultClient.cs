using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;

public class DefaultClient : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {

    }

    public void Connect() {
      AsynchronousClient.Connect("127.0.0.1", 11000);
    }

    public void SendData() {
      AsynchronousClient.Send("Hi from Unity");
    }

    public void Disconnect() {
      AsynchronousClient.Disconnect();
    }
}
