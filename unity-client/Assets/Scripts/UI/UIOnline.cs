using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIOnline : MonoBehaviour
{
    public Text pingText;
    public Button disconnectButton;
    public Button sendMessageButton;
    public bool mouseEnabled = true;
    public static UIOnline _instance;
    public static UIOnline GetInstance() {
        return _instance;
    }

    void Awake() {
        if(_instance == null) {
            _instance = this;
        } else {
            Object.Destroy(gameObject);
        }
    }

    public void SendMessage() {
        DefaultClient.GetInstance().SendChatMessage("Hello world!");
    }

    public void Disconnect() {
        DefaultClient.GetInstance().Disconnect();
    }

    // Start is called before the first frame update
    void Start()
    {
        pingText = GameObject.Find("PingText").GetComponent<Text>();
        disconnectButton = GameObject.Find("DisconnectButton").GetComponent<Button>();
        disconnectButton.onClick.AddListener(DisconnectButtonClick);
        //sendMessageButton = GameObject.Find("SendMessageButton").GetComponent<Button>();
    }

    void Update()
    {
        pingText.text = "Ping: " + DefaultClient.GetInstance().GetPing().ToString() + "ms";
    }

    private void DisconnectButtonClick() {
        DefaultClient.GetInstance().Disconnect();
    }

    public void ToggleMouse() {
        mouseEnabled = !mouseEnabled;
    }
    public void EnableMouse() {
        mouseEnabled = true;
    }
    public void DisableMouse() {
        mouseEnabled = false;
    }

    public void OnGUI() {
        if(mouseEnabled) {
            Cursor.visible = true;
            Cursor.lockState = CursorLockMode.None;
        } else {
            Cursor.visible = false;
            Cursor.lockState = CursorLockMode.Locked;
        }
    }
}
