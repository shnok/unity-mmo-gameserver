using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIOnline : MonoBehaviour
{
    public Text pingText;
    // Start is called before the first frame update
    void Start()
    {
        pingText = GameObject.Find("Ping").GetComponent<Text>();
    }

    // Update is called once per frame
    void Update()
    {
        //pingText.text = "Ping: " + AsynchronousClient.getPing().ToString() + "ms";
    }
}
