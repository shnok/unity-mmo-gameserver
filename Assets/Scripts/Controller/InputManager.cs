using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class InputManager : MonoBehaviour
{
    public static InputManager _instance;
    private CameraController cameraController;
    private PlayerController playerController;
    public static InputManager GetInstance() {
        return _instance;
    }

    void Awake() {
        if (_instance == null) {
            _instance = this;
        }
    }
    
    void Start() {
        cameraController = CameraController.GetInstance();
        playerController = PlayerController.GetInstance();
    }

    void Update() {
        
        cameraController.UpdateZoom(Input.GetAxis("Mouse ScrollWheel"));
        cameraController.UpdateInputs(Input.GetAxis ("Mouse X"), Input.GetAxis ("Mouse Y"));
        
        playerController.UpdateInputs(Input.GetAxisRaw ("Horizontal"), Input.GetAxisRaw ("Vertical"));
        if(Input.GetKeyDown(KeyCode.Space)) {
            playerController.Jump();
        }
    }
}
