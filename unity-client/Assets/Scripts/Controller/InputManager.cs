using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class InputManager : MonoBehaviour
{
    private CameraController _cameraController;
    private PlayerController _playerController;
    private bool _axisPressed = false;
    private bool _jump = false;
    private bool _dodge = false;
    private bool _attack = false;

    private static InputManager _instance;
    public static InputManager GetInstance() {
        return _instance;
    }
    void Awake() {
        if(_instance == null) {
            _instance = this;
        } else {
            Object.Destroy(gameObject);
        }
    }

    void Update() {       
        if(!UIOnline.GetInstance().mouseEnabled && _playerController && _cameraController) {
            _cameraController.UpdateZoom(Input.GetAxis("Mouse ScrollWheel"));
            _cameraController.UpdateInputs(Input.GetAxis("Mouse X"), Input.GetAxis("Mouse Y"));
            _playerController.UpdateInputs(Input.GetAxisRaw("Horizontal"), Input.GetAxisRaw("Vertical"));

            _axisPressed = Input.GetAxisRaw("Horizontal") != 0 || Input.GetAxisRaw("Vertical") != 0;
            _jump = Input.GetKeyDown(KeyCode.Space);
            _dodge = Input.GetKeyDown(KeyCode.LeftShift);
            _attack = Input.GetMouseButtonDown(0);
        } else {
            _axisPressed = false;
            _jump = false;
            _dodge = false;
            _attack = false;
        }

        if(Input.GetKeyDown(KeyCode.Return) || Input.GetKeyDown(KeyCode.KeypadEnter)) {
            ChatInput.GetInstance().ToggleOpenChat();

            if(ChatInput.GetInstance().chatOpened)
                UIOnline.GetInstance().EnableMouse();
            else
                UIOnline.GetInstance().DisableMouse();
        }

        if(Input.GetKeyDown(KeyCode.LeftAlt) || Input.GetKeyDown(KeyCode.RightAlt)) {
            if(!ChatInput.GetInstance().chatOpened)
                UIOnline.GetInstance().ToggleMouse();
        }
    }

    public void SetCameraController(CameraController controller) {
        _cameraController = controller;
    }
    public void SetPlayerController(PlayerController controller) {
        _playerController = controller;
    }
    public bool AxisPressed() {
        return _axisPressed;
    }

    public bool Jump() {
        return _jump;
    }

    public bool Dodge() {
        return _dodge;
    }

    public bool Attack() {
        return _attack;
    }
}
