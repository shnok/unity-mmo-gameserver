using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class InputManager : MonoBehaviour
{
    private CameraController cameraController;
    private PlayerController playerController;

    void Awake() {

    }
    
    void Start() {
        cameraController = CameraController.GetInstance();
        playerController = transform.GetComponent<PlayerController>();
    }

    void Update() {
        
        cameraController.UpdateZoom(Input.GetAxis("Mouse ScrollWheel"));
        cameraController.UpdateInputs(Input.GetAxis ("Mouse X"), Input.GetAxis ("Mouse Y"));
        
        playerController.UpdateInputs(Input.GetAxisRaw ("Horizontal"), Input.GetAxisRaw ("Vertical"));

        if(Input.GetKeyDown(KeyCode.Space)) {
           // playerController.Jump();
        }
    }
}
