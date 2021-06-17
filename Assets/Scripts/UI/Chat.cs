using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Chat : MonoBehaviour
{
    private static string chat = "";
    private string _oldChat;

    public Text chatBox;
    public Scrollbar scrollBar;

    /* System message */
    public static void AddMessage(byte type) {

    }

    public static void AddMessage(string sender, string text) {
        ConcatMessage(sender + ": " + text);
    }

    private static void ConcatMessage(string message) {
        if(chat.Length > 0) {
            chat += "\r\n";
        }

        chat += message;
    }

    // Start is called before the first frame update
    void Start() {
        chatBox = GameObject.Find("Chat").GetComponent<Text>();
        scrollBar = GameObject.Find("ChatScrollBar").GetComponent<Scrollbar>();
    }

    IEnumerator WaitForChatUpdate() {
        yield return new WaitForSeconds(0.1f);
        scrollBar.value = 0;
    }

    void Update() {
        if(_oldChat != chat) {
            _oldChat = chat;
            chatBox.text = chat;
            StartCoroutine(WaitForChatUpdate());
        }      
    }
}
