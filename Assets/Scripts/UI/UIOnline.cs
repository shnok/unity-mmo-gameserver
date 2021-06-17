using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIOnline : MonoBehaviour
{
    public Text pingText;
    public Button disconnectButton;
    public Button sendMessageButton;

    public void SendMessage() {
        DefaultClient.SendMessage();
    }

    public void Disconnect() {
        DefaultClient.Disconnect();
    }

    // Start is called before the first frame update
    void Start()
    {
        pingText = GameObject.Find("PingText").GetComponent<Text>();
        disconnectButton = GameObject.Find("DisconnectButton").GetComponent<Button>();
        sendMessageButton = GameObject.Find("SendMessageButton").GetComponent<Button>();
    }



    // Update is called once per frame
    void Update()
    {
        pingText.text = "Ping: " + DefaultClient.GetPing().ToString() + "ms";
    }
}
