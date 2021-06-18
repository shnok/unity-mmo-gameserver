using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using UnityEngine.EventSystems;
 
public class ChatInput : MonoBehaviour, IPointerClickHandler {
    public bool chatOpened = false; 
    public InputField inputField;

    void Awake() {
        inputField = GetComponent<InputField>();
    }
    public void OnPointerClick (PointerEventData eventData) {
        Debug.Log("Click on me");
        inputField.interactable = true;
        chatOpened = true; 
        inputField.Select();
    }

    void Update() {
        if (Input.GetKeyDown(KeyCode.Return) || Input.GetKeyDown(KeyCode.KeypadEnter)) {           
            inputField.interactable = !inputField.interactable;
            chatOpened = inputField.interactable; 

            if(chatOpened) {
               inputField.Select();
            } else {
                if(inputField.text.Length > 0) {
                    DefaultClient.SendChatMessage(inputField.text);
                    inputField.text = "";
                }
            }
        }
    }
}