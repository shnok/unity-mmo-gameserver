using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIOffline : MonoBehaviour
{
    public InputField field;

    void Start() {
        field = GameObject.Find("Username").GetComponent<InputField>();
        field.Select();
    }

    void Update() {
        if (Input.GetKeyDown(KeyCode.Return) || Input.GetKeyDown(KeyCode.KeypadEnter)) {  
            Connect();
        }
    }

    public void Connect() {
        string username = GetUsername();
        if(username != null) {
            DefaultClient.GetInstance().Connect(username);
        }       
    }

    private string GetUsername() {
        if(field != null) {
            if(field.text.Length != 0 && field.text.Length <= 16) {
                return field.text;
            } else {
                Debug.Log("Invalid username");                
            }
        }
        return null;
    }
}
